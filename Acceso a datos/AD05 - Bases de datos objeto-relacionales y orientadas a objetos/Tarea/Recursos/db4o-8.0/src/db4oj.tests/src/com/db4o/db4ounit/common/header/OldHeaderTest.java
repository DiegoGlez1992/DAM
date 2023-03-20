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
package com.db4o.db4ounit.common.header;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class OldHeaderTest implements TestLifeCycle, OptOutNoFileSystemData, OptOutWorkspaceIssue {
    
    public void test() throws IOException {
    	final String originalFilePath = originalFilePath();
    	final String dbFilePath = dbFilePath();
    	if(! File4.exists(originalFilePath)){
    		TestPlatform.emitWarning(originalFilePath + " does not exist. Can not run " + getClass().getName());
    		return;
    	}
        
    	File4.copy(originalFilePath, dbFilePath);
        
    	Db4o.configure().allowVersionUpdates(true);    	
    	Db4o.configure().exceptionsOnNotStorable(false);    	
        ObjectContainer oc = Db4o.openFile(dbFilePath);
        try {
        	Assert.isNotNull(oc);
        } finally {
        	oc.close();
        	Db4o.configure().exceptionsOnNotStorable(true);    	
        	Db4o.configure().allowVersionUpdates(false);
        }
    }
    
    private static String originalFilePath() {
    	return WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_5.5.2");
    }

    private static String dbFilePath() {
    	return WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_5.5.2.db4o");
    }

	public void setUp() throws Exception {
		
	}

	public void tearDown() throws Exception {
		String tempTestFilePath = dbFilePath();
		if (File4.exists(tempTestFilePath)) {
			File4.delete(tempTestFilePath);
		}
	}
}
