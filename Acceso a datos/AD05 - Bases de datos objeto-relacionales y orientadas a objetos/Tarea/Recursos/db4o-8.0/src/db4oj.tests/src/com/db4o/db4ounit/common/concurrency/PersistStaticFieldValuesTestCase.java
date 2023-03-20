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

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class PersistStaticFieldValuesTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new PersistStaticFieldValuesTestCase().runConcurrency();
	}

	public static final PsfvHelper ONE = new PsfvHelper();

	public static final PsfvHelper TWO = new PsfvHelper();

	public static final PsfvHelper THREE = new PsfvHelper();

	public PsfvHelper one;

	public PsfvHelper two;

	public PsfvHelper three;

	protected void configure(Configuration config) {
		config.objectClass(PersistStaticFieldValuesTestCase.class)
				.persistStaticFieldValues();
	}

	protected void store() {
		PersistStaticFieldValuesTestCase psfv = new PersistStaticFieldValuesTestCase();
		psfv.one = ONE;
		psfv.two = TWO;
		psfv.three = THREE;
		store(psfv);
	}

	public void conc(ExtObjectContainer oc) {
		PersistStaticFieldValuesTestCase psfv = (PersistStaticFieldValuesTestCase) retrieveOnlyInstance(
				oc, PersistStaticFieldValuesTestCase.class);
		Assert.areSame(ONE, psfv.one);
		Assert.areSame(TWO, psfv.two);
		Assert.areSame(THREE, psfv.three);
	}

	public static class PsfvHelper {

	}

}
