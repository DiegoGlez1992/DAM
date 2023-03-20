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
package com.db4o.cs.config;

import com.db4o.ext.*;

/**
 * Implement this interface for configuration items that encapsulate
 * a batch of configuration settings or that need to be applied 
 * to ClientObjectContainers after they are opened.
 * 
 * @since 7.12
 */
public interface ClientConfigurationItem {
	/**
	 * Gives a chance for the item to augment the configuration.
	 * 
	 * @param configuration the configuration that the item was added to
	 */
	public void prepare(ClientConfiguration configuration);
	
	/**
	 * Gives a chance for the item to configure the just opened ObjectContainer.
	 * 
	 * @param container the ObjectContainer to configure
	 */
	public void apply(ExtClient client);

}
