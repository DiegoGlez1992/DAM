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
package com.db4o.test.legacy.soda.experiments;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STCaseInsensitive implements STClass {

	public static transient SodaTest st;
	
	String str;
	
	
	public STCaseInsensitive() {
	}
	
	public STCaseInsensitive(String str) {
		this.str = str;
	}

	public Object[] store() {
		return new Object[] {
			new STCaseInsensitive("Hihoho"),
			new STCaseInsensitive("Hello"),
			new STCaseInsensitive("hello")
		};
	}

	public void test() {
		Query q = st.query();
		q.constrain(STCaseInsensitive.class);
		q.descend("str").constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                candidate.include(candidate.getObject().toString().toLowerCase().startsWith("hell"));
            }
        });
		Object[] r = store();
		st.expect(q, new Object[] { r[1], r[2] });
	}

}

