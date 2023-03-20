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
package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

public abstract class StringBasedValueTypeHandlerBase<T> implements ValueTypeHandler, BuiltinTypeHandler, VariableLengthTypeHandler, QueryableTypeHandler, Comparable4 {

	public final Class<T> _clazz;
    private ReflectClass _classReflector;
    
	public StringBasedValueTypeHandlerBase(Class<T> clazz) {
		_clazz = clazz;;
	}

	public void defragment(DefragmentContext context) {
	    stringHandler(context).defragment(context);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
	    stringHandler(context).delete(context);
	}

	public Object read(ReadContext context) {
	    Object read = stringHandler(context).read(context);
	    if (null == read) {
	        return null;
	    }
	    return convertString((String) read);
	}

	public void write(WriteContext context, Object obj) {
	    stringHandler(context).write(context, convertObject((T)obj));
	}

	private StringHandler stringHandler(Context context) {
	    return handlers(context)._stringHandler;
	}

	private HandlerRegistry handlers(Context context) {
	    return ((InternalObjectContainer) context.objectContainer()).handlers();
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
	    return stringHandler(context).prepareComparison(context, obj);
	}

	public ReflectClass classReflector() {
	    return _classReflector;
	}

	public void registerReflector(Reflector reflector) {
	    _classReflector = reflector.forClass(_clazz);
	}

	public boolean descendsIntoMembers() {
		return false;
	}

	protected abstract String convertObject(T obj);
	protected abstract T convertString(String str);

}