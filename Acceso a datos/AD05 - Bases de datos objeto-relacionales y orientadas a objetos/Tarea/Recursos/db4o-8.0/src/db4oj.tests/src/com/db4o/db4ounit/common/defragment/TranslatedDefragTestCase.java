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
package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.internal.*;

import db4ounit.*;

public class TranslatedDefragTestCase extends Db4oTestWithTempFile {

	private static final String TRANSLATED_NAME = "A";

	public static class Translated {
		public String _name;

		public Translated(String name) {
			_name = name;
		}
	}

	public static class TranslatedTranslator implements ObjectConstructor {
		public Object onInstantiate(ObjectContainer container, Object storedObject) {
			return new Translated((String)storedObject);
		}

		public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
		}

		public Object onStore(ObjectContainer container, Object applicationObject) {
			return ((Translated)applicationObject)._name;
		}

		public Class storedClass() {
			return String.class;
		}
	}

	public void testDefragWithTranslator() throws IOException {
		assertDefragment(true);
	}

	public void testDefragWithoutTranslator() throws IOException {
		assertDefragment(true);
	}

	private void assertDefragment(boolean registerTranslator) throws IOException {
		store();
		defragment(registerTranslator);
		assertTranslated();
	}

	private void defragment(boolean registerTranslator) throws IOException {
		DefragmentConfig defragConfig = new DefragmentConfig(tempFile());
		defragConfig.db4oConfig(config(registerTranslator));
		defragConfig.forceBackupDelete(true);
		Defragment.defrag(defragConfig);
	}

	private void store() {
		ObjectContainer db = openDatabase();
		db.store(new Translated(TRANSLATED_NAME));
		db.close();
	}

	private void assertTranslated() {
		ObjectContainer db = openDatabase();
		ObjectSet result = db.query(Translated.class);
		Assert.areEqual(1, result.size());
		Translated trans = (Translated) result.next();
		Assert.areEqual(TRANSLATED_NAME, trans._name);
		db.close();
	}

	private ObjectContainer openDatabase() {
		return Db4oEmbedded.openFile(config(true), tempFile());
	}
	
	private EmbeddedConfiguration config(boolean registerTranslator) {
		EmbeddedConfiguration config = newConfiguration();
		config.common().reflectWith(Platform4.reflectorForType(Translated.class));
		if(registerTranslator) {
			config.common().objectClass(Translated.class).translate(new TranslatedTranslator());
		}
		return config;
	}
}
