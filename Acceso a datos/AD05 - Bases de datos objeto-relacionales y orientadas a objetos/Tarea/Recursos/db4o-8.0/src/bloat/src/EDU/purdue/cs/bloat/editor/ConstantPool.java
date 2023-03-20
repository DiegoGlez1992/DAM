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
package EDU.purdue.cs.bloat.editor;

import java.util.*;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * ConstantPool models constants in the constant pool. Recall that the
 * reflection mechanism represents constants as a tag and a value. ConstantPool
 * consists of an array of <tt>reflect.Constant</tt>s that are resolved into
 * their appropriate value (e.g. Type, NameAndType, MemberRef, etc.) as they are
 * needed.
 * 
 * @see EDU.purdue.cs.bloat.reflect.Constant
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class ConstantPool {
	/**
	 * ConstantPool maintains some information about the constants in the
	 * constant pool to make its life easier.
	 * 
	 * constantIndices A mapping between constants and their indices in the
	 * constant pool. Knowing this information makes adding constants to the
	 * constant pool easier.
	 * 
	 * constants A list of the reflect.Constant objects that are used to create
	 * the ConstantPool.
	 * 
	 * resolved A list of the constants in their resolved form (such as
	 * NameAndType or String)
	 */
	private Map constantIndices;

	ResizeableArrayList constants;

	ResizeableArrayList resolved;

	/**
	 * Constructor. Resolve the constants in the constant pool, converting from
	 * the index-based representation of the class file to a more amenable
	 * external representation.
	 * 
	 * @param c
	 *            An array of Constant.
	 */
	public ConstantPool(final Constant[] c) {
		constantIndices = new HashMap();
		constants = new ResizeableArrayList(c.length);
		resolved = new ResizeableArrayList(c.length);

		/*
		 * constants = new ResizeableArrayList(c.length, 256); resolved = new
		 * ResizeableArrayList(c.length, 256);
		 */
		for (int i = 0; i < c.length; i++) {
			constants.add(c[i]);
			resolved.add(null);
			if (c[i] != null) {
				constantIndices.put(c[i], new Integer(i));
			}
		}
	}

	/**
	 * Creates a new, empty <code>ConstantPool</code>
	 */
	public ConstantPool() {
		this.constantIndices = new HashMap();
		this.constants = new ResizeableArrayList();
		this.resolved = new ResizeableArrayList();
	}

	/**
	 * Obtain the resolved value of a constant at given index in the constant
	 * pool.
	 * 
	 * @param idx
	 *            Index into the constant pool.
	 * @return The value of the constant at index.
	 */
	public Object constantAt(final int idx) {
		if (idx == 0) {
			return null;
		}

		Object value = resolved.get(idx);

		if (value != null) {
			return value;
		}

		final Constant c = (Constant) constants.get(idx);

		if (c == null) {
			return null;
		}

		value = c.value();

		if (value == null) {
			return null;
		}

		// Okay, we have to resolve the Constant.

		switch (c.tag()) {
		case Constant.CLASS: {
			// Lookup the UTF8 at the index and use the class name
			// to create a Type.
			Assert.isTrue(value instanceof Integer, "Invalid constant: " + c);

			final int index = ((Integer) value).intValue();
			Assert.isTrue(constantTag(index) == Constant.UTF8,
					"Invalid constant: " + c);

			final String className = (String) constantAt(index);

			value = Type.getType(Type.classDescriptor(className));
			break;
		}
		case Constant.STRING: {
			// Lookup the UTF8 at the index and store the String directly.
			Assert.isTrue(value instanceof Integer, "Invalid constant: " + c);

			final int index = ((Integer) value).intValue();
			Assert.isTrue(constantTag(index) == Constant.UTF8,
					"Invalid constant: " + c);

			value = constantAt(index);
			break;
		}
		case Constant.FIELD_REF:
		case Constant.METHOD_REF:
		case Constant.INTERFACE_METHOD_REF: {
			// The constant at the first index should be a CLASS.
			//
			// The constant at the second should be a NAME_AND_TYPE.
			//
			// Resolve the two constants and then create a MemberRef
			// for this constant.
			//
			Assert.isTrue(value instanceof int[], "Invalid constant: " + c);

			final int[] v = (int[]) value;

			Assert.isTrue(constantTag(v[0]) == Constant.CLASS,
					"Invalid constant: " + c);
			Assert.isTrue(constantTag(v[1]) == Constant.NAME_AND_TYPE,
					"Invalid constant: " + c);

			final Type clazz = (Type) constantAt(v[0]);
			final NameAndType nameAndType = (NameAndType) constantAt(v[1]);

			value = new MemberRef(clazz, nameAndType);
			break;
		}
		case Constant.NAME_AND_TYPE: {
			// The constant at the first index should be a UTF8 with the
			// name of the field.
			//
			// The constant at the second should be a UTF8 with the type
			// of the field.
			//
			// Resolve the two constants as a String and a Type and then
			// create a NameAndType for this constant.
			//
			Assert.isTrue(value instanceof int[], "Invalid constant: " + c);

			final int[] v = (int[]) value;

			Assert.isTrue(constantTag(v[0]) == Constant.UTF8,
					"Invalid constant: " + c);
			Assert.isTrue(constantTag(v[1]) == Constant.UTF8,
					"Invalid constant: " + c);

			final String name = (String) constantAt(v[0]);
			final String type = (String) constantAt(v[1]);

			value = new NameAndType(name, Type.getType(type));
			break;
		}
		default:
			break;
		}

		resolved.ensureSize(idx + 1);
		resolved.set(idx, value);

		return value;
	}

	public int numConstants() {
		return constants.size();
	}

	/**
	 * Get the tag of a constant.
	 * 
	 * @param index
	 *            Index into the constant pool.
	 * @return The tag of the constant at index, or Constant.UTF8.
	 */
	public int constantTag(final int index) {
		if ((0 < index) && (index < constants.size())) {
			final Constant c = (Constant) constants.get(index);

			if (c != null) {
				return c.tag();
			}
		}

		return Constant.UTF8;
	}

	/**
	 * Get the index of the constant with the given tag and value.
	 * 
	 * @param tag
	 *            The constant's tag (for example, <tt>Constant.UTF8</tt>).
	 * @param value
	 *            The constant's value (for example, a <tt>String</tt>).
	 * @return The index of the constant.
	 */
	public int constantIndex(final int tag, final Object value) {
		return addConstant(tag, value);
	}

	/**
	 * Returns the index of the constant pool entry for the given class
	 */
	public int getClassIndex(final Class c) {
		return (addConstant(Constant.CLASS, Type.getType(c)));
	}

	/**
	 * Returns the index of the constant pool entry for the given integer
	 */
	public int getIntegerIndex(final Integer i) {
		return (addConstant(Constant.INTEGER, i));
	}

	/**
	 * Returns the index of the constant pool entry for the given float
	 */
	public int getFloatIndex(final Float f) {
		return (addConstant(Constant.FLOAT, f));
	}

	/**
	 * Returns the index of the constant pool entry for the given long
	 */
	public int getLongIndex(final Long l) {
		return (addConstant(Constant.LONG, l));
	}

	/**
	 * Returns the index of the constant pool entry for the given double
	 */
	public int getDoubleIndex(final Double d) {
		return (addConstant(Constant.DOUBLE, d));
	}

	/**
	 * Returns the index of the constant pool entry for the given class.
	 */
	public int getClassIndex(final Type type) {
		Assert
				.isTrue(type.isObject(), "Type " + type
						+ " is not an class type");
		// Make sure that the descriptor constant is also there
		getTypeIndex(type);
		return (addConstant(Constant.CLASS, type));
	}

	/**
	 * Returns the index of the constant pool entry for the given
	 * <code>Type</code>
	 */
	public int getTypeIndex(final Type type) {
		return (addConstant(Constant.UTF8, type.descriptor()));
	}

	/**
	 * Returns the index of the constant pool entry for the given String
	 */
	public int getStringIndex(final String s) {
		return (addConstant(Constant.STRING, s));
	}

	/**
	 * Returns the index of the constant pool entry for the given
	 * <code>MemberRef</code>
	 */
	public int getMemberRefIndex(final MemberRef ref) {
		return (addConstant(Constant.FIELD_REF, ref));
	}

	/**
	 * Returns the index of the constant pool entry for the given
	 * <code>NameAndType</code>
	 */
	public int getNameAndTypeIndex(final NameAndType nat) {
		return (addConstant(Constant.NAME_AND_TYPE, nat));
	}

	/**
	 * Returns the index of the constant pool entry for the given UTF8 string
	 */
	public int getUTF8Index(final String s) {
		return (addConstant(Constant.UTF8, s));
	}

	/**
	 * Add a constant to the constant pool. Will not add the same constant
	 * twice.
	 * 
	 * @param tag
	 *            The constant's tag (for example, <tt>Constant.UTF8</tt>).
	 * @param value
	 *            The constant's value (for example, a <tt>String</tt>).
	 * @return The index of the constant.
	 */
	public int addConstant(final int tag, final Object value) {
		if (value == null) {
			return 0;
		}

		Constant c;

		switch (tag) {
		case Constant.CLASS: {
			Assert.isTrue(value instanceof Type, "Invalid value: " + value);
			final int index = addConstant(Constant.UTF8, ((Type) value)
					.className());
			c = new Constant(Constant.CLASS, new Integer(index));
			break;
		}
		case Constant.STRING: {
			Assert.isTrue(value instanceof String, "Invalid value: " + value);
			final int index = addConstant(Constant.UTF8, value);
			c = new Constant(Constant.STRING, new Integer(index));
			break;
		}
		case Constant.FIELD_REF:
		case Constant.METHOD_REF:
		case Constant.INTERFACE_METHOD_REF: {
			// The constant at the first index should be a CLASS.
			//
			// The constant at the second should be a NAME_AND_TYPE.
			//
			// Resolve the two constants and then create a MemberRef
			// for this constant.
			//
			Assert
					.isTrue(value instanceof MemberRef, "Invalid value: "
							+ value);

			final int[] v = new int[2];

			v[0] = addConstant(Constant.CLASS, ((MemberRef) value)
					.declaringClass());
			v[1] = addConstant(Constant.NAME_AND_TYPE, ((MemberRef) value)
					.nameAndType());

			c = new Constant(tag, v);
			break;
		}
		case Constant.NAME_AND_TYPE: {
			// The constant at the first index should be a UTF8 with the
			// name of the field.
			//
			// The constant at the second should be a UTF8 with the type
			// of the field.
			//
			// Resolve the two constants as a String and a Type and then
			// create a NameAndType for this constant.
			//
			Assert.isTrue(value instanceof NameAndType, "Invalid value: "
					+ value);

			final int[] v = new int[2];

			v[0] = addConstant(Constant.UTF8, ((NameAndType) value).name());
			v[1] = addConstant(Constant.UTF8, ((NameAndType) value).type()
					.descriptor());

			c = new Constant(tag, v);
			break;
		}
		case Constant.UTF8: {
			final String s = (String) value;
			c = new Constant(tag, s.intern());
			break;
		}
		default: {
			c = new Constant(tag, value);
			break;
		}
		}

		Integer index = (Integer) constantIndices.get(c);

		if (index == null) {
			index = new Integer(constants.size());
			constantIndices.put(c, index);
			constants.add(c);
			resolved.add(value);

			if ((tag == Constant.LONG) || (tag == Constant.DOUBLE)) {
				constants.add(null);
				resolved.add(null);
			}
		}

		return index.intValue();
	}

	/**
	 * Get an array of the constants in the pool.
	 * 
	 * @return An array of the constants in the pool.
	 */
	public Constant[] constants() {
		final Object[] a = constants.toArray();
		final Constant[] array = new Constant[a.length];
		System.arraycopy(a, 0, array, 0, a.length);
		return array;
	}

	/**
	 * Returns an unmodifiable List of constants in this constant pool.
	 */
	public List getConstantsList() {
		return Collections.unmodifiableList(this.constants);
	}
}
