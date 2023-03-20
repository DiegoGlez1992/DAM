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
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

/**
 * #COR-775
 * Currently this test doesn't work with JDKs that use a 
 * timer file lock because the new logic grabs into the Bin 
 * below the MockBin and reads open times there directly.
 * The times are then inconsistent with the written times.
 */
public class DefragEncryptedFileTestCase extends Db4oTestWithTempFile {

    private static String DEFGARED;
    
    public void setUp() throws Exception {
    	DEFGARED = tempFile() + ".bk";
	}

	public void tearDown() throws Exception {
		File4.delete(DEFGARED);
		super.tearDown();
	}

    public static void main(String[] args) {
        new ConsoleTestRunner(DefragEncryptedFileTestCase.class).run();
    }

	public void testCOR775() throws Exception {
        prepare();
        verifyDB();
        
        DefragmentConfig config = new DefragmentConfig(tempFile(), DEFGARED);
        config.forceBackupDelete(true);
        //config.storedClassFilter(new AvailableClassFilter());
        config.db4oConfig(getConfiguration());
        Defragment.defrag(config);

        verifyDB();
    }

    private void prepare() {
        File file = new File(tempFile());
        if (file.exists()) {
            file.delete();
        }
        
        ObjectContainer testDB = openDB();
        Item item = new Item("richard", 100);
        testDB.store(item);
        testDB.close();
    }
    
    private void verifyDB() {
        ObjectContainer testDB = openDB();
        ObjectSet result = testDB.queryByExample(Item.class);
        if (result.hasNext()) {
            Item retrievedItem = (Item) result.next();
            Assert.areEqual("richard", retrievedItem.name);
            Assert.areEqual(100, retrievedItem.value);
        } else {
            Assert.fail("Cannot retrieve the expected object.");
        }
        testDB.close();
    }

	private ObjectContainer openDB() {
    	return Db4oEmbedded.openFile(getConfiguration(), tempFile());
    }

	private EmbeddedConfiguration getConfiguration() {
        final EmbeddedConfiguration config = newConfiguration();

        config.common().activationDepth(Integer.MAX_VALUE);
        config.common().callConstructors(true);
        config.file().storage(new MockStorage(config.file().storage(), "db4o"));
        config.common().reflectWith(Platform4.reflectorForType(Item.class));

        Db4o.configure().password("encrypted");
        Db4o.configure().encrypt(true);
     
        //TODO: CHECK ENCRYPTION
        return config;
    }

    public static class Item {
        public String name;

        public int value;

        public Item(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class MockStorage extends StorageDecorator {

        private String password;

        public MockStorage(Storage storage, String password) {
        	super(storage);
            this.password = password;
        }

        @Override
        protected Bin decorate(BinConfiguration config, Bin bin) {
        	return new MockBin(bin, password);
        }


        static class MockBin extends BinDecorator {
        
        	private String _password;
        	
	        public MockBin(Bin bin, String password) {
				super(bin);
				_password = password;
			}

			public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
	            _bin.read(pos, bytes, length);
	            for (int i = 0; i < length; i++) {
	                bytes[i] = (byte) (bytes[i] - _password.hashCode());
	            }
	            return length;
	        }
			
			public int syncRead(long pos, byte[] bytes, int length) throws Db4oIOException {
				return read(pos, bytes, length);
	        }
	
	        public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
	        	byte[] encryptedBuffer = new byte[buffer.length];
	        	System.arraycopy(buffer, 0, encryptedBuffer, 0, buffer.length);
	            for (int i = 0; i < length; i++) {
	            	encryptedBuffer[i] = (byte) (encryptedBuffer[i] + _password.hashCode());
	            }
	            _bin.write(pos, encryptedBuffer, length);
	        }
        }
    }

}
