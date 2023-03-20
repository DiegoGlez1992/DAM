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
package com.db4o.test.types;

import java.util.*;

public class TypedPublic extends RTest
{
	public Boolean oBoolean;
	public Boolean nBoolean;
	public boolean sBoolean;
	
	public Byte oByte;
	public Byte nByte;
	public byte sByte;
	
	public Character oCharacter;
	public Character nCharacter;
	public char sChar;

	public Double oDouble;
	public Double nDouble;
	public double sDouble;
	
	public Float oFloat;
	public Float nFloat;
	public float sFloat;
	
	public Integer oInteger;
	public Integer nInteger;
	public int sInteger;
	
	public Long oLong;
	public Long nLong;
	public long	sLong;

	public Short oShort;
	public Short nShort;
	public short sShort;
	
	public String oString;
	public String nString;
	
	public Date oDate;
	public Date nDate;
	
	public ObjectSimplePublic oObject;
	public ObjectSimplePublic nObject;

	public void set(int ver){
		if(ver == 1){
			oBoolean = new Boolean(true);
			nBoolean = null;
			sBoolean = false;
	
			oByte = new Byte(Byte.MAX_VALUE);
			nByte = null;
			sByte = Byte.MIN_VALUE;
		
			oCharacter = new Character((char)(Character.MAX_VALUE - 1));
			nCharacter = null;
			sChar = Character.MIN_VALUE;

			oDouble = new Double(Double.MAX_VALUE - 1);
			nDouble = null;
			sDouble = Double.MIN_VALUE;
	
			oFloat = new Float(Float.MAX_VALUE - 1);
			nFloat = null;
			sFloat = Float.MIN_VALUE;
	
			oInteger = new Integer(Integer.MAX_VALUE - 1);
			nInteger = null;
			sInteger = Integer.MIN_VALUE;
	
			oLong = new Long(Long.MAX_VALUE - 1);
			nLong = null;
			sLong = Long.MIN_VALUE;

			oShort = new Short((short)(Short.MAX_VALUE - 1));
			nShort = null;
			sShort = Short.MIN_VALUE;
	
			oString = "db4o rules";
			nString = null;
		
			oDate = new GregorianCalendar(2000,0,1).getTime();
			nDate = null;
		
			oObject = new ObjectSimplePublic("s1");
			nObject = null;
		}else{
			oBoolean = new Boolean(false);
			nBoolean = new Boolean(true);
			sBoolean = true;
	
			oByte = new Byte((byte)0);
			nByte = new Byte(Byte.MIN_VALUE);
			sByte = Byte.MAX_VALUE;
		
			oCharacter = new Character((char)0);
			nCharacter = new Character(Character.MIN_VALUE);
			sChar = (char)(Character.MAX_VALUE - 1);

			oDouble = new Double(0);
			nDouble = new Double(Double.MIN_VALUE);
			sDouble = Double.MAX_VALUE - 1;
	
			oFloat = new Float(0);
			nFloat = new Float(Float.MIN_VALUE);
			sFloat = Float.MAX_VALUE - 1;
	
			oInteger = new Integer(0);
			nInteger = new Integer(Integer.MIN_VALUE);
			sInteger = Integer.MAX_VALUE - 1;
	
			oLong = new Long(0);
			nLong = new Long(Long.MIN_VALUE);
			sLong = Long.MAX_VALUE - 1;

			oShort = new Short((short)0);
			nShort = new Short(Short.MIN_VALUE);
			sShort = (short)(Short.MAX_VALUE - 1);
	
			oString = "db4o rules of course";
			nString = "yeah";
		
			oDate = null;
			nDate = new GregorianCalendar(2001,1,1).getTime();
		
			oObject = new ObjectSimplePublic("s2o");
			nObject = new ObjectSimplePublic("s2n");
		}
	}
}
