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
package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;
import com.db4o.internal.references.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class TransactionTestCase extends AbstractDb4oTestCase implements OptOutMultiSession{
    
    private static final int TEST_ID = 5;

    public void testRemoveReferenceSystemOnClose(){
        LocalObjectContainer container = (LocalObjectContainer) db();
        ReferenceSystem referenceSystem = container.createReferenceSystem();
        Transaction transaction = container.newTransaction(container.systemTransaction(), referenceSystem, false);
        
        referenceSystem.addNewReference(new ObjectReference(TEST_ID));
        referenceSystem.addNewReference(new ObjectReference(TEST_ID + 1));
        
        container.referenceSystemRegistry().removeId(TEST_ID);
        Assert.isNull(referenceSystem.referenceForId(TEST_ID));
        
        transaction.close(false);
        
        container.referenceSystemRegistry().removeId(TEST_ID + 1);
        Assert.isNotNull(referenceSystem.referenceForId(TEST_ID + 1));
    }

}
