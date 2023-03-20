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
package com.db4o.db4ounit.common.ta;

import com.db4o.activation.*;
import com.db4o.ta.*;

import db4ounit.*;


public class LinkedArrays implements CanAssertActivationDepth {
    
    public static class Item implements CanAssertActivationDepth{
        
        public String _name;
        
        public LinkedArrays _linkedArrays;
        
        public Item(){
            
        }
        
        public Item(int depth){
            if(depth > 1){
                _name = new Integer(depth).toString();
                _linkedArrays = newLinkedArrays(depth -1);
            }
        }

        public void assertActivationDepth(int depth, boolean transparent) {
            nullAssert(_name, depth);
            nullAssert(_linkedArrays, depth);
            if(depth < 1){
                return;
            }
            recurseAssertActivationDepth(_linkedArrays, depth, transparent);
        }
        
    }
    
    public static class ActivatableItem implements Activatable, CanAssertActivationDepth {
        
        public String _name;
        
        public LinkedArrays _linkedArrays;
        
        public ActivatableItem(){
            
        }
        
        public ActivatableItem(int depth){
            if(depth > 1){
                _name = new Integer(depth).toString();
                _linkedArrays = newLinkedArrays(depth - 1);
            }
        }
        
        private transient Activator _activator;
        
        public void activate(ActivationPurpose purpose) {
            if(_activator != null) {
                _activator.activate(purpose);
            }
        }

        public void bind(Activator activator) {
            _activator = activator;
        }

        public void assertActivationDepth(int depth, boolean transparent) {
            if(transparent){
                Assert.isNull(_name);
                Assert.isNull(_linkedArrays);
                return;
            }
            nullAssert(_name, depth);
            nullAssert(_linkedArrays, depth);
            if(depth < 1){
                return;
            }
            recurseAssertActivationDepth(_linkedArrays,depth, transparent);
        }
        
    }
    

    public boolean _isRoot;
    
    public LinkedArrays _next;
    
    public Object _objectArray;
    
    public Object[] _untypedArray;
    
    public String[] _stringArray;
    
    public int[] _intArray;
    
    public Item[] _itemArray;
    
    public ActivatableItem[] _activatableItemArray;
    
    public LinkedArrays[] _linkedArrays;
    
    
    public static LinkedArrays newLinkedArrayRoot(int depth){
        LinkedArrays root = newLinkedArrays(depth);
        root._isRoot = true;
        return root;
    }
    
    public static LinkedArrays newLinkedArrays(int depth){
        
        if(depth < 1){
            return null;
        }
        
        LinkedArrays la = new LinkedArrays();
        
        depth--;
        
        if(depth < 1){
            return la;
        }
        
        la._next = newLinkedArrays(depth);
        
        la._objectArray = new Object[] {newItem(depth)};
        la._untypedArray = new Object[] {newItem(depth)};
        la._stringArray = new String[] { new Integer(depth).toString()};
        la._intArray = new int[] {depth + 1};
        la._itemArray = new Item[]{newItem(depth)};
        la._activatableItemArray = new ActivatableItem[] { newActivatableItem(depth) };
        la._linkedArrays = new LinkedArrays[] { newLinkedArrays(depth)};
        
        return la;
    }
    
    private static Item newItem(int depth){
        if(depth < 1){
            return null;
        }
        return new Item(depth);
    }
    
    private static ActivatableItem newActivatableItem(int depth){
        if(depth < 1){
            return null;
        }
        return new ActivatableItem(depth);
    }


    public void assertActivationDepth(int depth, boolean transparent) {
        nullAssert(_next, depth);
        nullAssert(_objectArray, depth);
        nullAssert(_untypedArray, depth);
        nullAssert(_stringArray, depth);
        nullAssert(_intArray, depth);
        nullAssert(_itemArray, depth);
        nullAssert(_linkedArrays, depth);
        nullAssert(_activatableItemArray, depth);

        if(depth < 1){
            return;
        }
        
        Assert.isNotNull(_stringArray[0]);
        Assert.isGreater(0, _intArray[0]);
        
        recurseAssertActivationDepth(((Object[])_objectArray)[0], depth, transparent);
        recurseAssertActivationDepth(_untypedArray[0], depth, transparent);
        recurseAssertActivationDepth(_itemArray[0], depth, transparent);
        recurseAssertActivationDepth(_activatableItemArray[0], depth, transparent);
        recurseAssertActivationDepth(_linkedArrays[0], depth, transparent);
        
    }
    
    static void recurseAssertActivationDepth(Object obj, int depth, boolean transparent){
        nullAssert(obj, depth);
        if(obj == null){
            return;
        }
        ((CanAssertActivationDepth)obj).assertActivationDepth(depth-1, transparent);
    }
    
    static void nullAssert(Object obj, int depth){
        if(depth < 1){
            Assert.isNull(obj);
        }else{
            Assert.isNotNull(obj);
        }
    }
    
    
}
