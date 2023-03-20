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
package com.db4o.db4ounit.common.exceptions;

import com.db4o.db4ounit.common.assorted.*;
import com.db4o.db4ounit.common.exceptions.ExceptionSimulatingStorage.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

public class ExceptionSimulatingIdSystem extends DelegatingIdSystem{
	
	private final ExceptionFactory _exceptionFactory;
	
	private final ExceptionTriggerCondition _triggerCondition = new ExceptionTriggerCondition();

	public ExceptionSimulatingIdSystem(LocalObjectContainer container, ExceptionFactory exceptionFactory) {
		super(container);
		_exceptionFactory = exceptionFactory;
	}
	
	private void resetShutdownState() {
		_triggerCondition._isClosed = false;
	}

	public void triggerException(boolean exception) {
		resetShutdownState();
		_triggerCondition._triggersException = exception;
	}

	public boolean triggersException() {
		return this._triggerCondition._triggersException;
	}

	public boolean isClosed() {
		return _triggerCondition._isClosed;
	}
	
	@Override
	public Slot committedSlot(int id) {
		if (triggersException()) {
			_exceptionFactory.throwException();
		}
		return super.committedSlot(id);
	}
	
	@Override
	public int newId() {
		if (triggersException()) {
			_exceptionFactory.throwException();
		}
		return super.newId();
	}
	
	@Override
	public void close() {
		super.close();
		if(triggersException()) {
			_exceptionFactory.throwOnClose();
		}
	}
	
	@Override
	public void completeInterruptedTransaction(int transactionId1,
			int transactionId2) {
		if (triggersException()) {
			_exceptionFactory.throwException();
		}
		super.completeInterruptedTransaction(transactionId1, transactionId2);
	}
	
	@Override
	public void commit(Visitable<SlotChange> slotChanges, FreespaceCommitter freespaceCommitter) {
		if (triggersException()) {
			_exceptionFactory.throwException();
		}
		super.commit(slotChanges, freespaceCommitter);
	}
	

}
