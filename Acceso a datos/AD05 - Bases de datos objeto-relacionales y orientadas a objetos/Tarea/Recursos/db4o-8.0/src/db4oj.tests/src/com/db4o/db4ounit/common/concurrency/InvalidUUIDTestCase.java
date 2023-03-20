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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InvalidUUIDTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new InvalidUUIDTestCase().runConcurrency();
	}

	public String name;

	protected void configure(Configuration config) {
		config.objectClass(this.getClass()).generateUUIDs(true);
	}

	protected void store() {
		name = "theOne";
		store(this);
	}

	public void conc(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(InvalidUUIDTestCase.class);
		if (os.size() == 0) { // already deleted by other threads
			return;
		}
		Assert.areEqual(1, os.size());
		InvalidUUIDTestCase iu = (InvalidUUIDTestCase) os.next();
		Db4oUUID myUuid = oc.getObjectInfo(iu).getUUID();
		Assert.isNotNull(myUuid);

		byte[] mySignature = myUuid.getSignaturePart();
		long myLong = myUuid.getLongPart();

		long unknownLong = Long.MAX_VALUE - 100;
		byte[] unknownSignature = new byte[] { 1, 2, 4, 99, 33, 22 };

		Db4oUUID unknownLongPart = new Db4oUUID(unknownLong, mySignature);
		Db4oUUID unknownSignaturePart = new Db4oUUID(myLong, unknownSignature);
		Db4oUUID unknownBoth = new Db4oUUID(unknownLong, unknownSignature);

		Assert.isNull(oc.getByUUID(unknownLongPart));
		Assert.isNull(oc.getByUUID(unknownSignaturePart));
		Assert.isNull(oc.getByUUID(unknownBoth));

		Assert.isNull(oc.getByUUID(unknownLongPart));

		Thread.sleep(500);
		oc.delete(iu);
		oc.commit();
		Assert.isNull(oc.getByUUID(myUuid));
	}

}
