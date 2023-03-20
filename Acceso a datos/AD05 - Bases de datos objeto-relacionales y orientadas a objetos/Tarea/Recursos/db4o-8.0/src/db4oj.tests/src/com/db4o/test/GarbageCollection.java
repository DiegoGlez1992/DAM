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
import com.db4o.test.types.*;

public class GarbageCollection
{
	public static void main(String[] args){
		final String fileName = "tgc.db4o";
		new java.io.File(fileName).delete();

		int strSize = 1;
		int objectCount = 10000;
		ObjectContainer con = Db4o.openFile(fileName);
		String longString = "String";
		ObjectSimplePublic osp = null;
		ArrayTypedPublic atp = null;
		for(int i = 0; i < strSize; i++){
			longString = longString + longString;
		}
		int toGetTen = objectCount / 10;
		for(int i = 0; i < objectCount; i++){

			/*
			osp = new ObjectSimplePublic(longString);
			con.set(osp);
			con.deactivate(osp, 5);
			*/

			atp = new ArrayTypedPublic();
			atp.set(1);
			con.store(atp);

			if( (((double)i / toGetTen) - (i / toGetTen)) < 0.000001){
				con.commit();
				con.ext().purge();
			    mem();
			}
			
		}
		con.commit();
		con.ext().purge();
		longString = null;
		osp = null;
		mem();
		mem();
		con.close();
	}

     static void mem() {
     	System.runFinalization();
         Runtime r = Runtime.getRuntime();
         r.gc();
		 r.runFinalization();
		 r.gc();
         System.out.println(r.totalMemory() - r.freeMemory());
     }

}
