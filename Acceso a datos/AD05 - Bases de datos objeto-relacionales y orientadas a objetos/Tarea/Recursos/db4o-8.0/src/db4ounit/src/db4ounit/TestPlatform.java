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

import java.io.*;
import java.lang.reflect.*;

/**
 * @sharpen.ignore
 */
public class TestPlatform {
	
	public static String NEW_LINE = System.getProperty("line.separator");
	
	public static Throwable getExceptionCause(Throwable e) {
		try {
			Method method = e.getClass().getMethod("getCause", new Class[0]);
			return (Throwable) method.invoke(e, new Object[0]);
		} catch (Exception exc) {
			return null;
		}
	}
	
	public static void printStackTrace(Writer writer, Throwable t) {
		java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);
		t.printStackTrace(printWriter);
		printWriter.flush();
	}
	
    public static Writer getStdErr() {
        return new PrintWriter(System.err);
    }
    
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}

	public static boolean hasParameters(Method method) {
		return method.getParameterTypes().length > 0;
	}

	public static void emitWarning(String warning) {
		System.err.println(warning);
	}

	public static Writer openTextFile(String fname) throws IOException {
		return new java.io.FileWriter(fname);
	}
	
}
