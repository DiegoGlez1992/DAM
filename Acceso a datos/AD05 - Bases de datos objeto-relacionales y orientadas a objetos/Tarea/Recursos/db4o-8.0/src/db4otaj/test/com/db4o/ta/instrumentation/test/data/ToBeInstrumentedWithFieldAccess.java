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
package com.db4o.ta.instrumentation.test.data;

public class ToBeInstrumentedWithFieldAccess {

	public int _externallyAccessibleInt;
	
	private int _int;
	
	private int[] _intArray;
	
	private char _char;
	
	private double _double;
	
	private float _float;
	
	private long _long;
	
	private byte _byte;
	
	private volatile byte _volatileByte;
	
	private transient Object _transientField;

	public boolean compareID(ToBeInstrumentedWithFieldAccess other) {
		return _int == other._int;
	}
	
	public void setInt(int value) {
		_int = value;
	}
	
	public void setChar(char value) {
		_char = value;
	}
	
	public void setByte(byte value) {
		_byte = value;
	}
	
	public void setVolatileByte(byte value) {
		_volatileByte = value;
	}
	
	public void setLong(long value) {
		_long = value;
	}
	
	public void setFloat(float value) {
		_float = value;
	}
	
	public void setDouble(double value) {
		_double = value;
	}
	
	public void setIntArray(int[] value) {
		_intArray = value;
	}
	
	public int setDoubledAndGetInt(int value) {
		_int = value*2; // arbitrarily long expressions
		return _int;
	}
	
	public void wontBeInstrumented() {
		_transientField = null;
	}
}
