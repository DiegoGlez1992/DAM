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

import com.db4o.*;
import com.db4o.config.*;

public class TCustomConstructor implements ObjectConstructor
{
	public Object onStore(ObjectContainer con, Object obj){
		CustomConstructor cc = (CustomConstructor)obj;
		String[] strings = new String[2];
		strings[0] = cc.name;
		strings[1] = cc.tname;
		return strings;
	}

	public void onActivate(ObjectContainer con, Object obj, Object members){
		// do nothing. All is done in onInstantiate
	}

	public Object onInstantiate(ObjectContainer container, Object storedObject){
		String[] strings = (String[])storedObject;
		CustomConstructor cc = new CustomConstructor(strings[1]);
		cc.name = strings[0];
		return cc;
	}

	public Class storedClass(){
		return String[].class;
	}
}
