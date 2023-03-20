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

import java.io.*;

import com.db4o.ext.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class UTF8StringEncoding extends BuiltInStringEncoding{
	
	private final static String CHARSET_NAME = "UTF-8";
	
	public byte[] encode(String str) {
		try {
			return str.getBytes(CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new Db4oIOException(e);
		}
	}
	
	public String decode(byte[] bytes, int start, int length) {
		try {
			return new String(bytes, start, length, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new Db4oIOException(e);
		}
	}

}
