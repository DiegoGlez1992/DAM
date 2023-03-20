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
import com.db4o.io.*;
import com.db4o.query.*;


public class DeleteDeep {
    
    public String name;
    
    public DeleteDeep child;
    
    public void storeOne(){
        addNodes(10);
        name = "root";
    }
    
    private void addNodes(int count){
        if(count > 0){
            child = new DeleteDeep();
            child.name = "" + count;
            child.addNodes(count -1);
        }
    }
    
    public void test(){
        ObjectContainer objectContainer = Test.objectContainer();
        Query q = objectContainer.query();
        q.constrain(DeleteDeep.class);
        q.descend("name").constrain("root");
        DeleteDeep root = (DeleteDeep)q.execute().next();
        objectContainer.activate(root, Integer.MAX_VALUE);
        
        deleteDeep(objectContainer, root);
        
        objectContainer.commit();
        Test.ensureOccurrences(DeleteDeep.class, 0);
    }
    
    private void deleteDeep(ObjectContainer objectContainer, Object obj){
    	Storage storage = new MemoryStorage();
    	EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.file().storage(storage);
        ObjectContainer allToDelete = Db4oEmbedded.openFile(config, "inmemory");
        allToDelete.store(obj);
        ObjectSet objectSet = allToDelete.queryByExample(null);
        while(objectSet.hasNext()){
            objectContainer.delete(objectSet.next());
        }
        allToDelete.close();
    }
    
}
