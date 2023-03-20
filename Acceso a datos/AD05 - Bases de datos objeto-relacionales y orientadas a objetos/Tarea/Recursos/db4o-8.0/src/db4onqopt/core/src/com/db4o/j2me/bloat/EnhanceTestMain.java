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
package com.db4o.j2me.bloat;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.j2me.bloat.testdata.*;
import com.db4o.reflect.self.*;

public class EnhanceTestMain {
	private static final String FILENAME = "enhanceddog.db4o";

	public static void main(String[] args) throws Exception {
        Class registryClazz=Class.forName("com.db4o.j2me.bloat.testdata.GeneratedDogSelfReflectionRegistry");
        SelfReflectionRegistry registry=(SelfReflectionRegistry)registryClazz.newInstance();
        new File(FILENAME).delete();
        ObjectContainer db=Db4o.openFile(selfReflectorConfig(registry), FILENAME);
        db.store(new Dog("Laika",111,new Dog[]{},new int[]{1,2,3}));
        db.close();
        db=Db4o.openFile(selfReflectorConfig(registry), FILENAME);
        ObjectSet result=db.queryByExample(Dog.class);
        while(result.hasNext()) {
        	System.out.println(result.next());
        }
        db.close();
	}

	private static Configuration selfReflectorConfig(SelfReflectionRegistry registry) {
	    final Configuration config = Db4o.newConfiguration();
		config.reflectWith(new SelfReflector(registry));
	    return config;
    }
}
