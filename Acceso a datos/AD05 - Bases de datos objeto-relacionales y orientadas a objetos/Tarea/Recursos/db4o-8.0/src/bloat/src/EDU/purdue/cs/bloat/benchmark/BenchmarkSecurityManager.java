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
package EDU.purdue.cs.bloat.benchmark;

import java.io.*;

/**
 * The <tt>BenchmarkSecurityManager</tt> allows us to execute a "main" method
 * multiple times without the virtual machine exiting. If exit is not allowed,
 * the <tt>checkExit</tt> method will throw a <tt>SecurityException</tt>
 * that can be caught, thus allowing execution to continue.
 * 
 * @see Shade
 * @see Stats
 */
public class BenchmarkSecurityManager extends SecurityManager {
	boolean allowExit = false;

	/**
	 * A <tt>SecurityException</tt> is thrown if we do not allow the virtual
	 * machine to exit.
	 */
	public void checkExit(final int status) {
		if (!allowExit) {
			System.err.println("exit " + status);
			throw new SecurityException("Tried to exit (status=" + status + ")");
		}
	}

	public void checkCreateClassLoader() {
	}

	public void checkAccess(final Thread t) {
	}

	public void checkAccess(final ThreadGroup g) {
	}

	public void checkExec(final String cmd) {
	}

	public void checkLink(final String lib) {
	}

	public void checkRead(final FileDescriptor fd) {
	}

	public void checkRead(final String file) {
	}

	public void checkRead(final String file, final Object context) {
	}

	public void checkWrite(final FileDescriptor fd) {
	}

	public void checkWrite(final String file) {
	}

	public void checkDelete(final String file) {
	}

	public void checkConnect(final String host, final int port) {
	}

	public void checkConnect(final String host, final int port,
			final Object context) {
	}

	public void checkListen(final int port) {
	}

	public void checkAccept(final String host, final int port) {
	}

	public void checkPropertiesAccess() {
	}

	public void checkPropertyAccess(final String key) {
	}

	public void checkPropertyAccess(final String key, final String val) {
	}

	public boolean checkTopLevelWindow(final Object window) {
		return true;
	}

	public void checkPackageAccess(final String pkg) {
	}

	public void checkPackageDefinition(final String pkg) {
	}

	public void checkSetFactory() {
	}
}
