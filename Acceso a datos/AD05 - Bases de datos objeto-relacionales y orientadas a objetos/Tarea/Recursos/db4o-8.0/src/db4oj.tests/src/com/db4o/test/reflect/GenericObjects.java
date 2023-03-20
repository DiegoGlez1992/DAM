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

import java.util.*;

import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class GenericObjects extends Test {

	private GenericReflector _reflector;
    private final GenericClass _objectIClass;

    private GenericClass _iClass;
	
	public GenericObjects() throws ClassNotFoundException {
        _reflector = new GenericReflector(null, new JdkReflector(Thread.currentThread().getContextClassLoader()));
        _reflector.configuration(new MockReflectorConfiguration());
        _objectIClass = (GenericClass)_reflector.forClass(Object.class);
	}

	public void test() throws ClassNotFoundException {		
		_reflector.register(acmeDataClass());
        _iClass = (GenericClass)_reflector.forName("com.acme.Person");
        _assert(_iClass.getName().equals("com.acme.Person"));
        _assert(_iClass.getSuperclass() == _objectIClass);
        
        _assert(_iClass.isAssignableFrom(subclass()));
        _assert(!_iClass.isAssignableFrom(otherDataClass()));
        _assert(!_iClass.isAssignableFrom(_objectIClass));
    
        _assert(_iClass.isInstance(_iClass.newInstance()));
        _assert(_iClass.isInstance(subclass().newInstance()));
        _assert(!_iClass.isInstance(otherDataClass().newInstance()));
        _assert(!_iClass.isInstance("whatever")); 
        
        _assert(_reflector.forObject(_iClass.newInstance()) == _iClass);
        
        tstFields();
		tstReflectionDelegation();
		

	}

	private void tstReflectionDelegation() throws ClassNotFoundException {
		Reflection test = new Reflection(new GenericReflector(null, new JdkReflector(Thread.currentThread().getContextClassLoader())));
		test.tstEverything();
	}

    private GenericClass otherDataClass() {
        return new GenericClass(_reflector, null, "anyName", _objectIClass);
    }

    private GenericClass subclass() {
        return new GenericClass(_reflector, null, "anyName", _iClass);
    }

    private void tstFields() {
        ReflectField surname = _iClass.getDeclaredField("surname");
        ReflectField birthdate = _iClass.getDeclaredField("birthdate");
        ReflectField[] fields = _iClass.getDeclaredFields();
        _assert(fields.length == 3);
        _assert(fields[0] == surname);
        _assert(fields[1] == birthdate);
        
        Object person = _iClass.newInstance();
        _assert(birthdate.get(person) == null);
        surname.set(person, "Cleese");
        _assert(surname.get(person).equals("Cleese"));
    }

    private GenericClass acmeDataClass() {
        GenericClass result = new GenericClass(_reflector, null, "com.acme.Person", _objectIClass);
        result.initFields(fields(result));
        return result;
    }

    private GenericField[] fields(ReflectClass personClass) {
        return new GenericField[] {
                new GenericField("surname", _reflector.forClass(String.class), false),
                new GenericField("birthdate", _reflector.forClass(Date.class), false),
                new GenericField("bestFriend", personClass, false)
        };
    }

}
