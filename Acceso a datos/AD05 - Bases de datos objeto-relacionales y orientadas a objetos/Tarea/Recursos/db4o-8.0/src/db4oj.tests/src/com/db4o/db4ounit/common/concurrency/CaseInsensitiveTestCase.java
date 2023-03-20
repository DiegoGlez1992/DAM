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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * demonstrates a case-insensitive query using an Evaluation
 */
public class CaseInsensitiveTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CaseInsensitiveTestCase().runConcurrency();
	}

	public String name;

	public CaseInsensitiveTestCase() {
	}

	public CaseInsensitiveTestCase(String name) {
		this.name = name;
	}

	protected void store() {
		store(new CaseInsensitiveTestCase("HelloWorld"));
	}

	public void concQueryCaseInsenstive(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(CaseInsensitiveTestCase.class);
		q.constrain(new CaseInsensitiveEvaluation("helloworld"));
		Assert.areEqual(1, q.execute().size());
	}

	public static class CaseInsensitiveEvaluation implements Evaluation {
		public String name;

		public CaseInsensitiveEvaluation(String name) {
			this.name = name;
		}

		public void evaluate(Candidate candidate) {
			CaseInsensitiveTestCase ci = (CaseInsensitiveTestCase) candidate.getObject();
			candidate.include(ci.name.toLowerCase().equals(name.toLowerCase()));
		}

	}

}

