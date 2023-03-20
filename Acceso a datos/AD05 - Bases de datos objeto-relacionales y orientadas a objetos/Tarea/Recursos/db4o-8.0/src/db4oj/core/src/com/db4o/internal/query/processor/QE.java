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
import com.db4o.internal.*;
import com.db4o.types.*;


/**
 * Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
 * 
 * @exclude
 */
public class QE implements Unversioned {	
	
	static final QE DEFAULT = new QE();
	
	public static final int NULLS = 0;
	public static final int SMALLER = 1;
	public static final int EQUAL = 2;
	public static final int GREATER = 3;
	
	QE add(QE evaluator){
		return evaluator;
	}
    
	public boolean identity(){
		return false;
	}

    boolean isDefault(){
        return true;
    }

	boolean evaluate(QConObject constraint, QCandidate candidate, Object obj){
        PreparedComparison prepareComparison = constraint.prepareComparison(candidate);
        if (obj == null) {
            return prepareComparison instanceof Null;
        }
        if(prepareComparison instanceof PreparedArrayContainsComparison){
        	return ((PreparedArrayContainsComparison)prepareComparison).IsEqual(obj);
        }
        return prepareComparison.compareTo(obj) == 0;
	}
	
	public boolean equals(Object obj){
		return obj!=null&&obj.getClass() == this.getClass();
	}
	
	public int hashCode() {
		return getClass().hashCode();
	}
	
	// overridden in QENot 
	boolean not(boolean res){
		return res;
	}
	
	/**
	 * Specifies which part of the index to take.
	 * Array elements:
	 * [0] - smaller
	 * [1] - equal
	 * [2] - greater
	 * [3] - nulls
	 * 
	 * 
	 * @param bits
	 */
	public void indexBitMap(boolean[] bits){
	    bits[QE.EQUAL] = true;
	}
	
	public boolean supportsIndex(){
	    return true;
	}
	
}
