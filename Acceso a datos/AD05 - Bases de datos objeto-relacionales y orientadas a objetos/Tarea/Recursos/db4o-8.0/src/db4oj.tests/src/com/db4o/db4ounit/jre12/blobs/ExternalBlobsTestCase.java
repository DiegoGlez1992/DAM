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
package com.db4o.db4ounit.jre12.blobs;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.types.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ExternalBlobsTestCase extends AbstractDb4oTestCase  {

	public static void main(String[] args) {
		new ExternalBlobsTestCase().runNetworking();
	}
	
	private static final String BLOB_PATH = Path4.combine(Path4.getTempPath(), "db4oTestBlobs");
	private static final String BLOB_FILE_IN = BLOB_PATH + "/regressionBlobIn.txt"; 
	private static final String BLOB_FILE_OUT = BLOB_PATH + "/regressionBlobOut.txt"; 

	private static class Data {
		private Blob _blob;

		public Data() {
			_blob = null;
		}
		
		public Blob blob() {
			return _blob;
		}
	}

	protected void db4oSetupBeforeStore() throws Exception {
		deleteFiles();
		File4.mkdirs(BLOB_PATH);
	}
	
	protected void db4oTearDownAfterClean() throws Exception {
        deleteFiles();
	}
	
	protected void configure(Configuration config) throws IOException {
		config.setBlobPath(BLOB_PATH);
	}

	protected void store() throws Exception {
		store(new Data());
	}
	
	public void test() throws Exception {
		Data data = (Data) retrieveOnlyInstance(Data.class);
		Assert.isTrue(new File(BLOB_PATH).exists());
		char[] chout = new char[] { 'H', 'i', ' ', 'f', 'o', 'l', 'k', 's' };
		writeFile(BLOB_FILE_IN, chout);
		data.blob().readFrom(new File(BLOB_FILE_IN));
		double status = data.blob().getStatus();
		while (status > Status.COMPLETED) {
			Thread.sleep(50);
			status = data.blob().getStatus();
		}

		data.blob().writeTo(new File(BLOB_FILE_OUT));
		status = data.blob().getStatus();
		while (status > Status.COMPLETED) {
			Thread.sleep(50);
			status = data.blob().getStatus();
		}
		File resultingFile = new File(BLOB_FILE_OUT);
		Assert.isTrue(resultingFile.exists());

		char[] chin = new char[chout.length];
		readFileInto(resultingFile, chin);
		ArrayAssert.areEqual(chout, chin);
		
		Assert.areEqual(Status.COMPLETED, data.blob().getStatus());
		data.blob().deleteFile();
		Assert.areEqual(Status.UNUSED, data.blob().getStatus());
	}

	private void readFileInto(File fname, char[] buffer) throws FileNotFoundException, IOException {
	    FileReader fr = new FileReader(fname);
		fr.read(buffer);
		fr.close();
    }

	private void writeFile(final String fname, char[] contents) throws IOException {
	    FileWriter fw = new FileWriter(fname);
	    try {
			fw.write(contents);
			fw.flush();
	    } finally {
	    	fw.close();
	    }
    }

	private void deleteFiles() throws IOException {
		IOUtil.deleteDir(BLOB_PATH);
	}
}
