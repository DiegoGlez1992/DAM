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
package com.db4o.db4ounit.common.refactor;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class RemovedClassRefactoringTestSuite extends FixtureBasedTestSuite implements Db4oTestCase{
	
	private static final FixtureVariable<Boolean> DO_DEFRAGMENT = FixtureVariable.newInstance("defrag");
	
	private static final FixtureVariable<Boolean> INDEXED = FixtureVariable.newInstance("indexed");
	
	private static final FixtureVariable<Reflector> EXCLUDING_REFLECTOR = FixtureVariable.newInstance("reflector");
	

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[]{
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider(DO_DEFRAGMENT, new Object[]{new Boolean(true), new Boolean(false)}),
				new SimpleFixtureProvider(EXCLUDING_REFLECTOR, new ExcludingReflector(Super.class), new ExcludingReflector()),
				new SimpleFixtureProvider(INDEXED, new Object[]{new Boolean(true), new Boolean(false)}),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[]{RemovedClassRefactoringTestUnit.class};
	}
	
	public static class Super {
		
		public String _superField;
		
		public Super(String super_) {
			_superField = super_;
		}

	}
	
	public static class Sub extends Super {
		
		public String _subField; 
		
		public Sub(String super_, String sub) {
			super(super_);
			_subField = sub;
		}

	}
	
	public static class NoSuper {
		
		public NoSuper(String sub){
			_subField = "foo";
		}
		
		public String _subField;
		
	}

	
	public static class RemovedClassRefactoringTestUnit extends AbstractDb4oTestCase {
		
		@Override
		protected void configure(Configuration config) throws Exception {
			config.objectClass(Sub.class).objectField("_subField").indexed(INDEXED.value());
		}
		
		@Override
		protected void store() throws Exception {
			Sub sub = new Sub("super", "sub");
			store(sub);
		}
	
		public void test() throws Exception {
			fixture().resetConfig();
			Configuration config = fixture().config();
			config.reflectWith(EXCLUDING_REFLECTOR.value());
			TypeAlias alias = new TypeAlias(Sub.class, NoSuper.class);
			config.addAlias(alias);
			
			if(DO_DEFRAGMENT.value()){
				defragment();	
			}else{
				reopen();	
			}
			
			NoSuper result = retrieveOnlyInstance(NoSuper.class);
			Assert.areEqual("sub", result._subField);
			
			NoSuper newSuper = new NoSuper("foo");
			store(newSuper);
			
			Query q = newQuery(NoSuper.class);
			q.descend("_subField").constrain("foo");
			ObjectSet<NoSuper> objectSet = q.execute();
			Assert.areEqual(1, objectSet.size());
			result = objectSet.next();
			Assert.areEqual("foo", result._subField);
			
			db().refresh(result, Integer.MAX_VALUE);
			
		}
	}

}
