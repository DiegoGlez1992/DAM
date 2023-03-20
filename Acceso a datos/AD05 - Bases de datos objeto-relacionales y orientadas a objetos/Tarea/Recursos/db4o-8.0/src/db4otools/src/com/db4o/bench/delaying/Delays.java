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
package com.db4o.bench.delaying;


public class Delays {

	public static final int READ = 0;
	public static final int WRITE = 1;
	public static final int SEEK = 2;
	public static final int SYNC = 3;
	
	public static final int COUNT = 4;

	public static final String units = "nanoseconds";

	public long[] values;

	
	public Delays(long read, long write, long seek, long sync) {
		values = new long[] {read, write, seek, sync};
	}
	
	public String toString() {
		return "[delays in " + units + "] read: " + values[READ] + " | write: " + values[WRITE] +
				" | seek: " + values[SEEK] + " | sync: " + values[SYNC];
	}
	
}
