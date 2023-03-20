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
package EDU.purdue.cs.bloat.util;

import java.util.*;

/**
 * IdentityComparator compares two objects using the result of
 * System.identityHashCode.
 */
public class IdentityComparator implements Comparator {
	public int compare(final Object o1, final Object o2) {
		final int n1 = System.identityHashCode(o1);
		final int n2 = System.identityHashCode(o2);

		return n1 - n2;
		/*
		 * if (n1 > n2) { return 1; }
		 * 
		 * if (n1 < n2) { return -1; }
		 * 
		 * return 0;
		 */
	}
}
