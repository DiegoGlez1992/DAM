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
package com.db4o.diagnostic;

import com.db4o.config.*;

/**
 * Diagnostic if {@link ObjectClass#objectField(String)} was called on a 
 * field that does not exist.
 */
public class ObjectFieldDoesNotExist extends DiagnosticBase{
	
	public final String _className;
	
	public final String _fieldName;

	public ObjectFieldDoesNotExist(String className, String fieldName) {
		_className = className;
		_fieldName = fieldName;
	}

	@Override
	public String problem() {
		return "ObjectField was configured but does not exist.";
	}

	@Override
	public Object reason() {
		return _className + "." + _fieldName;
	}

	@Override
	public String solution() {
		return "Check your configuration.";
	}

}
