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
package com.db4o.instrumentation.api;

import java.io.*;
import java.lang.reflect.*;

/**
 * Cross platform interface for bytecode emission.
 */
public interface MethodBuilder {
	
	/**
	 * @sharpen.property
	 */
	ReferenceProvider references();
	
	void ldc(Object value);
	
	void loadArgument(int index);
	
	void pop();

	void loadArrayElement(TypeRef elementType);

	void add(TypeRef operandType);

	void subtract(TypeRef operandType);

	void multiply(TypeRef operandType);

	void divide(TypeRef operandType);

	void modulo(TypeRef operandType);

	void invoke(MethodRef method, CallingConvention convention);
	
	void invoke(Method method);	

	void loadField(FieldRef fieldRef);

	void loadStaticField(FieldRef fieldRef);
	
	void box(TypeRef boxedType);
	
	void endMethod();
	
	void print(PrintStream out);
}
