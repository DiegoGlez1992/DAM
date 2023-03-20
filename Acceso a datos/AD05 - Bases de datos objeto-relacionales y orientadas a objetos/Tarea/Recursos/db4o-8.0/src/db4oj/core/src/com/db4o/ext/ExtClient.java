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
package com.db4o.ext;


/**
 * extended client functionality for the
 * {@link ExtObjectContainer ExtObjectContainer} interface.
 * <br><br>Both 
 * {@link com.db4o.Db4o#openClient Db4o.openClient()} methods always
 * return an <code>ExtClient</code> object so a cast is possible.<br><br>
 * The ObjectContainer functionality is split into multiple interfaces to allow newcomers to
 * focus on the essential methods.
 */
public interface ExtClient extends ExtObjectContainer{
	
    /**
     * checks if the client is currently connected to a server.
     * @return true if the client is alive.
     */
	public boolean isAlive();
	
}

