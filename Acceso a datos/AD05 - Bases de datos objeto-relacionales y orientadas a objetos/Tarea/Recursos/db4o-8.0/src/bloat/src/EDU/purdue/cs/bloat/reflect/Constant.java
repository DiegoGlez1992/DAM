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

/**
 * A Constant is used to represent an item in the constant pool of a class.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public final class Constant {
	private int tag;

	private Object value;

	/**
	 * Constant tag for class types. This is used to reference other classes,
	 * such as the superclass, and is used by the checkcast and instanceof
	 * instructions. The Fieldref, Methodref and InterfaceMethodref constant
	 * types refer to this constant type.
	 */
	public static final byte CLASS = 7;

	/**
	 * Constant tag for field references. This is used to reference a field in
	 * (possibly) another class. The getfield, putfield, getstatic, and
	 * putstatic instructions use this constant type.
	 */
	public static final byte FIELD_REF = 9;

	/**
	 * Constant tag for method references. This is used to reference a method in
	 * (possibly) another class. The invokevirtual, invokespecial, and
	 * invokestatic instructions use this constant type.
	 */
	public static final byte METHOD_REF = 10;

	/**
	 * Constant tag for java.lang.String constants. The actual string value is
	 * stored indirectly in a Utf8 constant.
	 */
	public static final byte STRING = 8;

	/**
	 * Constant tag for int, short, byte, char, and boolean constants.
	 */
	public static final byte INTEGER = 3;

	/**
	 * Constant tag for float constants.
	 */
	public static final byte FLOAT = 4;

	/**
	 * Constant tag for long constants.
	 */
	public static final byte LONG = 5;

	/**
	 * Constant tag for double constants.
	 */
	public static final byte DOUBLE = 6;

	/**
	 * Constant tag for method references. This is used to reference a method in
	 * an interface. The invokeinterface instruction uses this constant type.
	 */
	public static final byte INTERFACE_METHOD_REF = 11;

	/**
	 * Constant tag for holding the name and type of a field or method. The
	 * Fieldref, Methodref and InterfaceMethodref constant types refer to this
	 * constant type.
	 */
	public static final byte NAME_AND_TYPE = 12;

	/**
	 * Constant tag for holding the a UTF8 format string. The string is used to
	 * hold the name and type descriptor for NameandType constants, the class
	 * name for Class constants, the string value for String constants.
	 */
	public static final byte UTF8 = 1;

	/**
	 * @param tag
	 *            The constant's tag.
	 * @param value
	 *            The constant's value.
	 */
	public Constant(final int tag, final Object value) {
		this.tag = tag;
		this.value = value;
	}

	/**
	 * Get the tag of the constant.
	 * 
	 * @return The tag.
	 */
	public final int tag() {
		return tag;
	}

	/**
	 * Get the value of the constant.
	 * 
	 * @return The value.
	 */
	public final Object value() {
		return value;
	}

	/**
	 * Hash the constant.
	 * 
	 * @return The hash code.
	 */
	public int hashCode() {
		switch (tag) {
		case CLASS:
		case STRING:
		case INTEGER:
		case FLOAT:
		case LONG:
		case DOUBLE:
		case UTF8:
			return tag ^ value.hashCode();
		case FIELD_REF:
		case METHOD_REF:
		case INTERFACE_METHOD_REF:
		case NAME_AND_TYPE:
			return tag ^ ((int[]) value)[0] ^ ((int[]) value)[1];
		}

		return tag;
	}

	/**
	 * Check if an object is equal to this constant.
	 * 
	 * @param other
	 *            The object to compare against.
	 * @return true if equal, false if not.
	 */
	public boolean equals(final Object other) {
		if (!(other instanceof Constant)) {
			return false;
		}

		final Constant c = (Constant) other;

		if (tag != c.tag) {
			return false;
		}

		switch (tag) {
		case CLASS:
		case STRING:
		case INTEGER:
		case FLOAT:
		case LONG:
		case DOUBLE:
		case UTF8:
			return value.equals(c.value);
		case FIELD_REF:
		case METHOD_REF:
		case INTERFACE_METHOD_REF:
		case NAME_AND_TYPE:
			return (((int[]) value)[0] == ((int[]) c.value)[0])
					&& (((int[]) value)[1] == ((int[]) c.value)[1]);
		}

		return false;
	}

	/**
	 * Convert the constant to a string.
	 * 
	 * @return A string representation of the constant.
	 */
	public String toString() {
		switch (tag) {
		case CLASS:
			return "Class " + value.toString();
		case STRING:
			return "String " + value.toString();
		case INTEGER:
			return "Integer " + value.toString();
		case FLOAT:
			return "Float " + value.toString();
		case LONG:
			return "Long " + value.toString();
		case DOUBLE:
			return "Double " + value.toString();
		case UTF8:
			final StringBuffer sb = new StringBuffer();
			final String s = (String) value;
			for (int i = 0; i < s.length(); i++) {
				final char c = s.charAt(i);
				if (Character.isWhitespace(c) || ((0x20 <= c) && (c <= 0x7e))) {
					sb.append(c);
				} else {
					sb.append("\\u");
					sb.append(Integer.toHexString(c));
				}
				if (sb.length() > 50) {
					sb.append("...");
					break;
				}
			}
			return "Utf8 '" + sb.toString() + "'";
		case FIELD_REF:
			return "Fieldref " + ((int[]) value)[0] + " " + ((int[]) value)[1];
		case METHOD_REF:
			return "Methodref " + ((int[]) value)[0] + " " + ((int[]) value)[1];
		case INTERFACE_METHOD_REF:
			return "InterfaceMethodref " + ((int[]) value)[0] + " "
					+ ((int[]) value)[1];
		case NAME_AND_TYPE:
			return "NameandType " + ((int[]) value)[0] + " "
					+ ((int[]) value)[1];
		}

		return "unknown constant";
	}
}
