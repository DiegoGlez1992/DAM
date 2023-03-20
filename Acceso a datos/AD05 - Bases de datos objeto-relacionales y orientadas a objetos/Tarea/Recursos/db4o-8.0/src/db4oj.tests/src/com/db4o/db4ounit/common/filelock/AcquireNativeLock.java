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
package com.db4o.db4ounit.common.filelock;

import java.io.*;
import java.nio.channels.*;

import com.db4o.internal.*;

import db4ounit.*;

@decaf.Remove
public class AcquireNativeLock {

	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = null;
		try{
			raf = new RandomAccessFile(args[0], "rw");
			FileChannel channel = raf.getChannel();
			try {
				channel.tryLock();
			}catch(ReflectException rex){
				Assert.fail("File shouldn't be locked already.");
				rex.printStackTrace();
			}
			System.out.println("ready");
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		}finally{
			raf.close();
		}
	}
	
}
