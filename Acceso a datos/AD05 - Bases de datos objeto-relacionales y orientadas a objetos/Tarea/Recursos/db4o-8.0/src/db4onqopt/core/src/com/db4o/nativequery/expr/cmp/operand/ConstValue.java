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
package com.db4o.nativequery.expr.cmp.operand;


public class ConstValue implements ComparisonOperand {
	
	private Object _value;
	
	public ConstValue(Object value) {
		this._value = value;
	}
	
	public Object value() {
		return _value;
	}
	
	public void value(Object value) {
		_value = value;
	}
	
	public String toString() {
		if (_value == null) return "null";
		if (_value instanceof String) return "\"" + _value + "\"";
		return _value.toString();
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other==null || getClass() != other.getClass()) {
			return false;
		}
		Object otherValue = ((ConstValue) other)._value;
		if (otherValue == _value) {
			return true;
		}
		if (otherValue == null || _value == null) {
			return false;
		}
		return _value.equals(otherValue);
	}
	
	public int hashCode() {
		return _value.hashCode();
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
