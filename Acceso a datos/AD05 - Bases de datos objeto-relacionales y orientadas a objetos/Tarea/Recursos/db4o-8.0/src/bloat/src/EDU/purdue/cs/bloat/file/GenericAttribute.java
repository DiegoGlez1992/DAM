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

/**
 * The Java Virtual Machine Specification allows implementors to invent their
 * own attributes. GenericAttribute models attributes whose name BLOAT does not
 * recognize.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class GenericAttribute extends Attribute {
	private byte[] data;

	/**
	 * Constructor. Create an attribute from a data stream.
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
	public GenericAttribute(final DataInputStream in, final int nameIndex,
			final int length) throws IOException {
		super(nameIndex, length);
		data = new byte[length];
		for (int read = 0; read < length;) {
			read += in.read(data, read, length - read);
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
		out.write(data, 0, data.length);
	}

	/**
	 * Private constructor used in cloning.
	 */
	private GenericAttribute(final GenericAttribute other) {
		super(other.nameIndex, other.length);

		this.data = new byte[other.data.length];
		System.arraycopy(other.data, 0, this.data, 0, other.data.length);
	}

	public Object clone() {
		return (new GenericAttribute(this));
	}
}
