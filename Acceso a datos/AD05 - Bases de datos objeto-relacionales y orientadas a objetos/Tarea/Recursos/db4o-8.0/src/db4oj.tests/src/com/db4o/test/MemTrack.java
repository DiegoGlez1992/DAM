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

public class MemTrack {
    
    static String bigString;
    static int counter;
    
    public void configure(){
        if(bigString == null){
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < 10000; i ++){
                sb.append(i);
            }
            bigString = sb.toString();
        }
    }
    
    
	
	public void test(){
		Test.deleteAllInstances(Atom.class);
		Test.store(new Atom(bigString));
	}

}

