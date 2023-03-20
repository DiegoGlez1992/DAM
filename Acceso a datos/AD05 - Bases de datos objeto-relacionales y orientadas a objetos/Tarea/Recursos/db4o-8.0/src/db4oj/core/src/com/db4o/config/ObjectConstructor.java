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

import com.db4o.*;

/**
 * interface to allow instantiating objects by calling specific constructors.
 * <br><br>
 * By writing classes that implement this interface, it is possible to
 * define which constructor is to be used during the instantiation of a stored object.
 * <br><br>
 * Before starting a db4o session, translator classes that implement the 
 * <code>ObjectConstructor</code> or 
 * {@link ObjectTranslator ObjectTranslator}
 * need to be registered.<br><br>
 * Example:<br>
 * <code>
 * EmbeddedConfiguration config = Db4oEmbedded.newConfiguration(); <br>
 * ObjectClass oc = config.common().objectClass("package.className");<br>
 * oc.translate(new FooTranslator());</code><br><br>
 */
public interface ObjectConstructor extends ObjectTranslator {

	/**
	 * db4o calls this method when a stored object needs to be instantiated.
	 * <br><br>
	 * @param container the ObjectContainer used
	 * @param storedObject the object stored with 
	 * {@link ObjectTranslator#onStore ObjectTranslator.onStore}.
	 * @return the instantiated object.
	 */
	public Object onInstantiate(ObjectContainer container, Object storedObject);
	
}