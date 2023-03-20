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
package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public abstract class AbstractFreespaceManager implements FreespaceManager {
	
    
    public static final byte FM_DEBUG = 127;
    public static final byte FM_DEFAULT = 0;
    public static final byte FM_LEGACY_RAM = 1;
    public static final byte FM_RAM = 2;
    public static final byte FM_IX = 3;
    public static final byte FM_BTREE = 4;
    
    private static final int INTS_IN_SLOT = 12;
    public static final int REMAINDER_SIZE_LIMIT = 20;
    
    public static byte checkType(byte systemType){
        if(systemType == FM_DEFAULT){
            return FM_RAM;    
        }
        return systemType;
    }
    
    protected Procedure4<Slot> _slotFreedCallback;
    
    private final int _discardLimit;
	private final int _remainderSizeLimit;
    
    public AbstractFreespaceManager(Procedure4<Slot> slotFreedCallback, int discardLimit, int remainderSizeLimit){
    	_slotFreedCallback = slotFreedCallback;
    	_discardLimit = discardLimit;
		_remainderSizeLimit = remainderSizeLimit;
    }
    
    public static AbstractFreespaceManager createNew(LocalObjectContainer file){
        return createNew(file, file.systemData().freespaceSystem());
    }
    
    public static AbstractFreespaceManager createNew(final LocalObjectContainer file, byte systemType){
        systemType = checkType(systemType);
        int unblockedDiscardLimit = file.configImpl().discardFreeSpace();
        int blockedDiscardLimit = unblockedDiscardLimit == Integer.MAX_VALUE ? 
        		unblockedDiscardLimit :
        		file.blockConverter().bytesToBlocks(unblockedDiscardLimit);
        int remainderSizeLimit = file.blockConverter().bytesToBlocks(REMAINDER_SIZE_LIMIT);
        Procedure4<Slot> slotFreedCallback = new Procedure4<Slot>() {
			public void apply(Slot slot) {
				file.overwriteDeletedBlockedSlot(slot);	
			}
		}; 
        switch(systemType){
        	case FM_IX:
        		return new FreespaceManagerIx(blockedDiscardLimit, remainderSizeLimit);
        	case FM_BTREE:
        		return new BTreeFreespaceManager(file, slotFreedCallback, blockedDiscardLimit, remainderSizeLimit);
            default:
                return new InMemoryFreespaceManager(slotFreedCallback, blockedDiscardLimit, remainderSizeLimit);
        }
    }
    
    public static int initSlot(LocalObjectContainer file){
        int address = file.allocateSlot(slotLength()).address();
        slotEntryToZeroes(file, address);
        return address;
    }
    
    public void migrateTo(final FreespaceManager fm) {
    	traverse(new Visitor4() {
			public void visit(Object obj) {
				fm.free((Slot) obj);
			}
		});
    }
    
    static void slotEntryToZeroes(LocalObjectContainer file, int address){
        StatefulBuffer writer = new StatefulBuffer(file.systemTransaction(), address, slotLength());
        for (int i = 0; i < INTS_IN_SLOT; i++) {
            writer.writeInt(0);
        }
        if (Debug4.xbytes) {
            writer.checkXBytes(false);
        }
        writer.writeEncrypt();
    }
    
    
    final static int slotLength(){
        return Const4.INT_LENGTH * INTS_IN_SLOT;
    }
    
    public int totalFreespace() {
        final IntByRef mint = new IntByRef();
        traverse(new Visitor4() {
            public void visit(Object obj) {
                Slot slot = (Slot) obj;
                mint.value += slot.length();
            }
        });
        return mint.value;
    }
    
	protected int discardLimit() {
		return _discardLimit;
	}
	
	protected final boolean splitRemainder(int length){
		if(canDiscard(length)){
			return false;
		}
		return length > _remainderSizeLimit;
	}
    
    final boolean canDiscard(int length) {
		return length == 0 || length < discardLimit();
	}
    
    public static void migrate(FreespaceManager oldFM, FreespaceManager newFM) {
    	oldFM.migrateTo(newFM);
    	oldFM.freeSelf();
    }
    
    public void debugCheckIntegrity(){
        final IntByRef lastStart = new IntByRef();
        final IntByRef lastEnd = new IntByRef();
        traverse(new Visitor4() {
            public void visit(Object obj) {
                Slot slot = (Slot) obj;
                if(slot.address() <= lastEnd.value){
                    throw new IllegalStateException();
                }
                lastStart.value = slot.address();
                lastEnd.value = slot.address() + slot.length();
            }
        });
        
    }
    
	public static boolean migrationRequired(byte systemType) {
		return systemType == FM_LEGACY_RAM  || systemType == FM_IX ;
	}
	
    public void slotFreed(Slot slot) {
    	if(_slotFreedCallback == null){
    		return;
    	}
    	_slotFreedCallback.apply(slot);
	}
    
}
