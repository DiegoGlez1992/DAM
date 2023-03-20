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
package com.db4o.db4ounit.util;

/**
 * @sharpen.ignore
 */
public class Strings {
	
	private static final char DATESEPARATOR = '-';
	private static final char TIMESEPARATOR = ':';
	private static final char DATETIMESEPARATOR = ' ';
	
	private String _string;
	
	private Strings(){}
	
	public Strings(String str){
		this();
		_string = str;
	}
	
	public Strings(char a_char, int a_count){
		char[] chars = new char[a_count];
		for (int i = 0; i < a_count;chars[i++] = a_char);
		_string = new String(chars);
	}
	
	public Strings(int i){
		_string = "" + i;
	}
	
	public Strings(long l){
		_string = "" + l;
	}
	
	public Strings(char c){
		_string = "" + c;
	}
	
	public void clear(){
		_string = "";
	}
	
	public String getString(){
		return _string;
	}
	
	protected String joinDate(String year, String month, String day ){
		_string = year + DATESEPARATOR + month + DATESEPARATOR + day;
		return _string;
	}
	
	protected String joinTime(String hours, String minutes,String seconds){
		_string = hours + TIMESEPARATOR + minutes + TIMESEPARATOR + seconds;
		return _string;
	}

	protected String joinDateTime(String date, String time){
		_string = date + DATETIMESEPARATOR + time;
		return _string;
	}
	
	public static String _left(String a_String, int a_chars){
		return new Strings(a_String).left(a_chars);
	}
	
	public String left(int a_chars){
		if(a_chars > _string.length()){
			a_chars = _string.length();
		}else{
			if (a_chars < 0){
				a_chars = 0;
			}
		}
		return _string.substring(0,a_chars);
	}
	
	public static boolean _left(String ofString, String isString){
		return new Strings(ofString).left(isString);
	}
	
	public boolean left(String compareString){
		return left(compareString.length()).toUpperCase().equals(compareString.toUpperCase());
	}
	
	public String padLeft(char a_char, int a_length){
		return new Strings(new Strings(a_char,a_length).getString() + _string).right(a_length);
	}
	
	public String PadRight(char a_char, int a_length){
		return (_string + new Strings(a_char,a_length).getString()).substring(0,a_length);
	}
	
	public void replace(String a_Replace, String a_With){
		replace(a_Replace, a_With,0);
	}
	
	public static String replace(String source, String replace, String with){
		Strings s = new Strings(source);
		s.replace(replace, with);
		return s.getString();
	}
	
	private void replace(String replace, String with, int start){
		int pos = 0;
		while((pos = _string.indexOf(replace,start)) > -1){
			_string = _string.substring(0,pos) + with + _string.substring(pos + replace.length());
		}
	}
	
	public void replace (String begin, String end, String with, int start){
		int from = _string.indexOf(begin, start);
		if (from > -1){
			int to = _string.indexOf(end,from + 1);
			if(to > - 1){
				_string = _string.substring(0,from) + with + _string.substring(to + end.length());
				replace(begin, end, with, to);
			}
		}
	}
	
	public static String _right(String ofString, int isChar){
		return new Strings(ofString).right(isChar);
	}
	
	public String right(int a_chars){
		int l_take = _string.length() - a_chars;
		if(l_take < 0){
			l_take = 0;
		}
		return _string.substring(l_take);
	}
	
	public static boolean _right(String ofString, String compareString){
		return new Strings(ofString).right(compareString);
	}
	
	public boolean right(String compareString){
		int l_take = _string.length() - compareString.length();
		if(l_take < 0){
			l_take = 0;
		}
		String right = _string.substring(l_take).toUpperCase();
		return right.equals(compareString.toUpperCase());
	}
	
	public static String _splitRight(String a_String, String a_Splitter){
		return new Strings(a_String).splitRight(a_Splitter);
	}
	
	public String splitRight(String a_Splitter){
		String l_Return = "";
		int l_pos = _string.lastIndexOf(a_Splitter);
		if(l_pos > 0){
			l_Return = _string.substring(l_pos + a_Splitter.length());
			_string = _string.substring(0,l_pos);
		}
		return l_Return;
	}
	
	public String toString(){
		return _string;
	}
	
}
