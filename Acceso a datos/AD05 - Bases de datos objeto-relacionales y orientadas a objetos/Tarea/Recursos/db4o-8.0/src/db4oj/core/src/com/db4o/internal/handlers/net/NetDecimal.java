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
 * .NET decimal layout is (bytewise)
 * |M3|M2|M1|M0|M7|M6|M5|M4|M11|M10|M9|M8|S[7]|E[4-0]|X|X|
 * (M=mantissa, E=exponent(negative powers of 10), S=sign, X=unused/unknown)
 * 
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class NetDecimal extends NetSimpleTypeHandler{
	
	private static final BigInteger BYTESHIFT_FACTOR=new BigInteger("100",16); //$NON-NLS-1$
	 
	private static final BigInteger ZERO = new BigInteger("0", 16); //$NON-NLS-1$
    
	private static final BigDecimal TEN = new BigDecimal("10");  //$NON-NLS-1$

	public NetDecimal(Reflector reflector) {
		super(reflector, 21, 16);
	}
	
	public String toString(byte[] bytes) {
		BigInteger mantissa=ZERO;
		for(int blockoffset=8;blockoffset>=0;blockoffset-=4) {
			for(int byteidx=0;byteidx<4;byteidx++) {
				mantissa=mantissa.multiply(BYTESHIFT_FACTOR);
				int idx=blockoffset+byteidx;
				mantissa=mantissa.add(new BigInteger(String.valueOf(bytes[idx]&0xff),10));
			}
		}
		
		// The exponent is stored negative by .NET so we change it back here !!!
		int exponent = - bytes[13]&0x1f;
		
		boolean negative=bytes[12]!=0;
		
		BigDecimal result=new BigDecimal(mantissa);
		
		if(exponent < 0) {
			for (int i = exponent; i < 0; i++) {
				result = result.divide(TEN, BigDecimal.ROUND_HALF_DOWN);
			}
		}else {
			for (int i = 0; i < exponent; i++) {
				result = result.multiply(TEN);
			}
		}
		
		if(negative) {
			result=result.negate();
		}
		return result.toString();
	}
}
