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
package com.db4o.filestats;

import static com.db4o.filestats.FileUsageStatsUtil.*;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * Byte usage statistics for a db4o database file
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class FileUsageStats {
	private TreeStringObject<ClassUsageStats> _classUsageStats = null;
	private long _fileSize;
	private final long _fileHeader;
	private final long _freespace;
	private final long _idSystem;
	private final long _classMetadata;
	private final long _freespaceUsage;
	private final long _uuidUsage;
	private final long _commitTimestampUsage;
	private final SlotMap _slots;
	
	FileUsageStats(long fileSize, long fileHeader, long idSystem, long freespace, long classMetadata, long freespaceUsage, long uuidUsage, SlotMap slots, long commitTimestampUsage) {
		_fileSize = fileSize;
		_fileHeader = fileHeader;
		_idSystem = idSystem;
		_freespace = freespace;
		_classMetadata = classMetadata;
		_freespaceUsage = freespaceUsage;
		_uuidUsage = uuidUsage;
		_slots = slots;
		_commitTimestampUsage = commitTimestampUsage;
	}
	
	/**
	 * @return bytes used by the db4o file header (static and variable parts)
	 */
	public long fileHeader() {
		return _fileHeader;
	}

	/**
	 * @return total number of bytes registered as freespace, available for reuse
	 */
	public long freespace() {
		return _freespace;
	}

	/**
	 * @return bytes used by the id system indices
	 */
	public long idSystem() {
		return _idSystem;
	}

	/**
	 * @return number of bytes used for class metadata (class metadata repository and schema definitions)
	 */
	public long classMetadata() {
		return _classMetadata;
	}

	/**
	 * @return number of bytes used for the bookkeeping of the freespace system itself
	 */
	public long freespaceUsage() {
		return _freespaceUsage;
	}

	/**
	 * @return number of bytes used for the uuid index
	 */
	public long uuidUsage() {
		return _uuidUsage;
	}

	/**
	 * @return number of bytes used for the commit timestamp indexes
	 */
	public long commitTimestampUsage() {
		return _commitTimestampUsage;
	}

	/**
	 * @return total file size in bytes
	 */
	public long fileSize() {
		return _fileSize;
	}
	
	/**
	 * @return number of bytes used aggregated from all categories - should always be equal to {@link #fileSize()}
	 */
	public long totalUsage() {
		final LongByRef total = new LongByRef(_fileHeader + _freespace + _idSystem + _classMetadata + _freespaceUsage + _uuidUsage + _commitTimestampUsage);
		Tree.traverse(_classUsageStats, new Visitor4<TreeStringObject<ClassUsageStats>>() {
			public void visit(TreeStringObject<ClassUsageStats> node) {
				total.value += node._value.totalUsage();
			}
		});
		return total.value;
	}

	/**
	 * @return the statistics for each persisted class
	 */
	public Iterator<ClassUsageStats> classUsageStats() {
		return Iterators.platformIterator(new TreeNodeIterator(_classUsageStats));
	}

	/**
	 * @param name a fully qualified class name
	 * @return the statistics for the class with the given name
	 */
	public ClassUsageStats classStats(String name) {
		TreeStringObject<ClassUsageStats> found = (TreeStringObject<ClassUsageStats>) Tree.find(_classUsageStats, new TreeStringObject<ClassUsageStats>(name, null));
		return found == null ? null : found._value;
	}
	
	@Override
	public String toString() {
		final StringBuffer str = new StringBuffer();
		Tree.traverse(_classUsageStats, new Visitor4<TreeStringObject<ClassUsageStats>>() {
			public void visit(TreeStringObject<ClassUsageStats> node) {
				node._value.toString(str);
			}
		});
		str.append("\n");
		str.append(formatLine("File header", fileHeader()));
		str.append(formatLine("Freespace", freespace()));
		str.append(formatLine("ID system", idSystem()));
		str.append(formatLine("Class metadata", classMetadata()));
		str.append(formatLine("Freespace usage", freespaceUsage()));
		str.append(formatLine("UUID usage", uuidUsage()));
		str.append(formatLine("Version usage", commitTimestampUsage()));
		str.append("\n");
		long totalUsage = totalUsage();
		str.append(formatLine("Total", totalUsage));
		str.append(formatLine("Unaccounted", fileSize() - totalUsage));
		str.append(formatLine("File", fileSize()));
		str.append(_slots);
		return str.toString();
	}
	
	void addClassStats(ClassUsageStats classStats) {
		_classUsageStats = Tree.add(_classUsageStats, new TreeStringObject<ClassUsageStats>(classStats.className(), classStats));
	}
	
	void addSlot(Slot slot) {
		_slots.add(slot);
	}	
}