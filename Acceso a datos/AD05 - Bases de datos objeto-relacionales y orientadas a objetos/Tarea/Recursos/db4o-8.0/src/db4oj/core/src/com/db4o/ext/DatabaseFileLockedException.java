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
 * this Exception is thrown during any of the db4o open calls
 * if the database file is locked by another process.
 * @see com.db4o.Db4o#openFile
 */
public class DatabaseFileLockedException extends Db4oFatalException {
	
	/**
	 * Constructor with a database description message 
	 * @param databaseDescription message, which can help to identify the database
	 */
	public DatabaseFileLockedException(String databaseDescription) {
		super(databaseDescription);
	}

	/**
	 * Constructor with a database description and cause exception
	 * @param databaseDescription database description
	 * @param cause previous exception caused DatabaseFileLockedException
	 */
	public DatabaseFileLockedException(String databaseDescription, Throwable cause) {
		super(databaseDescription,cause);
	}
	
}
