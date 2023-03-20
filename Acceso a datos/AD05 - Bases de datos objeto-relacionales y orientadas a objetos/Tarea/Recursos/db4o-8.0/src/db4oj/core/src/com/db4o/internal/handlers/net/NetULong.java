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
package com.db4o.internal.handlers.net;

import java.math.*;

import com.db4o.reflect.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class NetULong extends NetSimpleTypeHandler{
    
    private static final BigInteger ZERO = new BigInteger("0", 16); //$NON-NLS-1$
    
	private final static BigInteger FACTOR=new BigInteger("100",16); //$NON-NLS-1$
	
	public NetULong(Reflector reflector) {
		super(reflector, 23, 8);
	}
	
	public String toString(byte[] bytes) {
		BigInteger val=ZERO;
		for (int i = 0; i < 8; i++){
			val=val.multiply(FACTOR);
			val=val.add(new BigInteger(String.valueOf(bytes[i] & 0xff),10));
		}
		return val.toString(10);
	}
}
