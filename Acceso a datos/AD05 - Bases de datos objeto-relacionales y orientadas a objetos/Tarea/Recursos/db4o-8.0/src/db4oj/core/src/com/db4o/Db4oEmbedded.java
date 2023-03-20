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
package com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;

/**
 * Factory class to open db4o instances in embedded mode.
 * 
 * <br><br>
 * @see com.db4o.cs.Db4oClientServer class in
 * db4o-[version]-cs-java[java-version].jar
 * for methods to open db4o servers and db4o clients.
 * @since 7.5
 * 
 */
public class Db4oEmbedded {

	/**
	 * Creates a fresh {@link EmbeddedConfiguration EmbeddedConfiguration} instance.
	 * 
	 * @return a fresh, independent configuration with all options set to their default values
	 */
	@SuppressWarnings("deprecation")
	public static EmbeddedConfiguration newConfiguration() {
		return new EmbeddedConfigurationImpl(Db4o.newConfiguration());
	}

	/**
	 * opens an {@link ObjectContainer ObjectContainer}
	 * on the specified database file for local use.
	 * <br><br>A database file can only be opened once, subsequent attempts to open
	 * another {@link ObjectContainer ObjectContainer} against the same file will result in
	 * a {@link DatabaseFileLockedException DatabaseFileLockedException}.<br><br>
	 * Database files can only be accessed for read/write access from one process 
	 * (one Java VM) at one time. All versions except for db4o mobile edition use an
	 * internal mechanism to lock the database file for other processes. 
	 * <br><br>
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link newConfiguration}
	 * @param databaseFileName an absolute or relative path to the database file
	 * @return an open {@link ObjectContainer ObjectContainer}
	 * @see Configuration#readOnly
	 * @see Configuration#encrypt
	 * @see Configuration#password
	 * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
	 * @throws DatabaseFileLockedException the required database file is locked by 
	 * another process.
	 * @throws IncompatibleFileFormatException runtime 
	 * {@link com.db4o.config.Configuration configuration} is not compatible
	 * with the configuration of the database file. 
	 * @throws OldFormatException open operation failed because the database file
	 * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
	 * is set to false.
	 * @throws DatabaseReadOnlyException database was configured as read-only.
	 */
	public static final EmbeddedObjectContainer openFile(EmbeddedConfiguration config,
			String databaseFileName) throws Db4oIOException,
			DatabaseFileLockedException, IncompatibleFileFormatException,
			OldFormatException, DatabaseReadOnlyException {
		if (null == config) {
			throw new ArgumentNullException();
		}
		return ObjectContainerFactory.openObjectContainer(config, databaseFileName);
	}
	
	/**
	 * Same as calling {@link #openFile(EmbeddedConfiguration, String)} with a fresh configuration ({@link #newConfiguration()}).
	 * @param databaseFileName an absolute or relative path to the database file
	 * @see #openFile(EmbeddedConfiguration, String)
	 */
	public static final EmbeddedObjectContainer openFile(String databaseFileName)
		throws Db4oIOException, DatabaseFileLockedException, IncompatibleFileFormatException,
			OldFormatException, DatabaseReadOnlyException {
		return openFile(newConfiguration(), databaseFileName);
	}

}
