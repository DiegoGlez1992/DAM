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

import com.db4o.config.encoding.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * configuration interface.
 * <br><br>This interface contains methods to configure db4o.<br><br>
 * The global Configuration context is available with {@link com.db4o.Db4o#configure()}.
 * When an ObjectContainer or ObjectServer is opened, the global Configuration 
 * context is cloned and copied into the ObjectContainer/ObjectServer.
 * That means every ObjectContainer/ObjectServer gets it's own copy of
 * configuration settings.<br><br>
 * <b>Most configuration settings should be set before opening an 
 * ObjectContainer/ObjectServer</b>.
 * <br><br>Some configuration settings can be modified on an open 
 * ObjectContainer/ObjectServer. The local Configuration context is
 * available with {@link com.db4o.ext.ExtObjectContainer#configure()}
 * and {@link com.db4o.ext.ExtObjectServer#configure()}.
 */
public interface Configuration {

    /**
     * sets the activation depth to the specified value.
     * <br><br><b>Why activation?</b><br>
     * When objects are instantiated from the database, the instantiation of member
     * objects needs to be limited to a certain depth. Otherwise a single object
     * could lead to loading the complete database into memory, if all objects where
     * reachable from a single root object.<br><br>
     * db4o uses the concept "depth", the number of field-to-field hops an object
     * is away from another object. <b>The preconfigured "activation depth" db4o uses 
     * in the default setting is 5.</b>
     * <br><br>Whenever an application iterates through the 
     * {@link com.db4o.ObjectSet ObjectSet} of a query result, the result objects 
     * will be activated to the configured activation depth.<br><br>
     * A concrete example with the preconfigured activation depth of 5:<br>
     * <pre>
     * // Object foo is the result of a query, it is delivered by the ObjectSet 
     * Object foo = objectSet.next();</pre> 
     * foo.member1.member2.member3.member4.member5 will be a valid object<br>
     * foo, member1, member2, member3 and member4 will be activated<br>
     * member5 will be deactivated, all of it's members will be null<br>
     * member5 can be activated at any time by calling
     * {@link com.db4o.ObjectContainer#activate(Object, int)}.
     * <br><br>
     * Note that raising the global activation depth will consume more memory and
     * have negative effects on the performance of first-time retrievals. Lowering
     * the global activation depth needs more individual activation work but can
     * increase performance of queries.<br><br>
     * {@link com.db4o.ObjectContainer#deactivate(Object, int)}
     * can be used to manually free memory by deactivating objects.<br><br>
     * In client/server environment the same setting should be used on both 
     * client and server<br><br>.
     * @param depth the desired global activation depth.
     * @see ObjectClass#maximumActivationDepth configuring classes individually
     */
    public void activationDepth(int depth);
    
    /**
     * gets the configured activation depth.
     * 
     * @return the configured activation depth.
     */
    public int activationDepth();
    
    /**
     * adds ConfigurationItems to be applied when
     * an ObjectContainer or ObjectServer is opened. 
     * @param configurationItem the ConfigurationItem
     */
    public void add(ConfigurationItem configurationItem);
    
    /**
     * adds a new Alias for a class, namespace or package.
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
     * EmbeddedConfiguration config = Db4oEmbedded.newConfiguration(); <br>
     * <b>// Creating an Alias for a single class</b><br> 
     * config.common().addAlias(<br>
     * &#160;&#160;new TypeAlias("com.f1.Pilot", "com.f1.Driver"));<br>
     * <br><br>
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
     * or connecting to a server.<br><br>
     * In client/server environment this setting should be used on the server side.
     */
    public void addAlias(Alias alias);
    
    /**
     * Removes an alias previously added with {@link Configuration#addAlias(Alias)}.
     * 
     * @param alias the alias to remove
     */
    public void removeAlias(Alias alias);
    
    /**
     * turns automatic database file format version updates on.
     * <br><br>Upon db4o database file format version changes,
     * db4o can automatically update database files to the 
     * current version. db4objects does not provide functionality
     * to reverse this process. It is not ensured that updated
     * database files can be read with older db4o versions.  
     * In some cases (Example: using ObjectManager) it may not be
     * desirable to update database files automatically therefore
     * automatic updating is turned off by default for  
     * security reasons.
     * <br><br>Call this method to turn automatic database file
     * version updating on.
     * <br><br>If automatic updating is turned off, db4o will refuse
     * to open database files that use an older database file format.<br><br>
     * In client-server environment this setting should be used on both client 
     * and server.
     */
    public void allowVersionUpdates(boolean flag);
    
    /**
     * turns automatic shutdown of the engine on and off.
     * The default and recommended setting is <code>true</code>.<br><br>
     * In client-server environment this setting should be used on both client 
     * and server.
     * @param flag whether db4o should shut down automatically.
     */
    public void automaticShutDown(boolean flag);
    
    /**
     * sets the storage data blocksize for new ObjectContainers. 
     * <br><br>The standard setting is 1 allowing for a maximum
     * database file size of 2GB. This value can be increased
     * to allow larger database files, although some space will
     * be lost to padding because the size of some stored objects
     * will not be an exact multiple of the block size. A 
     * recommended setting for large database files is 8, since
     * internal pointers have this length.<br><br>
     * This setting is only effective when the database is first created, in 
     * client-server environment in most cases it means that the setting 
     * should be used on the server side.
     * @param bytes the size in bytes from 1 to 127
     */
    public void blockSize(int bytes) throws GlobalOnlyConfigException;
    
    
    /**
     * configures the size of BTree nodes in indexes.
     * <br><br>Default setting: 100
     * <br>Lower values will allow a lower memory footprint
     * and more efficient reading and writing of small slots.
     * <br>Higher values will reduce the overall number of
     * read and write operations and allow better performance
     * at the cost of more RAM use.<br><br>
     * This setting should be used on both client and server in
     * client-server environment. 
     * @param size the number of elements held in one BTree node.
     */
    public void bTreeNodeSize(int size);
    
    
    /**
     * configures caching of BTree nodes.
     * <br><br>Clean BTree nodes will be unloaded on #commit and
     * #rollback unless they are configured as cached here.
     * <br><br>Default setting: 0
     * <br>Possible settings: 1, 2 or 3
     * <br><br> The potential number of cached BTree nodes can be
     * calculated with the following formula:<br>
     * maxCachedNodes = bTreeNodeSize ^ bTreeCacheHeight<br><br>    
     * This setting should be used on both client and server in
     * client-server environment. 
     @param height the height of the cache from the root
     */
    public void bTreeCacheHeight(int height);
    
    
    /**
     * returns the Cache configuration interface.
     */
    public CacheConfiguration cache();
    
    
    /**
     * turns callback methods on and off.
     * <br><br>Callbacks are turned on by default.<br><br>
     * A tuning hint: If callbacks are not used, you can turn this feature off, to
     * prevent db4o from looking for callback methods in persistent classes. This will
     * increase the performance on system startup.<br><br>
     * In client/server environment this setting should be used on both 
     * client and server.
     * @param flag false to turn callback methods off
     * @see com.db4o.ext.ObjectCallbacks Using callbacks
     */
    public void callbacks(boolean flag);
    
    /**
     * advises db4o to try instantiating objects with/without calling
     * constructors.
     * <br><br>
     * Not all JDKs / .NET-environments support this feature. db4o will
     * attempt, to follow the setting as good as the enviroment supports.
     * In doing so, it may call implementation-specific features like
     * sun.reflect.ReflectionFactory#newConstructorForSerialization on the
     * Sun Java 1.4.x/5 VM (not available on other VMs) and 
     * FormatterServices.GetUninitializedObject() on
     * the .NET framework (not available on CompactFramework).
     * This setting may also be overridden for individual classes in
     * {@link ObjectClass#callConstructor(boolean)}.
     * <br><br>The default setting depends on the features supported by your current environment.<br><br>
     * In client/server environment this setting should be used on both 
     * client and server.
     * <br><br>
     * @param flag - specify true, to request calling constructors, specify
     * false to request <b>not</b> calling constructors.
     * @see ObjectClass#callConstructor
     */
    public void callConstructors(boolean flag);

    /**
     * turns 
     * {@link ObjectClass#maximumActivationDepth individual class activation depth configuration} on 
     * and off.
     * <br><br>This feature is turned on by default.<br><br>
     * In client/server environment this setting should be used on both 
     * client and server.<br><br>
     * @param flag false to turn the possibility to individually configure class
     * activation depths off
     * @see Configuration#activationDepth Why activation?
     */
    public void classActivationDepthConfigurable(boolean flag);
    
    /**
     * returns client/server configuration interface.
     */
    public ClientServerConfiguration clientServer();
    
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
     */
    public void databaseGrowthSize(int bytes);

    /**
     * tuning feature: configures whether db4o checks all persistent classes upon system
     * startup, for added or removed fields.
     * <br><br>If this configuration setting is set to false while a database is
     * being created, members of classes will not be detected and stored.
     * <br><br>This setting can be set to false in a production environment after
     * all persistent classes have been stored at least once and classes will not
     * be modified any further in the future.<br><br>
     * In a client/server environment this setting should be configured both on the 
     * client and and on the server.
     * <br><br>Default value:<br>
     * <code>true</code>
     * @param flag the desired setting
     */
    public void detectSchemaChanges(boolean flag);
    
    /**
     * returns the configuration interface for diagnostics.
     * @return the configuration interface for diagnostics.
     */
    public DiagnosticConfiguration diagnostic();
    
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
     * configures the use of encryption.
     * <br><br>This method needs to be called <b>before</b> a database file
     * is created with the first 
     * {@link com.db4o.Db4o#openFile(java.lang.String)}.
     * <br><br>If encryption is set to true,
     * you need to supply a password to seed the encryption mechanism.<br><br>
     * db4o database files keep their encryption format after creation.<br><br>
     * 
     * @deprecated use a custom encrypting {@link IoAdapter} instead

     * @param flag true for turning encryption on, false for turning encryption 
     * off.
     * @see #password
     */
    public void encrypt(boolean flag) throws GlobalOnlyConfigException;
    
    /**
     * configures whether Exceptions are to be thrown, if objects can not be stored.
     * <br><br>db4o requires the presence of a constructor that can be used to
     * instantiate objects. If no default public constructor is present, all 
     * available constructors are tested, whether an instance of the class can
     * be instantiated. Null is passed to all constructor parameters.
     * The first constructor that is successfully tested will
     * be used throughout the running db4o session. If an instance of the class
     * can not be instantiated, the object will not be stored. By default,
     * execution will continue without any message or error. This method can
     * be used to configure db4o to throw an
     * {@link com.db4o.ext.ObjectNotStorableException ObjectNotStorableException}
     * if an object can not be stored.
     * <br><br>
     * The default for this setting is <b>true</b>.<br><br>
     * In client/server environment this setting should be used on both 
     * client and server.<br><br>
     * @param flag false to not throw Exceptions if objects can not be stored (fail silently).
     */
    public void exceptionsOnNotStorable(boolean flag);
    
    /**
     * returns the freespace configuration interface.
     */
    public FreespaceConfiguration freespace();
    
    /**
     * configures db4o to generate UUIDs for stored objects.
     * 
     * This setting should be used when the database is first created.<br><br>
     * @param setting the scope for UUID generation: disabled, generate for all classes, or configure individually
     */
    public void generateUUIDs(ConfigScope setting);

    /**
     * configures db4o to generate version numbers for stored objects.
     * 
     * This setting should be used when the database is first created.
     * 
     * @param setting the scope for version number generation: disabled, generate for all classes, or configure individually
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
	 * @since 8.0
	 */
    public void generateCommitTimestamps(boolean flag);

    /**
     * configures db4o to call #intern() on strings upon retrieval.
     * In client/server environment the setting should be used on both
     * client and server.
     * @param flag true to intern strings
     */
    public void internStrings(boolean flag);
    
    /**
     * returns true if strings will be interned.
     */
    public boolean internStrings();
    
    /**
     * allows to configure db4o to use a customized byte IO adapter.
     * <br><br>Derive from the abstract class {@link IoAdapter} to
     * write your own. Possible usecases could be improved performance
     * with a native library, mirrored write to two files, encryption or 
     * read-on-write fail-safety control.<br><br>An example of a custom
     * io adapter can be found in xtea_db4o community project:<br>
     * http://developer.db4o.com/ProjectSpaces/view.aspx/XTEA<br><br>
     * In client-server environment this setting should be used on the server 
     * (adapter class must be available)<br><br>
     * @param adapter - the IoAdapter
     * 
     * @deprecated Use {@link #storage(Storage)} instead.
     */
    public void io(IoAdapter adapter) throws GlobalOnlyConfigException;
    
    /**
     * allows to configure db4o to use a customized byte IO storage mechanism.
     * <br><br>Implement the interface {@link Storage} to
     * write your own. Possible usecases could be improved performance
     * with a native library, mirrored write to two files, encryption or 
     * read-on-write fail-safety control.<br><br>
     * @param factory - the factory
     * @see CachingStorage
     * @see MemoryStorage
     * @see FileStorage
     * @see StorageDecorator
     * @sharpen.property
     */
    public void storage(Storage factory) throws GlobalOnlyConfigException;
    
    /**
     * returns the configured {@link Storage}
     * @sharpen.property
     */
    public Storage storage();
    
    /**
     * returns the configured {@link IoAdapter}.
     * 
     * @return
     * 
     * @deprecated Use {@link #storage()} instead.
     */
    public IoAdapter io();
    /**
     * allows to mark fields as transient with custom attributes.
     * <br><br>.NET only: Call this method with the attribute name that you
     * wish to use to mark fields as transient. Multiple transient attributes 
     * are possible by calling this method multiple times with different
     * attribute names.<br><br>
     * In client/server environment the setting should be used on both
     * client and server.<br><br>
     * @param attributeName - the fully qualified name of the attribute, including
     * it's namespace  
     */
    public void markTransient(String attributeName);

    /**
     * sets the detail level of db4o messages. Messages will be output to the 
     * configured output {@link java.io.PrintStream PrintStream}.
     * <br><br>
     * Level 0 - no messages<br>
     * Level 1 - open and close messages<br>
     * Level 2 - messages for new, update and delete<br>
     * Level 3 - messages for activate and deactivate<br><br>
     * When using client-server and the level is set to 0, the server will override this and set it to 1.  To get around this you can set the level to -1.  This has the effect of not returning any messages.<br><br>
     * In client-server environment this setting can be used on client or on server
     * depending on which information do you want to track (server side provides more
     * detailed information).<br><br>  
     * @param level integer from 0 to 3
     * @see #setOut
     */
    public void messageLevel(int level);

    /**
     * can be used to turn the database file locking thread off. 
     * <br><br>Since Java does not support file locking up to JDK 1.4,
     * db4o uses an additional thread per open database file to prohibit
     * concurrent access to the same database file by different db4o
     * sessions in different VMs.<br><br>
     * To improve performance and to lower ressource consumption, this
     * method provides the possibility to prevent the locking thread
     * from being started.<br><br><b>Caution!</b><br>If database file
     * locking is turned off, concurrent write access to the same
     * database file from different JVM sessions will <b>corrupt</b> the
     * database file immediately.<br><br> This method
     * has no effect on open ObjectContainers. It will only affect how
     * ObjectContainers are opened.<br><br>
     * The default setting is <code>true</code>.<br><br>
     * In client-server environment this setting should be used on both client and server.<br><br>  
     * @param flag <code>false</code> to turn database file locking off.
     */
    public void lockDatabaseFile(boolean flag);

    /**
     * returns an {@link ObjectClass ObjectClass} object
     * to configure the specified class.
     * <br><br>
     * The clazz parameter can be any of the following:<br>
     * - a fully qualified classname as a String.<br>
     * - a Class object.<br>
     * - any other object to be used as a template.<br><br>
     * @param clazz class name, Class object, or example object.<br><br>
     * @return an instance of an {@link ObjectClass ObjectClass}
     *  object for configuration.
     */
    public ObjectClass objectClass(Object clazz);

    /**
     * If set to true, db4o will try to optimize native queries
     * dynamically at query execution time, otherwise it will
     * run native queries in unoptimized mode as SODA evaluations.
     * On the Java platform the jars needed for native query 
     * optimization (db4o-X.x-nqopt.jar, bloat-X.x.jar) have to be 
     * on the classpath at runtime for this
     * switch to have effect. 
     * <br><br>The default setting is <code>true</code>.<br><br>
     * In client-server environment this setting should be used on both client and server.<br><br>  
     * @param optimizeNQ true, if db4o should try to optimize
     * native queries at query execution time, false otherwise
     */
    public void optimizeNativeQueries(boolean optimizeNQ);
    
    /**
     * indicates whether Native Queries will be optimized dynamically.
     * @return boolean true if Native Queries will be optimized
     * dynamically.
     * @see #optimizeNativeQueries
     */
    public boolean optimizeNativeQueries();
    
    /**
     * protects the database file with a password.
     * <br><br>To set a password for a database file, this method needs to be 
     * called <b>before</b> a database file is created with the first 
     * {@link com.db4o.Db4o#openFile}.
     * <br><br>All further attempts to open
     * the file, are required to set the same password.<br><br>The password
     * is used to seed the encryption mechanism, which makes it impossible
     * to read the database file without knowing the password.<br><br>
     * 
     * @deprecated use a custom encrypting {@link IoAdapter} instead
     * 
     * @param pass the password to be used.
     */
    public void password(String pass) throws GlobalOnlyConfigException;

    
    /**
     * returns the Query configuration interface.
     */
    public QueryConfiguration queries();
    
    /**
     * turns readOnly mode on and off.
     * <br><br>This method configures the mode in which subsequent calls to
     * {@link com.db4o.Db4o#openFile Db4o.openFile()} will open files.
     * <br><br>Readonly mode allows to open an unlimited number of reading
     * processes on one database file. It is also convenient
     * for deploying db4o database files on CD-ROM.<br><br>
     * In client-server environment this setting should be used on the server side 
     * in embedded mode and ONLY on client side in networked mode.<br><br>
     * @param flag <code>true</code> for configuring readOnly mode for subsequent
     * calls to {@link com.db4o.Db4o#openFile Db4o.openFile()}.
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
     */
    public void recoveryMode(boolean flag);

    /**
     * configures the use of a specially designed reflection implementation.
     * <br><br>
     * db4o internally uses java.lang.reflect.* by default. On platforms that
     * do not support this package, customized implementations may be written
     * to supply all the functionality of the interfaces in the com.db4o.reflect
     * package. This method can be used to install a custom reflection
     * implementation.<br><br>
     * In client-server environment this setting should be used on the server side
     * (reflector class must be available)<br><br>
     */
    public void reflectWith(Reflector reflector);
    
    /**
     * tuning feature only: reserves a number of bytes in database files.
     * <br><br>The global setting is used for the creation of new database
     * files. Continous calls on an ObjectContainer Configuration context
     * (see  {@link com.db4o.ext.ExtObjectContainer#configure()}) will
     * continually allocate space. 
     * <br><br>The allocation of a fixed number of bytes at one time
     * makes it more likely that the database will be stored in one
     * chunk on the mass storage. Less read/write head movement can result
     * in improved performance.<br><br>
     * <b>Note:</b><br> Allocated space will be lost on abnormal termination
     * of the database engine (hardware crash, VM crash). A Defragment run
     * will recover the lost space. For the best possible performance, this
     * method should be called before the Defragment run to configure the
     * allocation of storage space to be slightly greater than the anticipated
     * database file size.
     * <br><br> 
     * In client-server environment this setting should be used on the server side. <br><br>
     * Default configuration: 0<br><br> 
     * @param byteCount the number of bytes to reserve
     */
    public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException, NotSupportedException;

    /**
     * configures the path to be used to store and read 
     * Blob data.
     * <br><br>
     * In client-server environment this setting should be used on the
     * server side. <br><br>
     * @param path the path to be used
     */
    public void setBlobPath(String path) throws IOException;

    /**
     * Assigns a {@link java.io.PrintStream PrintStream} where db4o is to print its event messages.
     * <br><br>Messages are useful for debugging purposes and for learning
     * to understand, how db4o works. The message level can be raised with
     * {@link Configuration#messageLevel(int)}
     * to produce more detailed messages.
     * <br><br>Use <code>setOut(System.out)</code> to print messages to the
     * console.<br><br>
     * In client-server environment this setting should be used on the same side
     * where {@link Configuration#messageLevel(int)} is used.<br><br>
     * @param outStream the new <code>PrintStream</code> for messages.
     * @see #messageLevel
     */
    public void setOut(PrintStream outStream);
    
    /**
     * configures the string encoding to be used.
     * <br><br>The string encoding can not be changed in the lifetime of a 
     * database file. To set up the database with the correct string encoding,
     * this configuration needs to be set correctly <b>before</b> a database 
     * file is created with the first call to
     * {@link com.db4o.Db4o#openFile} or  {@link com.db4o.Db4o#openServer}.
     * <br><br>For subsequent open calls, db4o remembers built-in
     * string encodings. If a custom encoding is used (an encoding that is 
     * not supplied from within the db4o library), the correct encoding
     * needs to be configured correctly again for all subsequent calls 
     * that open database files.
     * <br><br>Example:<br>
     * <code>config.stringEncoding(StringEncodings.utf8()));</code>
     * @see StringEncodings
     */
	public void stringEncoding(StringEncoding encoding);

    
    /**
     * tuning feature: configures whether db4o should try to instantiate one instance
     * of each persistent class on system startup.
     * <br><br>In a production environment this setting can be set to <code>false</code>,
     * if all persistent classes have public default constructors.
     * <br><br>
     * In client-server environment this setting should be used on both client and server
     * side. <br><br>
     * Default value:<br>
     * <code>true</code>
     * @param flag the desired setting
     */
    public void testConstructors(boolean flag);

    /**
     * specifies the global updateDepth.
     * <br><br>see the documentation of
     * {@link com.db4o.ObjectContainer#set }
     * for further details.<br><br>
     * The value be may be overridden for individual classes.<br><br>
     * The default setting is 1: Only the object passed to
     * {@link com.db4o.ObjectContainer#set}
     * will be updated.<br><br>
     * In client-server environment this setting should be used on both client and
     * server sides.<br><br>
     * @param depth the depth of the desired update.
     * @see ObjectClass#updateDepth
     * @see ObjectClass#cascadeOnUpdate
     * @see com.db4o.ext.ObjectCallbacks Using callbacks
     */
    public void updateDepth(int depth);

    /**
     * turns weak reference management on or off.
     * <br><br>
     * This method must be called before opening a database.
     * <br><br>
     * Performance may be improved by running db4o without using weak
     * references durring memory management at the cost of higher
     * memory consumption or by alternatively implementing a manual
     * memory management scheme using 
     * {@link com.db4o.ext.ExtObjectContainer#purge(java.lang.Object)}
     * <br><br>Setting the value to <code>false</code> causes db4o to use hard
     * references to objects, preventing the garbage collection process 
     * from disposing of unused objects.
     * <br><br>The default setting is <code>true</code>.
     */
    public void weakReferences(boolean flag);
    
    /**
     * configures the timer for WeakReference collection.
     * <br><br>The default setting is 1000 milliseconds.
     * <br><br>Configure this setting to zero to turn WeakReference
     * collection off.
     * @param milliseconds the time in milliseconds
     */
    public void weakReferenceCollectionInterval(int milliseconds);
    
    /**
     * allows registering special TypeHandlers for customized marshalling
     * and customized comparisons. 
     * @param predicate to specify for which classes and versions the
     * TypeHandler is to be used.
     * @param typeHandler to be used for the classes that match the predicate.
     */
    public void registerTypeHandler(TypeHandlerPredicate predicate, TypeHandler4 typeHandler);


    /**
     * @see CommonConfiguration#maxStackDepth()
     */
	public int maxStackDepth();
	
	/**
	 * @see CommonConfiguration#maxStackDepth(int) 
	 */
	public void maxStackDepth(int maxStackDepth);

}