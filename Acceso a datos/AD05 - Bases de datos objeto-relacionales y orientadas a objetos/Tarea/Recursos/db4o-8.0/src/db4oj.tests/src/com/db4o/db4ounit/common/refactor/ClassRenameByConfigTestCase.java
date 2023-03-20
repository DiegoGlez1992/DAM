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

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;


public class ClassRenameByConfigTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {

	public static void main(String[] args) {
		new ClassRenameByConfigTestCase().runNetworking();
	}
	
	
    public static class Original {
    	
        public String originalName;

        public Original() {

        }

        public Original(String name) {
            originalName = name;
        }
    }

    public static class Changed {

        public String changedName;

    }

	
	public void test() throws Exception{
		
		store(new Original("original"));
		
		db().commit();
		
		Assert.areEqual(1, countOccurences(Original.class));
		
        // Rename messages are visible at level 1
        // fixture().config().messageLevel(1);
		
        ObjectClass oc = fixture().config().objectClass(Original.class);

        // allways rename fields first
        oc.objectField("originalName").rename("changedName");
        // we must use ReflectPlatform here as the string must include
        // the assembly name in .net
        oc.rename(CrossPlatformServices.fullyQualifiedName(Changed.class));

        reopen();
        
        Assert.areEqual(0, countOccurences(Original.class));
        Assert.areEqual(1, countOccurences(Changed.class));
        
        Changed changed = (Changed) retrieveOnlyInstance(Changed.class);
        
        Assert.areEqual("original", changed.changedName);

	}

}
