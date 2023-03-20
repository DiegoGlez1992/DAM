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
package com.db4o.foundation;


/**
 * @exclude
 */
public final class BitMap4 {
    
    private final byte[] _bits;
    
    public BitMap4(int numBits){
        _bits = new byte[byteCount(numBits)];
    }

    /** "readFrom  buffer" constructor **/
    public BitMap4(byte[] buffer, int pos, int numBits){
        this(numBits);
        System.arraycopy(buffer, pos, _bits, 0, _bits.length);
    }
    
    public BitMap4(byte singleByte){
    	_bits = new byte[]{singleByte};
    }
    
    public boolean isTrue(int bit) {
        return ((_bits[arrayOffset(bit)]>>>byteOffset(bit))&1)!=0;
    }
    
    public boolean isFalse(int bit) {
        return ! isTrue(bit);
    }

    public int marshalledLength(){
        return _bits.length;
    }
    
    public void setFalse(int bit){
        _bits[arrayOffset(bit)] &= (byte)~bitMask(bit);
    }
    
    public void set(int bit, boolean val){
    	if(val){
    		setTrue(bit);
    	}else{
    		setFalse(bit);
    	}
    }
    
    public void setTrue(int bit){
        _bits[arrayOffset(bit)] |= bitMask(bit);
    }
    
    public void writeTo(byte[] bytes, int pos){
        System.arraycopy(_bits, 0, bytes, pos, _bits.length);
    }
    
	private byte byteOffset(int bit) {
		return (byte)(bit % 8);
	}

	private int arrayOffset(int bit) {
		return bit / 8;
	}
	
	private byte bitMask(int bit) {
		return (byte)(1 << byteOffset(bit));
	}
	
	private int byteCount(int numBits) {
		return (numBits + 7) / 8;
	}

	public byte getByte(int index) {
		return _bits[index];
	}
	
	public byte[] bytes(){
	    return _bits;
	}
	
}
