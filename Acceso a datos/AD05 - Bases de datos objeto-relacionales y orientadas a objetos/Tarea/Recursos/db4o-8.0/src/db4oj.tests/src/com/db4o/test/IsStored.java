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

public class IsStored {
	
	String myString;
	
	public void test(){
		
		ObjectContainer con = Test.objectContainer();
		Test.deleteAllInstances(this);
		
		IsStored isStored = new IsStored();
		isStored.myString = "isStored";
		con.store(isStored);
		Test.ensure( con.ext().isStored(isStored) );
		Test.ensure( Test.occurrences(this) == 1 );
		con.delete(isStored);
		Test.ensure(! con.ext().isStored(isStored));
		Test.ensure( Test.occurrences(this) == 0 );
		con.commit();
		if(con.ext().isStored(isStored)){
			
			// this will fail in CS due to locally cached references
			if(! Test.clientServer){
				Test.error();
			}
			
		}
		Test.ensure( Test.occurrences(this) == 0 );
		con.store(isStored);
		Test.ensure( con.ext().isStored(isStored) );
		Test.ensure( Test.occurrences(this) == 1 );
		con.commit();
		Test.ensure( con.ext().isStored(isStored) );
		Test.ensure( Test.occurrences(this) == 1 );
		con.delete(isStored);
		Test.ensure( ! con.ext().isStored(isStored));
		Test.ensure( Test.occurrences(this) == 0 );
		con.commit();
		if(con.ext().isStored(isStored)){
			
			// this will fail in CS due to locally cached references
			if(! Test.clientServer){
				Test.error();
			}
		}
		Test.ensure( Test.occurrences(this) == 0 );
	}
	
}
