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
package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.query.*;

/**
 * required for CsSchemaUpdateTestCase
 */
public class CsSchemaMigrationSourceCode {
	
	public static class Item {
		
		//update
		//assert
		/*public String _name;
		
		public String toString() {
			return "Item " + _name;
		}
		*/

	}
	
	private static final String FILE = System.getProperty("java.io.tmpdir", ".") + File.separator + "csmig.db4o";
	private static final int PORT = 4447;

	public static void main(String[] arguments) throws IOException {
		new CsSchemaMigrationSourceCode().run();
	}
	
	public void run(){
		
		//store
		/*new File(FILE).delete();*/
		
		ServerConfiguration conf = Db4oClientServer.newServerConfiguration();
		ObjectServer server = Db4oClientServer.openServer(conf, FILE, PORT);
		server.grantAccess("db4o", "db4o");
		
		//store
		/*storeItem();*/
		
		//update
		/*updateItem();*/
		
		//assert
		/*assertItem();*/
		
		server.close();
		//assert
		/*new File(FILE).delete();*/
		
	}

	private void storeItem() {
		ObjectContainer client = openClient();
		Item item = new Item();
		client.store(item);
		client.close();
		//store
		/*System.err.println("Item stored");*/
	}
	
	private void updateItem() {
		ObjectContainer client = openClient();
		Query query = client.query();
		query.constrain(Item.class);
		Item item = (Item) query.execute().next();
		//update
		//assert
		/*item._name = "IsNamedOK";*/
		client.store(item);
		client.close();
	}

	private ObjectContainer openClient() {
		return Db4oClientServer.openClient("localhost", PORT, "db4o", "db4o");
	}
	
	private void assertItem() {
		ObjectContainer client = openClient();
		Query query = client.query();
		query.constrain(Item.class);
		Item item = (Item) query.execute().next();
		System.out.println(item);
		client.close();
	}

}

