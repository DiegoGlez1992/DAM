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
package com.db4o.instrumentation.test.classfilter;

import java.io.*;
import java.util.jar.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.util.*;

import db4ounit.*;

public class JarFileClassFilterTestCase implements TestCase {
	
	private static class NotAccepted {
	}
	
	// TODO: delegate to JarFileWriter and ZipFileExtraction, port directory creation there if needed
	public void test() throws IOException {
		String jarPath = Path4.getTempFileName();
		String resourcePath = BloatUtil.classPathForName(getClass().getName());

		InputStream byteIn = getClass().getResourceAsStream("/" + resourcePath);
		JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarPath));
		int slashIdx = -1;
		while(true) {
			int nextSlashIdx = resourcePath.indexOf('/', slashIdx + 1);
			if(nextSlashIdx == -1) {
				break;
			}
			String dirEntryName = resourcePath.substring(0, nextSlashIdx + 1);
			JarEntry jarEntry = new JarEntry(dirEntryName);
			jarOut.putNextEntry(jarEntry);
			slashIdx = nextSlashIdx;
		}
		jarOut.putNextEntry(new JarEntry(resourcePath));
		byte[] buf = new byte[4096];
		int bytesRead = 0;
		while((bytesRead = byteIn.read(buf)) >= 0) {
			jarOut.write(buf, 0, bytesRead);
		}
		jarOut.close();
		byteIn.close();
		
		JarFile jarFile = new JarFile(jarPath);
		JarFileClassFilter filter = new JarFileClassFilter(jarFile);
		Assert.isTrue(filter.accept(getClass()));
		Assert.isFalse(filter.accept(NotAccepted.class));
		
		new File(jarPath).delete();
	}

}
