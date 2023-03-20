/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.cs.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.cs.foundation.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.events.Event4;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.events.Event4Impl;

public final class ServerMessageDispatcherImpl implements ServerMessageDispatcher, Runnable {

    private String _clientName;

    private boolean _loggedin;
    
    private boolean _closeMessageSent;

    private final ObjectServerImpl _server;

    private Socket4Adapter _socket;

    private final ClientTransactionHandle _transactionHandle;
    
    private Hashtable4 _queryResults;
    
    final int _threadID;

	private CallbackObjectInfoCollections _committedInfo;

	private boolean _caresAboutCommitted;
	
	private boolean _isClosed;
	
	private final Object _lock = new Object();
	
	private final Object _mainLock;

	private final Event4Impl<MessageEventArgs> _messageReceived = Event4Impl.newInstance();
	
	private Thread _thread;
	
    ServerMessageDispatcherImpl(ObjectServerImpl server,
			ClientTransactionHandle transactionHandle, Socket4 socket4,
			int threadID, boolean loggedIn, Object mainLock) throws Exception {

    	_mainLock = mainLock;
		_transactionHandle = transactionHandle;
		_loggedin = loggedIn;

		_server = server;
		_threadID = threadID;
		_socket = new Socket4Adapter(socket4);
		_socket.setSoTimeout(((Config4Impl) server.configure())
				.timeoutServerSocket());

		// TODO: Experiment with packetsize and noDelay
		// i_socket.setSendBufferSize(100);
		// i_socket.setTcpNoDelay(true);
	}
    
    public boolean close() {
    	return close(ShutdownMode.NORMAL);
    }

    public boolean close(ShutdownMode mode) {
        synchronized(_lock) {
            if (!isMessageDispatcherAlive()) {
                return true;
            }
            _isClosed = true;
        }
    	synchronized(_mainLock) {
			_transactionHandle.releaseTransaction(mode);
			if(! mode.isFatal()){
				sendCloseMessage();
			}
			_transactionHandle.close(mode);
			closeSocket();
			removeFromServer();
			return true;
    	}
	}

    public void closeConnection() {
        synchronized (_lock) {
			if (!isMessageDispatcherAlive()) {
				return;
			}
			_isClosed = true;
        }
        synchronized (_mainLock) {
			closeSocket();
			removeFromServer();
		}
	}
    
    public boolean isMessageDispatcherAlive() {
        synchronized(_lock){
            return !_isClosed;
        }
    }

	private void sendCloseMessage() {
		try {
            if (! _closeMessageSent) {
                _closeMessageSent = true;
                write(Msg.CLOSE);
            }
        } catch (Exception e) {
            if (Debug4.atHome) {
                e.printStackTrace();
            }
        }
	}

	private void removeFromServer() {
		try {
            _server.removeThread(this);
        } catch (Exception e) {
            if (Debug4.atHome) {
                e.printStackTrace();
            }
        }
	}

	private void closeSocket() {
		try {
			if(_socket != null) {
				_socket.close();
			}
        } catch (Db4oIOException e) {
            if (Debug4.atHome) {
                e.printStackTrace();
            }
        }
	}

	public Transaction transaction() {
    	return _transactionHandle.transaction();
    }

	public void run() {
		_thread = Thread.currentThread();
		try{
			setDispatcherName("" + _threadID);
			_server.withEnvironment(new Runnable(){ public void run() {
					messageLoop();
			}});
			
		}finally{
			close();
		}
	}
    
    private void messageLoop(){
        while (isMessageDispatcherAlive()) {
            try {
                if(! messageProcessor()){
                    return;
                }
            } catch (Db4oIOException e) {
            	if(DTrace.enabled){
            		DTrace.ADD_TO_CLASS_INDEX.log(e.toString());
            	}
                return;
            }
        }
    }
    
    private boolean messageProcessor() throws Db4oIOException{
        Msg message = Msg.readMessage(this, transaction(), _socket);
        if(message == null){
            return true;
        }
        
        triggerMessageReceived(message);
        
        if(!_loggedin && !Msg.LOGIN.equals(message)) {
        	return true;
        }

        // TODO: COR-885 - message may process against closed server
        // Checking aliveness just makes the issue less likely to occur. Naive synchronization against main lock is prohibitive.        
    	return processMessage(message);
    }

	public boolean processMessage(Msg message) {
		if(isMessageDispatcherAlive()) {			
			if(message instanceof MessageWithResponse) {
				MessageWithResponse msgWithResp = (MessageWithResponse) message;
				try {
					Msg reply = msgWithResp.replyFromServer();
					write(reply);
				}
	    		catch(Db4oRecoverableException exc) {
					writeException(message, exc);
					return true;
	    		}
	    		catch(Throwable t){
	    			t.printStackTrace();
	    			fatalShutDownServer(t);
	    			return false;
	    		}
				try {
					msgWithResp.postProcessAtServer();
					return true;
	    		}
	    		catch(Exception exc) {
	    			exc.printStackTrace();
	    		}
				return true;
			}
			try {
				((ServerSideMessage)message).processAtServer();
				return true;
    		}
    		catch(Db4oRecoverableException exc) {
    			exc.printStackTrace();
        		return true;
    		}
    		catch(Throwable t){
    			t.printStackTrace();
    			fatalShutDownServer(t);
    		}
    	}
    	return false;
	}

	private void fatalShutDownServer(Throwable origExc) {
		new FatalServerShutdown(_server, origExc);
	}

	private void writeException(Msg message, Exception exc) {
		if(!(message instanceof MessageWithResponse)) {
			exc.printStackTrace();
			return;
		}
		if(!(exc instanceof RuntimeException)) {
			exc = new Db4oException(exc);
		}
		
		ensureStackTraceCapture(exc);
		
		// Writing exceptions can produce ClassMetadata in
		// the main ObjectContainer.
		synchronized (_mainLock) {
			message.writeException((RuntimeException)exc);
		}
	}
	
	/**
	 * @sharpen.remove
	 */
	private void ensureStackTraceCapture(Exception exc) {
		exc.printStackTrace(new PrintStream(new OutputStream(){
			@Override
			public void write(int arg0) throws IOException {
			}
		}));
	}

    private void triggerMessageReceived(Message message) {
    	_messageReceived.trigger(new MessageEventArgs(message));
    }

	public ObjectServerImpl server() {
    	return _server;
    }
    
	public void queryResultFinalized(int queryResultID) {
    	_queryResults.remove(queryResultID);
	}

	public void mapQueryResultToID(LazyClientObjectSetStub stub, int queryResultID) {
    	if(_queryResults == null){
    		_queryResults = new Hashtable4();
    	}
    	_queryResults.put(queryResultID, stub);
	}
	
	public LazyClientObjectSetStub queryResultForID(int queryResultID){
		return (LazyClientObjectSetStub) _queryResults.get(queryResultID);
	}

	public void switchToFile(MSwitchToFile message) {
        synchronized (_mainLock) {
            String fileName = message.readString();
            try {
                _transactionHandle.releaseTransaction(ShutdownMode.NORMAL);
            	_transactionHandle.acquireTransactionForFile(fileName);
                write(Msg.OK);
            } catch (Exception e) {
                if (Debug4.atHome) {
                    System.out.println("Msg.SWITCH_TO_FILE failed.");
                    e.printStackTrace();
                }
                _transactionHandle.releaseTransaction(ShutdownMode.NORMAL);
                write(Msg.ERROR);
            }
        }
    }

    public void switchToMainFile() {
        synchronized (_mainLock) {
            _transactionHandle.releaseTransaction(ShutdownMode.NORMAL);
            write(Msg.OK);
        }
    }

    public void useTransaction(MUseTransaction message) {
        int threadID = message.readInt();
		Transaction transToUse = _server.findTransaction(threadID);
		_transactionHandle.transaction(transToUse);
    }
    
    public boolean write(Msg msg){
    	synchronized(_lock) {
    	    if(! isMessageDispatcherAlive()){
    	        return false;
    	    }
    		return msg.write(_socket);
    	}
    }
    
    public Socket4Adapter socket(){
    	return _socket;
    }

	public String name() {
		return _clientName;
	}
	
	public void setDispatcherName(String name) {
		_clientName = name;
		thread().setName("db4o server message dispatcher " + name);
	}
    
    public int dispatcherID() {
    	return _threadID;
    }

	public void login() {
		_loggedin = true;
	}

	public boolean caresAboutCommitted() {
		return _caresAboutCommitted;
	}

	public void caresAboutCommitted(boolean care) {
		_caresAboutCommitted = true;
        server().checkCaresAboutCommitted();
	}

	public CallbackObjectInfoCollections committedInfo() {
		return _committedInfo;
	}

	public void dispatchCommitted(CallbackObjectInfoCollections committedInfo) {
		_committedInfo = committedInfo;
	}
	
	public boolean willDispatchCommitted() {
		return server().caresAboutCommitted();
	}

	public ClassInfoHelper classInfoHelper() {
		return server().classInfoHelper();
	}

	/**
	 * EventArgs => MessageEventArgs
	 */
	public Event4<MessageEventArgs> messageReceived() {
		return _messageReceived;
    }

	public void join() throws InterruptedException {
		thread().join();
    }

	private Thread thread() {
	    if (null == _thread) {
			throw new IllegalStateException();
		}
		return _thread;
    }
	
}