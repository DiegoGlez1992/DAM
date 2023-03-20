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
package com.db4o.db4ounit.jre12.defragment;

import java.io.*;

import com.db4o.db4ounit.common.defragment.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;

import db4ounit.extensions.util.*;

/**
 * This one tests common, non-jdk1.2 specific functionality, but requires an
 * ExcludingClassLoader which doesn't work on JDK < 1.2.
 */

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class DefragmentSkipClassTestCase extends DefragmentTestCaseBase {

	public void testSkipsClass() throws Exception {
		DefragmentConfig defragConfig = newDefragmentConfig();
		Defragment.defrag(defragConfig);
		SlotDefragmentFixture.assertDataClassKnown(sourceFile(), newConfiguration(), true);

		defragConfig = newDefragmentConfig();
		defragConfig.storedClassFilter(new AvailableClassFilter(SlotDefragmentFixture.Data.class.getClassLoader()));
		Defragment.defrag(defragConfig);
		SlotDefragmentFixture.assertDataClassKnown(sourceFile(), newConfiguration(), true);

		defragConfig = newDefragmentConfig();
		Collection4 excluded=new Collection4();
		excluded.add(SlotDefragmentFixture.Data.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(SlotDefragmentFixture.Data.class.getClassLoader(),excluded);
		defragConfig.storedClassFilter(new AvailableClassFilter(loader));
		Defragment.defrag(defragConfig);
		SlotDefragmentFixture.assertDataClassKnown(sourceFile(), newConfiguration(), false);
	}

	private DefragmentConfig newDefragmentConfig() {
		return SlotDefragmentFixture.defragConfig(sourceFile(), newConfiguration(), true);
	}

	public void setUp() throws Exception {
		new File(sourceFile()).delete();
		new File(backupFile()).delete();
		SlotDefragmentFixture.createFile(sourceFile(), newConfiguration());
	}
}
