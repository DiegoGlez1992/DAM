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

/**
 * 
 */
public class SameSizeOnReopen {
	
	public void test(){
		if(! Test.canCheckFileSize()){
			return;
		}
        Db4o.configure().freespace().discardSmallerThan(1);
        Test.close();
        Test.open();
		for (int i = 0; i < 30; i++) {
			Test.close();
			int fileLength = Test.fileLength();
			Test.open();
			Test.commit();
			Test.close();
			int newFileLength = Test.fileLength();
			Test.open();
			Test.ensure(fileLength == newFileLength);
		}
        Db4o.configure().freespace().discardSmallerThan(0);
	}

}
