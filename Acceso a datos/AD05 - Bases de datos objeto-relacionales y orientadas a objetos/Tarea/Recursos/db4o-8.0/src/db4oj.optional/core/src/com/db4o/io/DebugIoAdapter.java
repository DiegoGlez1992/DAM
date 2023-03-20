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
package com.db4o.io;

import com.db4o.ext.*;

/**
 * @exclude
 * @deprecated use {@link Storage}-equivalent instead.
 */  
public class DebugIoAdapter extends VanillaIoAdapter{
    
    static int counter;
    
    private static final int[] RANGE_OF_INTEREST = new int[] {0, 20};
    

    public DebugIoAdapter(IoAdapter delegateAdapter){
        super(delegateAdapter);
    }
    
    protected DebugIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
        super(delegateAdapter.open(path, lockFile, initialLength, readOnly));
    }

    public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
        return new DebugIoAdapter(new RandomAccessFileAdapter(),  path, lockFile, initialLength, readOnly);
    }
    
    public void seek(long pos) throws Db4oIOException {
        if(pos >= RANGE_OF_INTEREST[0] && pos <= RANGE_OF_INTEREST[1]){
            counter ++;
            System.out.println("seek: " + pos + "  counter: " + counter);
        }
        super.seek(pos);
    }

}
