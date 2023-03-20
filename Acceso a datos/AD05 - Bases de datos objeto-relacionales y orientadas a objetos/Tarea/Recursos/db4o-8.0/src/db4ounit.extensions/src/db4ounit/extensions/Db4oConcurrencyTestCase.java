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
package db4ounit.extensions;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class Db4oConcurrencyTestCase extends Db4oClientServerTestCase {
	
	private boolean[] _done;
	
	
	protected void db4oSetupAfterStore() throws Exception {
		initTasksDoneFlag();
		super.db4oSetupAfterStore();
	}

	private void initTasksDoneFlag() {
		_done = new boolean[threadCount()];
	}
	
	protected void markTaskDone(int seq, boolean done) {
		_done[seq] = done;
	}
	
	protected void waitForAllTasksDone() throws Exception {
		while(!areAllTasksDone()) {
			Runtime4.sleep(1);
		}
	}

	private boolean areAllTasksDone() {
		for(int i = 0; i < _done.length; ++i) {
			if(!_done[i]) {
				return false;
			}
		}
		return true;
	}
	
}
