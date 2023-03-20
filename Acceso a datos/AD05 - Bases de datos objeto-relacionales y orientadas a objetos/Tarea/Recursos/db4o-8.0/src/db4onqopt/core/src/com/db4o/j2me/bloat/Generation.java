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

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.j2me.bloat.testdata.*;
import com.db4o.reflect.self.*;

// TODO: Use plain classes for testing, not the SelfReflector test cases
// (which already implement SelfReflectable functionality)
public class Generation {

	public static void main(String[] args) {
		String outputDirName = "generated";
		ClassFileLoader loader = new ClassFileLoader();
		BloatJ2MEContext context = new BloatJ2MEContext(loader, outputDirName);
		
		ClassEditor ce = context.createClass(Modifiers.PUBLIC,
				"com.db4o.j2me.bloat.testdata.GeneratedDogSelfReflectionRegistry", Type.getType(Type.classDescriptor(SelfReflectionRegistry.class.getName())),
				new Type[0]);
		context.createLoadClassConstMethod(ce);

		RegistryEnhancer registryEnhancer = new RegistryEnhancer(context, ce,
				Dog.class);
		registryEnhancer.generate();
		ce.commit();

		enhanceClass(context,"../bin","com.db4o.j2me.bloat.testdata.Dog");
		enhanceClass(context,"../bin","com.db4o.j2me.bloat.testdata.Animal");
	}
	
	private static void enhanceClass(BloatJ2MEContext context,String path,String name) {
		ClassEditor cled = context.loadClass(path,name);
		ClassEnhancer classEnhancer = new ClassEnhancer(context, cled);
		classEnhancer.generate();
		cled.commit();
	}
}
