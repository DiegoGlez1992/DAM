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
 * Catch stores information about a protected block and an exception handler in
 * a method. The startPC, endPC, and handlerPC are indices into the bytecode of
 * the method where the protected block begins and ends and the catch block
 * begins, respectively. They are indices into the code array.
 * 
 * @see EDU.purdue.cs.bloat.file.Code#code
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Catch {
	private int startPC;

	private int endPC;

	private int handlerPC;

	private int catchType;

	/**
	 * Constructor.
	 * 
	 * @param startPC
	 *            The start PC of the protected block.
	 * @param endPC
	 *            The PC of the instruction after the end of the protected
	 *            block.
	 * @param handlerPC
	 *            The start PC of the exception handler.
	 * @param catchType
	 *            The type of exception to catch.
	 */
	public Catch(final int startPC, final int endPC, final int handlerPC,
			final int catchType) {
		this.startPC = startPC;
		this.endPC = endPC;
		this.handlerPC = handlerPC;
		this.catchType = catchType;
	}

	/**
	 * Get the start PC of the protected block.
	 * 
	 * @return The start PC of the protected block.
	 * @see Catch#setStartPC
	 */
	public int startPC() {
		return startPC;
	}

	/**
	 * Set the start PC of the protected block.
	 * 
	 * @param pc
	 *            The start PC of the protected block.
	 * @see Catch#startPC
	 */
	public void setStartPC(final int pc) {
		startPC = pc;
	}

	/**
	 * Get the end PC of the protected block.
	 * 
	 * @return The PC of the instruction after the end of the protected block.
	 * @see Catch#setEndPC
	 */
	public int endPC() {
		return endPC;
	}

	/**
	 * Set the end PC of the protected block.
	 * 
	 * @param pc
	 *            The PC of the instruction after the end of the protected
	 *            block.
	 * @see Catch#endPC
	 */
	public void setEndPC(final int pc) {
		endPC = pc;
	}

	/**
	 * Get the start PC of the exception handler.
	 * 
	 * @return The start PC of the exception handler.
	 * @see Catch#setHandlerPC
	 */
	public int handlerPC() {
		return handlerPC;
	}

	/**
	 * Set the start PC of the exception handler.
	 * 
	 * @param pc
	 *            The start PC of the exception handler.
	 * @see Catch#handlerPC
	 */
	public void setHandlerPC(final int pc) {
		handlerPC = pc;
	}

	/**
	 * Get the index into the constant pool of the type of exception to catch.
	 * 
	 * @return Index of the type of exception to catch.
	 * @see Catch#setCatchTypeIndex
	 */
	public int catchTypeIndex() {
		return catchType;
	}

	/**
	 * Set the index into the constant pool of the type of exception to catch.
	 * 
	 * @param index
	 *            Index of the type of exception to catch.
	 * @see Catch#catchTypeIndex
	 */
	public void setCatchTypeIndex(final int index) {
		this.catchType = index;
	}

	public Object clone() {
		return (new Catch(this.startPC, this.endPC, this.handlerPC,
				this.catchType));
	}

	/**
	 * Returns a string representation of the catch information.
	 */
	public String toString() {
		return "(try-catch " + startPC + " " + endPC + " " + handlerPC + " "
				+ catchType + ")";
	}
}
