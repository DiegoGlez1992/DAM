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

import com.db4o.config.*;
import com.db4o.internal.*;

public class Db4oLegacyConfigurationBridge {

	public static EmbeddedConfiguration asEmbeddedConfiguration(Configuration legacy) {
		return new EmbeddedConfigurationImpl(legacy);
	}

	public static CommonConfiguration asCommonConfiguration(Configuration config) {
		return new CommonConfigurationImpl((Config4Impl) config);
	}

	public static Config4Impl asLegacy(final Object config) {
		return ((LegacyConfigurationProvider)config).legacy();
	}

	public static FileConfiguration asFileConfiguration(Configuration config) {
		return new FileConfigurationImpl((Config4Impl)config);
	}
	
	public static IdSystemConfiguration asIdSystemConfiguration(Configuration config){
		return new IdSystemConfigurationImpl((Config4Impl) config);
	}

}
