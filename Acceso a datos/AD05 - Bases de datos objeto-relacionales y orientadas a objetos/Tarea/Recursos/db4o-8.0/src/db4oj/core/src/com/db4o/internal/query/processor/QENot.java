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

/**
 * @exclude
 */
public class QENot extends QE{
	
	@decaf.Public
    private QE i_evaluator;
    
    public QENot(){
        // CS
    }
	
	QENot(QE a_evaluator){
		i_evaluator = a_evaluator;
	}
	
	QE add(QE evaluator){
		if(! (evaluator instanceof QENot)){
			i_evaluator = i_evaluator.add(evaluator);
		}
		return this;
	}
	
	public QE evaluator() {
		return i_evaluator;
	}
	
	public boolean identity(){
		return i_evaluator.identity();
	}
    
    boolean isDefault(){
        return false;
    }
	
	boolean evaluate(QConObject a_constraint,  QCandidate a_candidate, Object a_value){
		return ! i_evaluator.evaluate(a_constraint, a_candidate, a_value);
	}
	
	boolean not(boolean res){
		return ! res;
	}
	
	public void indexBitMap(boolean[] bits){
	    i_evaluator.indexBitMap(bits);
	    for (int i = 0; i < 4; i++) {
            bits[i] = ! bits[i];
        }
	}
	
	public boolean supportsIndex(){
	    return i_evaluator.supportsIndex();
	}
}

