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
package com.db4o.db4ounit.common.activation;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class MaximumActivationDepthTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA {

	public static class Data {
		public int _id;
		public Data _prev;

		public Data(int id,Data prev) {
			_id=id;
			_prev = prev;
		}
	}

	protected void configure(Configuration config) {
		config.activationDepth(Integer.MAX_VALUE);
		config.objectClass(Data.class).maximumActivationDepth(1);
	}
	
	protected void store() throws Exception {
		Data data=new Data(2,null);
		data=new Data(1,data);
		data=new Data(0,data);
		store(data);
	}

	public void testActivationRestricted() {
		Query query=newQuery(Data.class);
		query.descend("_id").constrain(new Integer(0));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
		Data data=(Data)result.next();
		Assert.isNotNull(data._prev);
		Assert.isNull(data._prev._prev);
	}
}
