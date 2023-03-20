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
package com.db4o.foundation.io;

import java.io.*;

/**
 * Keep the API compatible with System.IO.Directory
 * 
 * @sharpen.ignore
 * @sharpen.rename System.IO.Directory
 */
public class Directory4 {
	
	public static void delete(String path, boolean recurse) {
		File f = new File(path);
		if (recurse) {
			final File[] files = f.listFiles();
			if (files != null) {
				delete(files);
			}
		}
		File4.translateDeleteFailureToException(f);
	}

	private static void delete(File[] files) {
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				delete(f.listFiles());
			}
			File4.translateDeleteFailureToException(f);
		}
	}
}
