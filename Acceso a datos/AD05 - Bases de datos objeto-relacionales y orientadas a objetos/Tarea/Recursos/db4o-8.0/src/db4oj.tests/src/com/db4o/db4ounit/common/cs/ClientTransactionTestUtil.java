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
package com.db4o.db4ounit.common.cs;

import com.db4o.foundation.io.*;

final class ClientTransactionTestUtil {
	
		static final String FILENAME_A = Path4.getTempFileName();
		static final String FILENAME_B = Path4.getTempFileName();
		public static final String MAINFILE_NAME = Path4.getTempFileName();
	
		private ClientTransactionTestUtil() {
		}
	
		static void deleteFiles() {
			File4.delete(MAINFILE_NAME);
			File4.delete(FILENAME_A);
			File4.delete(FILENAME_B);
		}			
}