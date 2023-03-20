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
package db4ounit;


/**
 * @sharpen.partial
 */
public final class Assert {
	
	public static Throwable expect(Class exception, CodeBlock block) {
		Throwable e = getThrowable(block);
		assertThrowable(exception, e, null);
		return e;
	}

	public static Throwable expect(Class exception, Class cause, CodeBlock block) {
		return expect(exception, cause, block, null);
	}

	public static Throwable expect(Class exception, Class cause, CodeBlock block, String customMessage) {
		Throwable e = getThrowable(block);
		assertThrowable(exception, e, customMessage);
		assertThrowable(cause, TestPlatform.getExceptionCause(e), customMessage);
		return e;
    }
	
	private static void assertThrowable(Class exception, Throwable e, String customMessage) {
		if (exception.isInstance(e)) 
			return;
		
		String messagePrefix = customMessage != null
			? customMessage + ": "
			: "";
		
		String message = e == null
			? "Exception '" + exception.getName() + "' expected"
			: "Expecting '" + exception.getName() + "' but got '" + e.getClass().getName() + "'";
			
		fail(messagePrefix + message, e);
	}
	
	private static Throwable getThrowable(CodeBlock block) {
		try {
			block.run();
		} catch (Throwable e) {
			return e;
		}
		return null;
	}
	
	public static void fail() {
		fail("FAILURE");
	}

	public static void fail(String msg) {
		throw new AssertionException(msg);
	}
	
	public static void fail(String msg, Throwable cause) {
		throw new AssertionException(msg, cause);
	}
	
	public static void isTrue(boolean condition) {
		isTrue(condition,"FAILURE");
	}

	public static void isTrue(boolean condition, String msg) {
		if (condition) return;
		fail(msg);
	}
	
	public static void isNull(Object reference) {
		if (reference != null) {
			fail(failureMessage("null", reference));
		}
	}

	public static void isNull(Object reference,String message) {
		if (reference != null) {
			fail(message);
		}
	}

	public static void isNotNull(Object reference) {
		if (reference == null) {
			fail(failureMessage("not null", reference));
		}
	}

	public static void isNotNull(Object reference,String message) {
		if (reference == null) {
			fail(message);
		}
	}

	public static void areEqual(boolean expected, boolean actual) {
		if (expected == actual) return;
		fail(failureMessage(new Boolean(expected), new Boolean(actual)));
	}

	public static void areEqual(int expected, int actual) {
		areEqual(expected,actual,null);
	}
	
	public static void areEqual(int expected, int actual, String message) {
		if (expected == actual) return;
		fail(failureMessage(new Integer(expected), new Integer(actual),message));
	}
	
	public static void areEqual(double expected, double actual) {
		areEqual(expected, actual, null);
	}
	
	public static void areEqual(double expected, double actual, String message) {
		if (expected == actual) return;
		fail(failureMessage(new Double(expected), new Double(actual), message));
	}
	
	public static void areEqual(long expected, long actual) {
		if (expected == actual) return;
		fail(failureMessage(new Long(expected), new Long(actual)));
	}

	public static void areEqual(Object expected, Object actual,String message) {		
		if (Check.objectsAreEqual(expected, actual)) return;
		fail(failureMessage(expected, actual, message));
	}
	
	public static void areEqual(Object expected, Object actual) {		
		areEqual(expected,actual,null);
	}

	public static void areSame(Object expected, Object actual) {
		if (expected == actual) return;
		fail(failureMessage(expected, actual));
	}
	
	public static void areNotSame(Object unexpected, Object actual) {
		if (unexpected != actual) return;
		fail("Expecting not '" + unexpected + "'.");
	}
	
	private static String failureMessage(Object expected, Object actual) {
		return failureMessage(expected,actual,null);
	}

	private static String failureMessage(Object expected, Object actual, String customMessage) {
		return failureMessage(expected, actual, "", customMessage);
	}

	private static String failureMessage(Object expected, Object actual, final String cmpOper, String customMessage) {
		return (customMessage==null ? "" : customMessage+": ")+"Expected " + cmpOper + "'"+ expected + "' but was '" + actual + "'";
	}

	private static String failureMessage(long expected, long actual, final String cmpOper, String customMessage) {
		return (customMessage==null ? "" : customMessage+": ")+"Expected " + cmpOper + "'"+ expected + "' but was '" + actual + "'";
	}

	public static void isFalse(boolean condition) {
		isTrue(!condition);
	}
	
	public static void isFalse(boolean condition, String message) {
		isTrue(!condition, message);
	}

	public static void isInstanceOf(Class expectedClass, Object actual) {
		isTrue(expectedClass.isInstance(actual), failureMessage(expectedClass, actual == null ? null : actual.getClass()));
	}

	public static void isGreater(long expected, long actual) {
		if (actual > expected) return;
		fail(failureMessage(expected, actual, "greater than ", null));
	}			
	
	public static void isGreaterOrEqual(long expected, long actual) {
		if (actual >= expected) return;
		fail(expected, actual, "greater than or equal to ", null);
	}
	
    public static void isSmaller(long expected, long actual) {
        if (actual < expected) return;
        fail(failureMessage(new Long(expected), new Long(actual), "smaller than ", null));
    }

    public static void isSmallerOrEqual(long expected, long actual) {
        if (actual <= expected) return;
        fail(expected, actual, "smaller than or equal to ", null);
    }
    
	private static void fail(long expected, long actual, final String operator, String customMessage) {
		fail(failureMessage(new Long(expected), new Long(actual), operator, null));
	}

	public static void areNotEqual(long unexpected, long actual) {
		areNotEqual(unexpected, actual, null);
	}

	public static void areNotEqual(long unexpected, long actual, String customMessage) {
		if (actual != unexpected) return;
		fail(unexpected, actual, "not equal to ", customMessage);
	}

	public static void areNotEqual(Object unexpected, Object actual) {
		if (!Check.objectsAreEqual(unexpected, actual)) return;
		fail("Expecting not '" + unexpected + "'");
	}

    public static void equalsAndHashcode(Object obj, Object same, Object other) {
        areEqual(obj, obj);
        areEqual(obj, same);
        areNotEqual(obj, other);
        areEqual(obj.hashCode(), same.hashCode());
        areEqual(same, obj);
        areNotEqual(other, obj);
        areNotEqual(obj, null);
    }
}
