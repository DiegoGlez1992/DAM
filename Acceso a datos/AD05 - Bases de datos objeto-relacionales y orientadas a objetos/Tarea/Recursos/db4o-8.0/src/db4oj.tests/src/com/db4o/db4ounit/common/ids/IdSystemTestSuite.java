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
package com.db4o.db4ounit.common.ids;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class IdSystemTestSuite extends FixtureBasedTestSuite {
	
	private static int MAX_VALID_ID = 1000;

	private static final int SLOT_LENGTH = 10;

	public static class IdSystemTestUnit extends AbstractDb4oTestCase implements
			OptOutMultiSession, Db4oTestCase {

		@Override
		protected void configure(Configuration config) throws Exception {
			IdSystemConfiguration idSystemConfiguration = Db4oLegacyConfigurationBridge
					.asIdSystemConfiguration(config);
			_fixture.value().apply(idSystemConfiguration);
		}

		public void testSlotForNewIdDoesNotExist() {
			int newId = idSystem().newId();
			Slot oldSlot = null;
			try {
				oldSlot = idSystem().committedSlot(newId);
			} catch (InvalidIDException ex) {

			}
			Assert.isFalse(isValid(oldSlot));
		}

		public void testSingleNewSlot() {
			int id = idSystem().newId();
			Assert.areEqual(allocateNewSlot(id), idSystem().committedSlot(id));
		}

		public void testSingleSlotUpdate() {
			int id = idSystem().newId();
			allocateNewSlot(id);

			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS
					.newInstance(id);
			Slot updatedSlot = localContainer().allocateSlot(SLOT_LENGTH);
			slotChange.notifySlotUpdated(freespaceManager(), updatedSlot);
			commit(slotChange);

			Assert.areEqual(updatedSlot, idSystem().committedSlot(id));
		}

		public void testSingleSlotDelete() {
			int id = idSystem().newId();
			allocateNewSlot(id);

			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS
					.newInstance(id);
			slotChange.notifyDeleted(freespaceManager());
			commit(slotChange);

			Assert.isFalse(isValid(idSystem().committedSlot(id)));
		}
		
		public void testReturnUnusedIds(){
			final int id = idSystem().newId();
			
			Slot slot = idSystem().committedSlot(id);
			Assert.areEqual(Slot.ZERO, slot);
			
			idSystem().returnUnusedIds(new Visitable<Integer>() {
				public void accept(Visitor4<Integer> visitor) {
					visitor.visit(id);
				}
			});
			
			if(idSystem() instanceof PointerBasedIdSystem){
				slot = idSystem().committedSlot(id);
				Assert.areEqual(Slot.ZERO, slot);
			} else {
				Assert.expect(InvalidIDException.class, new CodeBlock() {
					public void run() throws Throwable {
						idSystem().committedSlot(id);
					}
				});
			}
			
		}

		private Slot allocateNewSlot(int newId) {
			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS
					.newInstance(newId);
			Slot allocatedSlot = localContainer().allocateSlot(SLOT_LENGTH);
			slotChange.notifySlotCreated(allocatedSlot);
			commit(slotChange);
			return allocatedSlot;
		}

		private void commit(final SlotChange... slotChanges) {
			idSystem().commit(new Visitable<SlotChange>() {
				public void accept(Visitor4<SlotChange> visitor) {
					for (SlotChange slotChange : slotChanges) {
						visitor.visit(slotChange);
					}
				}
			}, FreespaceCommitter.DO_NOTHING);
		}

		private LocalObjectContainer localContainer() {
			return (LocalObjectContainer) container();
		}

		private boolean isValid(Slot slot) {
			return ! Slot.isNull(slot);
		}

		private FreespaceManager freespaceManager() {
			return localContainer().freespaceManager();
		}

		private IdSystem idSystem() {
			return localContainer().idSystem();
		}

	}
	
	public static class IdOverflowTestUnit extends AbstractDb4oTestCase implements
		OptOutMultiSession, Db4oTestCase {
		
		public void testNewIdOverflow(){
			
			if(! _fixture.value().supportsIdOverflow()){
				return;
			}
			
			LocalObjectContainer container = (LocalObjectContainer) container();
			
			final IdSystem idSystem = _fixture.value().newInstance(container);
			
			final List<Integer> allFreeIds = allocateAllAvailableIds(idSystem);
			assertNoMoreIdAvailable(idSystem);
			
			final List<Integer> subSetOfIds = new ArrayList<Integer>();
			
			int counter = 0;
			for (int currentId : allFreeIds){
				counter++;
				if(counter % 3 == 0){
					subSetOfIds.add(currentId);
				}
			}
			
			assertFreeAndReallocate(idSystem, subSetOfIds);
			assertFreeAndReallocate(idSystem, allFreeIds);
			
		}

		private void assertFreeAndReallocate(final IdSystem idSystem,
				final List<Integer> ids) {
			
			// Boundary condition: Last ID. Produced a bug when implementing. 
			if(! ids.contains(MAX_VALID_ID)){
				ids.add(MAX_VALID_ID);
			}
			
			Assert.isGreater(0, ids.size());
			
			idSystem.returnUnusedIds(new Visitable<Integer>() {
				public void accept(Visitor4<Integer> visitor) {
					for(Integer expectedFreeId : ids){
						visitor.visit(expectedFreeId);	
					}
				}
			});
			
			int freedCount = ids.size();
			
			for (int i = 0; i < freedCount; i++) {
				int newId = idSystem.newId();
				Assert.isTrue(ids.contains(newId));
				ids.remove((Object)newId);
			}
			
			Assert.isTrue(ids.size() == 0);
			assertNoMoreIdAvailable(idSystem);
		}

		private List<Integer> allocateAllAvailableIds(final IdSystem idSystem) {
			final List<Integer> ids = new ArrayList<Integer>();
			int newId = 0;
			do{
				newId = idSystem.newId();
				ids.add(newId);
			}
			while(newId < MAX_VALID_ID);
			return ids;
		}

		private void assertNoMoreIdAvailable(final IdSystem idSystem) {
			Assert.expect(Db4oFatalException.class, new CodeBlock() {
				public void run() throws Throwable {
					idSystem.newId();
				}
			});
		}
		
	}

	private static FixtureVariable<IdSystemProvider> _fixture = FixtureVariable
			.newInstance("IdSystem");

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider<IdSystemProvider>(
						_fixture, new IdSystemProvider() {
							public void apply(
									IdSystemConfiguration idSystemConfiguration) {
								idSystemConfiguration.usePointerBasedSystem();
							}

							public IdSystem newInstance(LocalObjectContainer container) {
								return null;
							}

							public boolean supportsIdOverflow() {
								return false;
							}

							public String label() {
								return "PointerBased";
							}
						}, new IdSystemProvider() {
							public void apply(
									IdSystemConfiguration idSystemConfiguration) {
								idSystemConfiguration.useInMemorySystem();
							}

							public IdSystem newInstance(LocalObjectContainer container) {
								return new InMemoryIdSystem(container, MAX_VALID_ID);
							}

							public boolean supportsIdOverflow() {
								return true;
							}

							public String label() {
								return "InMemory";
							}
						}, new IdSystemProvider() {
							public void apply(
									IdSystemConfiguration idSystemConfiguration) {
								idSystemConfiguration.useStackedBTreeSystem();
							}

							public IdSystem newInstance(LocalObjectContainer container) {
								return new BTreeIdSystem(container, new InMemoryIdSystem(container), MAX_VALID_ID);
							}

							public boolean supportsIdOverflow() {
								
								// FIXME: implement next
								
								return false;
							}

							public String label() {
								return "BTree";
							}
						}) };
	}

	@Override
	public Class[] testUnits() {
		return new Class[] { 
				IdSystemTestUnit.class,
				IdOverflowTestUnit.class,
				};
	}
	
	
	private static interface IdSystemProvider extends Labeled{
		
		public void apply(IdSystemConfiguration idSystemConfiguration);
		
		public boolean supportsIdOverflow();

		public IdSystem newInstance(LocalObjectContainer container);
		
	}
		
		
		
		
		
	
	

}
