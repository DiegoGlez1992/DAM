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
package com.db4o.io;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;

/**
 * Base interface for Storage adapters that open a {@link Bin}
 * to store db4o database data to.
 * @see FileConfiguration#storage(Storage) 
 */
public interface Storage {
	
	/**
	 * opens a {@link Bin} to store db4o database data. 
	 */
	Bin open(BinConfiguration config) throws Db4oIOException;

	/**
	 * returns true if a Bin (file or memory) exists with the passed name. 
	 */
	boolean exists(String uri);

	/**
	 * Deletes the bin for the given URI from the storage.
	 * @since 7.9
	 * @param uri bin URI
	 * @throws IOException if the bin could not be deleted
	 */
	void delete(String uri) throws IOException;

	/**
	 * Renames the bin for the given old URI to the new URI. If a bin for the new URI
	 * exists, it will be overwritten.
	 * @since 7.9
	 * @param oldUri URI of the existing bin
	 * @param newUri future URI of the bin
	 * @throws IOException if the bin could not be deleted
	 */
	void rename(String oldUri, String newUri) throws IOException;
}
