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

public class CodeGenerator {
	
	/**
	 * Generates an array that can be used with {@link MethodCallRecorder#verify(MethodCall[])}.
	 * 
	 * Example:
	 * MethodCallRecorder recorder = new MethodCallRecorder();
	 * runTest(recorder);
	 * System.out.println(CodeGenerator.generateMethodCallArray(recorder))
	 * 
	 * @param calls MethodCall generator
	 * @return array string
	 */
	public static String generateMethodCallArray(Iterable4 calls) {
		final Iterable4 callStrings = Iterators.map(calls, new Function4() {
			public Object apply(Object arg) {
				return generateMethodCall((MethodCall)arg);
			}
		});
		return Iterators.join(callStrings.iterator(), "," + TestPlatform.NEW_LINE);
	}
	
	public static String generateValue(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			return "\"" + value + "\"";
		}
		if (value instanceof Object[]) {
			return generateArray((Object[])value);
		}
		return value.toString();
	}
	
	public static String generateArray(Object[] array) {
		final Iterator4 values = Iterators.map(Iterators.iterate(array), new Function4() {
			public Object apply(Object arg) {
				return generateValue(arg);
			}
		});
		return "new Object[] " + Iterators.join(values, "{", "}", ", ");
	}

	public static String generateMethodCall(MethodCall call) {
		return "new MethodCall(\"" + call.methodName + "\", " + generateArray(call.args) + ")";
	}
}
