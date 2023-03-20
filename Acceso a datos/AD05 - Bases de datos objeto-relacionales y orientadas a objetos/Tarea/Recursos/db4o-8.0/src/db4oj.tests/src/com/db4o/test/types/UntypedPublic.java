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

public class UntypedPublic extends RTest
{
	public Object oBoolean;
	public Object nBoolean;
	
	public Object oByte;
	public Object nByte;
	
	public Object oCharacter;
	public Object nCharacter;

	public Object oDouble;
	public Object nDouble;
	
	public Object oFloat;
	public Object nFloat;
	
	public Object oInteger;
	public Object nInteger;
	
	public Object oLong;
	public Object nLong;

	public Object oShort;
	public Object nShort;
	
	public Object oString;
	public Object nString;
	
	public Object oDate;
	public Object nDate;
	
	public Object oObject;
	public Object nObject;

	public void set(int ver){
		if(ver == 1){
			oBoolean = new Boolean(true);
			nBoolean = null;
	
			oByte = new Byte(Byte.MAX_VALUE);
			nByte = null;
		
			oCharacter = new Character((char)(Character.MAX_VALUE - 1));
			nCharacter = null;

			oDouble = new Double(Double.MAX_VALUE - 1);
			nDouble = null;
	
			oFloat = new Float(Float.MAX_VALUE - 1);
			nFloat = null;
	
			oInteger = new Integer(Integer.MAX_VALUE - 1);
			nInteger = null;
	
			oLong = new Long(Long.MAX_VALUE - 1);
			nLong = null;

			oShort = new Short((short)(Short.MAX_VALUE - 1));
			nShort = null;
	
			oString = "db4o rules";
			nString = null;
		
			oDate = new GregorianCalendar(2000,0,1).getTime();
			nDate = null;
		
			oObject = new ObjectSimplePublic("s1");
			nObject = null;
		}else{
			oBoolean = new Boolean(false);
			nBoolean = new Boolean(true);
	
			oByte = new Byte((byte)0);
			nByte = new Byte(Byte.MIN_VALUE);
		
			oCharacter = new Character((char)0);
			nCharacter = new Character(Character.MIN_VALUE);

			oDouble = new Double(0);
			nDouble = new Double(Double.MIN_VALUE);
	
			oFloat = new Float(0);
			nFloat = new Float(Float.MIN_VALUE);
	
			oInteger = new Integer(0);
			nInteger = new Integer(Integer.MIN_VALUE);
	
			oLong = new Long(0);
			nLong = new Long(Long.MIN_VALUE);

			oShort = new Short((short)0);
			nShort = new Short(Short.MIN_VALUE);
	
			oString = "db4o rules of course";
			nString = "yeah";
		
			oDate = null;
			nDate = new GregorianCalendar(2001,1,1).getTime();
		
			oObject = new ObjectSimplePublic("s2o");
			nObject = new ObjectSimplePublic("s2n");
		}
	}
}
