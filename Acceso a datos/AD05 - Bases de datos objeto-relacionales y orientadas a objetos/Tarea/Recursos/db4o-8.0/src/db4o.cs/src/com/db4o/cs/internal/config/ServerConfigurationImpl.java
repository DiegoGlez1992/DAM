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
package com.db4o.cs.internal.config;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;

public class ServerConfigurationImpl extends NetworkingConfigurationProviderImpl implements ServerConfiguration {

	private List<ServerConfigurationItem> _configItems;
	
	public ServerConfigurationImpl(Config4Impl config) {
		super(config);
	}
	
	public CacheConfiguration cache() {
		return new CacheConfigurationImpl(legacy());
	}

	public FileConfiguration file() {
		return Db4oLegacyConfigurationBridge.asFileConfiguration(legacy());
	}

	public CommonConfiguration common() {
		return Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy());
	}

	public void timeoutServerSocket(int milliseconds) {
		legacy().timeoutServerSocket(milliseconds);
	}

	/**
	 * @sharpen.property
	 */
	public int timeoutServerSocket() {
		return legacy().timeoutServerSocket();
	}

	public void addConfigurationItem(ServerConfigurationItem configItem) {
		if(_configItems != null && _configItems.contains(configItem)) {
			return;
		}
		configItem.prepare(this);
		if(_configItems == null) {
			_configItems = new ArrayList<ServerConfigurationItem>();
		}
		_configItems.add(configItem);
	}

	public void applyConfigurationItems(ObjectServer server) {
		if(_configItems == null) {
			return;
		}
		for (ServerConfigurationItem configItem : _configItems) {
			configItem.apply(server);
		}
	}

	public IdSystemConfiguration idSystem() {
		return new IdSystemConfigurationImpl(legacy());
	}
}
