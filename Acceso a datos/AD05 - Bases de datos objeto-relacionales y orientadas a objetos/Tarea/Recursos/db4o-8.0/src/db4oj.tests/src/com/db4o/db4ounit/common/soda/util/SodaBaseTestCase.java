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
package com.db4o.db4ounit.common.soda.util;

import com.db4o.query.*;

import db4ounit.extensions.*;

public abstract class SodaBaseTestCase extends AbstractDb4oTestCase {

	protected transient Object[] _array;

	protected void db4oSetupBeforeStore() throws Exception {
		_array=createData();
	}

	protected void store() {
		Object[] data=createData();
		for (int idx = 0; idx < data.length; idx++) {
			db().store(data[idx]);
		}
	}
	
	public abstract Object[] createData();

    protected void expect(Query query, int[] indices) {
        SodaTestUtil.expect(query, collectCandidates(indices), false);
    }

    protected void expectOrdered(Query query, int[] indices) {
        SodaTestUtil.expectOrdered(query, collectCandidates(indices));
    }

	private Object[] collectCandidates(int[] indices) {
		Object[] data=new Object[indices.length];
    	for (int idx = 0; idx < indices.length; idx++) {
			data[idx]=_array[indices[idx]];
		}
		return data;
	}
}
