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
package db4ounit.extensions.util;

import com.db4o.foundation.io.*;
import com.db4o.internal.*;

public class CrossPlatformServices {

	public static String simpleName(String typeName) {
		int index = typeName.indexOf(',');
		if (index < 0) return typeName;
		return typeName.substring(0, index);
	}

	public static String fullyQualifiedName(Class klass) {
		return ReflectPlatform.fullyQualifiedName(klass);
	}

	public static String databasePath(String fileName) {
		String path = System.getProperty("db4ounit.file.path");
		if(path == null || path.length() == 0) {
			path =".";
		} else {
		    File4.mkdirs(path);
		}
		return Path4.combine(path, fileName);
	}
}
