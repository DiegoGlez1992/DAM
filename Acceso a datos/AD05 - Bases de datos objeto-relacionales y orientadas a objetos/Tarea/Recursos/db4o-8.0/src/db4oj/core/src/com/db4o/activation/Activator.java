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
package com.db4o.activation;

/**
 * Activator interface.<br>
 * <br><br>{@link com.db4o.ta.Activatable} objects need to have a reference to 
 * an Activator implementation, which is called
 * by Transparent Activation, when a request is received to 
 * activate the host object.
 * @see <a href="http://developer.db4o.com/resources/view.aspx/reference/Object_Lifecycle/Activation/Transparent_Activation_Framework">Transparent Activation framework.</a> 
 */
public interface Activator {
	
	/**
	 * Method to be called to activate the host object.
	 * 
	 * @param purpose for which purpose is the object being activated? {@link ActivationPurpose#WRITE} will cause the object
	 * to be saved on the next {@link com.db4o.ObjectContainer#commit} operation.
	 */
	void activate(ActivationPurpose purpose);
}
