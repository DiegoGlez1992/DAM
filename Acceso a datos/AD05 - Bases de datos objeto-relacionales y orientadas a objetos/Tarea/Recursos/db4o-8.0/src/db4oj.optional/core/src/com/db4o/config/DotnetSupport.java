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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

/**
 * Adds the basic configuration settings required to access a
 * .net generated database from java.
 * 
 * The configuration only makes sure that database files can be
 * successfully open and things like UUIDs can be successfully
 * retrieved.
 * @deprecated Since 8.0
 * @sharpen.ignore
 */
public class DotnetSupport implements ConfigurationItem {

	private final boolean _addCSSupport;
	
	/**
	 * @deprecated Use the constructor with the boolean parameter to specify if 
	 * client/server support is desired.
	 */
	public DotnetSupport() {
		_addCSSupport = false;	
	}
	
	/**
	 * @param addCSSupport true if mappings required for Client/Server 
	 *                     support should be included also.
	 *                     
	 * @deprecated Since 8.0
	 */
	public DotnetSupport(boolean addCSSupport) {
		_addCSSupport = addCSSupport;
	}

	public void prepare(Configuration config) {
		config.addAlias(new WildcardAlias("Db4objects.Db4o.Ext.*, Db4objects.Db4o", "com.db4o.ext.*"));		
		config.addAlias(new TypeAlias("Db4objects.Db4o.StaticField, Db4objects.Db4o", StaticField.class.getName()));
		config.addAlias(new TypeAlias("Db4objects.Db4o.StaticClass, Db4objects.Db4o", StaticClass.class.getName()));
		
		if (_addCSSupport) {
			ConfigurationItem dotNetCS;
			try {
				dotNetCS = (ConfigurationItem) Class.forName("com.db4o.cs.internal.config.DotNetSupportClientServer").newInstance();
			} catch (Exception e) {
				throw new Db4oException(e);
			} 
			dotNetCS.prepare(config);
		}
	}
	
	public void apply(InternalObjectContainer container) {
		NetTypeHandler[] handlers = Platform4.jdk().netTypes(container.reflector());
		for (int netTypeIdx = 0; netTypeIdx < handlers.length; netTypeIdx++) {
			NetTypeHandler handler = handlers[netTypeIdx];
			container.handlers().registerNetTypeHandler(handler);
		}
	}
}
