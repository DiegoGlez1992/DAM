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
package com.db4o.internal;

import com.db4o.foundation.*;

public class FieldIndexException extends ChainedRuntimeException {

	private String _className;
	private String _fieldName;
	
	public FieldIndexException(FieldMetadata field) {
		this(null,null,field);
	}

	public FieldIndexException(String msg,FieldMetadata field) {
		this(msg,null,field);
	}

	public FieldIndexException(Throwable cause,FieldMetadata field) {
		this(null,cause,field);
	}

	public FieldIndexException(String msg, Throwable cause,FieldMetadata field) {
		this(msg,cause,field.containingClass().getName(),field.getName());
	}

	public FieldIndexException(String msg, Throwable cause,String className,String fieldName) {
		super(enhancedMessage(msg,className, fieldName), cause);
		_className=className;
		_fieldName=fieldName;
	}

	public String className() {
		return _className;
	}
	
	public String fieldName() {
		return _fieldName;
	}
	
	private static String enhancedMessage(String msg,String className,String fieldName) {
		String enhancedMessage="Field index for "+className+"#"+fieldName;
		if(msg!=null) {
			enhancedMessage+=": "+msg;
		}
		return enhancedMessage;
	}
}
