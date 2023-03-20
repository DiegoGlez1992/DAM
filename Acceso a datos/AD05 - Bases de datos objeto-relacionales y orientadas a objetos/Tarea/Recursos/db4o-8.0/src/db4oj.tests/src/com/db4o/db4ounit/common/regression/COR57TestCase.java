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
package com.db4o.db4ounit.common.regression;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class COR57TestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new COR57TestCase().runSolo();
	}
	
	public static class Base {
	    public String name;
	    
	    public Base() {
	    }
	    
	    public Base(String name_) {
	    	name = name_;
	    }
	    
	    public String toString() {
	        return getClass() + ":" + name;
	    }
	}

	public static class BaseExt extends Base {
		public BaseExt() {
	    }
	    
	    public BaseExt(String name_) {
	    	super(name_);
	    }
	}

	public static class BaseExtExt extends BaseExt {
		public BaseExtExt() {
	    }
	    
	    public BaseExtExt(String name_) {
	    	super(name_);
	    }
	}
	
	protected void configure(Configuration config) {
		config.objectClass(Base.class).objectField("name").indexed(true);
	}
	
	protected void store() throws Exception {
		 for (int i = 0; i < 5; i++) {
			 String name = String.valueOf(i);
			 db().store(new Base(name));
			 db().store(new BaseExt(name));
			 db().store(new BaseExtExt(name));
		 } 
	}
	
	public void testQBE() {
		assertQBE(1, new BaseExtExt("1"));		
		assertQBE(2, new BaseExt("1"));
		assertQBE(3, new Base("1"));
	}
	
	public void testSODA() {
		assertSODA(1, new BaseExtExt("1"));
		assertSODA(2, new BaseExt("1"));
		assertSODA(3, new Base("1"));		
	}
	
	private void assertSODA(int expectedCount, final Base template) {
		assertQueryResult(expectedCount, template, createSODA(template).execute());
	}

	private Query createSODA(final Base template) {
		final Query q = newQuery(template.getClass());
		q.descend("name").constrain(template.name);
		return q;
	}

	private void assertQBE(int expectedCount, final Base template) {
		assertQueryResult(expectedCount, template, db().queryByExample(template));
	}

	private void assertQueryResult(int expectedCount, final Base expectedTemplate, final ObjectSet result) {
		Assert.areEqual(expectedCount, result.size(), simpleName(expectedTemplate.getClass()));
		while (result.hasNext()) {
			Base actual = (Base)result.next();
			Assert.areEqual(expectedTemplate.name, actual.name);
			Assert.isInstanceOf(expectedTemplate.getClass(), actual);
		}
	}

	private String simpleName(Class c) {
		final String name = c.getName();
		return name.substring(name.lastIndexOf('$')+1);
	}	
}
