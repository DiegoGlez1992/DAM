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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public abstract class FieldIndexTestCaseBase extends AbstractDb4oTestCase
		implements OptOutMultiSession {

	public FieldIndexTestCaseBase() {
		super();
	}

	protected void configure(Configuration config) {
		indexField(config, FieldIndexItem.class, "foo");
	}

	protected abstract void store();

	protected void storeItems(final int[] foos) {
		for (int i = 0; i < foos.length; i++) {
			store(new FieldIndexItem(foos[i]));
		}
	}

	protected Query createQuery(final int id) {
		Query q = createItemQuery();
		q.descend("foo").constrain(new Integer(id));
		return q;
	}

	protected Query createItemQuery() {
		return createQuery(FieldIndexItem.class);
	}

	protected Query createQuery(Class clazz) {
		return createQuery(trans(), clazz);
	}

	protected Query createQuery(Transaction trans, Class clazz) {
		Query q = createQuery(trans);
		q.constrain(clazz);
		return q;
	}

	protected Query createItemQuery(Transaction trans) {
		Query q = createQuery(trans);
		q.constrain(FieldIndexItem.class);
		return q;
	}

	private Query createQuery(Transaction trans) {
		return container().query(trans);
	}
}