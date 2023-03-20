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
package com.db4o.internal.activation;

import com.db4o.internal.*;


public abstract class FixedUpdateDepth implements UpdateDepth {

	private int _depth;
	private boolean _tpMode = false;
	
	public FixedUpdateDepth(int depth) {
		_depth = depth;
	}

	public void tpMode(boolean tpMode) {
		_tpMode = tpMode;
	}
	
	public boolean tpMode() {
		return _tpMode;
	}
	
	public boolean sufficientDepth() {
    	return _depth > 0;
	}

	public boolean negative() {
		// should never happen?
		return _depth < 0;
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + _depth;
	}

	public UpdateDepth adjust(ClassMetadata clazz) {
		if(clazz.cascadesOnDeleteOrUpdate()) {
			return adjustDepthToBorders().descend();
		}
		return descend();
	}
	
	public boolean isBroaderThan(FixedUpdateDepth other) {
		return _depth > other._depth;
	}

	// TODO code duplication in fixed activation/update depth
	public FixedUpdateDepth adjustDepthToBorders() {
		return forDepth(DepthUtil.adjustDepthToBorders(_depth));
	}

    public UpdateDepth adjustUpdateDepthForCascade(boolean isCollection) {
        int minimumUpdateDepth = isCollection ? 2 : 1;
        if (_depth < minimumUpdateDepth) {
            return forDepth(minimumUpdateDepth);
        }
        return this;
    }

    public UpdateDepth descend() {
    	return forDepth(_depth - 1);
    }
    
    @Override
    public boolean equals(Object other) {
    	if(this == other) {
    		return true;
    	}
    	if(other == null || getClass() != other.getClass()) {
    		return false;
    	}
    	return _depth == ((FixedUpdateDepth)other)._depth;
    }

    @Override
    public int hashCode() {
    	return _depth;
    }
    
    protected abstract FixedUpdateDepth forDepth(int depth);
}
