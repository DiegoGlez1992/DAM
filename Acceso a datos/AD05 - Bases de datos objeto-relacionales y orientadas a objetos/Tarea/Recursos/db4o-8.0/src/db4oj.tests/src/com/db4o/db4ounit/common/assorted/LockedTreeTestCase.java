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
package com.db4o.db4ounit.common.assorted;

import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class LockedTreeTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new LockedTreeTestCase().runSolo();
    }
    
    public void testAdd(){
        final LockedTree lockedTree = new LockedTree();
        lockedTree.add(new TreeInt(1));
        Assert.expect(IllegalStateException.class, new CodeBlock() {
            public void run() throws Throwable {
                lockedTree.traverseLocked(new Visitor4() {
                    public void visit(Object obj) {
                        TreeInt treeInt = (TreeInt) obj;
                        if(treeInt._key == 1){
                            lockedTree.add(new TreeInt(2));
                        }
                    }
                });
            }
        });
    }
    
    public void testClear(){
        final LockedTree lockedTree = new LockedTree();
        lockedTree.add(new TreeInt(1));
        Assert.expect(IllegalStateException.class, new CodeBlock() {
            public void run() throws Throwable {
                lockedTree.traverseLocked(new Visitor4() {
                    public void visit(Object obj) {
                        lockedTree.clear();
                    }
                });
            }
        });
    }


}
