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

import com.db4o.foundation.*;
import com.db4o.instrumentation.api.*;


public class StaticFieldRoot extends ComparisonOperandRoot {
	
	private TypeRef _type;
	
	public StaticFieldRoot(TypeRef type) {
		if (null == type) {
			throw new ArgumentNullException();
		}
		_type = type;
	}
	
	/**
	 * @sharpen.property
	 */
	public TypeRef type() {
		return _type;
	}

	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		StaticFieldRoot casted=(StaticFieldRoot)obj;
		return _type.equals(casted._type);
	}
	
	public int hashCode() {
		return _type.hashCode();
	}
	
	public String toString() {
		return _type.toString();
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}	
}
