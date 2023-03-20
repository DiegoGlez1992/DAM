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
package com.db4o.test.types;

import com.db4o.internal.*;
import com.db4o.test.*;

public class MasterMonster extends RTest
{
	public Object[] ooo;

	public void set(int ver){
		Object[] classes = allClassesButThis();
		ooo = new Object[classes.length];
		for(int i = 0;i < classes.length; i++){
			try{
				RTestable test = (RTestable)classes[i];
				if(Platform4.canSetAccessible() || !test.jdk2() ){
					ooo[i] = test.newInstance();
					test.set(ooo[i], ver);
				}
			}catch (Exception e){
				throw new RuntimeException("MasterMonster instantiation failed.");
			}
		}
	}

	Object[] allClassesButThis(){
		Object[] all = Regression.allClasses();
		Object[] classes = new Object[all.length - 1];
		System.arraycopy(all,0,classes,0,all.length - 1);
		return classes;
	}
}
