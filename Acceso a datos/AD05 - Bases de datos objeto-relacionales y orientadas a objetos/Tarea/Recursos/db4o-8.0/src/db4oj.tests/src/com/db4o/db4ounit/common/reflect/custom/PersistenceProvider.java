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
package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.foundation.*;

/**
 * Models an external storage model to which db4o have to be adapted to.
 *
 * This particular one is a tuple based persistence mechanism modeled after the GigaSpaces
 * persistence API.
 *
 * There are only two types of fields supported: string and int which are mapped
 * to java.lang.String and java.lang.Integer.
 */
public interface PersistenceProvider {

	void initContext(PersistenceContext context);

	void closeContext(PersistenceContext context);
	
	void purge(String url);

	void createEntryClass(PersistenceContext context,
			String className, String[] fieldNames, String[] fieldTypes);

	void createIndex(PersistenceContext context, String className, String fieldName);

	void dropIndex(PersistenceContext context, String className, String fieldName);

	void dropEntryClass(PersistenceContext context, String className);

	void insert(PersistenceContext context, PersistentEntry entry);

	void update(PersistenceContext context, PersistentEntry entry);

	int delete(PersistenceContext context, String className, Object uid);

	Iterator4 select(PersistenceContext context, PersistentEntryTemplate template);
}
