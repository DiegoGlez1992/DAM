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
package com.db4o.cs.config;

import com.db4o.cs.foundation.*;
import com.db4o.messaging.MessageRecipient;


/**
 * Configuration interface for networking configuration settings.<br><br>
 * The network settings should be configured in exactly the same on the server and client.
 * @since 7.5
 */
public interface NetworkingConfiguration {
	
	/**
	 * @sharpen.property
	 */
	public void clientServerFactory(ClientServerFactory factory);
	
	/**
	 * @sharpen.property
	 */
	ClientServerFactory clientServerFactory();
	
	/**
     * configures the client messaging system to be single threaded 
     * or multithreaded.
     * <br><br>Recommended settings:<br>
     * - <code>true</code> for low resource systems.<br>
     * - <code>false</code> for best asynchronous performance and fast
     * GUI response.
     * <br><br>Default value:<br>
     * - .NET Compact Framework: <code>true</code><br>
     * - all other platforms: <code>false</code><br><br>
     * This setting can be used on both client and server.<br><br>
     * @param flag the desired setting
     * 
     * @sharpen.property
     */
    public void singleThreadedClient(boolean flag);

	/**
	 * Configures to batch messages between client and server. By default, batch
	 * mode is enabled.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param flag
	 *            false, to turn message batching off.
	 *            
	 * @sharpen.property
	 */
	public void batchMessages(boolean flag);
	
	/**
	 * Configures the maximum memory buffer size for batched message. If the
	 * size of batched messages is greater than <code>maxSize</code>, batched
	 * messages will be sent to server.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param maxSize
	 * 
	 * @sharpen.property
	 */
	public void maxBatchQueueSize(int maxSize);
	

	/**
	 * sets the MessageRecipient to receive Client Server messages. <br>
	 * <br>
	 * This setting can be used on both client and server.<br><br>
	 * @param messageRecipient
	 *            the MessageRecipient to be used
	 *            
	 * @sharpen.property
	 */
	void messageRecipient(MessageRecipient messageRecipient);

	/**
	 * @since 7.11
	 * 
	 * @sharpen.property
	 */
	public Socket4Factory socketFactory();

	/**
	 * @since 7.11
	 * 
	 * @sharpen.property
	 */
	public void socketFactory(Socket4Factory socket4Factory);
}
