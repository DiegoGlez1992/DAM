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



/**
 */
@decaf.Remove
public class StackBasedDiskFullTestCase extends DiskFullTestCaseBase {

	public void testFailDuringCommitParticipants() {
		assertFailDuringCommit(1, false);
	}

	public void testFailDuringCommitParticipantsWithCache() {
		assertFailDuringCommit(1, true);
	}

	public void testFailDuringCommitWriteChanges() {
		assertFailDuringCommit(1, false);
	}

	public void testFailDuringCommitWriteChangesWithCache() {
		assertFailDuringCommit(1, true);
	}

	private void assertFailDuringCommit(int hitThreshold, boolean doCache) {
		StackBasedConfiguration config = new StackBasedConfiguration(
				"com.db4o.internal.LocalTransaction",
				"commitParticipants",
				hitThreshold);
		storeNAndFail(config, 95, 10, doCache);
		assertItemsStored(90, config, false, doCache);
	}
	
	protected void configureForFailure(ThrowCondition condition) {
		((StackBasedLimitedSpaceThrowCondition)condition).enabled(true);
	}

	protected ThrowCondition createThrowCondition(Object conditionConfig) {
		return new StackBasedLimitedSpaceThrowCondition((StackBasedConfiguration)conditionConfig);
	}

}
