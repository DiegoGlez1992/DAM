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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * 
 */

public class StringFieldIndexTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {

    public static class FieldIndexItem {
        
        public FieldIndexItem(String foo) {
            _foo = foo;
        }
        
        public String _foo;
        
        public String getFoo() {
            return _foo;
        }
    }
    
    public static class ExpectedVisitor implements Visitor4 {

        public String[] _values;
        
        public int _position;
        
        public ExpectedVisitor(int length) {
            _values = new String[length];
            _position = 0;
        }
        
        public void visit(Object obj) {
            _values[_position++] = (String) obj;
        }
        
        public String[] getValues() {
            return _values;
        }
        
    }
    
    private static String[] _fooValues = {"Andrew", "Richard"}; 
    
    public static void main(String[] args) {
        new StringFieldIndexTestCase().runSolo();
    }
    
    protected void configure(Configuration config) {
        indexField(config, FieldIndexItem.class, "_foo"); //$NON-NLS-1$
    }
    
    protected void store() throws Exception {
        for (int i = 0; i < _fooValues.length; i++) {
            FieldIndexItem item = new FieldIndexItem(_fooValues[i]);
            store(item);
        }
    }
    
    public void testTraverseValues() {
        StoredField field = storedField();
        ExpectedVisitor visitor = new ExpectedVisitor(2);
        field.traverseValues(visitor);
        for (int i = 0; i < _fooValues.length; i++) {
            Assert.areEqual(_fooValues[i], visitor.getValues()[i]);
        }
    }

    private StoredField storedField() {
        return classMetadataFor(FieldIndexItem.class).fieldMetadataForName("_foo");
    }
}
