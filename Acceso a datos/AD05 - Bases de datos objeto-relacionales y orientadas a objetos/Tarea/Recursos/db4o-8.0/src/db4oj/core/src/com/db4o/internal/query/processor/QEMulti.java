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
package com.db4o.internal.query.processor;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class QEMulti extends QE{
	
	@decaf.Public
    private Collection4 i_evaluators = new Collection4();
	
	// used by .net LINQ tests
	public Iterable4 evaluators() {
		return i_evaluators;
	}
	
	QE add(QE evaluator){
		i_evaluators.ensure(evaluator);
		return this;
	}
	
	public boolean identity(){
		boolean ret = false;
		Iterator4 i = i_evaluators.iterator();
		while(i.moveNext()){
			if(((QE)i.current()).identity()){
				ret = true;
			}else{
				return false;
			}
		}
		return ret;
	}
    
    boolean isDefault(){
        return false;
    }
	
	boolean evaluate(QConObject a_constraint, QCandidate a_candidate, Object a_value){
		Iterator4 i = i_evaluators.iterator();
		while(i.moveNext()){
			if(((QE)i.current()).evaluate(a_constraint, a_candidate, a_value)){
				return true;
			}
		}
		return false;
	}
	
	public void indexBitMap(boolean[] bits){
	    Iterator4 i = i_evaluators.iterator();
	    while(i.moveNext()){
	        ((QE)i.current()).indexBitMap(bits);
	    }
	}
	
	public boolean supportsIndex(){
	    Iterator4 i = i_evaluators.iterator();
	    while(i.moveNext()){
	        if(! ((QE)i.current()).supportsIndex()){
	            return false;
	        }
	    }
	    return true;
	}
	
	
	
}

