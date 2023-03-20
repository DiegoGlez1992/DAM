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

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @sharpen.ignore
 */
public class RandomAccessFileFactory {
	
	public static RandomAccessFile newRandomAccessFile(String path, boolean readOnly, boolean lockFile){
		boolean ok = false;
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(path, readOnly ? "r" : "rw");
			if (lockFile && ! readOnly) {
				Platform4.lockFile(path, raf);
			} 
			ok = true;
			return raf;
		} catch (IOException e) {
			throw new Db4oIOException(e);
		} finally{
			if(! ok && raf != null){
				try {
					raf.close();
				} catch (IOException e) {
				
				}
			}
		}
	}

}
