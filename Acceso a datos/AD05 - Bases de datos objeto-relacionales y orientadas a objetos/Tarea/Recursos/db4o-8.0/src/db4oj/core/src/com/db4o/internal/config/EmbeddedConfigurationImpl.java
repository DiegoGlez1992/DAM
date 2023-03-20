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
package com.db4o.internal.config;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;

public class EmbeddedConfigurationImpl implements EmbeddedConfiguration, LegacyConfigurationProvider {

	private final Config4Impl _legacy;
	private List<EmbeddedConfigurationItem> _configItems;

	public EmbeddedConfigurationImpl(Configuration legacy) {
		_legacy = (Config4Impl) legacy;
    }

	public CacheConfiguration cache() {
		return new CacheConfigurationImpl(_legacy);
	}
	
	public FileConfiguration file() {
		return new FileConfigurationImpl(_legacy);
	}

	public CommonConfiguration common() {
		return Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy());
	}

	public Config4Impl legacy() {
		return _legacy;
	}

	public void addConfigurationItem(EmbeddedConfigurationItem configItem) {
		if(_configItems != null && _configItems.contains(configItem)) {
			return;
		}
		configItem.prepare(this);
		if(_configItems == null) {
			_configItems = new ArrayList<EmbeddedConfigurationItem>();
		}
		_configItems.add(configItem);
	}

	public void applyConfigurationItems(EmbeddedObjectContainer container) {
		if(_configItems == null) {
			return;
		}
		for (EmbeddedConfigurationItem configItem : _configItems) {
			configItem.apply(container);
		}
	}

	public IdSystemConfiguration idSystem() {
		return new IdSystemConfigurationImpl(_legacy);
	}

}
