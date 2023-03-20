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
package com.db4o.db4ounit.jre11.soda;

import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SodaNumberCoercionTestCase extends AbstractDb4oTestCase {
	private static final String DOUBLEFIELD = "_doubleValue";
	private static final String FLOATFIELD = "_floatValue";
	private static final String LONGFIELD = "_longValue";
	private static final String INTFIELD = "_intValue";
	private static final String SHORTFIELD = "_shortValue";
	private static final String BYTEFIELD = "_byteValue";
	private static final Float ROUNDFLOATVALUE = new Float(100);
	private static final Double ROUNDDOUBLEVALUE = new Double(100);
	private static final Long LONGVALUE = new Long(100);
	private static final Integer INTVALUE = new Integer(100);
	private static final Short SHORTVALUE = new Short((short)100);
	private static final Byte BYTEVALUE = new Byte((byte)100);

	public static class Thing {
		public byte _byteValue;
		public short _shortValue;
		public int _intValue;
		public long _longValue;
		public float _floatValue;
		public double _doubleValue;

		public Thing(byte byteValue,short shortValue,int intValue,long longValue,float floatValue,double doubleValue) {
			_byteValue=byteValue;
			_shortValue=shortValue;
			_intValue=intValue;
			_longValue=longValue;
			_floatValue=floatValue;
			_doubleValue=doubleValue;
		}
	}

	protected void store() {
		store(new Thing((byte)10,(short)10,10,10L,10f,10d));
		store(new Thing((byte)100,(short)100,100,100L,100f,100d));
		store(new Thing((byte)42,(short)42,42,42L,42f,42d));
		store(new Thing((byte)111,(short)111,111,111L,111.11f,111.11d));
	}

	public void testIntegerTypes() {
		assertSingleCoercionResult(BYTEFIELD,SHORTVALUE);
		assertSingleCoercionResult(BYTEFIELD,INTVALUE);
		assertSingleCoercionResult(BYTEFIELD,LONGVALUE);

		assertSingleCoercionResult(SHORTFIELD,BYTEVALUE);
		assertSingleCoercionResult(SHORTFIELD,INTVALUE);
		assertSingleCoercionResult(SHORTFIELD,LONGVALUE);

		assertSingleCoercionResult(INTFIELD,BYTEVALUE);
		assertSingleCoercionResult(INTFIELD,SHORTVALUE);
		assertSingleCoercionResult(INTFIELD,LONGVALUE);

		assertSingleCoercionResult(LONGFIELD,BYTEVALUE);
		assertSingleCoercionResult(LONGFIELD,SHORTVALUE);
		assertSingleCoercionResult(LONGFIELD,INTVALUE);
	}

	public void testFloatingPointTypes() {
		assertSingleCoercionResult(FLOATFIELD,ROUNDDOUBLEVALUE);
		assertSingleCoercionResult(DOUBLEFIELD,ROUNDFLOATVALUE);
	}

	public void testMixed() {
		assertSingleCoercionResult(BYTEFIELD, ROUNDFLOATVALUE);
		assertSingleCoercionResult(BYTEFIELD, ROUNDDOUBLEVALUE);
		assertSingleCoercionResult(SHORTFIELD, ROUNDFLOATVALUE);
		assertSingleCoercionResult(SHORTFIELD, ROUNDDOUBLEVALUE);
		assertSingleCoercionResult(INTFIELD, ROUNDFLOATVALUE);
		assertSingleCoercionResult(INTFIELD, ROUNDDOUBLEVALUE);
		assertSingleCoercionResult(LONGFIELD, ROUNDFLOATVALUE);
		assertSingleCoercionResult(LONGFIELD, ROUNDDOUBLEVALUE);
		assertSingleCoercionResult(FLOATFIELD, BYTEVALUE);
		assertSingleCoercionResult(FLOATFIELD, SHORTVALUE);
		assertSingleCoercionResult(FLOATFIELD, INTVALUE);
		assertSingleCoercionResult(FLOATFIELD, LONGVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, BYTEVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, SHORTVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, INTVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, LONGVALUE);
	}
	
	public void testRounding() {
		assertCoercionResult(FLOATFIELD, new Double(111.11), 0); // 111.11f!=111.11d
		assertCoercionResult(FLOATFIELD, new Integer(111), 0);
		assertCoercionResult(INTFIELD, new Float(111.11), 0);
	}
	
	public void testOverflow() {
		long cmpval=((long)Integer.MAX_VALUE)-Integer.MIN_VALUE+1+INTVALUE.intValue();
		assertCoercionResult(INTFIELD, new Long(cmpval), 0);
	}
	
	private void assertSingleCoercionResult(String fieldName,Number value) {
		assertCoercionResult(fieldName,value,1);
	}
	
	private void assertCoercionResult(String fieldName,Number value,int expCount) {
		Query q = newQuery(Thing.class);
		Constraint constr=q.descend(fieldName).constrain(value);
		Assert.isNotNull(constr);
		Assert.areEqual(expCount, q.execute().size());
	}
}
