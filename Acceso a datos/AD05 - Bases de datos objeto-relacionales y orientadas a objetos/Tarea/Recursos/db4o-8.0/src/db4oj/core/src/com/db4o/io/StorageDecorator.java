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

import com.db4o.ext.*;

/**
 * Wrapper base class for all classes that wrap Storage.
 * Each class that adds functionality to a Storage must
 * extend this class.
 * @see BinDecorator 
 */
public class StorageDecorator implements Storage {

	protected final Storage _storage;

	public StorageDecorator(Storage storage) {
		_storage = storage;
	}

	public boolean exists(String uri) {
    	return _storage.exists(uri);
    }
	
	public Bin open(BinConfiguration config) throws Db4oIOException {
		return decorate(config, _storage.open(config));
	}

	protected Bin decorate(BinConfiguration config, Bin bin) {
		return bin;
    }

	public void delete(String uri) throws IOException {
		_storage.delete(uri);
	}

	public void rename(String oldUri, String newUri) throws IOException {
		_storage.rename(oldUri, newUri);
	}
}