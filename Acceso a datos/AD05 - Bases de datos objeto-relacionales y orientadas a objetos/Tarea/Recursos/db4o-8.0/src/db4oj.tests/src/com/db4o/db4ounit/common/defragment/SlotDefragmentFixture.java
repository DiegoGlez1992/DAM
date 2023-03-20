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
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;

public class SlotDefragmentFixture {

	public static final String PRIMITIVE_FIELDNAME = "_id";
	public static final String WRAPPER_FIELDNAME = "_wrapper";
	public static final String TYPEDOBJECT_FIELDNAME = "_next";
	
	public static class Data {
		
		public int _id;
		public Integer _wrapper;
		public Data _next;

		public Data(int id,Data next) {
			_id = id;
			_wrapper=new Integer(id);
			_next=next;
		}
		
		public Data() {
		}
	}

	public static final int VALUE = 42;

	public static DefragmentConfig defragConfig(String sourceFile, EmbeddedConfiguration db4oConfig, boolean forceBackupDelete) {
		DefragmentConfig defragConfig = new DefragmentConfig(sourceFile, DefragmentTestCaseBase.backupFileNameFor(sourceFile));
		defragConfig.forceBackupDelete(forceBackupDelete);
		defragConfig.db4oConfig(db4oConfig(db4oConfig));
		return defragConfig;
	}

	private static EmbeddedConfiguration db4oConfig(EmbeddedConfiguration db4oConfig) {
		db4oConfig.common().reflectWith(Platform4.reflectorForType(Data.class));
		return db4oConfig;
	}
	
	public static void createFile(String fileName, EmbeddedConfiguration config) {
		ObjectContainer db=Db4oEmbedded.openFile(config, fileName);
		Data data=null;
		for(int value=VALUE-1;value<=VALUE+1;value++) {
			data=new Data(value,data);
			db.store(data);
		}
		db.close();
	}

	public static void forceIndex(String databaseFileName, EmbeddedConfiguration config) {
		config.common().objectClass(Data.class).objectField(PRIMITIVE_FIELDNAME).indexed(true);
		config.common().objectClass(Data.class).objectField(WRAPPER_FIELDNAME).indexed(true);
		config.common().objectClass(Data.class).objectField(TYPEDOBJECT_FIELDNAME).indexed(true);
		ObjectContainer db=Db4oEmbedded.openFile(config, databaseFileName);
		Assert.isTrue(db.ext().storedClass(Data.class).storedField(PRIMITIVE_FIELDNAME,Integer.TYPE).hasIndex());
		Assert.isTrue(db.ext().storedClass(Data.class).storedField(WRAPPER_FIELDNAME,Integer.class).hasIndex());
		Assert.isTrue(db.ext().storedClass(Data.class).storedField(TYPEDOBJECT_FIELDNAME,Data.class).hasIndex());
		db.close();
	}

	public static void assertIndex(String fieldName, String databaseFileName, Closure4<EmbeddedConfiguration> configProvider) throws IOException {
		forceIndex(databaseFileName, configProvider.run());
		DefragmentConfig defragConfig = new DefragmentConfig(databaseFileName, DefragmentTestCaseBase.backupFileNameFor(databaseFileName));
		defragConfig.db4oConfig(configProvider.run());
		Defragment.defrag(defragConfig);
		ObjectContainer db=Db4oEmbedded.openFile(configProvider.run(), databaseFileName);
		Query query=db.query();
		query.constrain(Data.class);
		query.descend(fieldName).constrain(new Integer(VALUE));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
		db.close();
	}

	public static void assertDataClassKnown(String databaseFileName, EmbeddedConfiguration config, boolean expected) {
		ObjectContainer db=Db4oEmbedded.openFile(config, databaseFileName);
		try {
			StoredClass storedClass=db.ext().storedClass(Data.class);
			if(expected) {
				Assert.isNotNull(storedClass);
			}
			else {
				Assert.isNull(storedClass);
			}
		}
		finally {
			db.close();
		}
	}
}
