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

import java.io.PrintWriter;

/**
 * @sharpen.ignore
 */
public class TestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Throwable _reason;
	
	public TestException(String message, Throwable reason) {
		super(message);
		_reason = reason;
	}	

	public TestException(Throwable reason) {
		_reason = reason;
	}
	
	public final Throwable getReason() {
		return _reason;
	}
	
	public void printStackTrace(PrintWriter s) {
		if (null != _reason) {
			super.printStackTrace(s);
			s.write(" caused by ");
			_reason.printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
		
	}
	
	public String toString() {
		String s = super.toString();
		if (null != _reason) {
			s = s + " caused by " + _reason.toString();
		}
		
		return s;
	}
}
