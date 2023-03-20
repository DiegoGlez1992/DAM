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

import java.util.*;

import org.osgi.framework.*;

/**
 * db4o-osgi bundle BundleActivator implementation. 
 * Db4oActivator customizes the way the bundle is started
 * and stopped by the Framework.
 */
public class Db4oActivator implements BundleActivator {

	public final static String BUNDLE_ID = "db4o_osgi";
	
	/**
	 * This method is called when the bundle is started by the Framework. 
	 * The method registers Db4oService, making it available for clients.
	 * @param context The execution context of the bundle being started.
	 * @throws java.lang.Exception If this method throws an exception, this
	 *         bundle is marked as stopped and the Framework will remove this
	 *         bundle's listeners, unregister all services registered by this
	 *         bundle, and release all services used by this bundle. 
	 */
	public void start(BundleContext context) throws Exception {
		context.registerService(
				Db4oService.class.getName(),
				new Db4oServiceFactory(), 
				new Hashtable());		
	}
	
	/**
	 * This method is called when the bundle is stopped by the Framework.
	 * @param context The execution context of the bundle being stopped.
	 * @throws java.lang.Exception If this method throws an exception, the
	 *         bundle is still marked as stopped, and the Framework will remove
	 *         the bundle's listeners, unregister all services registered by the
	 *         bundle, and release all services used by the bundle.
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
