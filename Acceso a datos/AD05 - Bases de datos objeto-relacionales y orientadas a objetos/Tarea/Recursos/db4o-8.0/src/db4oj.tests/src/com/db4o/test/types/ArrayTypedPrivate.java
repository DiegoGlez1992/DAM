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

@SuppressWarnings("unused")
public class ArrayTypedPrivate extends RTest
{
	private Boolean[] oBoolean;
	private Boolean[] nBoolean;
	private boolean[] sBoolean;
	
	/*
	private Byte[] oByte;
	private Byte[] nByte;
	private byte[] sByte;
	*/
	
	private Character[] oCharacter;
	private Character[] nCharacter;
	private char[] sChar;

	private Double[] oDouble;
	private Double[] nDouble;
	private double[] sDouble;
	
	private Float[] oFloat;
	private Float[] nFloat;
	private float[] sFloat;
	
	private Integer[] oInteger;
	private Integer[] nInteger;
	private int[] sInteger;
	
	private Long[] oLong;
	private Long[] nLong;
	private long[] sLong;

	private Short[] oShort;
	private Short[] nShort;
	private short[] sShort;
	
	private String[] oString;
	private String[] nString;
	
	private Date[] oDate;
	private Date[] nDate;
	
	private ObjectSimplePrivate[] oObject;
	private ObjectSimplePrivate[] nObject;

	public void set(int ver){
		if(ver == 1){
			oBoolean = new Boolean[]{new Boolean(true), new Boolean(false), null };
			nBoolean = null;
			sBoolean = new boolean[]{true, true, false};
	
			/*
			oByte = new Byte[]{ new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0), null};
			nByte = null;
			sByte = new byte[]{Byte.MAX_VALUE, Byte.MIN_VALUE, 0, 1};
			*/
		
			oCharacter = new Character[]{ new Character((char)(Character.MAX_VALUE - 1)), new Character(Character.MIN_VALUE), new Character((char)(0)),null};
			nCharacter = null;
			sChar = new char[]{(char)(Character.MAX_VALUE - 1), Character.MIN_VALUE, (char)(0)};

			oDouble = new Double[]{new Double(Double.MAX_VALUE - 1), new Double(Double.MIN_VALUE), new Double(0), null };
			nDouble = null;
			sDouble = new double[]{Double.MAX_VALUE - 1, Double.MIN_VALUE, 0 };
	
			oFloat = new Float[] {new Float(Float.MAX_VALUE - 1), new Float(Float.MIN_VALUE), new Float(0), null};
			nFloat = null;
			sFloat = new float[] {Float.MAX_VALUE - 1, Float.MIN_VALUE, 0};
	
			oInteger = new Integer[] {new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(0), null};
			nInteger = null;
			sInteger = new int[] {Integer.MAX_VALUE - 1, Integer.MIN_VALUE, 0};
	
			oLong = new Long[] { new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(0), null};
			nLong = null;
			sLong = new long[] { Long.MAX_VALUE - 1, Long.MIN_VALUE, 0};

			oShort = new Short[] { new Short((short)(Short.MAX_VALUE - 1)), new Short(Short.MIN_VALUE), new Short((short)(0)), null };
			nShort = null;
			sShort = new short[] { (short)(Short.MAX_VALUE - 1), Short.MIN_VALUE, (short)0};
	
			oString = new String[] {"db4o rules", "cool", "supergreat"};
			nString = null;
		
			oDate = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
			nDate = null;
		
			oObject = new ObjectSimplePrivate[]{new ObjectSimplePrivate("so"), null, new ObjectSimplePrivate("far"), new ObjectSimplePrivate("O.K.")};
			nObject = null;
		}else{
			oBoolean = new Boolean[]{new Boolean(false), new Boolean(true), new Boolean(true)};
			nBoolean = new Boolean[]{null, new Boolean(true), new Boolean(false)};
			sBoolean = new boolean[]{true, true, true};
	
			/*
			oByte = new Byte[]{ new Byte(Byte.MIN_VALUE), new Byte(Byte.MAX_VALUE), new Byte((byte)1), new Byte((byte)-1)};
			nByte = new Byte[]{ null, new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0)};
			sByte = new byte[]{Byte.MIN_VALUE, Byte.MAX_VALUE, 0, -1, 1};
			*/
		
			oCharacter = new Character[]{ new Character(Character.MIN_VALUE), new Character((char)(Character.MAX_VALUE - 1)), new Character((char)(0)),new Character((char)(Character.MAX_VALUE - 1)),new Character((char)1)};
			nCharacter = new Character[]{ null, new Character((char)(Character.MAX_VALUE - 1)), new Character(Character.MIN_VALUE), new Character((char)(0))};
			sChar = new char[]{Character.MIN_VALUE, (char)(0)};

			oDouble = new Double[]{new Double(Double.MIN_VALUE), new Double(0)};
			nDouble = new Double[]{null, new Double(Double.MAX_VALUE - 1), new Double(Double.MIN_VALUE), new Double(- 123.12344), new Double( - 12345.123445566)};
			sDouble = new double[]{Double.MAX_VALUE - 1, Double.MIN_VALUE, 0, 0.12344, - 123.12344 };
	
			oFloat = new Float[] {new Float((float)- 98.765)};
			nFloat = null;
			sFloat = new float[] {(float)- 0.55, Float.MAX_VALUE - 1, Float.MIN_VALUE, 0, (float)0.33};
	
			oInteger = new Integer[] {new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(111), new Integer(-333)};
			nInteger = new Integer[] {null, new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(0)};
			sInteger = new int[] {888, 666, 999, 101010, 111111};
	
			oLong = new Long[] { new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(1)};
			nLong = new Long[] { null, new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(0)};
			sLong = new long[] { Long.MAX_VALUE - 1, Long.MIN_VALUE};

			oShort = new Short[] { new Short(Short.MIN_VALUE), new Short((short)(Short.MAX_VALUE - 1)), new Short((short)(0))};
			nShort = new Short[] { new Short((short)(Short.MAX_VALUE - 1)), null, new Short(Short.MIN_VALUE), new Short((short)(0))};
			sShort = null;
	
			oString = new String[] {"db4o rulez", "cool", "supergreat"};
			nString = new String[] {null, "db4o rules", "cool", "supergreat", null};
		
			oDate = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(1999,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime()};
			nDate = new Date[] {null, new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
		
			oObject = new ObjectSimplePrivate[]{new ObjectSimplePrivate("works"),  new ObjectSimplePrivate("far"), new ObjectSimplePrivate("excellent")};
			nObject = new ObjectSimplePrivate[]{};
		}
	}
	
	public boolean jdk2(){
		return true;
	}
}
