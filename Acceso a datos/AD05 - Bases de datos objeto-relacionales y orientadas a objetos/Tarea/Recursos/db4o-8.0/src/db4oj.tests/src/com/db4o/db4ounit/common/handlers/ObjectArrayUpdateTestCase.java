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
package com.db4o.db4ounit.common.handlers;

import com.db4o.ext.*;

import db4ounit.*;


public class ObjectArrayUpdateTestCase extends HandlerUpdateTestCaseBase {
    
    private static ParentItem[] childData = new ParentItem[]{
        new ChildItem("one"),
        new ChildItem("two"),
        null,
    };
    
    private static ParentItem[] mixedData = new ParentItem[]{
        new ParentItem("one"),
        new ChildItem("two"),
        new ChildItem("three"),
        null,
    };
    
    
    public static class ItemArrays {
        
        public ChildItem[] _typedChildren;
        
        public ParentItem[] _typedChildrenInParentArray;
        
        public Object[] _untypedChildren;
        
        public Object[] _untypedChildrenInParentArray;
        
        public Object _untypedChildrenInObject;
        
        public Object _untypedChildrenInParentArrayInObject;
        
        public ParentItem[] _typedMixed;
        
        public Object[] _untypedMixed;
        
        public Object _untypedMixedInObject;
        
    }
    
    public static class ParentItem {
        
        public String _name;
        
        public ParentItem(String name){
            _name = name;
        }
        
        public boolean equals(Object obj) {
            if(! (obj instanceof ParentItem)){
                return false;
            }
            if(obj instanceof ChildItem){
                return false;
            }
            return hasSameNameAs((ParentItem) obj); 
        }
        
        protected boolean hasSameNameAs(ParentItem other){
            if(_name == null){
                return other._name == null;
            }
            return _name.equals(other._name);
        }
        
    }
    
    public static class ChildItem extends ParentItem {
        
        public ChildItem(String name){
            super(name);
        }
        
        public boolean equals(Object obj) {
            if(! (obj instanceof ChildItem)){
                return false;
            }
            return hasSameNameAs((ParentItem) obj); 
        }
        
    }

    protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        item._typedChildren = castToChildItemArray(childData);
        item._typedChildrenInParentArray = childData;
        item._untypedChildren = castToChildItemArray(childData);
        item._untypedChildrenInParentArray = childData;
        item._untypedChildrenInObject = castToChildItemArray(childData);
        item._untypedChildrenInParentArrayInObject = childData;
        item._typedMixed = mixedData;
        item._untypedMixed = mixedData;
        item._untypedMixedInObject = mixedData;
        return item;
    }

    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays item = (ItemArrays) obj;
        ArrayAssert.areEqual(castToChildItemArray(childData), item._typedChildren);
        ArrayAssert.areEqual(childData, item._typedChildrenInParentArray);
        ArrayAssert.areEqual(castToChildItemArray(childData), item._untypedChildren);
        ArrayAssert.areEqual(childData, item._untypedChildrenInParentArray);
        ArrayAssert.areEqual(castToChildItemArray(childData), (Object[]) item._untypedChildrenInObject);
        ArrayAssert.areEqual(childData, (Object[]) item._untypedChildrenInParentArrayInObject);
        ArrayAssert.areEqual(mixedData, item._typedMixed);
        ArrayAssert.areEqual(mixedData, item._untypedMixed);
        ArrayAssert.areEqual(mixedData, (Object[]) item._untypedMixedInObject);
    }
    
    private ChildItem[] castToChildItemArray(ParentItem[] array){
        ChildItem[] res = new ChildItem[array.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = (ChildItem) array[i];
        }
        return res;
    }

    protected Object[] createValues() {
        // not used
        return null;
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        // not used
    }

    protected String typeName() {
        return "object-array";
    }

}
