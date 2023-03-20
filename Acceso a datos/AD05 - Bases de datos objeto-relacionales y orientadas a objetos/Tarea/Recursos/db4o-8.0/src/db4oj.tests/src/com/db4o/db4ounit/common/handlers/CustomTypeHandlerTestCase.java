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

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class CustomTypeHandlerTestCase extends AbstractDb4oTestCase{
    
    private static final int[] DATA = new int[] {1, 2};

    public static void main(String[] arguments) {
        new CustomTypeHandlerTestCase().runSolo();
    }
    
    private final class CustomItemTypeHandler implements ReferenceTypeHandler, CascadingTypeHandler, VariableLengthTypeHandler {

        public PreparedComparison prepareComparison(Context context, Object obj) {
            return new PreparedComparison() {
                public int compareTo(Object obj) {
                    return 0;
                }
            };
        }

    	public void write(WriteContext context, Object obj) {
            Item item = (Item)obj;
            if(item.numbers == null){
                context.writeInt(-1);
                return;
            }
            context.writeInt(item.numbers.length);
            for (int i = 0; i < item.numbers.length; i++) {
                context.writeInt(item.numbers[i]);
            }
        }

        public void activate(ReferenceActivationContext context) {
            Item item = (Item)((UnmarshallingContext) context).persistentObject();
            int elementCount = context.readInt();
            if(elementCount == -1){
                return;
            }
            item.numbers = new int[elementCount];
            for (int i = 0; i < item.numbers.length; i++) {
                item.numbers[i] = context.readInt();
            }
        }

        public void delete(DeleteContext context) throws Db4oIOException {
      
        }

        public void defragment(DefragmentContext context) {
      
        }

		public void cascadeActivation(ActivationContext context) {
		}

		public void collectIDs(QueryingReadContext context) {
		}

		public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
			return null;
		}
    }
    
    
    private final class CustomItemGrandChildTypeHandler implements ReferenceTypeHandler, CascadingTypeHandler, VariableLengthTypeHandler {

        public PreparedComparison prepareComparison(Context context, Object obj) {
            return new PreparedComparison() {
                public int compareTo(Object obj) {
                    return 0;
                }
            };
        }

    	public void write(WriteContext context, Object obj) {
            ItemGrandChild item = (ItemGrandChild)obj;
            context.writeInt(item.age);
            context.writeInt(100);
        }

        public void activate(ReferenceActivationContext context) {
            ItemGrandChild item = (ItemGrandChild)((ReferenceActivationContext) context).persistentObject();
            item.age = context.readInt();
            int check = context.readInt();
            if(check != 100){
                throw new IllegalStateException();
            }
        }


        public void delete(DeleteContext context) throws Db4oIOException {
      
        }

        public void defragment(DefragmentContext context) {
      
        }

		public void cascadeActivation(ActivationContext context) {
			// TODO Auto-generated method stub
			
		}

		public void collectIDs(QueryingReadContext context) {
			// TODO Auto-generated method stub
			
		}

		public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
			// TODO Auto-generated method stub
			return null;
		}
    }


    public static class Item {
        
        public int[] numbers;

        public Item(int[] numbers_) {
            numbers = numbers_;
        }
        
        public boolean equals(Object obj){
            if( ! (obj instanceof Item)){
                return false;
            }
            return areEqual(numbers, ((Item)obj).numbers); 
        }
        
        private boolean areEqual(int[] expected, int[] actual){
            if ( expected == null){
                return actual == null;
            }
            if(actual == null){
                return false;
            }
            if(expected.length != actual.length){
                return false;
            }
            for (int i = 0; i < expected.length; i++) {
                if(expected[i] != actual[i]){
                    return false;
                }
            }
            return true;
            
        }
        
    }
    
    public static class ItemChild extends Item {
        
        public String name;

        public ItemChild(String name_, int[] numbers_) {
            super(numbers_);
            name = name_;
        }
        
        public boolean equals(Object obj){
            if(! (obj instanceof ItemChild)){
                return false;
            }
            ItemChild other = (ItemChild) obj;
            if(name == null){
                if(other.name != null){
                    return false;
                }
                return super.equals(obj);
            }
            if(! name.equals(other.name)){
                return false;
            }
            return super.equals(obj);
        }
    }
    
    public static class ItemGrandChild extends ItemChild {
        
        public int age;

        public ItemGrandChild(int age_, String name_, int[] numbers_) {
            super(name_, numbers_);
            age = age_;
        }
        
        public boolean equals(Object obj){
            if(! (obj instanceof ItemGrandChild)){
                return false;
            }
            ItemGrandChild other = (ItemGrandChild) obj;
            if(age != other.age){
                return false;
            }
            return super.equals(obj);
        }
        
    }
    
    protected void configure(Configuration config) throws Exception {
        registerTypeHandler(config, Item.class, new CustomItemTypeHandler());
        registerTypeHandler(config, ItemGrandChild.class, new CustomItemGrandChildTypeHandler());
    }

    private void registerTypeHandler(Configuration config, Class clazz,
        TypeHandler4 typeHandler) {
        GenericReflector reflector = ((Config4Impl)config).reflector();
        final ReflectClass itemClass = reflector.forClass(clazz);
        TypeHandlerPredicate predicate = new TypeHandlerPredicate() {
            public boolean match(ReflectClass classReflector) {
                return itemClass.equals(classReflector);
            }
        };
        config.registerTypeHandler(predicate, typeHandler);
    }
    
    protected void store() throws Exception {
        store(storedItem());
        store(storedItemChild());
        store(storedItemGrandChild());
    }
    
    public void testRetrieveOnlyInstance(){
        Assert.areEqual(storedItem(), retrieveItemOfClass(Item.class));
    }
    
    public void testChildClass(){
        Assert.areEqual(storedItemChild(), retrieveItemOfClass(ItemChild.class));
    }
    
    public void testGrandChildClass(){
        Assert.areEqual(storedItemGrandChild(), retrieveItemOfClass(ItemGrandChild.class));
    }
    
    public void testStoredFields(){
    	StoredClass storedClass = db().storedClass(Item.class);
    	StoredField[] storedFields = storedClass.getStoredFields();
    	Assert.areEqual(0, storedFields.length);
    }
    
    private Item retrieveItemOfClass(Class class1) {
        Query q = newQuery(class1);
        Item retrievedItem = (Item) q.execute().next();
        return retrievedItem;
    }
    
    private Item storedItem(){
        return new Item(DATA);
    }
    
    private Item storedItemChild(){
        return new ItemChild("child", DATA);
    }
    
    private Item storedItemGrandChild(){
        return new ItemGrandChild(25, "child", DATA);
    }
    
    ReflectClass itemClass(){
        return reflector().forClass(Item.class);
    }

}
