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
package com.db4o.db4ounit.jre12.assorted;

import java.io.*;
import java.math.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TranslatorStoredClassesTestCase extends Db4oTestWithTempFile {

	public static class DataRawChild implements Serializable {
		public int _id;

		public DataRawChild(int id) {
			_id=id;
		}
	}

	public static class DataRawParent {
		public DataRawChild _child;

		public DataRawParent(int id) {
			_child=new DataRawChild(id);
		}
	}

	public static class DataBigDecimal {
		public BigDecimal _bd;

		public DataBigDecimal(int id) {
			_bd=new BigDecimal(String.valueOf(id));
		}
	}
	
	public void testBigDecimal() {
		assertStoredClassesAfterTranslator(BigDecimal.class,new DataBigDecimal(42));
	}

	public void testRaw() {
		assertStoredClassesAfterTranslator(DataRawChild.class,new DataRawParent(42));
	}

	public void assertStoredClassesAfterTranslator(Class translated,Object data) {
		createFile(translated,data);
		check(translated);
	}

	private void createFile(Class translated,Object data) {
        ObjectContainer server = db(translated,new TSerializable());
        server.store(data);
        server.close();
	}

	private void check(Class translated) {
		ObjectContainer db=db(translated,null);
		db.ext().storedClasses();
		db.close();
	}

	private ObjectContainer db(Class translated,ObjectTranslator translator) {
		EmbeddedConfiguration config = newConfiguration();
		config.common().objectClass(translated).translate(translator);
		return Db4oEmbedded.openFile(config, tempFile());
	}

}
