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
package db4ounit.mocking;

import com.db4o.foundation.*;

import db4ounit.*;

public class MethodCall {
	
	public static final Object IGNORED_ARGUMENT = new Object() {
		public String toString() {
			return "...";
		}
	};
	
	public static interface ArgumentCondition {
		void verify(Object argument);
	}
	
	public static class Conditions {
		public static ArgumentCondition isA(final Class<?> expectedClass) {
			return new ArgumentCondition() {
				public void verify(Object argument) {
					Assert.isInstanceOf(expectedClass, argument);
                }
			};
		}
	}
	
	public final String methodName;
	public final Object[] args;
	
	public MethodCall(String methodName, Object... args) {
		this.methodName = methodName;
		this.args = args;
	}

	public String toString() {
		return methodName + "(" + Iterators.join(Iterators.iterate(args), ", ") + ")";
	}
	
	public boolean equals(Object obj) {
		if (null == obj) return false;
		if (getClass() != obj.getClass()) return false;
		MethodCall other = (MethodCall)obj;
		if (!methodName.equals(other.methodName)) return false;
		if (args.length != other.args.length) return false;
		for (int i=0; i<args.length; ++i) {
			final Object expectedArg = args[i];
			if (expectedArg == IGNORED_ARGUMENT) {
				continue;
			}
			final Object actualArg = other.args[i];
			if (expectedArg instanceof ArgumentCondition) {
				((ArgumentCondition)expectedArg).verify(actualArg);
				continue;
			}
			
			if (!Check.objectsAreEqual(expectedArg, actualArg)) {
				return false;
			}
		}
		return true;
	}
}
