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

/**
 * Implement this interface when implementing special custom Aliases
 * for classes, packages or namespaces.
 * <br><br>Aliases can be used to persist classes in the running
 * application to different persistent classes in a database file
 * or on a db4o server.
 * <br><br>Two simple Alias implementations are supplied along with 
 * db4o:<br>
 * - {@link TypeAlias} provides an #equals() resolver to match
 * names directly.<br>
 * - {@link WildcardAlias} allows simple pattern matching
 * with one single '*' wildcard character.<br>
 * <br>
 * It is possible to create
 * own complex {@link Alias} constructs by creating own resolvers
 * that implement the {@link Alias} interface.
 * <br><br>
 * Examples of concrete usecases:
 * <br><br>
 * <code>
 * EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();<br>
 * <b>// Creating an Alias for a single class</b><br>
 * config.common().addAlias(<br>
 * &#160;&#160;new TypeAlias("com.f1.Pilot", "com.f1.Driver"));<br>
 * <br>
 * <b>// Mapping a Java package onto another</b><br> 
 * config.common().addAlias(<br>
 * &#160;&#160;new WildcardAlias(<br>
 * &#160;&#160;&#160;&#160;"com.f1.*",<br>
 * &#160;&#160;&#160;&#160;"com.f1.client*"));<br></code>
 * <br><br>Aliases that translate the persistent name of a class to 
 * a name that already exists as a persistent name in the database 
 * (or on the server) are not permitted and will throw an exception
 * when the database file is opened.
 * <br><br>Aliases should be configured before opening a database file
 * or connecting to a server.
 */
public interface Alias {
    
    /**
     * return the stored name for a runtime name or null if not handled. 
     */
	public String resolveRuntimeName(String runtimeTypeName);
	
    /**
     * return the runtime name for a stored name or null if not handled. 
     */
	public String resolveStoredName(String storedTypeName);
	

}
