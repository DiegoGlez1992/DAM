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
package EDU.purdue.cs.bloat.trans;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

/**
 * <tt>CompactArrayInitializer</tt> optimizes the initialization of arrays by
 * transforming the initialization code into a loop that loads the array
 * elements from a string in the class's constant pool.
 */
public class CompactArrayInitializer implements Opcode {
	public static boolean DEBUG = false;

	private static final MemberRef GET_CHARS;

	// Various states that the analyzer can be in. The state indicates
	// what kind of instruction the analyzer expects to see next.
	private static final int EXPECT_SIZE = 0;

	private static final int EXPECT_NEW = 1;

	private static final int EXPECT_DUP = 2;

	private static final int EXPECT_INDEX_OR_SIZE = 3;

	private static final int EXPECT_VALUE_OR_SIZE_OR_NEW = 4;

	private static final int EXPECT_STORE_OR_NEW = 5;

	private static final int EXPECT_PUT_OR_DUP = 6;

	private static final int THRESHOLD = 16;

	private static final String[] STATES = { "EXPECT_SIZE", "EXPECT_NEW",
			"EXPECT_DUP", "EXPECT_INDEX_OR_SIZE",
			"EXPECT_VALUE_OR_SIZE_OR_NEW", "EXPECT_STORE_OR_NEW",
			"EXPECT_PUT_OR_DUP" };

	static {

		// void String.getChars(int srcBegin, int scrEnd, char dst[],
		// int dstBegin);
		// Copies characters from a String object into a char array.

		GET_CHARS = new MemberRef(Type.STRING, new NameAndType("getChars", Type
				.getType("(II[CI)V")));
	}

	/**
	 * Some Java compilers initialize arrays using straight-line code. For
	 * classes that have large, initialized arrays this results in unnecessarily
	 * large classfiles. <tt>CompactArrayInitializer</tt> examines a method
	 * (via its <tt>MethodEditor</tt>) creates a string in the method's
	 * class's constant pool that contains all of the elements of the
	 * initialized array. After the old initialization code is removed from the
	 * method, new code is inserted that essentially is a loop that loads each
	 * element from the string into the array. Note that only arrays of
	 * <tt>int</tt>, <tt>short</tt>, <tt>char</tt>, <tt>byte</tt>,
	 * and <tt>boolean</tt> are compacted.
	 * 
	 * @param method
	 *            The method whose array initializations are to be compacted.
	 */

	public static boolean transform(final MethodEditor method) {
		if (CompactArrayInitializer.DEBUG) {
			System.out.println("Compacting array initializer in " + method);
		}

		boolean filled = false; // Was the constant string generated and
								// entered?

		int state = CompactArrayInitializer.EXPECT_SIZE; // The state that we
															// are currently in

		int size = 0; // Size of the array
		int value = 0; // A value in the array
		int index = 0; // Current index into the array
		int[] data = null; // Contents of the array whose initialization is
		// being optimized.
		Type elementType = null; // Of what Type is the array?

		// Keep track of all the Instructions and Labels that deal with array
		// initialization.
		final ArrayList2 buf = new ArrayList2(method.code().size());

		// Get the code (Labels and Instructions) for the method we're editing
		final Iterator iter = method.code().iterator();

		while (iter.hasNext()) {
			final Object ce = iter.next();

			if (CompactArrayInitializer.DEBUG) {
				System.out.println("Examining " + ce);
				// if (false) {
				System.out.println("state = "
						+ CompactArrayInitializer.STATES[state]);
				// }
			}

			if (ce instanceof Instruction) {
				final Instruction inst = (Instruction) ce;

				switch (state) {
				case EXPECT_SIZE:
					switch (inst.opcodeClass()) {
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {
							size = ((Number) inst.operand()).intValue();
							state = CompactArrayInitializer.EXPECT_NEW;
						}
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				case EXPECT_NEW:
					switch (inst.opcodeClass()) {
					case opcx_newarray:
						elementType = (Type) inst.operand();

						if (elementType.isIntegral()) {
							data = new int[size];
							state = CompactArrayInitializer.EXPECT_DUP;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {
							size = ((Number) inst.operand()).intValue();
							state = CompactArrayInitializer.EXPECT_NEW;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				case EXPECT_DUP:
					switch (inst.opcodeClass()) {
					case opcx_dup:
						state = CompactArrayInitializer.EXPECT_INDEX_OR_SIZE;
						break;
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {
							size = ((Number) inst.operand()).intValue();
							state = CompactArrayInitializer.EXPECT_NEW;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				case EXPECT_INDEX_OR_SIZE:
					switch (inst.opcodeClass()) {
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {

							index = ((Number) inst.operand()).intValue();

							if (index < data.length) {
								state = CompactArrayInitializer.EXPECT_VALUE_OR_SIZE_OR_NEW;
							} else {
								// Out of range. Can't be an index,
								// so assume it's a size.
								size = index;
								state = CompactArrayInitializer.EXPECT_NEW;
							}
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				case EXPECT_VALUE_OR_SIZE_OR_NEW:
					switch (inst.opcodeClass()) {
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {

							value = ((Number) inst.operand()).intValue();
							state = CompactArrayInitializer.EXPECT_STORE_OR_NEW;
						} else if (inst.operand() instanceof Character) {
							final Character ch = (Character) inst.operand();
							value = ch.charValue();
							state = CompactArrayInitializer.EXPECT_STORE_OR_NEW;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					case opcx_newarray:
						size = index;
						elementType = (Type) inst.operand();

						if (elementType.isIntegral()) {
							data = new int[size];
							state = CompactArrayInitializer.EXPECT_DUP;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				case EXPECT_STORE_OR_NEW:
					switch (inst.opcodeClass()) {
					case opcx_bastore:
						if (elementType.equals(Type.BYTE)
								|| elementType.equals(Type.BOOLEAN)) {
							data[index] = value;
							state = CompactArrayInitializer.EXPECT_PUT_OR_DUP;
						}
						break;
					case opcx_castore:
						if (elementType.equals(Type.CHARACTER)) {
							data[index] = value;
							state = CompactArrayInitializer.EXPECT_PUT_OR_DUP;
						}
						break;
					case opcx_sastore:
						if (elementType.equals(Type.SHORT)) {
							data[index] = value;
							state = CompactArrayInitializer.EXPECT_PUT_OR_DUP;
						}
						break;
					case opcx_iastore:
						if (elementType.equals(Type.INTEGER)) {
							data[index] = value;
							state = CompactArrayInitializer.EXPECT_PUT_OR_DUP;
						}
						break;
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {

							size = ((Number) inst.operand()).intValue();
							state = CompactArrayInitializer.EXPECT_NEW;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					case opcx_newarray:
						size = value;
						elementType = (Type) inst.operand();

						if (elementType.isIntegral()) {
							data = new int[size];
							state = CompactArrayInitializer.EXPECT_DUP;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				case EXPECT_PUT_OR_DUP:
					switch (inst.opcodeClass()) {
					case opcx_dup:
						state = CompactArrayInitializer.EXPECT_INDEX_OR_SIZE;
						break;
					case opcx_ldc:
						if ((inst.operand() instanceof Byte)
								|| (inst.operand() instanceof Short)
								|| (inst.operand() instanceof Integer)) {
							size = ((Number) inst.operand()).intValue();
							state = CompactArrayInitializer.EXPECT_NEW;
						} else {
							state = CompactArrayInitializer.EXPECT_SIZE;
						}
						break;
					case opcx_astore:
					case opcx_aastore:
					case opcx_putstatic:
					case opcx_putstatic_nowb:
					case opcx_putfield:
					case opcx_putfield_nowb:
						if (data.length >= CompactArrayInitializer.THRESHOLD) {
							CompactArrayInitializer.fillArray(method, buf,
									elementType, data);
							filled = true;
						}
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					default:
						state = CompactArrayInitializer.EXPECT_SIZE;
						break;
					}
					break;
				}
			} else {
				final Label label = (Label) ce;

				if (label.startsBlock()) {
					state = CompactArrayInitializer.EXPECT_SIZE;
				}
			}

			if (CompactArrayInitializer.DEBUG /* && false */) {
				System.out.println("     -> "
						+ CompactArrayInitializer.STATES[state]);
			}

			buf.add(ce);
		}

		if (filled) {
			method.code().clear();
			method.code().addAll(buf);

			if (CompactArrayInitializer.DEBUG) {
				for (int i = 0; i < method.code().size(); i++) {
					System.out.println("code[" + i + "] "
							+ method.code().get(i));
				}
			}
		}

		return filled;
	}

	/**
	 * Construct a UTF8 string that stores the contents of an integral (int,
	 * char, or byte/boolean) array. Each element of the UTF8 string is 16 bits
	 * wide meaning that an int is stored into two elements, a char is store in
	 * one element, and two bytes/booleans are stored in one element.
	 * Essentially each integer in the <tt>data</tt> parmeter is converted
	 * into a character and placed in an array.
	 * <p>
	 * A UTF8 string cannot be larger than 64K. To be on the safe side, we do
	 * not generate UTF8 strings that are larger than 32K.
	 * <p>
	 * We then remove all of the old initialization code, so that the previous
	 * instruction is a <tt>newarray</tt> opcode (meaning that the array
	 * object will be on top of the stack).
	 * <p>
	 * Then new code is generated for loading data from the UTF8 string. Loading
	 * character data is relatively straightforward. The <tt>getChars</tt>
	 * method is invoked on the UTF8 string (an instance of <tt>String</tt>)
	 * to copy characters from the string into a character array.
	 * <p>
	 * A little more work has to be done for non-character data types. First,
	 * the UTF8 string is read into a (local) character array. Then, the data is
	 * extracted from the character array and placed in the new array. For some
	 * data types, labels are added to the program.
	 * 
	 * @param m
	 *            The method that contains the array
	 * @param buf
	 *            Instructions from the method that are used in array
	 *            initialization
	 * @param elementType
	 *            The Type of the array
	 * @param data
	 *            The contents of the array
	 * 
	 */
	private static void fillArray(final MethodEditor m, final ArrayList2 buf,
			final Type elementType, final int[] data) {
		// Max UTF8 constant size is 65535 bytes. We divide this in 2 to
		// prevent DataOutputStream from crashing. Since our arrays can be
		// longer than 32767, we break the image into segments.

		char[] c; // string that will be entered into the constant pool

		if (elementType.equals(Type.CHARACTER)) {
			// Fill the string with char data (16-bits). Each char in string
			// holds a single char.
			c = new char[data.length];

			for (int i = 0; i < data.length; i++) {
				c[i] = (char) data[i];
			}
		} else if (elementType.equals(Type.BYTE)
				|| elementType.equals(Type.BOOLEAN)) {
			// Fill the string with 8-bit data. Each char in string holds
			// two 8-bit data.
			c = new char[(data.length + 1) / 2];

			int j = 0;

			for (int i = 0; i + 1 < data.length; i += 2) {
				c[j++] = (char) ((data[i] << 8) | (data[i + 1] & 0xff));
			}

			if (j != c.length) {
				c[j++] = (char) (data[data.length - 1] << 8);
			}
		} else if (elementType.equals(Type.SHORT)) {
			// Fill the string with short (16-bit) data. I don't know why we
			// add 0x8000 to it, but we subtract 32768 (0x8000) in the
			// generated byetcode. Mysteries of BLOAT...

			c = new char[data.length];

			for (int i = 0; i < data.length; i++) {
				c[i] = (char) (data[i] + 0x8000);
			}
		} else if (elementType.equals(Type.INTEGER)) {
			// Fill the string with int (32-bit) data. ints are stored as
			// chars in big-endian format
			c = new char[data.length * 2];

			int j = 0;

			for (int i = 0; i < data.length; i++) {
				final int n = data[i];
				c[j++] = (char) ((n >>> 16) & 0xffff);
				c[j++] = (char) ((n >>> 0) & 0xffff);
			}
		} else {
			return;
		}

		// The Strings of data divided into 32K chunks. Each chunk is an
		// element in the ArrayList
		final ArrayList image = new ArrayList();

		// The start index in the array for each segment of the image.
		final ArrayList startIndex = new ArrayList();

		// The end index+1 in the array for each segment of the image.
		final ArrayList endIndex = new ArrayList();

		StringBuffer sb = new StringBuffer();
		int utfLength = 0;
		startIndex.add(new Integer(0));

		// Iterate over every character in the array buffer. Use a
		// StringBuffer to create String of length less than 32K.
		for (int i = 0; i < c.length; i++) {
			final char n = c[i];
			int len = 0;

			if (n == '\u0000') {
				len = 2;
			} else if (n < '\u0800') {
				len = 1;
			} else if (n < '\u8000') {
				len = 2;
			} else {
				len = 3;
			}

			if (utfLength + len > 32767) {
				// We've reached the limit on the size of the constant pool
				// string.
				// Add the current string buffer, and make a new one.
				image.add(sb.toString());
				endIndex.add(new Integer(i));

				sb = new StringBuffer();
				utfLength = 0;
				startIndex.add(new Integer(i));
			}

			sb.append(n);
			utfLength += len;
		}

		if (sb.length() > 0) {
			// If we've got leftovers, add it to the end of the current image
			// entry.
			image.add(sb.toString());
			endIndex.add(new Integer(data.length));
		} else {
			startIndex.remove(startIndex.size() - 1);
		}

		int bufStart = -1;

		// Remove the old code, leaving just the creation of the array!!!!!

		for (int i = buf.size() - 1; i >= 0; i--) {
			final Instruction inst = (Instruction) buf.get(i);
			if (inst.opcodeClass() == Opcode.opcx_newarray) {
				// ..., ldc, new, dup ldc ldc store, dup ldc ldc store, ...
				buf.removeRange(i + 1, buf.size());
				bufStart = i;
				break;
			}
		}

		if (bufStart == -1) {
			// There was no code to remove? Something went wrong. Run away!
			return;
		}

		// Insert the new:
		if (elementType.equals(Type.CHARACTER)) {
			// We envoke the method String.getChars() to copy characters from
			// a String object (the UTF8 constant in the constant pool) to
			// a character array. Remember that the destination array is on
			// the top of the stack.

			final LocalVariable array = m.newLocal(Type.OBJECT); // character
																	// array

			buf.add(new Instruction(Opcode.opcx_dup, array));
			buf.add(new Instruction(Opcode.opcx_astore, array));

			// Call getChars() for every image

			for (int i = 0; i < image.size(); i++) {
				final String im = (String) image.get(i);
				final Integer start = (Integer) startIndex.get(i);

				// void getChars(int srcBegin, int srcEnd, char dst[],
				// int dstBegin)

				buf.add(new Instruction(Opcode.opcx_ldc, im)); // String
				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(0)));
				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(im
						.length())));
				buf.add(new Instruction(Opcode.opcx_aload, array));
				buf.add(new Instruction(Opcode.opcx_ldc, start)); // dstBegin
				buf.add(new Instruction(Opcode.opcx_invokevirtual,
						CompactArrayInitializer.GET_CHARS));
			}

		} else {

			// Loading and storing non-character data is a little more
			// tricky. First we must read the UTF8 string into a character
			// array. The we must go through the array and pick out the
			// elements of the int, short, byte/boolean array.

			// array is a character array used to hold the UTF8 string
			// index1 is an index into the destination (int, boolean, etc.)
			// array and index2 is an index into the char array from the
			// constant pool. tmp is a temporary char local variable that
			// is used because booleans and bytes need to be left shifted.

			final LocalVariable array = m.newLocal(Type.OBJECT); // char
																	// array
			final LocalVariable index1 = m.newLocal(Type.INTEGER);
			final LocalVariable index2 = m.newLocal(Type.INTEGER);
			LocalVariable tmp = null;

			if (elementType.equals(Type.BYTE)
					|| elementType.equals(Type.BOOLEAN)) {
				tmp = m.newLocal(Type.CHARACTER);
			}

			// Call getChars() to read the UTF8 string from the constant
			// pool into an array of characters, array.

			for (int i = 0; i < image.size(); i++) {
				final Label top = m.newLabel();
				top.setStartsBlock(true);

				final Label bottom = m.newLabel();
				bottom.setStartsBlock(true);

				final String im = (String) image.get(i);
				final Integer start = (Integer) startIndex.get(i);
				final Integer end = (Integer) endIndex.get(i);

				if (CompactArrayInitializer.DEBUG) {
					System.out.println("image " + im);
					System.out.println("start " + start);
					System.out.println("end   " + end);
				}

				buf.add(new Instruction(Opcode.opcx_ldc, start));
				buf.add(new Instruction(Opcode.opcx_istore, index1));

				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(0)));
				buf.add(new Instruction(Opcode.opcx_istore, index2));

				// Create a new array of characters and copy the UTF8 string
				// into it.
				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(im
						.length())));
				buf.add(new Instruction(Opcode.opcx_newarray, Type.CHARACTER));
				buf.add(new Instruction(Opcode.opcx_astore, array));
				buf.add(new Instruction(Opcode.opcx_ldc, im));
				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(0)));
				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(im
						.length())));
				buf.add(new Instruction(Opcode.opcx_aload, array));
				buf.add(new Instruction(Opcode.opcx_ldc, new Integer(0)));
				buf.add(new Instruction(Opcode.opcx_invokevirtual,
						CompactArrayInitializer.GET_CHARS));

				// Start the fill loop for [start[i], end[i]).
				buf.add(top);

				// Store the image data into the data array
				// (at the top of the stack).
				if (elementType.equals(Type.SHORT)) {
					// Load an integer from the character array and then
					// subtract
					// 32768 (0x8000) from it. Convert the integer to a short
					// and store it in the destination array (which happens to
					// be
					// on the top of the stack).

					buf.add(new Instruction(Opcode.opcx_dup));
					buf.add(new Instruction(Opcode.opcx_iload, index1));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index1, 1)));
					buf.add(new Instruction(Opcode.opcx_aload, array));
					buf.add(new Instruction(Opcode.opcx_iload, index2));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index2, 1)));
					buf.add(new Instruction(Opcode.opcx_caload));
					buf
							.add(new Instruction(Opcode.opcx_ldc, new Integer(
									32768)));
					buf.add(new Instruction(Opcode.opcx_isub));
					buf.add(new Instruction(Opcode.opcx_i2s));
					buf.add(new Instruction(Opcode.opcx_sastore));

				} else if (elementType.equals(Type.BYTE)
						|| elementType.equals(Type.BOOLEAN)) {
					// For byte (and boolean) arrays we need to use a temporary
					// variable to hold the character because it needs to be
					// shifted. Recall that two bytes were glued together into
					// one character.

					// Incremenet index2 and load the character from the
					// character array and store it in tmp.
					// t = c[j++]
					buf.add(new Instruction(Opcode.opcx_aload, array));
					buf.add(new Instruction(Opcode.opcx_iload, index2));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index2, 1)));
					buf.add(new Instruction(Opcode.opcx_caload));
					buf.add(new Instruction(Opcode.opcx_istore, tmp));

					// Store the higher 8 bits of tmp into the byte array
					// b[i++] = (byte) (t >>> 8)
					buf.add(new Instruction(Opcode.opcx_dup));
					buf.add(new Instruction(Opcode.opcx_iload, index1));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index1, 1)));
					buf.add(new Instruction(Opcode.opcx_iload, tmp));
					buf.add(new Instruction(Opcode.opcx_ldc, new Integer(8)));
					buf.add(new Instruction(Opcode.opcx_iushr));
					buf.add(new Instruction(Opcode.opcx_i2b));
					buf.add(new Instruction(Opcode.opcx_bastore));

					// If we've read the last byte, go home
					// if (i >= end) break
					buf.add(new Instruction(Opcode.opcx_iload, index1));
					buf.add(new Instruction(Opcode.opcx_ldc, end));
					buf.add(new Instruction(Opcode.opcx_if_icmpge, bottom));

					// Add a new label because we're starting a new basic
					// block(?)
					final Label nobreak = m.newLabel();
					nobreak.setStartsBlock(true);
					buf.add(nobreak);

					// Store the lower order 8 bits of tmp into the byte array
					// b[i++] = (byte) (t & 0xff)
					buf.add(new Instruction(Opcode.opcx_dup));
					buf.add(new Instruction(Opcode.opcx_iload, index1));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index1, 1)));
					buf.add(new Instruction(Opcode.opcx_iload, tmp));
					buf
							.add(new Instruction(Opcode.opcx_ldc, new Integer(
									0xff)));
					buf.add(new Instruction(Opcode.opcx_iand));
					buf.add(new Instruction(Opcode.opcx_i2b));
					buf.add(new Instruction(Opcode.opcx_bastore));

				} else if (elementType.equals(Type.INTEGER)) {
					// Recall that an integer is 32 bits and is therefore
					// contained in two characters. So, read the first, then
					// read the second.

					// Increment index1 and index2 and load the character from
					// the character array.
					buf.add(new Instruction(Opcode.opcx_dup));
					buf.add(new Instruction(Opcode.opcx_iload, index1));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index1, 1)));

					buf.add(new Instruction(Opcode.opcx_aload, array));
					buf.add(new Instruction(Opcode.opcx_iload, index2));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index2, 1)));
					buf.add(new Instruction(Opcode.opcx_caload));

					// Isolate the high-order 16 bits by shifting left
					buf.add(new Instruction(Opcode.opcx_ldc, new Integer(16)));
					buf.add(new Instruction(Opcode.opcx_ishl));

					// Increment index2 and obtain the character containing the
					// low-order 16 bits of the integer
					buf.add(new Instruction(Opcode.opcx_aload, array));
					buf.add(new Instruction(Opcode.opcx_iload, index2));
					buf.add(new Instruction(Opcode.opcx_iinc, new IncOperand(
							index2, 1)));
					buf.add(new Instruction(Opcode.opcx_caload));

					// Or the higher-order bits and the lower-order bits
					// together
					// and store the result in the integer array.
					buf.add(new Instruction(Opcode.opcx_ior));
					buf.add(new Instruction(Opcode.opcx_iastore));
				}

				// Branch back if we're not out of the loop.
				// while (i < end)
				buf.add(new Instruction(Opcode.opcx_iload, index1));
				buf.add(new Instruction(Opcode.opcx_ldc, end));
				buf.add(new Instruction(Opcode.opcx_if_icmplt, top));

				buf.add(bottom);
			}
		}

		if (CompactArrayInitializer.DEBUG) {
			for (int i = bufStart; i < buf.size(); i++) {
				System.out.println("fill[" + i + "] " + buf.get(i));
			}
		}
	}
}

/**
 * Recall that Nate used beta versions of the JDK1.2 util classes to build
 * BLOAT. While most of the conversion to the final util API was simple, there
 * were a couple of changes made that force us to make some changes.
 * 
 * The final version of the API makes the ArrayList.removeRange() method
 * protected. So, we have to make this silly wrapper class in order to access
 * it. Silly.
 */
class ArrayList2 extends ArrayList {
	public ArrayList2(final int initialCapacity) {
		super(initialCapacity);
	}

	public void removeRange(final int fromIndex, final int toIndex) {
		super.removeRange(fromIndex, toIndex);
	}
}
