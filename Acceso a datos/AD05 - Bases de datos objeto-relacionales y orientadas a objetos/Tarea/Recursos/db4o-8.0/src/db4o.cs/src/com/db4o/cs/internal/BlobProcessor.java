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

import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;

class BlobProcessor implements Runnable {
	
	private ClientObjectContainer			stream;
	private Queue4 				queue = new NonblockingQueue();
	private boolean				terminated = false;
	
	BlobProcessor(ClientObjectContainer aStream){
		stream = aStream;
	}

	void add(MsgBlob msg){
		synchronized(queue){
			queue.add(msg);
		}
	}
	
	synchronized boolean isTerminated(){
		return terminated;
	}
	
	public void run(){
		try{
			Socket4Adapter socket = stream.createParallelSocket();
			
			MsgBlob msg = null;
			
			// no blobLock synchronisation here, since our first msg is valid
			synchronized(queue){
				msg = (MsgBlob)queue.next();
			}
			
			while(msg != null){
				msg.write(socket);
				msg.processClient(socket);
				synchronized(stream._blobLock){
					synchronized(queue){
						msg = (MsgBlob)queue.next();
					}
					if(msg == null){
						terminated = true;
						Msg.CLOSE_SOCKET.write(socket);
						try{
							socket.close();
						}catch(Exception e){
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
