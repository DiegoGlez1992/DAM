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
 * LineNumberTable is an attribute of a code attribute. A LineNumberTable stores
 * information that relates indices into the code array (instructions) to the
 * lines of code in the source file from which they were compiled. This optional
 * attribute is used with debuggers (<i>duh</i>) and consists of an array of
 * <tt>reflect.LineNumberDebugInfo</tt>.
 * 
 * @see Code
 * @see EDU.purdue.cs.bloat.reflect.LineNumberDebugInfo
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LineNumberTable extends Attribute {
	private LineNumberDebugInfo[] lineNumbers;

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
	public LineNumberTable(final DataInputStream in, final int nameIndex,
			final int length) throws IOException {
		super(nameIndex, length);

		final int numLines = in.readUnsignedShort();

		lineNumbers = new LineNumberDebugInfo[numLines];

		for (int i = 0; i < lineNumbers.length; i++) {
			final int startPC = in.readUnsignedShort();
			final int lineNumber = in.readUnsignedShort();
			lineNumbers[i] = new LineNumberDebugInfo(startPC, lineNumber);
		}
	}

	/**
	 * Get the line number debug info for the code.
	 * 
	 * @return The line number debug info for the code.
	 */
	public LineNumberDebugInfo[] lineNumbers() {
		return lineNumbers;
	}

	/**
	 * Set the line number debug info for the code.
	 * 
	 * @param lineNumbers
	 *            The line number debug info for the code.
	 */
	public void setLineNumbers(final LineNumberDebugInfo[] lineNumbers) {
		this.lineNumbers = lineNumbers;
	}

	/**
	 * Get the length of the attribute.
	 * 
	 * @return The length of the attribute.
	 */
	public int length() {
		return 2 + lineNumbers.length * 4;
	}

	public String toString() {
		String x = "(lines";

		for (int i = 0; i < lineNumbers.length; i++) {
			x += "\n          (line #" + lineNumbers[i].lineNumber() + " pc="
					+ lineNumbers[i].startPC() + ")";
		}

		return x + ")";
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
		out.writeShort(lineNumbers.length);

		for (int i = 0; i < lineNumbers.length; i++) {
			out.writeShort(lineNumbers[i].startPC());
			out.writeShort(lineNumbers[i].lineNumber());
		}
	}

	/**
	 * Private constructor used in cloning.
	 */
	private LineNumberTable(final LineNumberTable other) {
		super(other.nameIndex, other.length);

		this.lineNumbers = new LineNumberDebugInfo[other.lineNumbers.length];
		for (int i = 0; i < other.lineNumbers.length; i++) {
			this.lineNumbers[i] = (LineNumberDebugInfo) other.lineNumbers[i]
					.clone();
		}
	}

	public Object clone() {
		return (new LineNumberTable(this));
	}
}
