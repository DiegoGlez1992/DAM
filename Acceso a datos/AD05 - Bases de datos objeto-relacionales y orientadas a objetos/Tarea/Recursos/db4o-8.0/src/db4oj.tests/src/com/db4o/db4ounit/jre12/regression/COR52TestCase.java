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
package com.db4o.db4ounit.jre12.regression;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.internal.*;

import db4ounit.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class COR52TestCase extends TestWithTempFile {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(COR52TestCase.class).run();
	}
	
	/**
	 * @deprecated using deprecated api
	 */
	public void test() throws Exception {
		int originalActivationDepth = ((Config4Impl) Db4o.configure())
				.activationDepth();
		Db4o.configure().activationDepth(0);
		ObjectServer server = Db4oClientServer.openServer(tempFile(), -1);
		try {
			server.grantAccess("db4o", "db4o");
			ObjectContainer oc = Db4oClientServer.openClient("localhost", server.ext().port(), "db4o",
					"db4o");
			oc.close();
		} finally {
			Db4o.configure().activationDepth(originalActivationDepth);
			server.close();
		}

	}
}
