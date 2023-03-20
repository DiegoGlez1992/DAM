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
package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class CascadeDeleteDeletedTestCase extends Db4oClientServerTestCase {

	public String name;

	public Object untypedMember;

	public CddMember typedMember;
	
	public static void main(String[] args) {
		new CascadeDeleteDeletedTestCase().runNetworking();
	}
	
	public CascadeDeleteDeletedTestCase() {
	}

	public CascadeDeleteDeletedTestCase(String name) {
		this.name = name;
	}

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
	}

	protected void store() {
		ExtObjectContainer oc = db();
		membersFirst(oc, "membersFirst commit");
		membersFirst(oc, "membersFirst");
		twoRef(oc, "twoRef");
		twoRef(oc, "twoRef commit");
		twoRef(oc, "twoRef delete");
		twoRef(oc, "twoRef delete commit");
	}

	private void membersFirst(ExtObjectContainer oc, String name) {
		CascadeDeleteDeletedTestCase cdd = new CascadeDeleteDeletedTestCase(name);
		cdd.untypedMember = new CddMember();
		cdd.typedMember = new CddMember();
		oc.store(cdd);
	}

	private void twoRef(ExtObjectContainer oc, String name) {
		CascadeDeleteDeletedTestCase cdd = new CascadeDeleteDeletedTestCase(name);
		cdd.untypedMember = new CddMember();
		cdd.typedMember = new CddMember();
		CascadeDeleteDeletedTestCase cdd2 = new CascadeDeleteDeletedTestCase(name);
		cdd2.untypedMember = cdd.untypedMember;
		cdd2.typedMember = cdd.typedMember;
		oc.store(cdd);
		oc.store(cdd2);
	}

	public void _testDeleteDeleted() throws Exception {
		int total = 10;
		final int CDD_MEMBER_COUNT = 12;
		ExtObjectContainer[] containers = new ExtObjectContainer[total];
		ExtObjectContainer oc = null;
		try {
			for (int i = 0; i < total; i++) {
				containers[i] = openNewSession();
				assertOccurrences(containers[i], CddMember.class,
						CDD_MEMBER_COUNT);
			}
			for (int i = 0; i < total; i++) {
				deleteAll(containers[i], CddMember.class);
			}
			oc = openNewSession();
			assertOccurrences(oc, CddMember.class, CDD_MEMBER_COUNT);
			// ocs[0] deleted all CddMember objects, and committed the change
			containers[0].commit();
			containers[0].close();
			// FIXME: following assertion fails
			assertOccurrences(oc, CddMember.class, 0);
			for (int i = 1; i < total; i++) {
				containers[i].close();
			}
			assertOccurrences(oc, CddMember.class, 0);
		} finally {
			if (oc != null) {
				oc.close();
			}
			for (int i = 0; i < total; i++) {
				if (containers[i] != null) {
					containers[i].close();
				}
			}
		}

	}
/*
	private void tMembersFirst(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		Query q = oc.query();
		q.constrain(CascadeDeleteDeletedTestCase.class);
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		CascadeDeleteDeletedTestCase cdd = (CascadeDeleteDeletedTestCase) objectSet.next();
		oc.delete(cdd.untypedMember);
		oc.delete(cdd.typedMember);
		if (commit) {
			oc.commit();
		}
		oc.delete(cdd);
		if (!commit) {
			oc.commit();
		}
	}

	private void tTwoRef(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		boolean delete = name.indexOf("delete") > 1;

		Query q = oc.query();
		q.constrain(this.getClass());
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		CascadeDeleteDeletedTestCase cdd = (CascadeDeleteDeletedTestCase) objectSet.next();
		CascadeDeleteDeletedTestCase cdd2 = (CascadeDeleteDeletedTestCase) objectSet.next();
		if (delete) {
			oc.delete(cdd.untypedMember);
			oc.delete(cdd.typedMember);
		}
		oc.delete(cdd);
		if (commit) {
			oc.commit();
		}
		oc.delete(cdd2);
		if (!commit) {
			oc.commit();
		}
	}
*/
	public static class CddMember {
		public String name;
	}

}
