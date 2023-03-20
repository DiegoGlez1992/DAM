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
package com.db4o.db4ounit.common.qlin;

import static com.db4o.qlin.QLinSupport.*;

import java.util.*;


/**
 * @sharpen.if !SILVERLIGHT
 */
@decaf.Remove(decaf.Platform.JDK11)
public class Closures extends BasicQLinTestCase {
	
	public static class Closure {
	
	}
	
	public void with(Object obj, Object obj2){
		
	}
	
	public List<Cat> listOf(Cat...cats){
		return null;
	}
	
	public void closureSample(){
		// List<Cat> occamAndZora = occamAndZora();
			
	final Cat cat = prototype(Cat.class);
	
	List<Cat> cats = listOf(new Cat("Zora"), new Cat("Wolke"));
	with(cats, new Closure(){{ cat.feed(); }});
            
            
//            
//            Iterable<Cat> query = occamAndZora();
//            
//            with(db().from(Cat.class).select()).feed();
//        
//        
//            query = occamAndZora();
//            
//            Iterable<Color> colors = map(db().from(Cat.class).select(), cat.color());
//        


		
//		final Cat cat = prototype(Cat.class);
//		List<Cat> occamAndZora = occamAndZora();
//		with(occamAndZora, new Closure { cat.feed() } );
		
		
	}

	
//	   public <T> T with(Iterable<T> withOn){
//	        // magic goes here
//	        return null;
//	    }
//
//	    public <T,TResult> Iterable<TResult> map(Iterable<T> withOn,TResult projection ){
//	        // magic goe here
//	        return null;
//	    }

	

	
	
	
	private void with(List<Cat> occamAndZora, Object closure) {
		
	}


}
