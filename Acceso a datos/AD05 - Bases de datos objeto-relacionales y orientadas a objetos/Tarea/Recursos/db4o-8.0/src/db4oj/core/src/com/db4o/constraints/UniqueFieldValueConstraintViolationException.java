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
package com.db4o.constraints;


/**
 * db4o-specific exception.<br><br>
 * This exception can be thrown by a 
 * {@link com.db4o.constraints.UniqueFieldValueConstraint} on commit.
 * @see com.db4o.config.ObjectField#indexed(boolean)
 * @see com.db4o.config.Configuration#add(com.db4o.config.ConfigurationItem)
 */
public class UniqueFieldValueConstraintViolationException extends ConstraintViolationException {

	/**
	 * Constructor with a message composed from the class and field
	 * name of the entity causing the exception.
	 * @param className class, which caused the exception
	 * @param fieldName field, which caused the exception
	 */
	public UniqueFieldValueConstraintViolationException(String className, String fieldName) {
		super("class: " + className + " field: " + fieldName);
	}

}
