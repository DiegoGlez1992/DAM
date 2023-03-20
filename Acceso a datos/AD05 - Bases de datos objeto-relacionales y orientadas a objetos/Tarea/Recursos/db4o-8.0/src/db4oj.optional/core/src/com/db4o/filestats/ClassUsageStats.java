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
package com.db4o.filestats;

import static com.db4o.filestats.FileUsageStatsUtil.*;

/**
 * Statistics for the byte usage for a single class (instances, indices, etc.) in a db4o database file.
 * 
 * @exclude
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ClassUsageStats {
	private final String _className;
	private final long _slotUsage;
	private final long _classIndexUsage;
	private final long _fieldIndexUsage;
	private final long _miscUsage;	
	
	ClassUsageStats(String className, long slotSpace, long classIndexUsage, long fieldIndexUsage, long miscUsage) {
		_className = className;
		_slotUsage = slotSpace;
		_classIndexUsage = classIndexUsage;
		_fieldIndexUsage = fieldIndexUsage;
		_miscUsage = miscUsage;
	}
	
	/**
	 * @return the name of the persistent class
	 */
	public String className() {
		return _className;
	}

	/**
	 * @return number of bytes used slots containing the actual class instances
	 */
	public long slotUsage() {
		return _slotUsage;
	}

	/**
	 * @return number of bytes used for the index of class instances
	 */
	public long classIndexUsage() {
		return _classIndexUsage;
	}

	/**
	 * @return number of bytes used for field indexes, if any
	 */
	public long fieldIndexUsage() {
		return _fieldIndexUsage;
	}

	/**
	 * @return number of bytes used for features that are specific to this class (ex.: the BTree encapsulated within a {@link com.db4o.internal.collections.BigSet} instance)
	 */
	public long miscUsage() {
		return _miscUsage;
	}

	/**
	 * @return aggregated byte usage for this persistent class
	 */
	public long totalUsage() {
		return _slotUsage + _classIndexUsage + _fieldIndexUsage + _miscUsage;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		toString(str);
		return str.toString();
	}
	
	void toString(StringBuffer str) {
		str.append(className()).append("\n");
		str.append(formatLine("Slots", slotUsage()));
		str.append(formatLine("Class index", classIndexUsage()));
		str.append(formatLine("Field indices", fieldIndexUsage()));
		if(miscUsage() > 0) {
			str.append(formatLine("Misc", miscUsage()));
		}
		str.append(formatLine("Total", totalUsage()));
	}
}