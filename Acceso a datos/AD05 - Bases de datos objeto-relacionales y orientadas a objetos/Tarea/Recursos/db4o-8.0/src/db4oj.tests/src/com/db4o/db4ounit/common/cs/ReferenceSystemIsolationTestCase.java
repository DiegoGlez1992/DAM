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
package com.db4o.db4ounit.common.cs;

import com.db4o.query.*;

import db4ounit.*;

public class ReferenceSystemIsolationTestCase extends EmbeddedAndNetworkingClientTestCaseBase {
	
	public static final class IncludeAllEvaluation implements Evaluation {
		public void evaluate(Candidate candidate) {
			candidate.include(true);
		}
	}

	public static class Item{
		
	}
	
	public void test(){
		Item item = new Item();
		networkingClient().store(item);
		int id = (int) networkingClient().getID(item);
		
		Query query = networkingClient().query();
		query.constrain(Item.class);
		query.constrain(new IncludeAllEvaluation());
		query.execute();
		
		Assert.isNull(embeddedClient().transaction().referenceForId(id));
	}

}
