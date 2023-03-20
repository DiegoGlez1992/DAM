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

import com.db4o.query.*;

public class FileSizeOnReopen {
    
    public String foo;
    
    public void storeOne(){
        foo = "foo";
    }
    
    public void testOne(){
        if(! Test.canCheckFileSize()){
        	return;
        }
        queryForSingleItem();
        for (int i = 0; i < 5; i++) {
            tLength();
        }
    }
    
    public void tLength(){
        int fileLength = Test.fileLength();
        Test.reOpen();
        FileSizeOnReopen fsor = queryForSingleItem();
        Test.ensure(fsor.foo.equals("foo"));
        Test.reOpen();
        Test.ensureEquals(fileLength,Test.fileLength());
    }

	private FileSizeOnReopen queryForSingleItem() {
		Query q = Test.query();
        q.constrain(this.getClass());
        FileSizeOnReopen fsor =  (FileSizeOnReopen)q.execute().next();
		return fsor;
	}
}
