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
 * This class will start a dedicated server for AllTests to run
 * tests on different machines. The server will run the configuration
 * entries also as needed.
 */
public class AllTestsServer extends AllTests implements Runnable{
	
	public static void main(String[] args){
		new AllTestsServer().run();
	}
	
	public void run(){
		Db4o.configure().messageLevel(-1);
		logConfiguration();
		System.out.println("Waiting for tests to be run from different machine.");
		System.out.println("\n\nThe server will need to be closed with CTRL + C.\n\n");
		if(DELETE_FILE){
			Test.delete();
		}
		configure();
		Test.runServer = true;
		Test.clientServer = true;
		Test.open();
	}
}
