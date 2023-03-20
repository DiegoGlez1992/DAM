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
package com.db4o.db4ounit.common.freespace;

import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public class FreespaceManagerDiscardLimitTestCase extends FreespaceManagerTestCaseBase implements OptOutNonStandardBlockSize{
	
	public static void main(String[] args) {
		new FreespaceManagerDiscardLimitTestCase().runSolo();
	}
	
	protected void configure(Configuration config) {
		config.freespace().discardSmallerThan(10 * ((Config4Impl)config).blockSize());
	}
	
	public void testGetSlot(){
		for (int i = 0; i < fm.length; i++) {
			if(fm[i].systemType() == AbstractFreespaceManager.FM_IX){
				continue;
			}
			fm[i].free(new Slot(20,15));
			
			Slot slot = fm[i].allocateSlot(5);
			assertSlot(new Slot(20,15), slot);
			Assert.areEqual(0, fm[i].slotCount());
			fm[i].free(slot);
			Assert.areEqual(1, fm[i].slotCount());
			
			slot = fm[i].allocateSlot(6);
			assertSlot(new Slot(20,15), slot);
			Assert.areEqual(0, fm[i].slotCount());
			fm[i].free(slot);
			Assert.areEqual(1, fm[i].slotCount());
			slot = fm[i].allocateSlot(10);
			assertSlot(new Slot(20,15), slot);
			Assert.areEqual(0, fm[i].slotCount());
		}
	}
	
	

}
