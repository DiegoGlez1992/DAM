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
package com.db4o;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public final class Debug4 {
    
    /**
     * indexes all fields
     */
    public static final boolean indexAllFields = false;
    
    /**
     * prints query graph information to the console
     */
    public static final boolean queries = false;
    
    /**
     * allows faking the Db4oDatabase identity object, so the first
     * stored object in the debugger is the actually persisted object
     * 
     * Changing this setting to true will fail some tests that expect 
     * database files to have identity
     */
    public static final boolean staticIdentity = queries;

    /**
     * prints more stack traces
     */
    public static final boolean atHome = false;

    /**
     * makes C/S timeouts longer, so C/S does not time out in the debugger
     */
    public static final boolean longTimeOuts = false;

    /**
     * turns freespace debuggin on 
     */
    public static final boolean freespace = Deploy.debug;
    
    /**
     * fills deleted slots with 'X' and overrides any configured
     * freespace filler
     */
    public static final boolean xbytes = freespace;
    
    /**
     * checks monitor conditions to make sure only the thread
     * with the global monitor is allowed entry to the core
     */
    public static final boolean checkSychronization = false;
    
    /**
     * makes sure a configuration entry is generated for each persistent
     * class 
     */
    public static final boolean configureAllClasses = indexAllFields;
    
    /**
     * makes sure a configuration entry is generated for each persistent
     * field
     */
    public static final boolean configureAllFields = indexAllFields;
    
    /**
     * allows turning weak references off
     */
    public static final boolean weakReferences = true;

    /**
     * prints all communicated messages to the console
     */
    public static final boolean messages = false;

    /**
     * allows turning NIO off on Java
     */
    public static final boolean nio = true;
    
    /**
     * allows overriding the file locking mechanism to turn it off
     */
    public static final boolean lockFile = true;
    
    public static void expect(boolean cond){
        if(! cond){
            throw new RuntimeException("Should never happen");
        }
    }
    
    public static void ensureLock(Object obj) {
        if (atHome) {
            try {
                obj.wait(1);
            } catch (IllegalMonitorStateException imse) {
                System.err.println("No Lock Alarm.");
                imse.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean exceedsMaximumBlockSize(int a_length) {
        if (a_length > Const4.MAXIMUM_BLOCK_SIZE) {
            if (atHome) {
                System.err.println("Maximum block size  exceeded!!!");
                new Exception().printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    public static boolean exceedsMaximumArrayEntries(int a_entries, boolean a_primitive){
        if (a_entries > (a_primitive ? Const4.MAXIMUM_ARRAY_ENTRIES_PRIMITIVE : Const4.MAXIMUM_ARRAY_ENTRIES)) {
            if (atHome) {
                System.err.println("Maximum array elements exceeded!!!");
                new Exception().printStackTrace();
            }
            return true;
        }
        return false;
    }

	public static final void readBegin(ReadBuffer buffer, byte identifier) {
		if (Deploy.debug) {
			if (Deploy.brackets) {
				if (buffer.readByte() != Const4.YAPBEGIN) {
					throw new Db4oException("Debug.readBegin() YAPBEGIN expected.");
				}
			}
			if (Deploy.identifiers) {
				byte readB = buffer.readByte();
				if (readB != identifier) {
					throw new Db4oException("Debug.readBegin() wrong identifier: "+(char)readB);
				}
			}
		}
		
	}
	
    public static final void readEnd(ReadBuffer buffer) {
        if (Deploy.debug && Deploy.brackets) {
            if (buffer.readByte() != Const4.YAPEND) {
                throw new RuntimeException("Debug.readEnd() YAPEND expected");
            }
        }
    }
    
    public static final void writeBegin(WriteBuffer buffer, byte identifier) {
        if (Deploy.debug) {
            if(buffer instanceof MarshallingContext){
                ByteArrayBuffer prepend = new ByteArrayBuffer(2);
                if (Deploy.brackets) {
                    prepend.writeByte(Const4.YAPBEGIN);
                }
                if (Deploy.identifiers) {
                    prepend.writeByte(identifier);
                }
                ((MarshallingContext)buffer).debugPrependNextWrite(prepend);
                return;
            }
            if (Deploy.brackets) {
                buffer.writeByte(Const4.YAPBEGIN);
            }
            if (Deploy.identifiers) {
                buffer.writeByte(identifier);
            }
        }
    }

    public static final void writeEnd(WriteBuffer buffer) {
        if (Deploy.debug && Deploy.brackets) {
            if(buffer instanceof MarshallingContext){
                ((MarshallingContext)buffer).debugWriteEnd(Const4.YAPEND);
                return;
            }
            buffer.writeByte(Const4.YAPEND);
        }
    }
    
    private Debug4() {
    }
}
