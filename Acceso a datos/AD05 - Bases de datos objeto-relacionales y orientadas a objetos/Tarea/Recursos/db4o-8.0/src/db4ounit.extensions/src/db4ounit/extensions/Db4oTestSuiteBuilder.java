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
package db4ounit.extensions;

import java.lang.reflect.*;

import com.db4o.foundation.*;

import db4ounit.*;

public class Db4oTestSuiteBuilder extends ReflectionTestSuiteBuilder {
		
	private Db4oFixture _fixture;
    
	public Db4oTestSuiteBuilder(Db4oFixture fixture, Class clazz) {		
		this(fixture, new Class[] { clazz });
	}
    
    public Db4oTestSuiteBuilder(Db4oFixture fixture, Class[] classes) {     
        super(classes);
        fixture(fixture);
    }
    
    private void fixture(Db4oFixture fixture){
        if (null == fixture) throw new ArgumentNullException("fixture");     
        _fixture = fixture;
    }

    protected boolean isApplicable(Class clazz) {
    	return _fixture.accept(clazz);
    }
    
    protected Test createTest(Object instance, Method method) {
    	final Test test = super.createTest(instance, method);
    	return new TestDecorationAdapter(test) {
			public String label() {
				return "(" + Db4oFixtureVariable.fixture().label() + ") " + test.label();
			}
		};
    }
    
    protected Object withContext(Closure4 closure) {
    	return Db4oFixtureVariable.FIXTURE_VARIABLE.with(_fixture, closure);
    }
}