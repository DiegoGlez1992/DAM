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
import com.db4o.foundation.*;
import com.db4o.internal.Const4;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * Common configuration methods, applicable for
 * embedded, client and server use of db4o.<br><br>
 * In Client/Server use it is good practice to configure the
 * client and the server in exactly the same way. 
 * @since 7.5
 */
public interface CommonConfiguration {

	
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
     * <b>// Creating an Alias for a single class</b><br>
     * EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
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
     * In client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     */
    public void addAlias(Alias alias);
    
    /**
     * Removes an alias previously added with {@link CommonConfiguration#addAlias(Alias)}.
     * 
     * @param alias the alias to remove
     */
    public void removeAlias(Alias alias);
    


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
     * {@link com.db4o.ObjectContainer#activate ObjectContainer#activate(member5, depth)}.
     * <br><br>
     * Note that raising the global activation depth will consume more memory and
     * have negative effects on the performance of first-time retrievals. Lowering
     * the global activation depth needs more individual activation work but can
     * increase performance of queries.<br><br>
     * {@link com.db4o.ObjectContainer#deactivate ObjectContainer#deactivate(Object, depth)}
     * can be used to manually free memory by deactivating objects.<br><br>
     * In client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * @param depth the desired global activation depth.
     * @see ObjectClass#maximumActivationDepth configuring classes individually
     * 
     * @sharpen.property
     */
    public void activationDepth(int depth);
    
    /**
     * gets the configured activation depth.
     * 
     * @return the configured activation depth.
     * 
     * @sharpen.property
     */
    public int activationDepth();

    /**
     * adds ConfigurationItems to be applied when
     * an ObjectContainer or ObjectServer is opened. 
     * @param configurationItem the ConfigurationItem
     */
    public void add(ConfigurationItem configurationItem);

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
     * In client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * 
     * @sharpen.property
     */
    public void allowVersionUpdates(boolean flag);

    /**
     * turns automatic shutdown of the engine on and off.
     * The default and recommended setting is <code>true</code>.
     * @param flag whether db4o should shut down automatically.
     * 
     * @sharpen.property
     */
    public void automaticShutDown(boolean flag);

    /**
     * configures the size of BTree nodes in indexes.
     * <br><br>Default setting: 100
     * <br>Lower values will allow a lower memory footprint
     * and more efficient reading and writing of small slots.
     * <br>Higher values will reduce the overall number of
     * read and write operations and allow better performance
     * at the cost of more RAM use.<br><br>
     * In client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * @param size the number of elements held in one BTree node.
     * 
     * @sharpen.property
     */
    public void bTreeNodeSize(int size);

    /**
     * turns callback methods on and off.
     * <br><br>Callbacks are turned on by default.<br><br>
     * A tuning hint: If callbacks are not used, you can turn this feature off, to
     * prevent db4o from looking for callback methods in persistent classes. This will
     * increase the performance on system startup.<br><br>
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * @param flag false to turn callback methods off
     * @see com.db4o.ext.ObjectCallbacks Using callbacks
     * 
     * @sharpen.property
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
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * <br><br>
     * @param flag - specify true, to request calling constructors, specify
     * false to request <b>not</b> calling constructors.
     * @see ObjectClass#callConstructor
     * 
     * @sharpen.property
     */
    public void callConstructors(boolean flag);

    /**
     * tuning feature: configures whether db4o checks all persistent classes upon system
     * startup, for added or removed fields.
     * <br><br>If this configuration setting is set to false while a database is
     * being created, members of classes will not be detected and stored.
     * <br><br>This setting can be set to false in a production environment after
     * all persistent classes have been stored at least once and classes will not
     * be modified any further in the future.<br><br>
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * <br><br>Default value:<br>
     * <code>true</code>
     * @param flag the desired setting
     * 
     * @sharpen.property
     */
    public void detectSchemaChanges(boolean flag);

    /**
     * returns the configuration interface for diagnostics.
     * @return the configuration interface for diagnostics.
     * 
     * 
     * @sharpen.property
     */
    // TODO: refactor to use provider?
    public DiagnosticConfiguration diagnostic();

    /**
     * configures whether Exceptions are to be thrown, if objects can not be stored.
     * <br><br>db4o requires the presence of a constructor that can be used to
     * instantiate objects. If no default public constructor is present, all 
     * available constructors are tested, whether an instance of the class can
     * be instantiated. Null is passed to all constructor parameters.
     * The first constructor that is successfully tested will
     * be used throughout the running db4o session. If an instance of the class
     * can not be instantiated, the object will not be stored. By default,
     * execution will be stopped with an Exception. This method can
     * be used to configure db4o to not throw an
     * {@link com.db4o.ext.ObjectNotStorableException ObjectNotStorableException}
     * if an object can not be stored.
     * <br><br>
     * The default for this setting is <b>true</b>.<br><br>
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way.<br><br> 
     * @param flag true to throw Exceptions if objects can not be stored.
     * 
     * @sharpen.property
     */
    public void exceptionsOnNotStorable(boolean flag);

    /**
     * configures db4o to call #intern() on strings upon retrieval.
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. 
     * @param flag true to intern strings
     * 
     * @sharpen.property
     */
    public void internStrings(boolean flag);

    /**
     * allows to mark fields as transient with custom annotations/attributes.
     * <br><br>.NET only: Call this method with the attribute name that you
     * wish to use to mark fields as transient. Multiple transient attributes 
     * are possible by calling this method multiple times with different
     * attribute names.<br><br>
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. <br><br>
     * @param attributeName - the fully qualified name of the attribute, including
     * it's namespace  
     *
     */
    // TODO: can we provide meaningful java side semantics for this one?
    // TODO: USE A CLASS!!!!!!
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
     * @see #outStream
     * 
     * TODO: replace int with enumeration
     * 
     * @sharpen.property
     */
    public void messageLevel(int level);

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
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. <br><br>  
     * @param optimizeNQ true, if db4o should try to optimize
     * native queries at query execution time, false otherwise
     * 
     * @sharpen.property
     */
    public void optimizeNativeQueries(boolean optimizeNQ);

    /**
     * indicates whether Native Queries will be optimized dynamically.
     * @return boolean true if Native Queries will be optimized
     * dynamically.
     * @see #optimizeNativeQueries
     * 
     * @sharpen.property
     */
    public boolean optimizeNativeQueries();

    /**
     * returns the Query configuration interface.
     * 
     * @sharpen.property
     */
    public QueryConfiguration queries();
   
    /**
     * configures the use of a specially designed reflection implementation.
     * <br><br>
     * db4o internally uses java.lang.reflect.* by default. On platforms that
     * do not support this package, customized implementations may be written
     * to supply all the functionality of the interfaces in the com.db4o.reflect
     * package. This method can be used to install a custom reflection
     * implementation.<br><br>
     * In client-server environment this setting should be used on both the client and
     * the server side (reflector class must be available)<br><br>
     */
    public void reflectWith(Reflector reflector);   

    /**
     * Assigns a {@link java.io.PrintStream PrintStream} where db4o is to print its event messages.
     * <br><br>Messages are useful for debugging purposes and for learning
     * to understand, how db4o works. The message level can be raised with
     * {@link Configuration#messageLevel(int)}
     * to produce more detailed messages.
     * <br><br>Use <code>outStream(System.out)</code> to print messages to the
     * console.<br><br>
     * In client-server environment this setting should be used on the same side
     * where {@link Configuration#messageLevel(int)} is used.<br><br>
     * @param outStream the new <code>PrintStream</code> for messages.
     * @see #messageLevel
     * 
     * @sharpen.property
     */
    public void outStream(PrintStream outStream);

    /**
     * configures the string encoding to be used.
     * <br><br>The string encoding can not be changed in the lifetime of a 
     * database file. To set up the database with the correct string encoding,
     * this configuration needs to be set correctly <b>before</b> a database 
     * file is created with the first call to
     * {@link com.db4o.Db4oEmbedded#openFile} or  {@link com.db4o.cs.Db4oClientServer#openServer}.
     * <br><br>For subsequent open calls, db4o remembers built-in
     * string encodings. If a custom encoding is used (an encoding that is 
     * not supplied from within the db4o library), the correct encoding
     * needs to be configured correctly again for all subsequent calls 
     * that open database files.<br><br>
     * In a client-server mode, the server and all clients need to have the same string encoding.<br><br>
     * <br><br>Example:<br>
     * <code>config.stringEncoding(StringEncodings.utf8()));</code>
     * @see StringEncodings
     * 
     * @sharpen.property
     */
	public void stringEncoding(StringEncoding encoding);

    /**
     * tuning feature: configures whether db4o should try to instantiate one instance
     * of each persistent class on system startup.
     * <br><br>In a production environment this setting can be set to <code>false</code>,
     * if all persistent classes have public default constructors.
     * <br><br>
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. <br><br>
     * Default value:<br>
     * <code>true</code>
     * @param flag the desired setting
     * 
     * @sharpen.property
     */
    public void testConstructors(boolean flag);

    /**
     * specifies the global updateDepth.
     * <br><br>see the documentation of
     * {@link com.db4o.ObjectContainer#store }
     * for further details.<br><br>
     * The value be may be overridden for individual classes.<br><br>
     * The default setting is 1: Only the object passed to
     * {@link com.db4o.ObjectContainer#store}
     * will be updated.<br><br>
     * In a client/server environment it is good practice to configure the
     * client and the server in exactly the same way. <br><br>
     * @param depth the depth of the desired update.
     * @see ObjectClass#updateDepth
     * @see ObjectClass#cascadeOnUpdate
     * @see com.db4o.ext.ObjectCallbacks Using callbacks
     * 
     * @sharpen.property
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
     * 
     * @sharpen.property
     */
    public void weakReferences(boolean flag);

    /**
     * configures the timer for WeakReference collection.
     * <br><br>The default setting is 1000 milliseconds.
     * <br><br>Configure this setting to zero to turn WeakReference
     * collection off.
     * @param milliseconds the time in milliseconds
     * 
     * @sharpen.property
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
     * @see Environment
     * @sharpen.property
     */
	public EnvironmentConfiguration environment();

	/**
	 * Registers a {@link NameProvider} that assigns a custom name to the database to be used in
	 * {@link Object#toString()}.
	 */
	public void nameProvider(NameProvider provider);
	
	

    /**
     * <p>Sets the max stack depth that will be used for recursive storing and activating an object.
     * <p>The default value is set to {@link Const4#DEFAULT_MAX_STACK_DEPTH}
     * <p>On Android platform, we recomend setting this to 2.

     * @param depth the desired max stack depth.
     * 
     * @sharpen.property
     */
    public void maxStackDepth(int maxStackDepth);
    
    /**
     * gets the configured max stack depth.
     * 
     * @return the configured max stack depth.
     * 
     * @sharpen.property
     */
    public int maxStackDepth();


}
