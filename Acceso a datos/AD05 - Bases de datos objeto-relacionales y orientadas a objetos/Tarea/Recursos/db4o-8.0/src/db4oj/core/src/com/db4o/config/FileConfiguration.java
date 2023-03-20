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

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

/**
 * File-related configuration methods, applicable 
 * for db4o embedded use and on the server in a 
 * Client/Server setup.
 * @since 7.5
 * @see FileConfigurationProvider#file()
 */
public interface FileConfiguration {

    /**
     * sets the storage data blocksize for new ObjectContainers. 
     * <br><br>The standard setting is 1 allowing for a maximum
     * database file size of 2GB. This value can be increased
     * to allow larger database files, although some space will
     * be lost to padding because the size of some stored objects
     * will not be an exact multiple of the block size. A 
     * recommended setting for large database files is 8, since
     * internal pointers have this length.<br><br>
     * This setting is only effective when the database is first created.
     * @param bytes the size in bytes from 1 to 127
     * 
     * @sharpen.property
     */
    public void blockSize(int bytes);
    
	/**
	 * configures the size database files should grow in bytes, when no 
	 * free slot is found within.
	 * <br><br>Tuning setting.
	 * <br><br>Whenever no free slot of sufficient length can be found 
	 * within the current database file, the database file's length
	 * is extended. This configuration setting configures by how much
	 * it should be extended, in bytes.<br><br>
	 * This configuration setting is intended to reduce fragmentation.
	 * Higher values will produce bigger database files and less
	 * fragmentation.<br><br>
	 * To extend the database file, a single byte array is created 
	 * and written to the end of the file in one write operation. Be 
	 * aware that a high setting will require allocating memory for 
	 * this byte array.
	 *  
     * @param bytes amount of bytes
     * 
     * @sharpen.property
     */
    public void databaseGrowthSize(int bytes);

    /**
     * turns commit recovery off.
     * <br><br>db4o uses a two-phase commit algorithm. In a first step all intended
     * changes are written to a free place in the database file, the "transaction commit
     * record". In a second step the
     * actual changes are performed. If the system breaks down during commit, the
     * commit process is restarted when the database file is opened the next time.
     * On very rare occasions (possibilities: hardware failure or editing the database
     * file with an external tool) the transaction commit record may be broken. In this
     * case, this method can be used to try to open the database file without commit
     * recovery. The method should only be used in emergency situations after consulting
     * db4o support. 
     */
    public void disableCommitRecovery();
    
    /**
     * returns the freespace configuration interface.
     * 
     * @sharpen.property
     */
    public FreespaceConfiguration freespace();

    /**
     * configures db4o to generate UUIDs for stored objects.
     * 
     * This setting should be used when the database is first created.<br><br>
     * @param setting the scope for UUID generation: disabled, generate for all classes, or configure individually
     * 
     * @sharpen.property
     */
    public void generateUUIDs(ConfigScope setting);

    /**
     * configures db4o to generate version numbers for stored objects.
     * 
     * This setting should be used when the database is first created.
     * 
     * @param setting the scope for version number generation: disabled, generate for all classes, or configure individually
     * @sharpen.property
     * @deprecated As of version 8.0 please use {@link #generateCommitTimestamps(boolean)} instead.
     */
    @Deprecated
    public void generateVersionNumbers(ConfigScope setting);
    
	/**
	 * Configures db4o to generate commit timestamps for all stored objects.<br>
	 * <br>
	 * All the objects commited within a transaction will share the same commit timestamp.
	 * <br>
	 * This setting should be used when the database is first created.<br>
	 * <br>
	 * Afterwards you can access the object's commit timestamp like this:<br>
	 * <br>
	 * 
	 * <pre>
	 * ObjectContainer container = ...;
	 * ObjectInfo objectInfo = container.ext().getObjectInfo(obj);
	 * long commitTimestamp = objectInfo.getVersion();
	 * </pre>
	 * 
	 * @param flag
	 *            if true, commit timetamps will be generated for all stored
	 *            objects. If you already have commit timestamps for stored
	 *            objects and later set this flag to false, although you wont be
	 *            able to access them, the commit timestamps will still be taking
	 *            space in your file container. The only way to free that space
	 *            is defragmenting the container.
	 *            
     * @sharpen.property
	 * @since 8.0
	 */
    public void generateCommitTimestamps(boolean flag);

    /**
     * allows to configure db4o to use a customized byte IO storage mechanism.
     * <br><br>You can implement the interface {@link Storage} to
     * write your own. Possible usecases could be improved performance
     * with a native library, mirrored write to two files, encryption or 
     * read-on-write fail-safety control.<br><br>
     * @param storage - the storage
     * @see FileStorage
     * @see CachingStorage
     * @see MemoryStorage
     * @sharpen.property
     */
    public void storage(Storage storage) throws GlobalOnlyConfigException;

    /**
     * returns the configured {@link Storage}.
     * 
     * @return
     * 
     * @sharpen.property
     */
    public Storage storage();

    /**
     * can be used to turn the database file locking thread off. 
     * <br><br>Since Java does not support file locking up to JDK 1.4,
     * db4o uses an additional thread per open database file to prohibit
     * concurrent access to the same database file by different db4o
     * sessions in different VMs.<br><br>
     * To improve performance and to lower resource consumption, this
     * method provides the possibility to prevent the locking thread
     * from being started.<br><br><b>Caution!</b><br>If database file
     * locking is turned off, concurrent write access to the same
     * database file from different JVM sessions will <b>corrupt</b> the
     * database file immediately.<br><br> This method
     * has no effect on open ObjectContainers. It will only affect how
     * ObjectContainers are opened.<br><br>
     * The default setting is <code>true</code>.<br><br>
     * @param flag <code>false</code> to turn database file locking off.
     * 
     * @sharpen.property
     */
    public void lockDatabaseFile(boolean flag);

    /**
     * tuning feature only: reserves a number of bytes in database files.
     * <br><br>The global setting is used for the creation of new database
     * files. 
     * <br><br>Without this setting, storage space will be allocated
     * continuously as required. However, allocation of a fixed number 
     * of bytes at one time makes it more likely that the database will be 
     * stored in one chunk on the mass storage. Less read/write head movement 
     * can result in improved performance.<br><br>
     * <b>Note:</b><br> Allocated space will be lost on abnormal termination
     * of the database engine (hardware crash, VM crash). A Defragment run
     * will recover the lost space. For the best possible performance, this
     * method should be called before the Defragment run to configure the
     * allocation of storage space to be slightly greater than the anticipated
     * database file size.
     * <br><br> 
     * Default configuration: 0<br><br> 
     * @param byteCount the number of bytes to reserve
     * 
     * @sharpen.property
     */
    public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException, NotSupportedException;
    
    /**
     * configures the path to be used to store and read 
     * Blob data.
     * <br><br>
     * @param path the path to be used
     * 
     * @sharpen.property
     */
    public void blobPath(String path) throws IOException;
    
    /**
     * turns readOnly mode on and off.
     * <br><br>This method configures the mode in which subsequent calls to
     * {@link com.db4o.Db4oEmbedded#openFile(EmbeddedConfiguration, String)} 
     * will open files.
     * <br><br>Readonly mode allows to open an unlimited number of reading
     * processes on one database file. It is also convenient
     * for deploying db4o database files on CD-ROM.<br><br>
     * @param flag <code>true</code> for configuring readOnly mode for subsequent
     * calls to {@link com.db4o.Db4o#openFile Db4o.openFile()}.
     * 
     * TODO: this is rather embedded + client than base?
     * 
     * @sharpen.property
     */
    public void readOnly(boolean flag);
    

    /**
     * turns recovery mode on and off.<br><br>
     * Recovery mode can be used to try to retrieve as much as possible
     * out of an already corrupted database. In recovery mode internal 
     * checks are more relaxed. Null or invalid objects may be returned 
     * instead of throwing exceptions.<br><br>
     * Use this method with care as a last resort to get data out of a
     * corrupted database.
     * @param flag <code>true</code> to turn recover mode on.
     * 
     * @sharpen.property
     */
    public void recoveryMode(boolean flag);
    
    
    /**
     * turns asynchronous sync on and off.<br><br>
     * One of the most costly operations during commit is the call to
     * flush the buffers of the database file. In regular mode the
     * commit call has to wait until this operation has completed. 
     * When asynchronous sync is turned on, the sync operation will
     * run in a dedicated thread, blocking all other file access
     * until it has completed. This way the commit call can return
     * immediately. This will allow db4o and other processes to 
     * continue running side-by-side while the flush call executes.
     * Use this setting with care: It means that you can not be sure
     * when a commit call has actually made the changes of a
     * transaction durable (flushed through OS and file system
     * buffers). The latency time until flushing happens is extremely
     * short. The dedicated sync thread does nothing else
     * except for calling sync and writing the header of the database
     * file when needed. A setup with this option still guarantees
     * ACID transaction processing: A database file always will be 
     * either in the state before commit or in the state after 
     * commit. Corruption can not occur. You can just not rely
     * on the transaction already having been applied when the
     * commit() call returns. 
     * 
     * @sharpen.property
     */
    public void asynchronousSync(boolean flag);

}
