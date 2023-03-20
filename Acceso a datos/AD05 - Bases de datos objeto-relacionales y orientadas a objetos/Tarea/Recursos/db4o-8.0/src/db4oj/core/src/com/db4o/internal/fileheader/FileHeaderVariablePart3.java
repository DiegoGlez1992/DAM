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
package com.db4o.internal.fileheader;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class FileHeaderVariablePart3 extends FileHeaderVariablePart2 {

	public FileHeaderVariablePart3(LocalObjectContainer container) {
		super(container);
	}
	
	@Override
	public int ownLength() {
		return super.ownLength() + Const4.INT_LENGTH * 2;
	}
	@Override
	protected void readBuffer(ByteArrayBuffer buffer, boolean versionsAreConsistent) {
		super.readBuffer(buffer, versionsAreConsistent);
		
		SystemData systemData = systemData();
		systemData.idToTimestampIndexId(buffer.readInt());
		systemData.timestampToIdIndexId(buffer.readInt());
	}
	
	@Override
	protected void writeBuffer(ByteArrayBuffer buffer, boolean shuttingDown) {
		super.writeBuffer(buffer, shuttingDown);
		
		SystemData systemData = systemData();
        buffer.writeInt(systemData.idToTimestampIndexId());
        buffer.writeInt(systemData.timestampToIdIndexId());
	}
	
	
}
