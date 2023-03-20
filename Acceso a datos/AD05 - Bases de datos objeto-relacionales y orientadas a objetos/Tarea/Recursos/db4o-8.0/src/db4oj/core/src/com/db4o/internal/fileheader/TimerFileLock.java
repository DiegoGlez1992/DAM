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
public abstract class TimerFileLock implements Runnable{
    
	/**
	 * @sharpen.remove.first
	 */
    public static TimerFileLock forFile(LocalObjectContainer file){
    	
        if(file.needsLockFileThread()){
            return new TimerFileLockEnabled((IoAdaptedObjectContainer)file);
        }
        
        return new TimerFileLockDisabled();
    }

    public abstract void checkHeaderLock();

    public abstract void checkOpenTime();

    public abstract boolean lockFile();

    public abstract long openTime();

    public abstract void setAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset);

    public abstract void start() throws Db4oIOException;

    public abstract void writeHeaderLock();

    public abstract void writeOpenTime();

    public abstract void close() throws Db4oIOException;

    public abstract void checkIfOtherSessionAlive(LocalObjectContainer container, int address,
		int offset, long lastAccessTime) throws Db4oIOException;
}
