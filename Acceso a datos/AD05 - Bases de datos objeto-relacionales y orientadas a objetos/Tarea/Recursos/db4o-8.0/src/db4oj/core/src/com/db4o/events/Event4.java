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
package com.db4o.events;

/**
 * An event.
 * 
 * Holds a list of {@link EventListener4} objects 
 * which receive {@link EventListener4#onEvent(Event4, EventArgs)}
 * notifications whenever this event is triggered.
 * 
 * @sharpen.ignore
 */
public interface Event4<T extends EventArgs> {
	
	/**
	 * Adds a new listener to the notification list..
	 * 
	 * @sharpen.event.add
	 */
	public void addListener(EventListener4<T> listener);
	
	/**
	 * Removes a previously registered listener from the notification list.
	 */
	public void removeListener(EventListener4<T> listener);
}
