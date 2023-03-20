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
package com.db4o.osgi;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;


/**
 * This API is registered as an OSGi service by the db4o_osgi bundle. It can be accessed like this:
 * <br><br>
 * ServiceReference serviceRef = _context.getServiceReference(Db4oService.class.getName());
 * <code>Db4oService db4oService = (Db4oService)bundleContext.getService(serviceRef);<br>
 * Configuration config = db4oService.newConfiguration();<br>
 * // ...<br>
 * ObjectContainer database = db4oService.openFile(config,fileName);</code>
 * <br><br>
 * Subsequently, the database reference can be handled like any other db4o instance.
 * <br><br>
 * The main purpose of this service is to configure an OSGi bundle aware reflector for
 * the database instance, so classes that are owned by the client bundle are accessible
 * to the db4o engine. To emulate this behavior when using db4o directly through the
 * exported packages of the db4o_osgi plugin, db4o can be configured like this:
 * <br><br>
 * <code>Configuration config = Db4o.newConfiguration();<br>
 * config.reflectWith(new JdkReflector(SomeData.class.getClassLoader()));<br>
 * // ...<br>
 * ObjectContainer database = Db4o.openFile(config,fileName);</code>
 * <br><br>
 * Access through the service is recommended over direct usage, though, as the service
 * may implement further OSGi specific features in the future.
 * 
 * @see Db4o
 * @see com.db4o.reflect.Reflector
 * @see "org.osgi.framework.BundleContext"
 */

public interface Db4oService {

	/**
	 * Creates a fresh {@link Configuration Configuration} instance.
	 * 
	 * @return a fresh, independent configuration with all options set to their default values
	 */
	Configuration newConfiguration();

	/**
	 * Operates just like {@link Db4o#openClient(Configuration, String, int, String, String)}, but uses
	 * a newly created, vanilla {@link Configuration Configuration} context.
	 * 
	 * opens an {@link ObjectContainer ObjectContainer}
	 * client and connects it to the specified named server and port.
	 * <br><br>
	 * The server needs to
	 * {@link ObjectServer#grantAccess allow access} for the specified user and password.
	 * <br><br>
	 * A client {@link ObjectContainer ObjectContainer} can be cast to 
	 * {@link ExtClient ExtClient} to use extended
	 * {@link ExtObjectContainer ExtObjectContainer} 
	 * and {@link ExtClient ExtClient} methods.
	 * <br><br>
	 * @param hostName the host name
	 * @param port the port the server is using
	 * @param user the user name
	 * @param password the user password
	 * @return an open {@link ObjectContainer ObjectContainer}
	 * @see ObjectServer#grantAccess
	 * @throws Db4oException
	 */

	ObjectContainer openClient(String hostName, int port, String user, String password) throws Db4oException;

	/**
	 * opens an {@link ObjectContainer ObjectContainer}
	 * client and connects it to the specified named server and port.
	 * <br><br>
	 * The server needs to
	 * {@link ObjectServer#grantAccess allow access} for the specified user and password.
	 * <br><br>
	 * A client {@link ObjectContainer ObjectContainer} can be cast to 
	 * {@link ExtClient ExtClient} to use extended
	 * {@link ExtObjectContainer ExtObjectContainer} 
	 * and {@link ExtClient ExtClient} methods.
	 * <br><br>
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
	 * @param hostName the host name
	 * @param port the port the server is using
	 * @param user the user name
	 * @param password the user password
	 * @return an open {@link ObjectContainer ObjectContainer}
	 * @see ObjectServer#grantAccess
	 * @throws Db4oException
	 */
	ObjectContainer openClient(Configuration config, String hostName, int port, String user, String password) throws Db4oException;

	/**
	 * Operates just like {@link Db4o#openFile(Configuration, String)}, but uses
	 * a newly created, vanilla {@link Configuration Configuration} context.
	 * 
	 * opens an {@link ObjectContainer ObjectContainer}
	 * on the specified database file for local use.
	 * <br><br>A database file can only be opened once, subsequent attempts to open
	 * another {@link ObjectContainer ObjectContainer} against the same file will result in
	 * a {@link DatabaseFileLockedException DatabaseFileLockedException}.<br><br>
	 * Database files can only be accessed for readwrite access from one process 
	 * (one Java VM) at one time. All versions except for db4o mobile edition use an
	 * internal mechanism to lock the database file for other processes. 
	 * <br><br>
	 * @param databaseFileName an absolute or relative path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
	 * @see Configuration#readOnly
	 * @see Configuration#encrypt
	 * @see Configuration#password
	 * @throws Db4oException
	 */
	ObjectContainer openFile(String databaseFileName) throws Db4oException;

	/**
	 * opens an {@link ObjectContainer ObjectContainer}
	 * on the specified database file for local use.
	 * <br><br>A database file can only be opened once, subsequent attempts to open
	 * another {@link ObjectContainer ObjectContainer} against the same file will result in
	 * a {@link DatabaseFileLockedException DatabaseFileLockedException}.<br><br>
	 * Database files can only be accessed for readwrite access from one process 
	 * (one Java VM) at one time. All versions except for db4o mobile edition use an
	 * internal mechanism to lock the database file for other processes. 
	 * <br><br>
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
	 * @param databaseFileName an absolute or relative path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
	 * @see Configuration#readOnly
	 * @see Configuration#encrypt
	 * @see Configuration#password
	 * @throws Db4oException
	 */
	ObjectContainer openFile(Configuration config, String databaseFileName) throws Db4oException;

	/**
	 * Operates just like {@link Db4o#openServer(Configuration, String, int)}, but uses
	 * a newly created, vanilla {@link Configuration Configuration} context.
	 * 
	 * opens an {@link ObjectServer ObjectServer}
	 * on the specified database file and port.
	 * <br><br>
	 * If the server does not need to listen on a port because it will only be used
	 * in embedded mode with {@link ObjectServer#openClient}, specify '0' as the
	 * port number.
	 * @param databaseFileName an absolute or relative path to the database file
	 * @param port the port to be used, or 0, if the server should not open a port,
	 * because it will only be used with {@link ObjectServer#openClient()}
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
	 * @see Configuration#readOnly
	 * @see Configuration#encrypt
	 * @see Configuration#password
	 * @throws Db4oException
	 */
	ObjectServer openServer(String databaseFileName, int port) throws Db4oException;

	/**
	 * opens an {@link ObjectServer ObjectServer}
	 * on the specified database file and port.
	 * <br><br>
	 * If the server does not need to listen on a port because it will only be used
	 * in embedded mode with {@link ObjectServer#openClient}, specify '0' as the
	 * port number.
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
	 * @param databaseFileName an absolute or relative path to the database file
	 * @param port the port to be used, or 0, if the server should not open a port,
	 * because it will only be used with {@link ObjectServer#openClient()}
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
	 * @see Configuration#readOnly
	 * @see Configuration#encrypt
	 * @see Configuration#password
	 * @throws Db4oException
	 */
	ObjectServer openServer(Configuration config, String databaseFileName, int port) throws Db4oException;
}
