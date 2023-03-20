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
package com.db4o.config;

import com.db4o.messaging.*;

/**
 * Client/Server configuration interface. 
 */
public interface ClientServerConfiguration {
		
	/**
	 * Sets the number of IDs to be pre-allocated in the database for new 
	 * objects created on the client.
	 * This setting should be used on the client side. In embedded mode this setting
	 * has no effect.
	 * @param prefetchIDCount
	 *            The number of IDs to be prefetched
	 */
	void prefetchIDCount(int prefetchIDCount);

	/**
	 * Sets the number of objects to be prefetched for an ObjectSet.
	 * This setting should be used on the server side.
	 * 
	 * @param prefetchObjectCount
	 *            The number of objects to be prefetched
	 */
	void prefetchObjectCount(int prefetchObjectCount);
	
	/**
	 * Sets the depth to which prefetched objects are activated.
	 * This setting should be used on the client side.
	 * 
	 * @param prefetchDepth
	 */
	void prefetchDepth(int prefetchDepth);    
	
	/**
	 * Sets the slot cache size to the given value.
	 * 
	 * @param slotCacheSize
	 */
	void prefetchSlotCacheSize(int slotCacheSize);


	/**
	 * sets the MessageRecipient to receive Client Server messages. <br>
	 * <br>
	 * This setting should be used on the server side.<br><br>
	 * @param messageRecipient
	 *            the MessageRecipient to be used
	 */
	public void setMessageRecipient(MessageRecipient messageRecipient);

	/**
	 * returns the MessageSender for this Configuration context.
	 * This setting should be used on the client side.
	 * @return MessageSender
	 */
	public MessageSender getMessageSender();

	/**
	 * configures the time a client waits for a message response 
	 * from the server. <br>
	 * <br>
	 * Default value: 600000ms (10 minutes)<br>
	 * <br>
     * It is recommended to use the same values for {@link #timeoutClientSocket(int)}
     * and {@link #timeoutServerSocket(int)}.
     * <br>
	 * This setting can be used on both client and server.<br><br> 
	 * @param milliseconds
	 *            time in milliseconds
	 */
	public void timeoutClientSocket(int milliseconds);

	/**
	 * configures the timeout of the serverside socket. <br>
	 * <br>
	 * The serverside handler waits for messages to arrive from the client.
	 * If no more messages arrive for the duration configured in this
	 * setting, the client will be disconnected.
	 * <br>  
	 * Clients send PING messages to the server at an interval of
	 * Math.min(timeoutClientSocket(), timeoutServerSocket()) / 2 
	 * and the server will respond to keep connections alive.
	 * <br> 
	 * Decrease this setting if you want clients to disconnect faster.
     * <br>
     * Increase this setting if you have a large number of clients and long
     * running queries and you are getting disconnected clients that you 
     * would like to wait even longer for a response from the server. 
     * <br>
	 * Default value: 600000ms (10 minutes)<br>
	 * <br>
	 * It is recommended to use the same values for {@link #timeoutClientSocket(int)}
	 * and {@link #timeoutServerSocket(int)}.
	 * <br>
	 * This setting can be used on both client and server.<br><br>
	 * @param milliseconds
	 *            time in milliseconds
	 */
	public void timeoutServerSocket(int milliseconds);

	
	/**
     * configures the client messaging system to be single threaded 
     * or multithreaded.
     * <br><br>Recommended settings:<br>
     * - <code>true</code> for low resource systems.<br>
     * - <code>false</code> for best asynchronous performance and fast
     * GUI response.
     * <br><br>Default value:<br>
     * - .NET Compactframework: <code>true</code><br>
     * - all other platforms: <code>false</code><br><br>
     * This setting can be used on both client and server.<br><br>
     * @param flag the desired setting
     */
    public void singleThreadedClient(boolean flag);


	/**
	 * Configures to batch messages between client and server. By default, batch
	 * mode is enabled.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param flag
	 *            false, to turn message batching off.
	 */
	public void batchMessages(boolean flag);
	
	/**
	 * Configures the maximum memory buffer size for batched message. If the
	 * size of batched messages is greater than <code>maxSize</code>, batched
	 * messages will be sent to server.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param maxSize
	 */
	public void maxBatchQueueSize(int maxSize);

}
