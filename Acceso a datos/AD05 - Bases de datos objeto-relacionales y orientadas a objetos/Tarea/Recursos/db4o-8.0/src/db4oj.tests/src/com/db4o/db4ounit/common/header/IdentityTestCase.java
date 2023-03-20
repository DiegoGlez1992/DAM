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
package com.db4o.db4ounit.common.header;

import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class IdentityTestCase extends AbstractDb4oTestCase {

	public static void main(String[] arguments) {
		new IdentityTestCase().runAll();
	}
	
	public void testIdentitySignatureIsNotNull() {
		Db4oDatabase identity = db().identity();
		Assert.isNotNull(identity.getSignature());
	}

	public void testIdentityPreserved() throws Exception {

		Db4oDatabase ident = db().identity();

		reopen();

		Db4oDatabase ident2 = db().identity();

		Assert.isNotNull(ident);
		Assert.areEqual(ident, ident2);
	}

	public void testGenerateIdentity() throws Exception {
		if(isMultiSession()){
			return;
		}

		byte[] oldSignature = db().identity().getSignature();

		generateNewIdentity();

		reopen();

		ArrayAssert.areNotEqual(oldSignature, db().identity().getSignature());
	}

	private void generateNewIdentity() {
		((LocalObjectContainer) db()).generateNewIdentity();
	}
}
