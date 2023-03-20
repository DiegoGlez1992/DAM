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
package com.db4o.internal.convert.conversions;

import com.db4o.internal.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.convert.ConversionStage.SystemUpStage;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public class VersionNumberToCommitTimestamp_8_0 extends Conversion {

	public static final int VERSION = 12;
	private VersionFieldMetadata versionFieldMetadata;

	public void convert(SystemUpStage stage) {
		
		LocalObjectContainer container = stage.file();
        if (! container.config().generateCommitTimestamps().definiteYes()){
            return;
        }
		container.classCollection().writeAllClasses();
		buildCommitTimestampIndex(container);

		container.systemTransaction().commit();

	}

	private void buildCommitTimestampIndex(LocalObjectContainer container) {
		versionFieldMetadata = container.handlers().indexes()._version;
		final ClassMetadataIterator i = container.classCollection().iterator();
		while (i.moveNext()) {
			final ClassMetadata clazz = i.currentClass();
			if (clazz.hasVersionField() && ! clazz.isStruct()) {
				rebuildIndexForClass(container, clazz);
			}
		}
	}

	public boolean rebuildIndexForClass(LocalObjectContainer container, ClassMetadata classMetadata) {
		long[] ids = classMetadata.getIDs();
		for (int i = 0; i < ids.length; i++) {
			rebuildIndexForObject(container, (int) ids[i]);
		}
		return ids.length > 0;
	}

	protected void rebuildIndexForObject(LocalObjectContainer container, final int objectId) throws FieldIndexException {
		StatefulBuffer writer = container.readStatefulBufferById(container.systemTransaction(), objectId);
		if (writer != null) {
			rebuildIndexForWriter(container, writer, objectId);
		}
	}

	protected void rebuildIndexForWriter(LocalObjectContainer container, StatefulBuffer buffer, final int objectId) {
		ObjectHeader objectHeader = new ObjectHeader(container, buffer);
		ObjectIdContextImpl context = new ObjectIdContextImpl(container.systemTransaction(), buffer, objectHeader, objectId);
		ClassMetadata classMetadata = context.classMetadata();
		if(classMetadata.isStruct()){
			// We don't keep version information for structs.
			return;
		}
		if (classMetadata.seekToField(container.systemTransaction(), buffer, versionFieldMetadata) != HandlerVersion.INVALID) {
			long version = (Long) versionFieldMetadata.read(context);
			if (version != 0) {
				LocalTransaction t = (LocalTransaction) container.systemTransaction();
				t.commitTimestampSupport().put(container.systemTransaction(), objectId, version);
			}
		}
	}

}
