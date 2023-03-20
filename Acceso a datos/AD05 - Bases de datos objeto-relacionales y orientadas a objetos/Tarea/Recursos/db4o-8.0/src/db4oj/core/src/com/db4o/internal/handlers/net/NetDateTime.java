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

import java.util.*;

import com.db4o.reflect.*;

/**
 * @exclude
 * @sharpen.ignore
 */
// TODO: Between .NET and Java there seems to be a difference of two days between era offsets?!?
@decaf.Ignore(decaf.Platform.JDK11)
public class NetDateTime extends NetSimpleTypeHandler{
	private final static String ZEROES="0000"; //$NON-NLS-1$
	
	private final static String[] MONTHS= {
		"Jan", //$NON-NLS-1$
		"Feb", //$NON-NLS-1$
		"Mar", //$NON-NLS-1$
		"Apr", //$NON-NLS-1$
		"May", //$NON-NLS-1$
		"Jun", //$NON-NLS-1$
		"Jul", //$NON-NLS-1$
		"Aug", //$NON-NLS-1$
		"Sep", //$NON-NLS-1$
		"Oct", //$NON-NLS-1$
		"Nov", //$NON-NLS-1$
		"Dec" //$NON-NLS-1$
	};
	
    // ms between 01.01.0001,00:00:00.000 and 01.01.1970,00:00:00.000
	//private static final long ERA_DIFFERENCE_IN_MS = 62135604000000L; // Carl's diff
	private static final long ERA_DIFFERENCE_IN_MS = 62135596800000L; // .net diff	
    //private static final long ERA_DIFFERENCE_IN_MS = 62135769600000L; // java diff
    
    // ratio from .net ticks (100ns) to java ms
    private static final long TICKS_TO_MS_RATIO = 10000;

	public NetDateTime(Reflector reflector) {
		super(reflector, 25, 8);
	}
	
	public String toString(byte[] bytes) {
        long ticks = 0;
        for (int i = 0; i < 8; i++) {
            ticks = (ticks << 8) + (bytes[i] & 255);
        }
        long ms = ticks / TICKS_TO_MS_RATIO - ERA_DIFFERENCE_IN_MS;
        Date date=new Date(ms);
        Calendar cal=Calendar.getInstance(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
        cal.setTime(date);
        StringBuffer result=new StringBuffer()
        	.append(prependZeroes(cal.get(Calendar.YEAR),4))
        	.append('-')
        	.append(MONTHS[cal.get(Calendar.MONTH)])
        	.append('-')
        	.append(prependZeroes(cal.get(Calendar.DAY_OF_MONTH),2))
        	.append(", ") //$NON-NLS-1$
        	.append(prependZeroes(cal.get(Calendar.HOUR_OF_DAY),2))
        	.append(':')
        	.append(prependZeroes(cal.get(Calendar.MINUTE),2))
        	.append(':')
        	.append(prependZeroes(cal.get(Calendar.SECOND),2))
        	.append('.')
        	.append(prependZeroes(cal.get(Calendar.MILLISECOND),3))
        	.append(" UTC"); //$NON-NLS-1$
        return result.toString();
    }
	
	private String prependZeroes(int val,int size) {
		String str=String.valueOf(val);
		int missing=size-str.length();
		if(missing>0) {
			str=ZEROES.substring(0,missing)+str;
		}
		return str;
	}
}
