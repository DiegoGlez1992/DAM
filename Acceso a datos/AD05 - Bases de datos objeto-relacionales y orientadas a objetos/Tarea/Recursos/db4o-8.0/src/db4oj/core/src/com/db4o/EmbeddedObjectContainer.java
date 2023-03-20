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

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

/**
 * Represents a local ObjectContainer attached to a 
 * database file.
 * @since 7.10
 */
public interface EmbeddedObjectContainer extends ObjectContainer{
	
	
    /**
     * backs up a database file of an open ObjectContainer.
     * <br><br>While the backup is running, the ObjectContainer can continue to be
     * used. Changes that are made while the backup is in progress, will be applied to
     * the open ObjectContainer and to the backup.<br><br>
     * While the backup is running, the ObjectContainer should not be closed.<br><br>
     * If a file already exists at the specified path, it will be overwritten.<br><br>
     * The {@link Storage} used for backup is the one configured for this container.
     * @param path a fully qualified path
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     * @throws NotSupportedException is thrown when the operation is not supported in current 
     * configuration/environment
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     */
    public void backup(String path) throws Db4oIOException,
			DatabaseClosedException, NotSupportedException;

}
