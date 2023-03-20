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
package com.db4o.internal.encoding;

import com.db4o.config.encoding.*;

/**
 * @exclude
 */
public abstract class BuiltInStringEncoding implements StringEncoding {
	
	/**
	 * keep the position in the array. 
	 * Information is used to look up encodings.  
	 */
	private static final BuiltInStringEncoding[] ALL_ENCODINGS = new BuiltInStringEncoding[] {
		null,
		new LatinStringEncoding(),
		new UnicodeStringEncoding(),
		new UTF8StringEncoding(), 
	};
	
	public static byte encodingByteForEncoding(StringEncoding encoding){
		for (int i = 1; i < ALL_ENCODINGS.length; i++) {
			if(encoding.getClass() == ALL_ENCODINGS[i].getClass()){
				return (byte) i;
			}
		}
		return 0;
	}
	
    public static LatinStringIO stringIoForEncoding(byte encodingByte, StringEncoding encoding){
    	if(encodingByte < 0 || encodingByte > ALL_ENCODINGS.length){
    		throw new IllegalArgumentException();
    	}
		if(encodingByte == 0){
			if(encoding instanceof BuiltInStringEncoding){
				System.out.println("Warning! Database was created with a custom string encoding but no custom string encoding is configured for this session.");
			}
			return new DelegatingStringIO(encoding);
		};
		BuiltInStringEncoding builtInEncoding = ALL_ENCODINGS[encodingByte];
		return builtInEncoding.createStringIo(encoding);
    }

	protected LatinStringIO createStringIo(StringEncoding encoding) {
		return new DelegatingStringIO(encoding);
	}


}
