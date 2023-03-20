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

public class CallbackHelper {

		public String name;
		public CallbacksTestCase parent;
		
		public void objectOnActivate(ObjectContainer container){
		    CallbacksTestCase.called[CallbacksTestCase.ACTIVATE] = true;
		    container.activate(parent, 3);
		}
		
		public void objectOnDeactivate(ObjectContainer container){
			container.deactivate(parent, 3);
		}
		
		public void objectOnDelete(ObjectContainer container){
			container.delete(parent);
		}
		
		public void objectOnNew(ObjectContainer container){
			
			// New logic: Updating a touched object inside a callback
			//            is not allowes. The following is no longer
			//            a legal call:
			
			// container.store(parent);
		}
		
		public void objectOnUpdate(ObjectContainer container){
		    
		    // circular sets are necessary in many cases
		    // Don' stop them!
		    
		    // Accordingly the following will produce an endless loop
			// container.set(parent);
		}
}
