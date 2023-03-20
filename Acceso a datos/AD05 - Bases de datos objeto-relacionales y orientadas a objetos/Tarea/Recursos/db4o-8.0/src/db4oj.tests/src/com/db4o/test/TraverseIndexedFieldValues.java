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
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;


public class TraverseIndexedFieldValues {
    
    private static final int COUNT = 30;
    
    public Atom _atom;
    
    public String _string;
    
    public int  _int;
    
    
    public TraverseIndexedFieldValues(){
        
    }
    
    public TraverseIndexedFieldValues(Atom atom, String str, int anint){
        _atom = atom;
        _string = str;
        _int = anint;
    }
    
    public void configure(){
        ObjectClass objectClass = Db4o.configure().objectClass(getClass());
        objectClass.objectField("_atom").indexed(true);
        objectClass.objectField("_string").indexed(true);
        objectClass.objectField("_int").indexed(true);
    }
    
    public void store(){
        for (int i = 0; i < COUNT; i++) {
            String str = "" + i;
            Test.store(new TraverseIndexedFieldValues(new Atom(str), str, i));
        }
    }
    
    public void test(){
        final ExtObjectContainer oc = Test.objectContainer();
        StoredClass sc = oc.storedClass(this);
        
        StoredField atomField = sc.storedField("_atom", null);
        StoredField stringField = sc.storedField("_string", null);
        StoredField intField = sc.storedField("_int", null);
        
        final Collection4 fieldValues = new Collection4(); 
        
        atomField.traverseValues(new Visitor4() {
            public void visit(Object obj) {
                oc.activate(obj, 1);
                Test.ensure(! fieldValues.contains(obj));
                Test.ensure(obj instanceof Atom);
                fieldValues.add(obj);
            }
        });
        
        Test.ensure(fieldValues.size() == COUNT);
        
        
        fieldValues.clear();
        
        stringField.traverseValues(new Visitor4() {
            public void visit(Object obj) {
                Test.ensure(! fieldValues.contains(obj));
                Test.ensure(obj instanceof String);
                fieldValues.add(obj);
            }
        });
        
        
        Test.ensure(fieldValues.size() == COUNT);
        
        fieldValues.clear();
        
        intField.traverseValues(new Visitor4() {
            public void visit(Object obj) {
                Test.ensure(! fieldValues.contains(obj));
                Test.ensure(obj instanceof Integer);
                fieldValues.add(obj);
            }
        });
        
        Test.ensure(fieldValues.size() == COUNT);
    }
    
    

}
