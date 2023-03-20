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
import com.db4o.ext.*;
import com.db4o.query.*;

public class TwoClients extends AllTestsConfAll{
	
	public void test(){
		if(Test.clientServer){
			Test.deleteAllInstances(new Atom());
			Test.commit();

            ExtObjectContainer client1 = Test.objectContainer();
			ExtObjectContainer client2 = Test.openClient();
            Atom a_1_1 = new Atom("One");
            Atom a_1_2 = new Atom("Two");
            Atom a_1_3 = new Atom("Three");
            client1.store(a_1_1);
            client1.store(a_1_2);
            client1.store(a_1_3);
            ensureAtomCount(client2,null, 0);
            Test.commitSync(client1, client2);
            ensureAtomCount(client2,null, 3);
            Atom a_2_1 = (Atom)client2.queryByExample(new Atom("One")).next();
            a_1_1.child = new Atom("OneChild");
            client1.store(a_1_1);
            ensureAtomCount(client2,null, 3);
            Test.commitSync(client1, client2);
            ensureAtomCount(client2,null, 4);
            client2.deactivate(a_2_1, Integer.MAX_VALUE);
            client2.activate(a_2_1, Integer.MAX_VALUE);
            Test.ensure(a_2_1.child.name.equals("OneChild"));
            a_2_1.name = "Zulu";
            client2.store(a_2_1);
            
            Atom a_1_4 = new Atom("Zorro");
            client1.store(a_1_4);
            Atom a_1_5 = new Atom("Zzerk");
            client1.store(a_1_5);
            
            ensureAtomCount(client1, "Zulu", 0);
            
            Test.commitSync(client2, client1);
            
            ensureAtomCount(client1, "Zulu", 1);

            
            Query q = client1.query();
            q.constrain(Atom.class);
            q.descend("name").constrain("Zulu");
            ObjectSet os = q.execute();
            Atom q_1_1 = (Atom)os.next();
            
            Test.ensure(a_1_1 == q_1_1);
            a_1_1.name = "Bozo";
            client1.store(a_1_1);
            a_1_1.child.name = "BozoChild";
            client1.store(a_1_1.child);
            a_1_4.name = "Bozo";
            client1.store(a_1_4);
            a_1_5.name = "Cue";
            client1.store(a_1_5);
            
            client2.refresh(a_2_1, Integer.MAX_VALUE);
            Test.ensure(a_2_1.name.equals("Zulu"));
            Test.ensure(a_2_1.child.name.equals("OneChild"));
            ensureAtomCount(client2, "Bozo", 0);
            
            Test.commitSync(client1, client2);
			
            client2.refresh(a_2_1, Integer.MAX_VALUE);
            Test.ensure(a_2_1.name.equals("Bozo"));
            Test.ensure(a_2_1.child.name.equals("BozoChild"));
            ensureAtomCount(client2, "Bozo", 2);
            ensureAtomCount(client2, "Cue", 1);
            ensureAtomCount(client2, "BozoChild", 1);
            
            client2.close();
		}
	}

	private void ensureAtomCount(ObjectContainer con, String name, int count){
		
		// try five times
		// commit timing might cause delay to see result
		for (int i = 0; i < 5; i++) {
		    Query q = con.query();
		    q.constrain(Atom.class);
		    if(name != null){
		        q.descend("name").constrain(name);
		    }
			if(q.execute().size() == count){
			    Test.assertionCount ++;
				return;
			}
        }
        Test.error();
	}
	
	
	
	
	
}
