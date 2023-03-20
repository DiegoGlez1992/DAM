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

import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.messaging.*;

public class ClientConfigurationImpl extends NetworkingConfigurationProviderImpl implements ClientConfiguration {

	private List<ClientConfigurationItem> _configItems;
	
	public ClientConfigurationImpl(Config4Impl config) {
		super(config);
	}

	public MessageSender messageSender() {
		return legacy().getMessageSender();
	}

	public void prefetchIDCount(int prefetchIDCount) {
		legacy().prefetchIDCount(prefetchIDCount);
	}

	public void prefetchObjectCount(int prefetchObjectCount) {
		legacy().prefetchObjectCount(prefetchObjectCount);
	}

	public CommonConfiguration common() {
		return Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy());
	}

	public void prefetchDepth(int prefetchDepth) {
		legacy().prefetchDepth(prefetchDepth);
    }

	public void prefetchSlotCacheSize(int slotCacheSize) {
		legacy().prefetchSlotCacheSize(slotCacheSize); 
	}
	
	public void timeoutClientSocket(int milliseconds) {
		legacy().timeoutClientSocket(milliseconds);
	}

	/**
	 * @sharpen.property
	 */
	public int timeoutClientSocket() {
		return legacy().timeoutClientSocket();
	}

	public void addConfigurationItem(ClientConfigurationItem configItem) {
		if(_configItems != null && _configItems.contains(configItem)) {
			return;
		}
		configItem.prepare(this);
		if(_configItems == null) {
			_configItems = new ArrayList<ClientConfigurationItem>();
		}
		_configItems.add(configItem);
	}

	public void applyConfigurationItems(ExtClient client) {
		if(_configItems == null) {
			return;
		}
		for (ClientConfigurationItem configItem : _configItems) {
			configItem.apply(client);
		}
	}

}
