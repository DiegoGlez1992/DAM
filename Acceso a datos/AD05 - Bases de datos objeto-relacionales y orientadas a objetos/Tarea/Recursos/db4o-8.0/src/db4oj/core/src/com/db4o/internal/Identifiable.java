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

import com.db4o.*;

/**
 * @exclude
 */
public abstract class Identifiable {
	
    protected int _id;

    protected int _state = 2; // DIRTY and ACTIVE

    public final boolean beginProcessing() {
        if (bitIsTrue(Const4.PROCESSING)) {
            return false;
        }
        bitTrue(Const4.PROCESSING);
        return true;
    }

    final void bitFalse(int bitPos) {
        _state &= ~(1 << bitPos);
    }
    
    final boolean bitIsFalse(int bitPos) {
        return (_state | (1 << bitPos)) != _state;
    }

    final boolean bitIsTrue(int bitPos) {
        return (_state | (1 << bitPos)) == _state;
    }

    final void bitTrue(int bitPos) {
        _state |= (1 << bitPos);
    }

    public void endProcessing() {
        bitFalse(Const4.PROCESSING);
    }
    
    public int getID() {
        return _id;
    }

    public final boolean isActive() {
        return bitIsTrue(Const4.ACTIVE);
    }

    public boolean isDirty() {
        return bitIsTrue(Const4.ACTIVE) && (!bitIsTrue(Const4.CLEAN));
    }
    
    public final boolean isNew(){
        return getID() == 0;
    }

    public void setID(int id) {
    	if(DTrace.enabled){
    		DTrace.PERSISTENTBASE_SET_ID.log(id);
    	}
        _id = id;
    }

    public final void setStateClean() {
        bitTrue(Const4.ACTIVE);
        bitTrue(Const4.CLEAN);
    }

    public final void setStateDeactivated() {
        bitFalse(Const4.ACTIVE);
    }

    public void setStateDirty() {
        bitTrue(Const4.ACTIVE);
        bitFalse(Const4.CLEAN);
    }

    public int hashCode() {
    	if(isNew()){
    		throw new IllegalStateException();
    	}
    	return getID();
    }

}
