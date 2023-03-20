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
package EDU.purdue.cs.bloat.file;

import java.io.*;

import EDU.purdue.cs.bloat.reflect.*;

/**
 * Exceptions describes the types of exceptions that a method may throw. The
 * Exceptions attribute stores a list of indices into the constant pool of the
 * typs of exceptions thrown by the method.
 * 
 * @see Method
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Exceptions extends Attribute {
	private int[] exceptions;

	private ClassInfo classInfo;

	/**
	 * Constructor for create an <code>Exceptions</code> from scratch.
	 * 
	 * @param nameIndex
	 *            The index of the UTF8 string "Exceptions" in the class's
	 *            constant pool
	 * @param exceptions
	 *            A non-<code>null</code> array of indices into the constant
	 *            pool for the types of the exceptions
	 */
	Exceptions(final ClassInfo info, final int nameIndex, final int[] exceptions) {
		super(nameIndex, (2 * exceptions.length) + 2);
		this.classInfo = info;
		this.exceptions = exceptions;
	}

	/**
	 * Constructor. Create an Exceptions attribute from a data stream.
	 * 
	 * @param in
	 *            The data stream of the class file.
	 * @param nameIndex
	 *            The index into the constant pool of the name of the attribute.
	 * @param length
	 *            The length of the attribute, excluding the header.
	 * @exception IOException
	 *                If an error occurs while reading.
	 */
	public Exceptions(final ClassInfo classInfo, final DataInputStream in,
			final int nameIndex, final int length) throws IOException {
		super(nameIndex, length);

		this.classInfo = classInfo;

		final int count = in.readUnsignedShort();

		exceptions = new int[count];

		for (int i = 0; i < count; i++) {
			exceptions[i] = in.readUnsignedShort();
		}
	}

	/**
	 * Write the attribute to a data stream.
	 * 
	 * @param out
	 *            The data stream of the class file.
	 * @exception IOException
	 *                If an error occurs while writing.
	 */
	public void writeData(final DataOutputStream out) throws IOException {
		out.writeShort(exceptions.length);

		for (int i = 0; i < exceptions.length; i++) {
			out.writeShort(exceptions[i]);
		}
	}

	/**
	 * Get the indices into the constant pool of the types of the exceptions
	 * thrown by this method.
	 * 
	 * @return The indices of the types of the exceptions thrown.
	 */
	public int[] exceptionTypes() {
		return exceptions;
	}

	/**
	 * Get the length of the attribute.
	 */
	public int length() {
		return 2 + exceptions.length * 2;
	}

	/**
	 * Private constructor used for cloning.
	 */
	private Exceptions(final Exceptions other) {
		super(other.nameIndex, other.length);

		this.exceptions = new int[other.exceptions.length];
		System.arraycopy(other.exceptions, 0, this.exceptions, 0,
				other.exceptions.length);
		this.classInfo = other.classInfo;
	}

	public Object clone() {
		return (new Exceptions(this));
	}

	/**
	 * Returns a string representation of the attribute.
	 */
	public String toString() {
		return "(exceptions)";
	}
}
