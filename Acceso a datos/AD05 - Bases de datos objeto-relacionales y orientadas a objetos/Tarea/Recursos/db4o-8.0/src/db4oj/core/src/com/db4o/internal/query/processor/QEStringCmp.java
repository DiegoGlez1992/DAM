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

import com.db4o.internal.*;


/**
 * @exclude
 */
public abstract class QEStringCmp extends QEAbstract {
    
	@decaf.Public
    private boolean caseSensitive;

	/** for C/S messaging only */
	public QEStringCmp() {
	}
	
	public QEStringCmp(boolean caseSensitive_) {
		caseSensitive = caseSensitive_;
	}

	boolean evaluate(QConObject constraint, QCandidate candidate, Object obj){
		if(obj != null){
		    if(obj instanceof ByteArrayBuffer) {
                obj = candidate.readString((ByteArrayBuffer)obj);
		    }
		    String candidateStringValue = obj.toString();
		    String stringConstraint = constraint.getObject().toString();
		    if(!caseSensitive) {
		    	candidateStringValue=candidateStringValue.toLowerCase();
		    	stringConstraint=stringConstraint.toLowerCase();
		    }
			return compareStrings(candidateStringValue,stringConstraint);
		}
		return constraint.getObject()==null;
	}
	
	public boolean supportsIndex(){
	    return false;
	}
	
	protected abstract boolean compareStrings(String candidate,String constraint);
}
