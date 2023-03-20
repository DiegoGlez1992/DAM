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

import com.db4o.config.*;

/**
 * Configuration interface for db4o servers.
 * @since 7.5
 */
public interface ServerConfiguration extends FileConfigurationProvider, NetworkingConfigurationProvider, CommonConfigurationProvider , CacheConfigurationProvider, IdSystemConfigurationProvider{

	/**
     * adds ConfigurationItems to be applied when
     * an ObjectServer is opened. 
     * @param configItem the {@link ServerConfigurationItem}
     * @since 7.12
     */
	public void addConfigurationItem(ServerConfigurationItem configItem);

	/**
	 * configures the timeout of the server side socket. <br>
	 * <br>
	 * The server side handler waits for messages to arrive from the client.
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
	 *            
	 * @sharpen.property
	 */
	public void timeoutServerSocket(int milliseconds);
	

}
