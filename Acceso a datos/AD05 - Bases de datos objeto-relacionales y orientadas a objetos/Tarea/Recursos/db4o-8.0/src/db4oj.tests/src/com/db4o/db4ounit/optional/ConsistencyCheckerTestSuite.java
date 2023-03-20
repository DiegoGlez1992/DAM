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
package com.db4o.db4ounit.optional;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.consistency.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class ConsistencyCheckerTestSuite extends FixtureBasedTestSuite {

	public static class BlockSizeSpec implements Labeled {
		private int _blockSize;
		
		public BlockSizeSpec(int blockSize) {
			_blockSize = blockSize;
		}

		public int blockSize() {
			return _blockSize;
		}
		
		public String label() {
			return String.valueOf(_blockSize);
		}
		
	}
	
	public static void main(String[] args) {
		new ConsoleTestRunner(ConsistencyCheckerTestSuite.class).run();		
	}

	private final static FixtureVariable<BlockSizeSpec> BLOCK_SIZE = FixtureVariable.newInstance("blockSize");
	
	@Override
	public Class[] testUnits() {
		return new Class[] {
			ConsistencyCheckerTestUnit.class,
		};
	}

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new SimpleFixtureProvider<BlockSizeSpec>(BLOCK_SIZE, 
						new BlockSizeSpec(1), 
						new BlockSizeSpec(7), 
						new BlockSizeSpec(9), 
						new BlockSizeSpec(13),
						new BlockSizeSpec(17), 
						new BlockSizeSpec(19)
				),
		};
	}

	public static class Item {
		byte[] bytes = new byte[BLOCK_SIZE.value().blockSize()];
	}

	public static class ConsistencyCheckerTestUnit implements TestLifeCycle {
	
		private LocalObjectContainer _db;
	
		public void testFreeUsedSlot() {
			assertInconsistencyDetected(new Procedure4<Item>() {
				public void apply(Item item) {
					int id = (int) _db.getID(item);
					Slot slot = _db.idSystem().committedSlot(id);
					_db.freespaceManager().free(slot);
				}
			});
		}
	
		public void testFreeShiftedUsedSlot() {
			assertInconsistencyDetected(new Procedure4<Item>() {
				public void apply(Item item) {
					int id = (int) _db.getID(item);
					Slot slot = _db.idSystem().committedSlot(id);
					_db.freespaceManager().free(new Slot(slot.address() + 1, slot.length()));
				}
			});
		}
	
		public void testNegativeAddressSlot() {
			assertBogusSlotDetected(-1, 10);
		}
	
		public void testExceedsFileLengthSlot() {
			assertBogusSlotDetected(Integer.MAX_VALUE - 1, 1);
		}
	
		private void assertBogusSlotDetected(final int address, final int length) {
			assertInconsistencyDetected(new Procedure4<Item>() {
				public void apply(Item item) {
					final int id = (int) _db.getID(item);
					_db.idSystem().commit(new Visitable<SlotChange>() {
						public void accept(Visitor4<SlotChange> visitor) {
							SlotChange slotChange = new SlotChange(id);
							slotChange.notifySlotCreated(new Slot(address, length));
							visitor.visit(slotChange);
						}
					}, FreespaceCommitter.DO_NOTHING);
				}
			});
		}
	
		private void assertInconsistencyDetected(Procedure4<Item> proc) {
			Item item = new Item();
			_db.store(item);
			_db.commit();
			Assert.isTrue(new ConsistencyChecker(_db).checkSlotConsistency().consistent());
			proc.apply(item);
			_db.commit();
			Assert.isFalse(new ConsistencyChecker(_db).checkSlotConsistency().consistent());
		}
	
		public void setUp() throws Exception {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.file().storage(new MemoryStorage());
			config.file().blockSize(BLOCK_SIZE.value().blockSize());
			_db = (LocalObjectContainer) Db4oEmbedded.openFile(config, "inmem.db4o");
		}
	
		public void tearDown() throws Exception {
			_db.close();
		}
	}
}
