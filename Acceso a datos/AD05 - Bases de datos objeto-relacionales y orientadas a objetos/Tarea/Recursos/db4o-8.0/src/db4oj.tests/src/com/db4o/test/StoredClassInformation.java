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

import com.db4o.ext.*;

public class StoredClassInformation {
	
	static final int COUNT = 10;
	
	public String name;
	
	public void test(){

		Test.deleteAllInstances(this);
		name = "hi";
		Test.store(this);
		for(int i = 0; i < COUNT; i ++){
			Test.store(new StoredClassInformation());
		}
		
		StoredClass[] storedClasses = Test.objectContainer().ext().storedClasses();
		StoredClass myClass = Test.objectContainer().ext().storedClass(this);
		
		boolean found = false;
		for (int i = 0; i < storedClasses.length; i++) {
            if(storedClasses[i].getName().equals(myClass.getName())){
            	found = true;
            	break;
            }
        }
        
        Test.ensure(found);
        
        long id = Test.objectContainer().getID(this);
        
        long ids[] = myClass.getIDs();
        Test.ensure(ids.length == COUNT + 1);
        
        found = false;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == id){
            	found = true;
            	break;
            }
        }
        
        Test.ensure(found);
        
	}
	
}
