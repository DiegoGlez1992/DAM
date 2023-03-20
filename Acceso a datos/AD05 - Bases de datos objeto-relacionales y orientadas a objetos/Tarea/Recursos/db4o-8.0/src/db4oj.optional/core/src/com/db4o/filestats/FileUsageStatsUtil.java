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

/**
* @exclude
*/
@decaf.Ignore(decaf.Platform.JDK11)
public final class FileUsageStatsUtil {

	private final static String PADDING = "                    ";
	
	private FileUsageStatsUtil() {
	}

	public static String formatLine(String label, long amount) {
		return padLeft(label, 20) + ": " + padLeft(String.valueOf(amount), 12) + "\n";
	}

	private static String padLeft(String val, int len) {
		return PADDING.substring(0, len - val.length()) + val;
	}
}
