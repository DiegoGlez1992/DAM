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
package com.db4o.db4ounit.common.filelock;

import com.db4o.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.IOServices.ProcessRunner;

@decaf.Remove
public class DatabaseFileLockedAcrossVMTestCase
	extends TestWithTempFile
	implements OptOutInMemory, OptOutNoInheritedClassPath, OptOutWorkspaceIssue {
	
	
	public void testLockedFile() throws Exception {
		ProcessRunner externalVM = JavaServices.startJava(AcquireNativeLock.class.getName(), new String[]{ tempFile() });		
		
		waitToFinish(externalVM);
		
		try {
			Assert.expect(DatabaseFileLockedException.class, new CodeBlock() {
				public void run() throws Throwable {
					Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), tempFile());
				}
			});
		} finally {
			externalVM.write("");
			try {
				externalVM.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	private void waitToFinish(ProcessRunner process) {
		try {
			process.waitFor("ready", 3000);
		} catch (Exception ex) {
			process.destroy();
			throw new RuntimeException(ex);
		}			
	}	

	public static void main(String[] args) {
		new ConsoleTestRunner(DatabaseFileLockedAcrossVMTestCase.class).run();
	}
}
