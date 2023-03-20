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
package com.db4o.db4ounit.common.foundation.network;

import com.db4o.cs.foundation.*;
import com.db4o.cs.internal.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.threading.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NetworkSocketTestCase implements TestLifeCycle {

	private ServerSocket4 _serverSocket;

	private int _port;
	
	Socket4Adapter _client;
	
	Socket4Adapter _server;

	private Socket4Factory _plainSocketFactory = new StandardSocket4Factory();

	public static void main(String[] args) {
		new ConsoleTestRunner(NetworkSocketTestCase.class).run();
	}

	public void setUp() throws Exception {
		_serverSocket = _plainSocketFactory.createServerSocket(0);
		_port = _serverSocket.getLocalPort();
		_client = new Socket4Adapter(_plainSocketFactory.createSocket("localhost", _port));
		_server = new Socket4Adapter(_serverSocket.accept());
	}

	public void tearDown() throws Exception {
		_serverSocket.close();
	}

	public void testReadByteArrayCloseClient() throws Exception {
		assertReadClose(_client, new CodeBlock (){
			public void run() {
				_server.read(new byte[10], 0, 10);
			}			
		});
	}

	public void testReadByteArrayCloseServer() throws Exception {
		assertReadClose(_server, new CodeBlock (){
			public void run() {
				_client.read(new byte[10], 0, 10);
			}			
		});
	}

	
	public void testWriteByteArrayCloseClient() throws Exception {	
		assertWriteClose(_client, new CodeBlock (){
			public void run() {
			    _server.write(new byte[10]);
			}			
		});
	}
	
	public void testWriteByteArrayCloseServer() throws Exception {	
		assertWriteClose(_server, new CodeBlock (){
			public void run() {
			    _client.write(new byte[10]);
			}			
		});
	}
	
	public void testWriteByteArrayPartCloseClient() throws Exception {	
		assertWriteClose(_client, new CodeBlock (){
			public void run() {
			    _server.write(new byte[10], 0, 10);
			}			
		});
	}
	
	public void testWriteByteArrayPartCloseServer() throws Exception {	
		assertWriteClose(_server, new CodeBlock (){
			public void run() {
			    _client.write(new byte[10], 0, 10);
			}			
		});
	}
	
	private void assertReadClose(final Socket4Adapter socketToBeClosed,final CodeBlock codeBlock) throws InterruptedException {
	    CatchAllThread thread = new CatchAllThread(codeBlock);
	    thread.ensureStarted();
		socketToBeClosed.close();
		thread.join();
		Assert.isInstanceOf(Db4oIOException.class, thread.caught());
	}
	
	private void assertWriteClose(final Socket4Adapter socketToBeClosed,final CodeBlock codeBlock){
		socketToBeClosed.close();
		Assert.expect(Db4oIOException.class, new CodeBlock() {
            public void run() throws Throwable {
                // This is a magic number: 
                // On my machine all tests start to pass when I write at least 7 times.
                // Trying with 20 on the build machine.
                for (int i = 0; i < 20; i++) {
                    codeBlock.run();
                }
            }
        });
	}
	
	
	static class CatchAllThread {
	    
	    private final Thread _thread;
	    
	    boolean _isRunning;
	    
        final CodeBlock _codeBlock;
        
	    Throwable _throwable;
	    
	    public CatchAllThread(CodeBlock codeBlock){
	        _thread = new Thread(new Runnable() {
                public void run() {
                    try{
                        synchronized(this){
                            _isRunning = true;
                        }
                        _codeBlock.run();
                    } catch (Throwable t){
                        _throwable = t;
                    }
                }
            }, "NetworkSocketTestCase.CatchAllThread");
	        _thread.setDaemon(true);
	        _codeBlock = codeBlock;
	    }
	    
	    public void join() throws InterruptedException {
	        _thread.join();
        }
	    
	    private boolean isRunning(){
	        synchronized(this){
	            return _isRunning;
	        }
	    }
	    
	    public void ensureStarted(){
	        _thread.start();
	        while(! isRunning()){
	            Runtime4.sleep(10);
	        }
	        Runtime4.sleep(10);
	    }
	    
	    public Throwable caught(){
	        return _throwable;
	    }
	    
	}
	
}
