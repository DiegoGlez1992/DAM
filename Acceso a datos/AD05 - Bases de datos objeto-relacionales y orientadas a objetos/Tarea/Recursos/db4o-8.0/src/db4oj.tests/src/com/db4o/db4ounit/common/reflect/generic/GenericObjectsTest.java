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
package com.db4o.db4ounit.common.reflect.generic;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class GenericObjectsTest extends AbstractDb4oTestCase {
	private String PERSON_CLASSNAME = "com.acme.Person";

	public static void main(String[] args) {
		new GenericObjectsTest().runAll();
	}

	public void testCreate() throws Exception {

		initGenericObjects();
		// fixture().reopen();
		ExtObjectContainer oc = fixture().db();
		// now check to see if person was saved
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		ObjectSet results = q.execute();
		Assert.isTrue(results.size() == 1);
		//Db4oUtil.dumpResults(fixture().db(), results);

	}

	private Object initGenericObjects() {
		GenericClass personClass = initGenericClass();
		ReflectField surname = personClass.getDeclaredField("surname");
		ReflectField birthdate = personClass.getDeclaredField("birthdate");
		//ReflectField nArray = personClass.getDeclaredField("nArray");
		Object person = personClass.newInstance();
		surname.set(person, "John");
//		/int[][] arrayData = new int[2][2];
		// todo: FIXME: nArray doesn't work
		// nArray.set(person, arrayData);
		birthdate.set(person, new Date());
		fixture().db().store(person);
		fixture().db().commit();
		return person;
	}

	/**
	 * todo: Move the GenericClass creation into a utility/factory class.
	 * @return
	 */
	public GenericClass initGenericClass() {
		GenericReflector reflector = new GenericReflector(
				null,
				Platform4.reflectorForType(GenericObjectsTest.class));
		GenericClass _objectIClass = (GenericClass) reflector
				.forClass(Object.class);
		GenericClass result = new GenericClass(reflector, null,
				PERSON_CLASSNAME, _objectIClass);
		result.initFields(fields(result, reflector));
		return result;
	}

	private GenericField[] fields(GenericClass personClass,
			GenericReflector reflector) {
		return new GenericField[] {
				new GenericField("surname", reflector.forClass(String.class),
						false),
				new GenericField("birthdate", reflector.forClass(Date.class),
						false),
				new GenericField("bestFriend", personClass, false),
				new GenericField("nArray", reflector.forClass(int[][].class),
						true) };
	}

	public void testUpdate() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		//Db4oUtil.dump(oc);
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		ObjectSet results = q.execute();
		//Db4oUtil.dumpResults(oc, results);
		Assert.isTrue(results.size() == 1);

	}

	public void testQuery() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		// now query to make sure there are none left
		Query q = oc.query();
		q.constrain(rc);
		q.descend("surname").constrain("John");
		ObjectSet results = q.execute();
		Assert.isTrue(results.size() == 1);
	}

	public void testDelete() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		ObjectSet results = q.execute();
		while (results.hasNext()) {
			Object o = results.next();
			oc.delete(o);
		}
		oc.commit();

		// now query to make sure there are none left
		q = oc.query();
		q.constrain(rc);
		q.descend("surname").constrain("John");
		results = q.execute();
		Assert.isTrue(results.size() == 0);
	}

	private ReflectClass getReflectClass(ExtObjectContainer oc, String className) {
		return oc.reflector().forName(className);
	}

	/**
	 * This is to ensure that reflector.forObject(GenericArray) returns an instance of GenericArrayClass instead of GenericClass
	 * http://tracker.db4o.com/jira/browse/COR-376
	 */
	public void testGenericArrayClass() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass elementType = oc.reflector().forName(PERSON_CLASSNAME);

		Object array = reflector().array().newInstance(elementType, 5);

		ReflectClass arrayClass = oc.reflector().forObject(array);
		Assert.isTrue(arrayClass.isArray());
		Assert.isTrue(arrayClass instanceof GenericArrayClass);

		arrayClass = oc.reflector().forName(array.getClass().getName());
		Assert.isTrue(arrayClass.isArray());
		Assert.isTrue(arrayClass instanceof GenericArrayClass);

		arrayClass = oc.reflector().forClass(array.getClass());
		Assert.isTrue(arrayClass.isArray());
		Assert.isTrue(arrayClass instanceof GenericArrayClass);
		
		Assert.areEqual(arrayClass.getName(), ReflectPlatform.fullyQualifiedName(array.getClass()));
	
		Assert.areEqual("(GA) " + elementType.getName(), array.toString()); 
	}
}
