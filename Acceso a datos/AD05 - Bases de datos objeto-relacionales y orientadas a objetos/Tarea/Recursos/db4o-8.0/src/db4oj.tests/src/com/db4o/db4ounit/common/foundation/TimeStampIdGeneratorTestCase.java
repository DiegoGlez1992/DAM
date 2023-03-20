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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

import static db4ounit.extensions.util.Binary.*;

public class TimeStampIdGeneratorTestCase implements TestCase {
	
	public void testObjectCounterPartOnlyUses6Bits(){
		
		long[] ids = generateIds();
		
		for (int i = 1; i < ids.length; i++) {
			Assert.isGreater(ids[i] - 1, ids[i]);
			long creationTime = TimeStampIdGenerator.idToMilliseconds(ids[i]);
			long timePart = TimeStampIdGenerator.millisecondsToId(creationTime);
			long objectCounter = ids[i] - timePart;
			
			// 6 bits
			Assert.isSmallerOrEqual(longForBits(6), objectCounter);
		}
	}

	private long[] generateIds() {
		int count = 500;
		TimeStampIdGenerator generator = new TimeStampIdGenerator();
		long[] ids = new long[count];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = generator.generate(); 
		}
		return ids;
	}
	
	public void testContinousIncrement(){
		TimeStampIdGenerator generator = new TimeStampIdGenerator();
		assertContinousIncrement(generator);
	}

	private void assertContinousIncrement(TimeStampIdGenerator generator) {
		long oldId = generator.generate();
		for (int i = 0; i < 1000000; i++) {
			long newId = generator.generate();
			Assert.isGreater(oldId, newId);
			oldId = newId;
		}
	}
	
	public void testTimeStaysTheSame(){
		TimeStampIdGenerator generatorWithSameTime = new TimeStampIdGenerator(){
			protected long now() {
				return 1;
			};
		};
		assertContinousIncrement(generatorWithSameTime);
	}

}
