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
package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class SortedSameOrder {
	private static class Sortable {
		private String a;

		public Sortable(String a) {
			this.a = a;
		}
		
		public String toString() {
			return a;
		}
	}

	private static class SortableComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((Sortable)first).a.compareTo(((Sortable)second).a);
		}
	}

	public void store() {
		Test.store(new Sortable("a"));
		Test.store(new Sortable("c"));
		Test.store(new Sortable("b"));
	}
	
	public void test() {
		Query query=Test.query();
		query.constrain(Sortable.class);
		SortableComparator cmp = new SortableComparator();
		query.sortBy(cmp);
		ObjectSet result=query.execute();
		
		Object last=null;
		while(result.hasNext()) {
			Object cur=result.next();
			Test.ensure(last==null||cmp.compare(last,cur)<=0);
			last=cur;
		}
		last=null;
		for (int i=0;i<result.size();i++) {
			Object cur = result.get(i);
			Test.ensure(last==null||cmp.compare(last,cur)<=0);
			last=cur;
		}
	}
}
