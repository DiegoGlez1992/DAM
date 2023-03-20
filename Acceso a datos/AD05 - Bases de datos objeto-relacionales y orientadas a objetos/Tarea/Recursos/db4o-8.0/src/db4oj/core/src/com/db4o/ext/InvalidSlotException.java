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
package com.db4o.ext;

/**
 * db4o-specific exception.<br><br>
 * This exception is thrown when db4o reads slot
 * information which is not valid (length or address).
 */
public class InvalidSlotException extends Db4oRecoverableException {

	/**
	 * Constructor allowing to specify a detailed message.
	 * @param msg message
	 */
	public InvalidSlotException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructor allowing to specify the address, length and id.
	 * @param address offending address
	 * @param length offending length
	 * @param id id where the address and length were read. 
	 */
	public InvalidSlotException(int address, int length, int id) {
		super("address: " + address + ", length : " + length + ", id : " + id);
	}

}
