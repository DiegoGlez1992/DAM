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

import java.util.*;

import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;


public class FreespaceManagerTestCase extends FreespaceManagerTestCaseBase{
	
	public static void main(String[] args) {
		new FreespaceManagerTestCase().runSolo();
	}
	
	public void testAllocateTransactionLogSlot(){
		for (int i = 0; i < fm.length; i++) {
			if(fm[i].systemType() == AbstractFreespaceManager.FM_RAM){
				Slot slot = fm[i].allocateTransactionLogSlot(1);
				Assert.isNull(slot);
				
				fm[i].free(new Slot(5, 10));
				fm[i].free(new Slot(100, 5));
				fm[i].free(new Slot(140, 27));
				
				slot = fm[i].allocateSafeSlot(28);
				Assert.isNull(slot);
				Assert.areEqual(3, fm[i].slotCount());
				
				slot = fm[i].allocateSafeSlot(27);
				Assert.areEqual(2, fm[i].slotCount());
				Assert.areEqual(new Slot(140, 27), slot);
			}
		}
	}
	
	public void testConstructor() {
		for (int i = 0; i < fm.length; i++) {
			Assert.areEqual(0, fm[i].slotCount());
			Assert.areEqual(0, fm[i].totalFreespace());
		}
	}
	
	public void testFree() {
		for (int i = 0; i < fm.length; i++) {
			int count = fm[i].slotCount();
			fm[i].free(new Slot(1000, 1));
			Assert.areEqual(count + 1, fm[i].slotCount());
		}
	}
	
	public void testGetSlot(){
		for (int i = 0; i < fm.length; i++) {
			Slot slot = fm[i].allocateSlot(1);
			Assert.isNull(slot);
			Assert.areEqual(0, fm[i].slotCount());
			
			fm[i].free(new Slot(10, 1));
			slot = fm[i].allocateSlot(1);
			Assert.areEqual(slot.address(), 10);
			Assert.areEqual(0, fm[i].slotCount());
			
			slot = fm[i].allocateSlot(1);
			Assert.isNull(slot);
			
			fm[i].free(new Slot(10, 1));
			fm[i].free(new Slot(20, 2));
			slot = fm[i].allocateSlot(1);
			Assert.areEqual(1, fm[i].slotCount());
			Assert.areEqual(slot.address(), 10);
			
			slot = fm[i].allocateSlot(3);
			Assert.isNull(slot);
			
			slot = fm[i].allocateSlot(1);
			Assert.isNotNull(slot);

		}
	}
	
	public void testMerging() {
		for (int i = 0; i < fm.length; i++) {
			fm[i].free(new Slot(5, 5));
			fm[i].free(new Slot(15, 5));
			fm[i].free(new Slot(10, 5));
			Assert.areEqual(1, fm[i].slotCount());
		}
	}
	
	public void testTotalFreeSpace(){
		for (int i = 0; i < fm.length; i++) {
			fm[i].free(new Slot(5, 10));
			fm[i].free(new Slot(100, 5));
			fm[i].free(new Slot(140, 27));
			Assert.areEqual(42, fm[i].totalFreespace());
			fm[i].allocateSlot(8);
			Assert.areEqual(32, fm[i].totalFreespace());
			fm[i].allocateSlot(6);
			Assert.areEqual(26, fm[i].totalFreespace());
			fm[i].free(new Slot(120, 14));
			Assert.areEqual(40, fm[i].totalFreespace());
		}
	}
	
	public void testMigrateTo(){
		for (int from = 0; from < fm.length; from++) {
			for (int to = 0; to < fm.length; to++) {
				if(to != from){
					
					clear(fm[from]);
					clear(fm[to]);
					
                    AbstractFreespaceManager.migrate(fm[from], fm[to]);
                    
					assertSame(fm[from], fm[to]);
					
					fm[from].free(new Slot(5, 10));
					fm[from].free(new Slot(100, 5));
					fm[from].free(new Slot(140, 27));
                    AbstractFreespaceManager.migrate(fm[from], fm[to]);
					
					assertSame(fm[from], fm[to]);
				}
			}
		}
	}
	
	public void testListener(){
		for (int i = 0; i < fm.length; i++) {
			final ArrayList<Freespace> removed = new ArrayList<Freespace>();
			final ArrayList<Freespace> added = new ArrayList<Freespace>();
			fm[i].listener(new FreespaceListener() {
			
				public void slotRemoved(int size) {
					removed.add(new Freespace(size));
				}
			
				public void slotAdded(int size) {
					added.add(new Freespace(size));
				}
			});
			fm[i].free(new Slot(5, 100));
			Assert.isTrue(added.contains(new Freespace(100)));
			fm[i].allocateSlot(30);
			Assert.isTrue(removed.contains(new Freespace(100)));
			Assert.isTrue(added.contains(new Freespace(70)));
		}
		
	}
	
	public static class Freespace{
		
		private final int _size;
		
		public Freespace(int size){
			_size = size;
		}
		
		@Override
		public boolean equals(Object obj) {
			Freespace other = (Freespace) obj;
			return _size == other._size;
		}
		
	}
	
	

}
