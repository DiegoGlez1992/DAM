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

import com.db4o.instrumentation.api.*;

public abstract class ComparisonOperandDescendant implements ComparisonOperandAnchor {
	private ComparisonOperandAnchor _parent;
	
	protected ComparisonOperandDescendant(ComparisonOperandAnchor _parent) {
		this._parent = _parent;
	}

	public final ComparisonOperandAnchor parent() {
		return _parent;
	}
	
	public final ComparisonOperandAnchor root() {
		return _parent.root();
	}
	
	/**
	 * @sharpen.property
	 */
	public abstract TypeRef type();
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		ComparisonOperandDescendant casted=(ComparisonOperandDescendant)obj;
		return _parent.equals(casted._parent);
	}
	
	public int hashCode() {
		return _parent.hashCode();
	}
	
	public String toString() {
		return _parent.toString();
	}
}
