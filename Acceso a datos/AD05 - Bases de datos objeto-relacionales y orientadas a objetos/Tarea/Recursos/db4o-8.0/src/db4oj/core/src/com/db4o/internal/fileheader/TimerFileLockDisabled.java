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
package com.db4o.internal.fileheader;

import com.db4o.ext.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class TimerFileLockDisabled  extends TimerFileLock{
    
    public void checkHeaderLock() {
    }

    public void checkOpenTime() {
    }

    public void close() {
    }
    
    public boolean lockFile() {
        return false;
    }

    public long openTime() {
        return 0;
    }

    public void run() {
    }

    public void setAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset) {
    }

    public void start() {
    }

    public void writeHeaderLock(){
    }

    public void writeOpenTime() {
    }

	public void checkIfOtherSessionAlive(LocalObjectContainer container, int address, int offset,
		long lastAccessTime) throws Db4oIOException {		
	}


    
}
