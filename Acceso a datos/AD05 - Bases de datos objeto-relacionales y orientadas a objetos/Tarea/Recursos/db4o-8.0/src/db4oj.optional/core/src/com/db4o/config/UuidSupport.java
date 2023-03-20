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
package com.db4o.config;

import java.util.UUID;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.typehandlers.*;

/**
 * Registers type handler for {@link java.util.UUID}
 * providing better performance and support for querying.
 * 
 * @sharpen.ignore
 */
@decaf.Ignore
public class UuidSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
	}

	public void prepare(Configuration configuration) {
		configuration.registerTypeHandler(new SingleClassTypeHandlerPredicate(UUID.class), new UuidTypeHandler());
	}

}
