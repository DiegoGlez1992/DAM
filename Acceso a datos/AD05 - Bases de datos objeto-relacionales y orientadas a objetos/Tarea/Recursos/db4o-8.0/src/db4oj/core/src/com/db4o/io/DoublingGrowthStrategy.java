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
package com.db4o.io;

/**
 * Strategy for file/byte array growth that will always double the current size
 */
public class DoublingGrowthStrategy implements GrowthStrategy {
	public long newSize(long curSize, long requiredSize) {
		if(curSize == 0) {
			return requiredSize;
		}
		long newSize = curSize;
		while(newSize < requiredSize) {
			newSize *= 2;
		}
		return newSize;
	}
}
