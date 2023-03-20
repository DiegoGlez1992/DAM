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
package com.db4o.internal;


/**
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
class JDK_1_3 extends JDK_1_2{
	
	@decaf.Remove(decaf.Platform.JDK11)
	public final static class Factory implements JDKFactory {
		public JDK tryToCreate() {
	    	if(Reflection4.getMethod("java.lang.Runtime","addShutdownHook",
	            new Class[] { Thread.class }) == null){
	      		return null;
	      	}
	      	return new JDK_1_3();
		}
	}

	Thread addShutdownHook(Runnable runnable){
		Thread thread = new Thread(runnable, "Shutdown Hook");
	    Reflection4.invoke(Runtime.getRuntime(), "addShutdownHook", new Object[]{thread});
		return thread;
	}
	
	void removeShutdownHook(Thread thread){
	    Reflection4.invoke(Runtime.getRuntime(), "removeShutdownHook", new Object[]{thread});
	}
	
	public int ver(){
	    return 3;
	}
	
}
