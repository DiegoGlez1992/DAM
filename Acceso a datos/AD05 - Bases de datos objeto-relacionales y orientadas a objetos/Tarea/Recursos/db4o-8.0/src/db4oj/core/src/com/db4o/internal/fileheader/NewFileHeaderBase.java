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

import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class NewFileHeaderBase extends FileHeader {
    
	protected static final byte[] SIGNATURE = {(byte)'d', (byte)'b', (byte)'4', (byte)'o'};
	
	protected static final int HEADER_LOCK_OFFSET = SIGNATURE.length + 1;
	
	protected static final int OPEN_TIME_OFFSET = HEADER_LOCK_OFFSET + Const4.INT_LENGTH;
	
	protected static final int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + Const4.LONG_LENGTH;
	
	
	protected TimerFileLock _timerFileLock;
	
	protected FileHeaderVariablePart _variablePart;

	public void close() throws Db4oIOException {
		if(_timerFileLock == null){
			return;
		}
	    _timerFileLock.close();
	}

	protected void newTimerFileLock(LocalObjectContainer file) {
	    _timerFileLock = TimerFileLock.forFile(file);
	    _timerFileLock.setAddresses(0, OPEN_TIME_OFFSET, ACCESS_TIME_OFFSET);
	}
	
	protected abstract NewFileHeaderBase createNew();
	
    protected abstract byte version();

	public final void initNew(LocalObjectContainer file) throws Db4oIOException {
	    newTimerFileLock(file);
		oldEncryptionOff(file);
	    _variablePart = createVariablePart(file);
	    writeVariablePart(file);
	}
	
	public abstract FileHeaderVariablePart createVariablePart(LocalObjectContainer file);

	protected void oldEncryptionOff(LocalObjectContainer file) {
		file._handlers.oldEncryptionOff();
	}

	public final void writeVariablePart(LocalObjectContainer file, boolean shuttingDown) {
		if(! isInitalized()){
			return;
		}
		Runnable commitHook = commit(shuttingDown);
		file.syncFiles();
		commitHook.run();
		file.syncFiles();
	}

	private boolean isInitalized() {
		return _variablePart != null;
	}

	protected FileHeader newOnSignatureMatch(LocalObjectContainer file, ByteArrayBuffer reader) {
	    if(signatureMatches(reader, SIGNATURE, version())){
	        return createNew();
	    }
	    return null;
	}

	@Override
	public void completeInterruptedTransaction(LocalObjectContainer container) {
		SystemData systemData = container.systemData();
		container.idSystem().completeInterruptedTransaction(systemData.transactionPointer1(), systemData.transactionPointer2());
	}

	protected void checkThreadFileLock(LocalObjectContainer container, ByteArrayBuffer reader) {
		reader.seek(ACCESS_TIME_OFFSET);
		long lastAccessTime = reader.readLong();
		if(FileHeader.lockedByOtherSession(container, lastAccessTime)){
			_timerFileLock.checkIfOtherSessionAlive(container, 0, ACCESS_TIME_OFFSET, lastAccessTime);
		}
	}

	@Override
	public void readIdentity(LocalObjectContainer container) {
		_variablePart.readIdentity((LocalTransaction) container.systemTransaction());
	}

	@Override
	public Runnable commit(boolean shuttingDown) {
		return _variablePart.commit(shuttingDown);
	}

}
