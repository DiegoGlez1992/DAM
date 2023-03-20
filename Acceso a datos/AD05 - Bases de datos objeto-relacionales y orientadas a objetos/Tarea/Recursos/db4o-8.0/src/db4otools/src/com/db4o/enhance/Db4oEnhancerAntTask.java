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
package com.db4o.enhance;

import com.db4o.instrumentation.ant.*;
import com.db4o.nativequery.main.*;
import com.db4o.ta.instrumentation.ant.*;


/**
 * Ant task to enhance class files for db4o.
 */
public class Db4oEnhancerAntTask extends Db4oFileEnhancerAntTask {
    
    private boolean _nq = true;
	private boolean _ta = true;
	private boolean _collections = true;

	public Db4oEnhancerAntTask(){
    }

	/**
	 * @param nq true if native query optimization instrumentation should take place, false otherwise
	 */
    public void setNq(boolean nq) {
    	_nq = nq;
    }
    
	/**
	 * @param ta true if transparent activation/persistence instrumentation should take place, false otherwise
	 */
    public void setTa(boolean ta) {
    	_ta = ta;
    }

	/**
	 * @param collections true if native collections should be instrumented for transparent activation/persistence, false otherwise
	 */
    public void setCollections(boolean collections) {
    	_collections = collections;
    }
    
    public void execute() {
    	if(_nq) {
            add(new NQAntClassEditFactory());
    	}
    	if(_ta) {
            add(new TAAntClassEditFactory(_collections));
    	}
    	super.execute();
    }
}
