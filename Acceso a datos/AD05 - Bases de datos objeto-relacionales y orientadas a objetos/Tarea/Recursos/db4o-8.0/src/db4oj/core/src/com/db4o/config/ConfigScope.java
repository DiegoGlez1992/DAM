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

import java.io.*;

import com.db4o.foundation.*;

/**
 * Defines a scope of applicability of a config setting.<br><br>
 * Some of the configuration settings can be either: <br><br>
 * - enabled globally; <br>
 * - enabled individually for a specified class; <br>
 * - disabled.<br><br>
 * @see com.db4o.config.Configuration#generateUUIDs(ConfigScope)
 * @see com.db4o.config.Configuration#generateVersionNumbers(ConfigScope)
 */
public final class ConfigScope implements Serializable {

	public static final int DISABLED_ID = -1;
	public static final int INDIVIDUALLY_ID = 1;
	public static final int GLOBALLY_ID = Integer.MAX_VALUE;

	private static final String DISABLED_NAME="disabled";
	private static final String INDIVIDUALLY_NAME="individually";
	private static final String GLOBALLY_NAME="globally";
	
	/**
	 * Marks a configuration feature as globally disabled.
	 */
	public static final ConfigScope DISABLED = new ConfigScope(DISABLED_ID,DISABLED_NAME);

	/**
	 * Marks a configuration feature as individually configurable.
	 */
	public static final ConfigScope INDIVIDUALLY = new ConfigScope(INDIVIDUALLY_ID,INDIVIDUALLY_NAME);

	/**
	 * Marks a configuration feature as globally enabled.
	 */
	public static final ConfigScope GLOBALLY = new ConfigScope(GLOBALLY_ID,GLOBALLY_NAME);

	private final int _value;
	private final String _name;
	
	private ConfigScope(int value,String name) {
		_value=value;
		_name=name;
	}

	/**
	 * Checks if the current configuration scope is globally
	 * enabled or disabled. 
	 * @param defaultValue - default result 
	 * @return false if disabled, true if globally enabled, default 
	 * value otherwise
	 */
	public boolean applyConfig(TernaryBool defaultValue) {
		switch(_value) {
			case DISABLED_ID:
				return false;
			case GLOBALLY_ID: 
				return !defaultValue.definiteNo();
			default:
				return defaultValue.definiteYes();
		}
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		ConfigScope tb=(ConfigScope)obj;
		return _value==tb._value;
	}
	
	public int hashCode() {
		return _value;
	}
	
	private Object readResolve() {
		switch(_value) {
		case DISABLED_ID:
			return DISABLED;
		case INDIVIDUALLY_ID:
			return INDIVIDUALLY;
		default:
			return GLOBALLY;
		}
	}
	
	public String toString() {
		return _name;
	}
}
