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
package EDU.purdue.cs.bloat.reflect;

import java.io.*;

/**
 * ClassInfoLoader provides an interface for loading classes. Implementing
 * classes can load classes from a file, from the JVM, or elsewhere.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface ClassInfoLoader {
	/**
	 * Load a class.
	 * 
	 * @param name
	 *            The name of the class to load, including the package name.
	 * @return A ClassInfo for the class.
	 * @exception ClassNotFoundException
	 *                The class cannot be found in the class path.
	 * @see ClassInfo
	 */
	public ClassInfo loadClass(String name) throws ClassNotFoundException;

	/**
	 * Creates a new class or interface.
	 * 
	 * @param modifiers
	 *            The modifiers describing the newly-created class
	 * @param classIndex
	 *            The index of the name of the newly-created class in its
	 *            constant pool
	 * @param superClassIndex
	 *            The index of the name of the newly-created class's superclass
	 *            in its constant pool
	 * @param interfaceIndexes
	 *            The indexes of the names of the interfaces that the
	 *            newly-created class implements
	 * @param constants
	 *            The constant pool for the newly created class (a list of
	 *            {@link Constant}s).
	 */
	public ClassInfo newClass(int modifiers, int classIndex,
			int superClassIndex, int[] interfaceIndexes,
			java.util.List constants);

	/**
	 * Returns an <code>OutputStream</code> to which a class should be
	 * written.
	 */
	OutputStream outputStreamFor(ClassInfo info) throws IOException;
}
