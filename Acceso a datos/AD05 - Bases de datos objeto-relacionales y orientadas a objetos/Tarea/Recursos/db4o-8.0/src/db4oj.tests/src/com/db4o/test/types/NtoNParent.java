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

public class NtoNParent extends RTest
{
	public NtoNChild[] children;

	public void set(int ver){
		children = new NtoNChild[3];
		for(int i =0; i < 3; i++){
			children[i] = new NtoNChild();
			children[i].parents = new NtoNParent[2];
			children[i].parents[0] = this;
			children[i].parents[1] = new NtoNParent();
			children[i].parents[1].children = new NtoNChild[1];
			children[i].parents[1].children[0] = children[i];
			children[i].name = "ver" + ver + "child" + i;
		}
	}
}
