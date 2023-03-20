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
 * Interface to configure the IdSystem.
 */
public interface IdSystemConfiguration {
	
	/**
	 * configures db4o to store IDs as pointers.
	 */
	public void usePointerBasedSystem();
	
	/**
	 * configures db4o to use a stack of two BTreeIdSystems on 
	 * top of an InMemoryIdSystem. This setup is scalable for
	 * large numbers of IDs. It is the default configuration
	 * when new databases are created.
	 */
	public void useStackedBTreeSystem();
	
	
	/**
	 * configures db4o to use a single BTreeIdSystem on
	 * top of an InMemoryIdSystem. This setup is suitable for 
	 * smaller databases with a small number of IDs.
	 * For larger numbers of IDs call {@link #useStackedBTreeSystem()}.
	 */
	public void useSingleBTreeSystem();
	
	
	/**
	 * configures db4o to use an in-memory ID system.
	 * All IDs get written to the database file on every commit.
	 */
	public void useInMemorySystem();
	
	/**
	 * configures db4o to use a custom ID system.
	 * Pass an {@link IdSystemFactory} that creates the IdSystem.
	 * Note that this factory has to be configured every time you
	 * open a database that you configured to use a custom IdSystem.
	 */
	public void useCustomSystem(IdSystemFactory factory);

}
