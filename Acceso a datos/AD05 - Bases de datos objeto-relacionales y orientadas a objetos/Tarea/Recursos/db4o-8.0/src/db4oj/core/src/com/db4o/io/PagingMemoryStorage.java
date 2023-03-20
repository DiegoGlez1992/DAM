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
import java.util.*;

import com.db4o.ext.*;

/**
 * {@link Storage} implementation that produces {@link Bin} instances
 * that operate in memory.
 * Use this {@link Storage} to work with db4o as an in-memory database. 
 */
public class PagingMemoryStorage implements Storage {

	private static final int DEFAULT_PAGESIZE = 4096;
	private final Map<String, Bin> _binsByUri = new HashMap<String, Bin>();
	private final int _pageSize;

	public PagingMemoryStorage() {
		this(DEFAULT_PAGESIZE);
	}

	public PagingMemoryStorage(int pageSize) {
		_pageSize = pageSize;
	}
	
	/**
	 * returns true if a MemoryBin with the given URI name already exists
	 * in this Storage.
	 */
	public boolean exists(String uri) {
		return _binsByUri.containsKey(uri);
	}

	/**
	 * opens a MemoryBin for the given URI (name can be freely chosen).
	 */
	public Bin open(BinConfiguration config) throws Db4oIOException {
		final Bin bin = produceBin(config);
		return config.readOnly() ? new ReadOnlyBin(bin) : bin;
	}

	/**
	 * Returns the memory bin for the given URI for external use.
	 */
	public Bin bin(String uri) {
		return _binsByUri.get(uri);
	}

	/**
	 * Registers the given bin for this storage with the given URI.
	 */
	public void bin(String uri, Bin bin) {
		_binsByUri.put(uri, bin);
	}

	private Bin produceBin(BinConfiguration config) {
	    final Bin storage = bin(config.uri());
		if (null != storage) {
			return storage;
		}
		final Bin newStorage = new PagingMemoryBin(_pageSize, config.initialLength());
		_binsByUri.put(config.uri(), newStorage);
		return newStorage;
    }

	public void delete(String uri) throws IOException {
		_binsByUri.remove(uri);
	}

	public void rename(String oldUri, String newUri) throws IOException {
		Bin bin = _binsByUri.remove(oldUri);
		if (bin == null) {
			throw new IOException("Bin not found: " + oldUri);
		}
		_binsByUri.put(newUri, bin);
		
	}

}
