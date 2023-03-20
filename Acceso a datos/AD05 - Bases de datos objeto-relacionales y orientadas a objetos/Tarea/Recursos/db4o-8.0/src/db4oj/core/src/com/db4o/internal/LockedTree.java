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
package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class LockedTree {
    
    private Tree _tree;
    
    private int _version;

    public void add(Tree tree) {
        changed();
        _tree = _tree == null ? tree : _tree.add(tree); 
    }

    private void changed() {
        _version++;
    }

    public void clear() {
        changed();
        _tree = null;
    }

    public Tree find(int key) {
        return TreeInt.find(_tree, key);
    }

    public void read(ByteArrayBuffer buffer, Readable template) {
        clear();
        _tree = new TreeReader(buffer, template).read();
        changed();
    }

    public void traverseLocked(Visitor4 visitor) {
        int currentVersion = _version;
        Tree.traverse(_tree, visitor);
        if(_version != currentVersion){
            throw new IllegalStateException();
        }
    }
    
    public void traverseMutable(Visitor4 visitor){
        final Collection4 currentContent = new Collection4();
        traverseLocked(new Visitor4() {
            public void visit(Object obj) {
                currentContent.add(obj);
            }
        });
        Iterator4 i = currentContent.iterator();
        while(i.moveNext()){
            visitor.visit(i.current());
        }
    }
    
    public boolean isEmpty(){
    	return _tree == null;
    }

}
