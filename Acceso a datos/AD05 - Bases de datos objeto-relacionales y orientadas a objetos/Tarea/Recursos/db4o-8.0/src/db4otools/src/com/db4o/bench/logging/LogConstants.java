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
package com.db4o.bench.logging;

import java.util.*;


public class LogConstants {

	public final static String READ_ENTRY = "READ ";
	public static final String WRITE_ENTRY = "WRITE ";
	public static final String SYNC_ENTRY = "SYNC ";
	public static final String SEEK_ENTRY = "SEEK ";
	
	public static final String[] ALL_CONSTANTS = {READ_ENTRY, WRITE_ENTRY, SYNC_ENTRY, SEEK_ENTRY};
	
	public static final String SEPARATOR = ",";
	
	public static Set allEntries() {
		HashSet entries = new HashSet();
		entries.addAll(Arrays.asList(ALL_CONSTANTS));
		return entries;
	}
}
