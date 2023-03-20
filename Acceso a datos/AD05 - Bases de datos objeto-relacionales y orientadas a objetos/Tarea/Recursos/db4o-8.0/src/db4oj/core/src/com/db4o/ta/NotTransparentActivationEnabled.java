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
package com.db4o.ta;

import com.db4o.diagnostic.*;
import com.db4o.internal.*;

public class NotTransparentActivationEnabled extends DiagnosticBase {

	private ClassMetadata _class;
	
	public NotTransparentActivationEnabled(ClassMetadata clazz) {
		_class = clazz;
	}

	public String problem() {
		return "An object of class "+_class+" was stored. Instances of this class very likely are not subject to transparent activation.";
	}

	public Object reason() {
		return _class;
	}

	public String solution() {
		return "Use a TA aware class with equivalent functionality or ensure that this class provides a sensible implementation of the " + Activatable.class.getName() + " interface and the implicit TA hooks, either manually or by applying instrumentation.";
	}
}
