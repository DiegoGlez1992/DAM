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

import com.db4o.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public class SystemInfoTestCase extends Db4oTestWithTempFile implements OptOutNoFileSystemData {

	private ObjectContainer _db;
	
    public static class Item {
        
    }
    
    public static void main(String[] arguments) {
        new ConsoleTestRunner(SystemInfoTestCase.class).run();
    }

    public void setUp() throws Exception {
    	_db = Db4oEmbedded.openFile(newConfiguration(), tempFile());
    }
    
    @Override
    public void tearDown() throws Exception {
    	close();
    	super.tearDown();
    }

	private void close() {
		if (_db != null) {
			_db.close();
			_db = null;
		}
	}
    
    public void testDefaultFreespaceInfo(){
        assertFreespaceInfo(fileSession().systemInfo());
    }
    
    private LocalObjectContainer fileSession() {
		return (LocalObjectContainer) db();
	}

	private ExtObjectContainer db() {
		return _db.ext();
	}

	private void assertFreespaceInfo(SystemInfo info){
        Assert.isNotNull(info);
        Item item = new Item();
        db().store(item);
        db().commit();
        db().delete(item);
        db().commit();
        Assert.isTrue(info.freespaceEntryCount() > 0);
        Assert.isTrue(info.freespaceSize() > 0);
    }

    public void testTotalSize(){
        long actual = db().systemInfo().totalSize();
        close();
            
		long expectedSize = File4.size(tempFile());
        Assert.areEqual(expectedSize, actual);
    }
}
