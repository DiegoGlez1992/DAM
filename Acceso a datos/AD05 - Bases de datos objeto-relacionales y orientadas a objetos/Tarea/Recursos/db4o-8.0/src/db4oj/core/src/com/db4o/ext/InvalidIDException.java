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
 * This exception is thrown when the supplied object ID
 * is incorrect (outside the scope of the database IDs).
 * @see com.db4o.ext.ExtObjectContainer#bind(Object, long)
 * @see com.db4o.ext.ExtObjectContainer#getByID(long)
 */
public class InvalidIDException extends Db4oRecoverableException {
	
	/**
	 * Constructor allowing to specify the exception cause
	 * @param cause cause exception
	 */
	public InvalidIDException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor allowing to specify the offending id 
	 * @param id the offending id
	 */
	public InvalidIDException(int id){
		super("id: " + id);
	}
}
