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
import com.db4o.query.*;


/**
 */
@decaf.Ignore
public class CountAnnotatedSheep {
	public final static int NUMSHEEP=10;
	
	public void configure() {
		Db4o.configure().activationDepth(0);
		Db4o.configure().updateDepth(0);
	}
	
	public void store() {
		Sheep parent=null;
		SheepNotAnnotated noParent=null;
		for(int i=0;i<10;i++) {
			Sheep sheep=new Sheep(String.valueOf(i+1),parent);
			SheepNotAnnotated noSheep=new SheepNotAnnotated(sheep.getName(),noParent);
			Test.store(sheep);
			Test.store(noSheep);
			parent=sheep;
			noParent=noSheep;
		}
	}
	
	public void testRead() {
		Test.objectContainer().purge();
		Sheep curSheep = (Sheep) fetch(Sheep.class,String.valueOf(NUMSHEEP));
		int sheepCount=1;
		while(curSheep.parent!=null) {
			Test.ensure(curSheep.constructorCalled());
			curSheep=curSheep.parent;
			sheepCount++;
		}
		Test.ensureEquals(NUMSHEEP,sheepCount);

		SheepNotAnnotated curNoSheep = (SheepNotAnnotated) fetch(SheepNotAnnotated.class,String.valueOf(NUMSHEEP));
		Test.ensure(!curNoSheep.constructorCalled());
		Test.ensure(curNoSheep.parent==null);
	}

	public void testUpdate() {
		Test.objectContainer().purge();
		Sheep curSheep = (Sheep) fetch(Sheep.class,String.valueOf(NUMSHEEP));
		String oldName=curSheep.getName();
		curSheep.setName(oldName+"X");
		Test.store(curSheep);
		SheepNotAnnotated curNoSheep = (SheepNotAnnotated) fetch(SheepNotAnnotated.class,String.valueOf(NUMSHEEP));
		Test.objectContainer().ext().activate(curNoSheep,1);
		String oldNoName=curNoSheep.getName();
		curNoSheep.setName(oldNoName+"X");
		Test.store(curNoSheep);
		Test.commit();
		Test.reOpen();
		fetch(Sheep.class,oldName+"X");
		// FIXME: problem with configuration or rather with global update depth of 0?
		// fetch(SheepNotAnnotated.class,oldNoName);
	}

	private Object fetch(Class clazz,String name) {
		Query noSheepQuery=Test.query();
		noSheepQuery.constrain(clazz);
		noSheepQuery.descend("name").constrain(name);
		ObjectSet noSheep=noSheepQuery.execute();
		Test.ensureEquals(1,noSheep.size());
		return noSheep.next();
	}

}
