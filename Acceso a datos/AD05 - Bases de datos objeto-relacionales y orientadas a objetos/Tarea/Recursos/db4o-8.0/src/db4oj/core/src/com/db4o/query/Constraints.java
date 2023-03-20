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
package com.db4o.query;

/**
 * set of {@link Constraint} objects.
 * <br><br>This extension of the {@link Constraint} interface allows
 * setting the evaluation mode of all contained {@link Constraint}
 * objects with single calls.
 * <br><br>
 * See also {@link Query#constraints()}.
 */
public interface Constraints extends Constraint
{
	
	/**
	 * returns an array of the contained {@link Constraint} objects.
	 * @return  an array of the contained {@link Constraint} objects.
	 */
	public Constraint[] toArray();
}
