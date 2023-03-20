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
package db4ounit.extensions.dbmock;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.qlin.*;
import com.db4o.query.*;

/**
 * @sharpen.partial
 */
public class MockEmbedded implements EmbeddedObjectContainer {

	public void backup(String path) throws Db4oIOException, DatabaseClosedException, NotSupportedException {
		throw new NotImplementedException();
	}

	public ObjectContainer openSession() {
		throw new NotImplementedException();
	}

	public void activate(Object obj, int depth) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public boolean close() throws Db4oIOException {
		throw new NotImplementedException();
	}

	public void commit() throws Db4oIOException, DatabaseClosedException, DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public void deactivate(Object obj, int depth) throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public void delete(Object obj) throws Db4oIOException, DatabaseClosedException, DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public ExtObjectContainer ext() {
		throw new NotImplementedException();
	}

	public <T> ObjectSet<T> get(Object template) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public Query query() throws DatabaseClosedException {
		throw new NotImplementedException();
	}

	public <TargetType> ObjectSet<TargetType> query(Class<TargetType> clazz) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public <TargetType> ObjectSet<TargetType> query(Predicate<TargetType> predicate) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public <TargetType> ObjectSet<TargetType> query(Predicate<TargetType> predicate, QueryComparator<TargetType> comparator) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public <TargetType> ObjectSet<TargetType> query(Predicate<TargetType> predicate, Comparator<TargetType> comparator) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public <T> ObjectSet<T> queryByExample(Object template) throws Db4oIOException, DatabaseClosedException {
		throw new NotImplementedException();
	}

	public void rollback() throws Db4oIOException, DatabaseClosedException, DatabaseReadOnlyException {
		throw new NotImplementedException();
	}

	public void store(Object obj) throws DatabaseClosedException, DatabaseReadOnlyException {
		throw new NotImplementedException();
	}
	
	public <T> QLin<T> from(Class<T> clazz) {
		throw new NotImplementedException();
	}

}
