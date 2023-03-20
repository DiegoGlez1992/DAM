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

import com.db4o.cs.*;
import com.db4o.ext.*;

public class Semaphores extends AllTestsConfAll{
	
	public void test(){
		
		ExtObjectContainer eoc = Test.objectContainer();
		eoc.setSemaphore("SEM", 0);
		
		Test.ensure(eoc.setSemaphore("SEM", 0) == true);
		
		if(Test.clientServer){
			ExtObjectContainer client2 = null;
			try {
				client2 =
					Db4oClientServer.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
				Test.ensure(client2.setSemaphore("SEM", 0) == false);
				eoc.releaseSemaphore("SEM");
				Test.ensure(client2.setSemaphore("SEM", 0) == true);
			} catch (Exception e) {
				e.printStackTrace();
				return ;
			}
		}else{
			eoc.releaseSemaphore("SEM");
		}
	}

}
