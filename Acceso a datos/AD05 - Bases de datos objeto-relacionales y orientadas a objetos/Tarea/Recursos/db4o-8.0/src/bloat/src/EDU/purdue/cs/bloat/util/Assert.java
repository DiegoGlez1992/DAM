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

/**
 * Mechanism for making assertions about things in BLOAT. If an assertion fails,
 * an <tt>IllegalArgumentException</tt> is thrown.
 */
public abstract class Assert {
	public static void isTrue(boolean test, final String msg) {
		if (!test) {
			throw new IllegalArgumentException("Assert.isTrue: " + msg);
		}
	}

	public static void isFalse(final boolean test, final String msg) {
		if (test) {
			throw new IllegalArgumentException("Assert.isFalse: " + msg);
		}
	}

	public static void isNotNull(final Object test, final String msg) {
		if (test == null) {
			throw new IllegalArgumentException("Assert.isNotNull: " + msg);
		}
	}

	public static void isNull(final Object test, final String msg) {
		if (test != null) {
			throw new IllegalArgumentException("Assert.isNull: " + msg);
		}
	}

	public static void isTrue(boolean test) {
		if (!test) {
			throw new IllegalArgumentException("Assert.isTrue failed");
		}
	}

	public static void isFalse(final boolean test) {
		if (test) {
			throw new IllegalArgumentException("Assert.isFalse failed");
		}
	}

	public static void isNotNull(final Object test) {
		if (test == null) {
			throw new IllegalArgumentException("Assert.isNotNull failed");
		}
	}

	public static void isNull(final Object test) {
		if (test != null) {
			throw new IllegalArgumentException("Assert.isNull failed");
		}
	}
}
