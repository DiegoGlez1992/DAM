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
package com.db4o.test.reflect;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.*;
import com.db4o.test.*;
import com.db4o.test.Test;

import db4ounit.extensions.util.*;

// TODO: Works for solo mode only currently
/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class GRHierarchy {
	public static abstract class A {
		private int id;

		public A(int id) {
			this.id = id;
		}
	}

	public static abstract class B {
		private String name;
		
		public B(String name) {
			this.name = name;
		}
	}

	public static class A1 extends A {
		private Character ch;

		public A1(int id, Character ch) {
			super(id);
			this.ch = ch;
		}
	}

	public static class B1 extends B {
		private A a;

		public B1(String name, A a) {
			super(name);
			this.a = a;
		}
	}

	public void store() {
		if(Test.clientServer) {
			return;
		}
		A a = new A1(42, new Character('x'));
		B b = new B1("test", a);
		Db4o.configure().reflectWith(new JdkReflector(getClass().getClassLoader()));
		com.db4o.test.Test.reOpenServer();
		com.db4o.test.Test.reOpen();
		com.db4o.test.Test.store(b);
	}

	public void test() {
		if(Test.clientServer) {
			return;
		}
		Collection4 excluded=new Collection4();
		excluded.add(A.class.getName());
		excluded.add(B.class.getName());
		excluded.add(A1.class.getName());
		excluded.add(B1.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(getClass().getClassLoader(),excluded);
		Db4o.configure().reflectWith(new JdkReflector(loader));
		com.db4o.test.Test.reOpenServer();
		com.db4o.test.Test.reOpen();
		
		com.db4o.test.Test.objectContainer().storedClasses();
		
		GenericReflector reflector = com.db4o.test.Test.objectContainer().ext().reflector();
		ReflectClass proto=reflector.forName(B.class.getName());
		
		ReflectClass protoSuper = proto.getSuperclass();
		Test.ensure(protoSuper!=null);
		Test.ensureEquals(Object.class.getName(), protoSuper.getName());
		
		Query query=com.db4o.test.Test.query();
		query.constrain(proto);
		ObjectSet result=query.execute();
		com.db4o.test.Test.ensureEquals(1,result.size());
		Object obj=result.next();
		com.db4o.test.Test.ensure(obj instanceof GenericObject);
		
		ReflectClass clazz=reflector.forObject(obj);
		com.db4o.test.Test.ensure(clazz instanceof GenericClass);
		com.db4o.test.Test.ensureEquals(B1.class.getName(),clazz.getName());
		ReflectClass superclazz=clazz.getSuperclass();
		com.db4o.test.Test.ensure(superclazz instanceof GenericClass);
		com.db4o.test.Test.ensureEquals(B.class.getName(),superclazz.getName());
		
		ReflectField[] subfields=clazz.getDeclaredFields();
		com.db4o.test.Test.ensureEquals(1,subfields.length);
		com.db4o.test.Test.ensureEquals("a",subfields[0].getName());
		ReflectClass fieldtype=reflector.forName(A.class.getName());
		com.db4o.test.Test.ensureEquals(fieldtype,subfields[0].getFieldType());
		Object subfieldvalue=subfields[0].get(obj);
		com.db4o.test.Test.ensure(subfieldvalue instanceof GenericObject);		
		ReflectClass concretetype=reflector.forObject(subfieldvalue);
		com.db4o.test.Test.ensure(concretetype instanceof GenericClass);		
		com.db4o.test.Test.ensureEquals(A1.class.getName(),concretetype.getName());				
		
		ReflectField[] superfields=superclazz.getDeclaredFields();
		com.db4o.test.Test.ensureEquals(1,superfields.length);
		com.db4o.test.Test.ensureEquals("name",superfields[0].getName());
		fieldtype=reflector.forName(String.class.getName());
		com.db4o.test.Test.ensureEquals(fieldtype,superfields[0].getFieldType());
		Object superfieldvalue=superfields[0].get(obj);
		com.db4o.test.Test.ensureEquals("test",superfieldvalue);		
		
		Db4o.configure().reflectWith(new JdkReflector(getClass().getClassLoader()));
	}
	
	public static void main(String[] args) {
		AllTests.run(GRHierarchy.class);
	}
}
