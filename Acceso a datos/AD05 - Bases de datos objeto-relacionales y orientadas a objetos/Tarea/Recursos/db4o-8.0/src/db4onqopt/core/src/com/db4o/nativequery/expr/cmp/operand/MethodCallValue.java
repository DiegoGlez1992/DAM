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


public class MethodCallValue extends ComparisonOperandDescendant {
	private final MethodRef _method;
	private final ComparisonOperand[] _args;
	private final CallingConvention _callingConvention;
	
	public MethodCallValue(MethodRef method, CallingConvention callingConvention, ComparisonOperandAnchor parent, ComparisonOperand[] args) {
		super(parent);
		_method = method;
		_args = args;
		_callingConvention = callingConvention;
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * @sharpen.property
	 */
	public ComparisonOperand[] args() {
		return _args;
	}
	
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		MethodCallValue casted=(MethodCallValue)obj;
		return _method.equals(casted._method)
			&& _callingConvention == casted._callingConvention;
	}

	public int hashCode() {
		int hc=super.hashCode();
		hc*=29+_method.hashCode();
		hc*=29+_args.hashCode();
		hc*=29+_callingConvention.hashCode();
		return hc;
	}
	
	public String toString() {
		
		return super.toString()
			+ "."
			+ _method.name()
			+ Iterators.join(Iterators.iterate(_args), "(", ")", ", ");
	}
	
	/**
	 * @sharpen.property
	 */
	public MethodRef method() {
		return _method;
	}

	/**
	 * @sharpen.property
	 */
	public CallingConvention callingConvention() {
		return _callingConvention;
	}

	/**
	 * @sharpen.property
	 */
	@Override
	public TypeRef type() {
		return _method.returnType();
	}
}
