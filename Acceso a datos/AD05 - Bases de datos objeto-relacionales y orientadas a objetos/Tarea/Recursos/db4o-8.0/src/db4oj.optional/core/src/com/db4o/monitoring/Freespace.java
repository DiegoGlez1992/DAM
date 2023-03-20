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
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.*;
import com.db4o.internal.freespace.*;
import com.db4o.monitoring.internal.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Freespace extends MBeanRegistrationSupport implements FreespaceMBean, FreespaceListener{
	
	private final TimedReading _reusedSlots = TimedReading.newPerSecond();
	
	private int _slotCount;
	
	private int _totalFreespace;
	
	public Freespace(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	public double getAverageSlotSize() {
		
		// Preventing division by zero concurrency by using local var
		double slotCount = _slotCount;  
		if(slotCount == 0){
			return 0;
		}
		
		return _totalFreespace / slotCount;
	}

	public double getReusedSlotsPerSecond() {
		return _reusedSlots.read();
	}

	public int getSlotCount() {
		return _slotCount;
	}

	public int getTotalFreespace() {
		return _totalFreespace;
	}

	public void slotAdded(int size) {
		_slotCount++;
		_totalFreespace+=size;
	}

	public void slotRemoved(int size) {
		_reusedSlots.increment();
		_slotCount--;
		_totalFreespace-=size;
	}

}
