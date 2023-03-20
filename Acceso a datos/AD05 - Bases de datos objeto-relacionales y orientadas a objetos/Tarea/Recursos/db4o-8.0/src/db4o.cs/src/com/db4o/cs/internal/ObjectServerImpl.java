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
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.foundation.*;
import com.db4o.cs.internal.config.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.internal.events.*;
import com.db4o.internal.threading.*;
import com.db4o.types.*;

public class ObjectServerImpl implements ObjectServerEvents, ObjectServer, ExtObjectServer, Runnable, TransientClass {
	
	private static final int START_THREAD_WAIT_TIMEOUT = 5000;

	private final String _name;

	private ServerSocket4 _serverSocket;
	
	private int _port;

	private int i_threadIDGen = 1;

	private final Collection4 _dispatchers = new Collection4();

	private LocalObjectContainer _container;
	private ClientTransactionPool _transactionPool;

	private final Lock4 _startupLock = new Lock4();
	
	private ServerConfigurationImpl _serverConfig;
	
	private BlockingQueue _committedInfosQueue = new BlockingQueue();
	
	private CommittedCallbacksDispatcher _committedCallbacksDispatcher;
    
    private boolean _caresAboutCommitted;

	private final Socket4Factory _socketFactory;

	private final boolean _isEmbeddedServer;
	
	private final ClassInfoHelper _classInfoHelper;
	
	private final Event4Impl<StringEventArgs> _clientDisconnected = Event4Impl.newInstance();
	private final Event4Impl<ClientConnectionEventArgs> _clientConnected = Event4Impl.newInstance();
	private final Event4Impl<ServerClosedEventArgs> _closed = Event4Impl.newInstance();
	
	public ObjectServerImpl(final LocalObjectContainer container, ServerConfiguration serverConfig, int port) {
		this(container, (ServerConfigurationImpl) serverConfig, (port < 0 ? 0 : port), port == 0);
	}

	private ObjectServerImpl(final LocalObjectContainer container, ServerConfigurationImpl serverConfig, int port, boolean isEmbeddedServer) {
		_isEmbeddedServer = isEmbeddedServer;
		_container = container;
		_serverConfig = serverConfig;
		_socketFactory = serverConfig.networking().socketFactory();
		_transactionPool = new ClientTransactionPool(container);
		_port = port;
		_name = "db4o ServerSocket FILE: " + container.toString() + "  PORT:"+ _port;
		
		_container.setServer(true);	
		configureObjectServer();
		
		_classInfoHelper = new ClassInfoHelper(Db4oClientServerLegacyConfigurationBridge.asLegacy(serverConfig));
		
		_container.classCollection().checkAllClassChanges();
		
		boolean ok = false;
		try {
			ensureLoadStaticClass();
			startCommittedCallbackThread(_committedInfosQueue);
			startServer();
			if(_serverConfig != null) {
				_serverConfig.applyConfigurationItems(this);
			}
			ok = true;
		} finally {
			if(!ok) {
				close();
			}
		}
	}

	private void startServer() {		
		if (isEmbeddedServer()) {
			return;
		}
		
		_startupLock.run(new Closure4() { public Object run() {
			startServerSocket();
			startServerThread();
			boolean started=false;
			while(!started) {
				try {
					_startupLock.snooze(START_THREAD_WAIT_TIMEOUT);
					started=true;
				}
				// not specialized to InterruptException for .NET conversion
				catch (Exception exc) {
				}
			}
			
			return null;
		}});
	}

	private void startServerThread() {
		_startupLock.run(new Closure4() { public Object run() {
			threadPool().start(_name, ObjectServerImpl.this);
			return null;
		}});
	}

	private ThreadPool4 threadPool() {
	    return _container.threadPool();
    }

	private void startServerSocket() {
		try {
			_serverSocket = _socketFactory.createServerSocket(_port);
			_port = _serverSocket.getLocalPort();
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
		_serverSocket.setSoTimeout(_serverConfig.timeoutServerSocket());
	}

	private boolean isEmbeddedServer() {
		return _isEmbeddedServer;
	}

	private void ensureLoadStaticClass() {
		_container.produceClassMetadata(_container._handlers.ICLASS_STATICCLASS);
	}

	private void configureObjectServer() {
		((CommonConfigurationImpl)_serverConfig.common()).callbackMode(CallBackMode.DELETE_ONLY);
		// the minimum activation depth of com.db4o.User.class should be 1.
		// Otherwise, we may get null password.
		_serverConfig.common().objectClass(User.class).minimumActivationDepth(1);
	}

	public void backup(String path) throws IOException {
		_container.backup(path);
	}

	final void checkClosed() {
		if (_container == null) {
			Exceptions4.throwRuntimeException(Messages.CLOSED_OR_OPEN_FAILED, _name);
		}
		_container.checkClosed();
	}
	
	/**
	 * System.IDisposable.Dispose()
	 */
	public void dispose() {
		close();
	}

	public synchronized boolean close() {
		return close(ShutdownMode.NORMAL);
	}

	public synchronized boolean close(ShutdownMode mode) {
		try {
			closeServerSocket();
			stopCommittedCallbacksDispatcher();
			closeMessageDispatchers(mode);
			return closeFile(mode);
		}
		finally {
			triggerClosed();
		}
	}

	private void stopCommittedCallbacksDispatcher() {
		if(_committedCallbacksDispatcher != null){
			_committedCallbacksDispatcher.stop();
		}
	}

	private boolean closeFile(ShutdownMode mode) {
		if (_container != null) {
			_transactionPool.close(mode);
			_container = null;
		}
		return true;
	}

	private void closeMessageDispatchers(ShutdownMode mode) {
		Iterator4 i = iterateDispatchers();
		while (i.moveNext()) {
			try {
				((ServerMessageDispatcher) i.current()).close(mode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Iterator4 iterateDispatchers() {
		synchronized (_dispatchers) {
			return new Collection4(_dispatchers).iterator();
		}
	}

	private void closeServerSocket() {
		try {
			if (_serverSocket != null) {
				_serverSocket.close();
			}
		} catch (Exception e) {
			if (Deploy.debug) {
				System.out
						.println("ObjectServer.close() ServerSocket failed to close.");
			}
		}
		_serverSocket = null;
	}

	public Configuration configure() {
		return Db4oClientServerLegacyConfigurationBridge.asLegacy(_serverConfig);
	}

	public ExtObjectServer ext() {
		return this;
	}

	private ServerMessageDispatcherImpl findThread(int a_threadID) {
		synchronized (_dispatchers) {
			Iterator4 i = _dispatchers.iterator();
			while (i.moveNext()) {
				ServerMessageDispatcherImpl serverThread = (ServerMessageDispatcherImpl) i.current();
				if (serverThread._threadID == a_threadID) {
					return serverThread;
				}
			}
		}
		return null;
	}

	Transaction findTransaction(int threadID) {
		ServerMessageDispatcherImpl dispatcher = findThread(threadID);
		return (dispatcher == null ? null : dispatcher.transaction());
	}

	public synchronized void grantAccess(String userName, String password) {
		checkClosed();
		synchronized (_container.lock()) {
			User existing = getUser(userName);
			if (existing != null) {
				setPassword(existing, password);
			} else {
				addUser(userName, password);
			}
			_container.commit();
		}
	}

	private void addUser(String userName, String password) {
		_container.store(new User(userName, password));
	}

	private void setPassword(User existing, String password) {
		existing.password = password;
		_container.store(existing);
	}

	public User getUser(String userName) {
		final ObjectSet result = queryUsers(userName);
		if (!result.hasNext()) {
			return null;
		}
		return (User) result.next();
	}

	private ObjectSet queryUsers(String userName) {
		_container.showInternalClasses(true);
		try {
			return _container.queryByExample(new User(userName, null));
		} finally {
			_container.showInternalClasses(false);
		}
	}

	public ObjectContainer objectContainer() {
		return _container;
	}

	public synchronized ObjectContainer openClient() {
		checkClosed();
		synchronized (_container.lock()) {
		    return new ObjectContainerSession(_container);
		}
	}
	
	void removeThread(ServerMessageDispatcherImpl dispatcher) {
		synchronized (_dispatchers) {
			_dispatchers.remove(dispatcher);
            checkCaresAboutCommitted();
		}
		
		triggerClientDisconnected(dispatcher.name());
	}

	public synchronized void revokeAccess(String userName) {
		checkClosed();
		synchronized (_container.lock()) {
			deleteUsers(userName);
			_container.commit();
		}
	}

	private void deleteUsers(String userName) {
		ObjectSet set = queryUsers(userName);
		while (set.hasNext()) {
			_container.delete(set.next());
		}
	}

	public void run() {
		logListeningOnPort();
		notifyThreadStarted();
		listen();
	}

	private void startCommittedCallbackThread(BlockingQueue committedInfosQueue) {
		if(isEmbeddedServer()) {
			return;
		}
		_committedCallbacksDispatcher = new CommittedCallbacksDispatcher(this, committedInfosQueue);
		threadPool().start("Server commit callback dispatcher thread", _committedCallbacksDispatcher);
	}

	private void listen() {
		// we are keeping a reference to container to avoid race conditions upon closing this server
		final LocalObjectContainer threadContainer = _container;
		while (_serverSocket != null) {
			threadContainer.withEnvironment(new Runnable() { public void run() {
				try {
					Socket4 socket = _serverSocket.accept();
					ServerMessageDispatcherImpl messageDispatcher = 
						new ServerMessageDispatcherImpl(
								ObjectServerImpl.this, 
								new ClientTransactionHandle(_transactionPool),
								socket,
								newThreadId(),
								false,
								threadContainer.lock());
					
					addServerMessageDispatcher(messageDispatcher);
						
					threadPool().start("server message dispatcher (still initializing)", messageDispatcher);
				} catch (Exception e) {
					
					// CatchAll because we can get expected timeout exceptions
					// although we still want to continue to use the ServerSocket.
					
					// No nice way to catch a specific exception because 
					// SocketTimeOutException is JDK 1.4 and above.
					
					//e.printStackTrace();
				}
			}});								
		}
	}

	private void triggerClientConnected(ServerMessageDispatcher messageDispatcher) {
		_clientConnected.trigger(new ClientConnectionEventArgs(messageDispatcher));
    }
	
	private void triggerClientDisconnected(String clientName) {
		_clientDisconnected.trigger(new StringEventArgs(clientName));
    }	

	private void triggerClosed() {
		_closed.trigger(new ServerClosedEventArgs());
    }


	private void notifyThreadStarted() {
		_startupLock.run(new Closure4() { public Object run() {
			_startupLock.awake();
			return null;
		}});
	}

	private void logListeningOnPort() {
		_container.logMsg(Messages.SERVER_LISTENING_ON_PORT, "" + _serverSocket.getLocalPort());
	}

	private int newThreadId() {
		return i_threadIDGen++;
	}

	private void addServerMessageDispatcher(ServerMessageDispatcher dispatcher) {
		synchronized (_dispatchers) {
			_dispatchers.add(dispatcher);
            checkCaresAboutCommitted();
		}
		
		triggerClientConnected(dispatcher);
	}

	public void addCommittedInfoMsg(MCommittedInfo message) {
		_committedInfosQueue.add(message);			
	}
	
	public void broadcastReplicationCommit(long timestamp, List concurrentTimestamps) {
		Iterator4 i = iterateDispatchers();
		while(i.moveNext()){
			ServerMessageDispatcher dispatcher = (ServerMessageDispatcher) i.current();
			LocalTransaction transaction = (LocalTransaction) dispatcher.transaction();
			transaction.notifyAboutOtherReplicationCommit(timestamp, concurrentTimestamps);
		}
	}
	
	public void broadcastMsg(Msg message, BroadcastFilter filter) {		
		Iterator4 i = iterateDispatchers();
		while(i.moveNext()){
			ServerMessageDispatcher dispatcher = (ServerMessageDispatcher) i.current();
			if(filter.accept(dispatcher)) {
				dispatcher.write(message);
			}
		}
	}
    
    public boolean caresAboutCommitted(){
        return _caresAboutCommitted;
    }
    
    public void checkCaresAboutCommitted(){
        _caresAboutCommitted = anyDispatcherCaresAboutCommitted();
    }

	private boolean anyDispatcherCaresAboutCommitted() {
        Iterator4 i = iterateDispatchers();
        while(i.moveNext()){
            ServerMessageDispatcher dispatcher = (ServerMessageDispatcher) i.current();
            if(dispatcher.caresAboutCommitted()){
                return true;
            }
        }
		return false;
	}

	public int port() {
		return _port;
	}
	
	public int clientCount(){
	    synchronized(_dispatchers){
	        return _dispatchers.size();
	    }
	}

	public ClassInfoHelper classInfoHelper() {
		return _classInfoHelper;
	}

	public Event4<ClientConnectionEventArgs> clientConnected() {
		return _clientConnected;
    }
	
	public Event4<StringEventArgs> clientDisconnected() {
		return _clientDisconnected;
    }	

	public Event4<ServerClosedEventArgs> closed() {
		return _closed;
    }
	
	void withEnvironment(Runnable runnable) {
		_container.withEnvironment(runnable);
	}

	public int transactionCount() {
		return _transactionPool.openTransactionCount();
	}	
}
