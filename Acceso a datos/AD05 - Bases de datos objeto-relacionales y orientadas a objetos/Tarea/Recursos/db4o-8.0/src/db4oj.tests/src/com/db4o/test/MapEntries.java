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

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.test.types.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class MapEntries {
	
	static String FILE = "hm.db4o";
	
	HashMap hm;

	public static void main(String[] args) {
		// createAndDelete();
		
		set();
		check();
		LogAll.run(FILE);
		update();
		check();
		LogAll.run(FILE);
	}
	
	static void createAndDelete(){
		new File(FILE).delete();
		ObjectContainer con = Db4o.openFile(FILE);
		HashMap map = new HashMap();
		map.put("delme", new Integer(99));
		con.store(map);
		con.close();
		con = Db4o.openFile(FILE);
		con.delete(con.queryByExample(new HashMap()).next());
		con.close();
		LogAll.run(FILE);
	}
	
	static void check(){
		ObjectContainer con = Db4o.openFile(FILE);
		System.out.println("Entry elements: " + con.queryByExample(new com.db4o.config.Entry()).size());
		con.close();
	}
	
	static void set(){
		new File(FILE).delete();
		ObjectContainer con = Db4o.openFile(FILE);
		MapEntries me = new MapEntries();
		me.hm = new HashMap();
		me.hm.put("t1", new ObjectSimplePublic());
		me.hm.put("t2", new ObjectSimplePublic());
		con.store(me);
		con.close();
	}
	
	static void update(){
		ObjectContainer con = Db4o.openFile(FILE);
		ObjectSet set = con.queryByExample(new MapEntries());
		while(set.hasNext()){
			MapEntries me = (MapEntries)set.next();
			me.hm.put("t1", new Integer(100));
			con.store(me.hm);
		}
		con.close();
	}
	
	
}
