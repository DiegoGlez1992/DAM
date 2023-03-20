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
package com.db4o.foundation;

import java.lang.reflect.*;

/**
 * @sharpen.ignore
 */
public class Enum4 implements Comparable {
	private final String _name;
	private final int _ordinal;
	
	protected Enum4(String name, int ordinal) {
		_ordinal = ordinal;
		_name = name;
	}
	
	public final String toString() {
		return _name;
	}	

	public final int compareTo(Object rhs) {
		if (rhs.getClass() != getClass()) {
			throw new ClassCastException();
		}
		
		Enum4 other = (Enum4) rhs;
		return _ordinal - other._ordinal;
	}
	
	public final String name() {
		return _name;
	}
	
	public final int ordinal() {
		return _ordinal;
	}
	
	public Enum4 valueOf(Class enumClass, String value) {
		Enum4[] values = null;
		Throwable t = null;
		
		try {
			values = values(enumClass);
		} catch (IllegalArgumentException e) {
			t = e;
		} catch (SecurityException e) {
			t = e;
		} catch (IllegalAccessException e) {
			t = e;
		} catch (InvocationTargetException e) {
			t = e;
		} catch (NoSuchMethodException e) {
			t = e;
		}
		
		if (t != null) {
			throw new IllegalArgumentException(enumClass + ": " + t.getMessage());
		}
		
		for(int i = 0; i < values.length; i++) {
			if (values[i].name().equals(value)) return values[i];			
		}
		
		throw new IllegalArgumentException("No enum const class: " + enumClass.getName() + "." + value); 
	}
	
	private Enum4[] values(Class enumClass) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		if (!Enum4.class.isAssignableFrom(enumClass)) {
			throw new ClassCastException(enumClass.getName());
		}
		
		final Method valuesMethod = enumClass.getMethod("values", new Class[0]);
		return (Enum4[]) valuesMethod.invoke(null, new Object[0]);
	}
}