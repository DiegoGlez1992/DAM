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
 * provides information about system state and system settings.
 */
public interface SystemInfo {
    
    /**
     * returns the number of entries in the Freespace Manager.
     * <br><br>A high value for the number of freespace entries
     * is an indication that the database is fragmented and 
     * that defragment should be run.  
     * @return the number of entries in the Freespace Manager.
     */
    public int freespaceEntryCount();
    
    /**
     * returns the freespace size in the database in bytes.
     * <br><br>When db4o stores modified objects, it allocates
     * a new slot for it. During commit the old slot is freed.
     * Free slots are collected in the freespace manager, so
     * they can be reused for other objects.
     * <br><br>This method returns a sum of the size of all  
     * free slots in the database file.
     * <br><br>To reclaim freespace run defragment.
     * @return  the freespace size in the database in bytes.
     */
    public long freespaceSize();

    /**
     * Returns the total size of the database on disk.
     * @return total size of database on disk
     */
    public long totalSize();

}
