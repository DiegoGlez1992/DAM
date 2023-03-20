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
package db4ounit.fixtures;

public class LabeledObject<T> implements Labeled {
	
	private final T _value;
	
	private final String _label;
	
	public LabeledObject  (T value, String label){
		_value = value;
		_label = label;
	}
	
	public LabeledObject  (T value){
		this(value, null);
	}


	public String label() {
		if(_label == null){
			return _value.toString();
		}
		return _label;
	}
	
	public T value(){
		return _value;
	}
	
	public static <T> LabeledObject<T>[] forObjects(T...values){
		LabeledObject<T> [] labeledObjects = new LabeledObject[values.length];
		for (int i = 0; i < values.length; i++) {
			labeledObjects[i] = new LabeledObject<T>(values[i]);
		}
		return labeledObjects;
	}

}
