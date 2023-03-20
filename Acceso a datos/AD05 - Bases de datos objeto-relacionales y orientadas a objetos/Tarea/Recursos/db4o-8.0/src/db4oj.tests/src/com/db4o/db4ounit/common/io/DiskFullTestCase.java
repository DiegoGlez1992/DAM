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
package com.db4o.db4ounit.common.io;

import java.io.*;

import db4ounit.*;


/**
 */
@decaf.Remove
public class DiskFullTestCase extends DiskFullTestCaseBase {

	private static final long NO_SIZE_LIMIT = -1;
	
	public static void main(String[] arguments) {
		new ConsoleTestRunner(DiskFullTestCase.class).run();
	}

	public void testReleasesFileLocks() {
		assertReleasesFileLocks(false);
	}

	public void testReleasesFileLocksWithCache() {
		assertReleasesFileLocks(true);
	}

	public void testKeepsCommittedDataReadOnlyLimited() {
		assertKeepsCommittedDataReadOnlyLimited(false);
	}

	public void testKeepsCommittedDataReadOnlyLimitedWithCache() {
		assertKeepsCommittedDataReadOnlyLimited(true);
	}

	public void testKeepsCommittedDataReadWriteUnlimited() {
		assertKeepsCommittedDataReadWriteUnlimited(false);
	}

	public void testKeepsCommittedDataReadWriteUnlimitedWithCache() {
		assertKeepsCommittedDataReadWriteUnlimited(true);
	}

	private void assertReleasesFileLocks(boolean doCache) {
		openDatabase(NO_SIZE_LIMIT, false, doCache);
		triggerDiskFullAndClose();
		openDatabase(NO_SIZE_LIMIT, true, false);
		closeDb();
	}

	private void assertKeepsCommittedDataReadOnlyLimited(boolean doCache) {
		storeOneAndFail(NO_SIZE_LIMIT, doCache);
		assertItemsStored(1, curFileLength(), true, doCache);
	}

	private void assertKeepsCommittedDataReadWriteUnlimited(boolean doCache) {
		storeOneAndFail(NO_SIZE_LIMIT, doCache);
		assertItemsStored(1, NO_SIZE_LIMIT, false, doCache);
	}

	@Override
	protected void configureForFailure(ThrowCondition condition) {
		((LimitedSizeThrowCondition)condition).size(curFileLength());
	}

	@Override
	protected ThrowCondition createThrowCondition(Object conditionConfig) {
		return new LimitedSizeThrowCondition((Long) conditionConfig);
	}
	
	private long curFileLength() {
		return new File(tempFile()).length();
	}

}
