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
 * A listener for {@link Event4} events.
 *
 * @see Event4
 * @sharpen.ignore
 */
public interface EventListener4<T extends EventArgs> {
	
	/**
	 * The event was triggered.
	 * 
	 * @param e the specific event that was triggered
	 * @param args the arguments for the specific event
	 */
	public void onEvent(Event4<T> e, T args);
}
