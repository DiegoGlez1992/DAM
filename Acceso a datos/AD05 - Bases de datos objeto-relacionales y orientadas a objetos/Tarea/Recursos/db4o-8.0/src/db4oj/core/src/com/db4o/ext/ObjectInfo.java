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

import com.db4o.config.*;


/**
 * interface to the internal reference that an ObjectContainer
 * holds for a stored object.
 */
public interface ObjectInfo {
    
	/**
	 * returns the internal db4o ID.
	 */
	public long getInternalID();

    /**
     * returns the object that is referenced.
     * <br><br>This method may return null, if the object has
     * been garbage collected.
     * @return the referenced object or null, if the object has
     * been garbage collected.
     */
    public Object getObject();
    
    /**
     * returns a UUID representation of the referenced object.
	 * UUID generation has to be turned on, in order to be able
	 * to use this feature:
	 * {@link com.db4o.config.Configuration#generateUUIDs(int)}
     * @return the UUID of the referenced object.
     */
    public Db4oUUID getUUID();

	/**
	 * returns the transaction serial number ("version") the referenced object
	 * was stored with last. Version number generation has to be turned on, in
	 * order to be able to use this feature:
	 * {@link com.db4o.config.Configuration#generateVersionNumbers(int)} <br>
	 * This feature was replaced by {@link #getCommitTimestamp()}. The main
	 * difference is that the old version mechamism used to assign a serial
	 * timestamp to the object upon storing time, and the new commiTimestamp
	 * approach, assigns it upon commit time.<br>
	 * 
	 * @return the version number.
	 * @deprecated As of version 8.0 please use {@link #getCommitTimestamp()} instead.
	 */
	@Deprecated
    public long getVersion();
	
	/**
	 * The serial timestamp the object is assigned to when it is commited.<br>
	 * <br>
	 * You need to enable this feature before using it in {@link FileConfiguration#generateCommitTimestamps(boolean)}.<br>
	 * <br>
	 * All the objects commited within the same transaction will receive the same commitTimestamp.<br>
	 * <br>
	 * db4o replication system (dRS) relies on this feature.<br>
	 * @return the serial timestamp that was given to the object upon commit.
	 * @see FileConfiguration#generateCommitTimestamps(boolean)
	 * @since 8.0
	 */
	public long getCommitTimestamp();
	
}
