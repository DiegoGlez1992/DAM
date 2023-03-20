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
package com.db4o.db4ounit.common.ta;

import com.db4o.activation.*;
import com.db4o.ta.*;

import db4ounit.*;

public class ActivationBasedOnConcreteTypeTestCase extends TransparentActivationTestCaseBase {
	
	public static void main(String[] args) {
		new ActivationBasedOnConcreteTypeTestCase().runNetworking();
	}
	
	public static class NonActivatableParent {
	}
	
	public static class ActivatableChild extends NonActivatableParent implements Activatable {
		private transient Activator _activator;

		public void bind(Activator activator) {
	    	if (_activator == activator) {
	    		return;
	    	}
	    	if (activator != null && _activator != null) {
	            throw new IllegalStateException();
	        }

			_activator = activator;
		}
		
		public void activate(ActivationPurpose purpose) {
			if (_activator == null) return;
			_activator.activate(purpose);
		}		
		
		public int _value;
		public ActivatableChild(int value) {
			_value = value;
		}
	}
	
	public static class Holder {
		public Holder(NonActivatableParent object) {
			_object = object;
		}

		public NonActivatableParent _object;		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Holder(new ActivatableChild(42)));
	}
	
	public void testActivationIsBasedOnConcretType() {
		final Holder holder = retrieveOnlyInstance(Holder.class);
		Assert.isFalse(db().ext().isActive(holder._object));
	}

}
