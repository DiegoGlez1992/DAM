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
package com.db4o.db4ounit.common.ext;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class UnavailableClassesWithTranslatorTestCase extends TestWithTempFile implements OptOutNetworkingCS {
	

	public static class HolderForClassWithTranslator {
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			HolderForClassWithTranslator other = (HolderForClassWithTranslator) obj;
			return Check.objectsAreEqual(_fieldWithTranslator, other._fieldWithTranslator);
		}

		public HolderForClassWithTranslator(TranslatedType value) {
			_fieldWithTranslator = value;
		}

		public TranslatedType _fieldWithTranslator;
	}
	
	public static class TranslatedType {
		
		@Override
		public boolean equals(Object obj) {
			if (null == obj) {
				return false;
			}
			return getClass() == obj.getClass();
		}
	}
	
	public static void main(String[] args) {
		new ConsoleTestRunner(UnavailableClassesWithTranslatorTestCase.class).run();
	}

	public void test() {		
		store(tempFile(), new HolderForClassWithTranslator(new TranslatedType()));
		assertStoredClasses(tempFile());
	}

	private void assertStoredClasses(final String databaseFileName) {
		ObjectContainer db = Db4oEmbedded.openFile(configExcludingStack(), databaseFileName);

		try {
			Assert.isGreater(2, db.ext().storedClasses().length);
		} finally {
			db.close();
		}
	}

	private void store(final String databaseFileName, Object obj) {
		ObjectContainer db = Db4oEmbedded.openFile(newConfiguration(), databaseFileName);
		try {
			db.store(obj);
			db.ext().purge(obj);
			Assert.areEqual(obj, db.query(obj.getClass()).next());
		} finally {
			db.close();
		}
	}

	private EmbeddedConfiguration newConfiguration() {
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(TranslatedType.class.getName()).translate(new Translator());
		return config;
	}

	private EmbeddedConfiguration configExcludingStack() {
		final EmbeddedConfiguration config = newConfiguration();				
		config.common().reflectWith(new ExcludingReflector(TranslatedType.class));
		return config;
	}
	
	private static final class Translator implements ObjectTranslator {
		public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
		}
		
		public Object onStore(ObjectContainer container, Object applicationObject) {
			return 42;
		}
		
		public Class storedClass() {
			return Integer.class;
		}
	}
}
