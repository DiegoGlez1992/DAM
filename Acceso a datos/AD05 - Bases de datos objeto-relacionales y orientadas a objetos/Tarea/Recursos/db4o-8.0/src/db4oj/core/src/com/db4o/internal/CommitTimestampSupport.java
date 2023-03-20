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
package com.db4o.internal;

import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;

public class CommitTimestampSupport {
	
	private BTree _idToTimestamp;
	private BTree _timestampToId;
	private final LocalObjectContainer _container;

	public CommitTimestampSupport(LocalObjectContainer container) {
		_container = container;
	}

	public void ensureInitialized() {
		if(_idToTimestamp != null){
			return;
		}
		if (! _container.config().generateCommitTimestamps().definiteYes()) {
			return;
		}
		initialize();
	}

	public BTree idToTimestamp() {
		if(_idToTimestamp != null){
			return _idToTimestamp;
		}
		ensureInitialized();
		return _idToTimestamp;
	}
	
	public BTree timestampToId() {
		if(_timestampToId != null){
			return _timestampToId;
		}
		ensureInitialized();
		return _timestampToId;
	}

	private void initialize() {
		
		int idToTimestampIndexId = _container.systemData().idToTimestampIndexId();
		int timestampToIdIndexId = _container.systemData().timestampToIdIndexId();
		
		if(_container.config().isReadOnly()){
			if(idToTimestampIndexId == 0){
				return;
			}
		}
		
		_idToTimestamp = new BTree(_container.systemTransaction(), idToTimestampIndexId, new TimestampEntryById());
		_timestampToId = new BTree(_container.systemTransaction(), timestampToIdIndexId, new IdEntryByTimestamp());

		if (idToTimestampIndexId != _idToTimestamp.getID()) {
			storeBtreesIds();
		}

		EventRegistryFactory.forObjectContainer(_container).committing().addListener(new EventListener4<CommitEventArgs>() {
			public void onEvent(Event4<CommitEventArgs> e, CommitEventArgs args) {

				LocalTransaction trans = (LocalTransaction) args.transaction();
				long transactionTimestamp = trans.timestamp();
				
				long commitTimestamp = 
					(transactionTimestamp > 0) ? transactionTimestamp :_container.generateTimeStampId();
				

				Transaction sysTrans = trans.systemTransaction();

				addTimestamp(sysTrans, args.added().iterator(), commitTimestamp);
				addTimestamp(sysTrans, args.updated().iterator(), commitTimestamp);
				addTimestamp(sysTrans, args.deleted().iterator(), 0);
			}

			private void addTimestamp(Transaction trans, Iterator4 it, long commitTimestamp) {
				while (it.moveNext()) {
					ObjectInfo objInfo = (ObjectInfo) it.current();
					TimestampEntry te = new TimestampEntry((int) objInfo.getInternalID(), commitTimestamp);
					TimestampEntry oldEntry = (TimestampEntry) _idToTimestamp.remove(trans, te);
					if(oldEntry != null){
						_timestampToId.remove(trans, oldEntry);
					}
					if (commitTimestamp != 0) {
						_idToTimestamp.add(trans, te);
						_timestampToId.add(trans, te);
					}
				}
			}
		});
	}

	private void storeBtreesIds() {
		_container.systemData().idToTimestampIndexId(_idToTimestamp.getID());
		_container.systemData().timestampToIdIndexId(_timestampToId.getID());
		_container.getFileHeader().writeVariablePart(_container);
	}

	public static class TimestampEntry implements FieldIndexKey {

		public final int objectId;
		public final long commitTimestamp;

		@Override
		public String toString() {
			return "TimestampEntry [objectId=" + objectId + ", commitTimestamp=" + commitTimestamp + "]";
		}

		public TimestampEntry(int objectId, long commitTimestamp) {
			this.objectId = objectId;
			this.commitTimestamp = commitTimestamp;
		}

		public int parentID() {
			return objectId;
		}

		public long getCommitTimestamp() {
			return commitTimestamp;
		}

		public Object value() {
			return commitTimestamp;
		}

	}
	
	private static class TimestampEntryById implements Indexable4<TimestampEntry> {
		public PreparedComparison prepareComparison(Context context, final TimestampEntry first) {
			return new PreparedComparison<TimestampEntry>() {
				public int compareTo(TimestampEntry second) {
					return IntHandler.compare(first.objectId, second.objectId);
				}
			};
		}

		public int linkLength() {
			return Const4.INT_LENGTH + Const4.LONG_LENGTH;
		}

		public TimestampEntry readIndexEntry(Context context, ByteArrayBuffer reader) {
			return new TimestampEntry(reader.readInt(), reader.readLong());
		}

		public void writeIndexEntry(Context context, ByteArrayBuffer writer, TimestampEntry obj) {
			writer.writeInt(obj.parentID());
			writer.writeLong(obj.getCommitTimestamp());
		}

		public void defragIndexEntry(DefragmentContextImpl context) {
			// we are storing ids in the btree, so the order will change when the ids change
			// to properly defrag the btree we need to readd all the entries
			throw new UnsupportedOperationException();
		}
	}

	private static final class IdEntryByTimestamp extends TimestampEntryById {
		public PreparedComparison prepareComparison(Context context, final TimestampEntry first) {
			return new PreparedComparison<TimestampEntry>() {
				public int compareTo(TimestampEntry second) {
					int result = LongHandler.compare(first.commitTimestamp, second.commitTimestamp);
					if(result != 0){
						return result;
					}
					return IntHandler.compare(first.objectId, second.objectId);
				}
			};
		}
	}

	public long versionForId(int id) {

		if (idToTimestamp() == null || id == 0) {
			return 0;
		}

		TimestampEntry te = (TimestampEntry) idToTimestamp().search(_container.systemTransaction(), new TimestampEntry(id, 0));

		if (te == null) {
			return 0;
		}

		return te.getCommitTimestamp();
	}

	public void put(Transaction trans, int objectId, long version) {
		TimestampEntry te = new TimestampEntry(objectId, version);
		idToTimestamp().add(trans, te);
		timestampToId().add(trans, te);
	}

}
