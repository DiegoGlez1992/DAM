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
package com.db4o.db4ounit.common.diagnostics;

import java.util.*;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IndexFieldDiagnosticTestCase extends AbstractDb4oTestCase {
	
	private boolean _diagnosticsCalled;
	
	public static class Car {
	    public String model;
	    public List history;

	    public Car(String model) {
	        this(model,new ArrayList());
	    }

	    public Car(String model,List history) {
	        this.model=model;
	        this.history=history;
	    }

	    public String getModel() {
	        return model;
	    }

	    public List getHistory() {
	        return history;
	    }
	    
	    
	    public String toString() {
	        return model;
	    }
	}
	
	@Override
	protected void store() throws Exception {
		Car car = new Car("BMW");
		store(car);
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.diagnostic().addListener(new DiagnosticListener(){
			public void onDiagnostic(Diagnostic d) {
				if ( d instanceof LoadedFromClassIndex){
					_diagnosticsCalled = true;	
				}
			}
		});
	}
	
	public void testNonIndexedFieldQuery(){
		Query query = newQuery(Car.class);
		query.descend("model").constrain("BMW");
		query.execute();
		Assert.isTrue(_diagnosticsCalled);
	}

	public void testClassQuery(){
		db().query(Car.class);
		Assert.isFalse(_diagnosticsCalled);
	}

}
