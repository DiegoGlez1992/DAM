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


public class InterfaceHandlerUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    public static interface ItemInterface {
    }
    
    public static class ItemContainer {
        
        ItemInterface _item;
        
        ItemInterface[] _items;
        
        Object[] _objects;
        
        Object _object;
        
        public static ItemContainer createNew(){
            ItemContainer itemContainer = new ItemContainer();
            itemContainer._item = storedItem();
            itemContainer._items = newItemInterfaceArray();
            itemContainer._objects = newItemInterfaceArray();
            itemContainer._object = newItemInterfaceArray();
            return itemContainer;
        }

        private static ItemInterface[] newItemInterfaceArray() {
            return new ItemInterface[]{ storedItem() };
        }
    }
    
    public static class Item implements ItemInterface {
        
        public String _name;
        
        public Item(String name) {
            _name = name;
        }
        
        public boolean equals(Object obj) {
            if(! (obj instanceof Item)){
                return false;
            }
            return _name.equals(((Item)obj)._name);
        }
        
        public String toString() {
            return "Item " + _name;
        }
    }
    
    protected Object[] createValues() {
        return new Object[]{
            ItemContainer.createNew()
        };
    }

    protected Object createArrays() {
        return ItemContainer.createNew();
    }

    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        if(db4oMajorVersion() == 4){
            return;
        }
        assertItemInterfaceArrays(storedItemName(), obj);
    }

    private void assertItemInterfaceArrays(String name, Object itemContainerObject) {
        ItemContainer itemContainer = (ItemContainer) itemContainerObject;
        assertItemInterfaceArray(name, itemContainer._items);
        assertItemInterfaceArray(name, itemContainer._objects);
        assertItemInterfaceArray(name, (Object[]) itemContainer._object);
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        if(db4oMajorVersion() == 4){
            return;
        }
        assertItem(storedItemName(), itemFromValues(values));
    }
    
    protected void updateValues(Object[] values) {
        if(db4oMajorVersion() == 4){
            return;
        }
        updateItem(itemFromValues(values));
    }

    private void updateItem(Item item) {
        item._name = updatedItemName();
    }

    private String updatedItemName() {
        return "updated";
    }
    
    protected void assertUpdatedValues(Object[] values) {
        if(db4oMajorVersion() == 4){
            return;
        }
        assertItem(updatedItemName(), itemFromValues(values));
    }
    
    protected void updateArrays(Object obj) {
        if(db4oMajorVersion() == 4){
            return;
        }
        ItemContainer itemContainer = (ItemContainer) obj;
        updateItemInterfaceArray(itemContainer._items);
        updateItemInterfaceArray(itemContainer._objects);
        updateItemInterfaceArray((Object[]) itemContainer._object);
    }
    
    protected void assertUpdatedArrays(Object obj) {
        if(db4oMajorVersion() == 4){
            return;
        }
        assertItemInterfaceArrays(updatedItemName(), obj);
    }

    private Item itemFromValues(Object[] values) {
        ItemContainer itemContainer = (ItemContainer) values[0];
        ItemInterface item = itemContainer._item;
        return (Item) item;
    }

    private void assertItem(String name, Object item) {
        Assert.isInstanceOf(Item.class, item);
        Assert.areEqual(name, ((Item)item)._name);
    }

    private void assertItemInterfaceArray(String itemName, Object[] items) {
        assertItem(itemName, items[0]);
    }
    
    private void updateItemInterfaceArray(Object[] items) {
        updateItem((Item) items[0]);
    }

    protected String typeName() {
        return "interface";
    }
    
    public static Item storedItem() {
        return new Item(storedItemName());
    }

    private static String storedItemName() {
        return "stored";
    }
    

    

}
