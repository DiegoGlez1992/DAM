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
package  com.db4o.config;

/**
 * configuration interface for classes.
 * <br><br>
 * Use the global {@link CommonConfiguration#objectClass(Object)} to configure 
 * object class settings. 
 */
public interface ObjectClass {
    
	
    /**
     * advises db4o to try instantiating objects of this class with/without
     * calling constructors.
     * <br><br>
     * Not all JDKs / .NET-environments support this feature. db4o will
     * attempt, to follow the setting as good as the environment supports.
     * In doing so, it may call implementation-specific features like
     * sun.reflect.ReflectionFactory#newConstructorForSerialization on the
     * Sun Java 1.4.x/5 VM (not available on other VMs) and 
     * FormatterServices.GetUninitializedObject() on
     * the .NET framework (not available on CompactFramework).<br><br>
     * This setting may also be set globally for all classes in
     * {@link Configuration#callConstructors(boolean)}.<br><br>
     * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param flag - specify true, to request calling constructors, specify
     * false to request <b>not</b> calling constructors.
	 * @see Configuration#callConstructors
     */
    public void callConstructor(boolean flag);
	
	
	/**
	 * sets cascaded activation behaviour.
	 * <br><br>
	 * Setting cascadeOnActivate to true will result in the activation
	 * of all member objects if an instance of this class is activated.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * Can be applied to an open ObjectContainer.<br><br>
     * @param flag whether activation is to be cascaded to member objects.
	 * @see ObjectField#cascadeOnActivate
	 * @see com.db4o.ObjectContainer#activate
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
	 * @see Configuration#activationDepth Why activation?
	 */
	public void cascadeOnActivate(boolean flag);


	/**
	 * sets cascaded delete behaviour.
	 * <br><br>
	 * Setting cascadeOnDelete to true will result in the deletion of
	 * all member objects of instances of this class, if they are 
	 * passed to
     * {@link com.db4o.ObjectContainer#delete(Object)}. 
	 * <br><br>
	 * <b>Caution !</b><br>
	 * This setting will also trigger deletion of old member objects, on
	 * calls to {@link com.db4o.ObjectContainer#store(Object)}.<br><br>
	 * An example of the behaviour:<br>
	 * <code>
	 * ObjectContainer con;<br>
	 * Bar bar1 = new Bar();<br>
	 * Bar bar2 = new Bar();<br>
	 * foo.bar = bar1;<br>
	 * con.store(foo);  // bar1 is stored as a member of foo<br>
	 * foo.bar = bar2;<br>
	 * con.store(foo);  // bar2 is stored as a member of foo
	 * </code><br>The last statement will <b>also</b> delete bar1 from the
	 * ObjectContainer, no matter how many other stored objects hold references
	 * to bar1.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param flag whether deletes are to be cascaded to member objects.
	 * @see ObjectField#cascadeOnDelete(boolean)
	 * @see com.db4o.ObjectContainer#delete(Object)
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
	 */
	public void cascadeOnDelete(boolean flag);
	
	
	/**
	 * sets cascaded update behaviour.
	 * <br><br>
	 * Setting cascadeOnUpdate to true will result in the update
	 * of all member objects if a stored instance of this class is passed
	 * to {@link com.db4o.ObjectContainer#store(Object)}.<br><br>
	 * The default setting is <b>false</b>. Setting it to true 
	 * may result in serious performance degradation.<br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param flag whether updates are to be cascaded to member objects.
	 * @see ObjectField#cascadeOnUpdate
	 * @see com.db4o.ObjectContainer#set
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
	 */
	public void cascadeOnUpdate(boolean flag);
	
	
	/**
	 * registers an attribute provider for special query behavior.
	 * <br><br>The query processor will compare the object returned by the
	 * attribute provider instead of the actual object, both for the constraint
	 * and the candidate persistent object.<br><br> 
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
	 * @param attributeProvider the attribute provider to be used
	 * @deprecated since version 7.0
	 */
	public void compare(ObjectAttribute attributeProvider);
	
	
    /**
     * Must be called before databases are created or opened
     * so that db4o will control versions and generate UUIDs
     * for objects of this class, which is required for using replication.
     * 
     * @param setting 
     * @deprecated As of version 8.0 please use {@link #generateUUIDs(boolean)} and {@link FileConfiguration#generateCommitTimestamps(boolean)} instead
     */
    public void enableReplication(boolean setting);

	
	/**
     * generate UUIDs for stored objects of this class.
     * This setting should be used before the database is first created.<br><br>
     * @param setting 
     */
    public void generateUUIDs(boolean setting);

    
    /**
     * generate version numbers for stored objects of this class.
     * This setting should be used before the database is first created.<br><br>
     * @param setting
     * @deprecated As of version 8.0 please use {@link FileConfiguration#generateCommitTimestamps(boolean)} instead
     */
    public void generateVersionNumbers(boolean setting);

    /**
     * turns the class index on or off.
     * <br><br>db4o maintains an index for each class to be able to 
     * deliver all instances of a class in a query. If the class 
     * index is never needed, it can be turned off with this method
     * to improve the performance to create and delete objects of 
     * a class.
     * <br><br>Common cases where a class index is not needed:<br>
     * - The application always works with sub classes or super classes.<br>
     * - There are convenient field indexes that will always find instances
     * of a class.<br>
     * - The application always works with IDs.<br><br>
     * In client-server environment this setting should be used on both 
     * client and server. <br><br> 
     * This setting can be applied to an open object container. <br><br>
     */
    public void indexed(boolean flag);
    
    /**
	 * sets the maximum activation depth to the desired value.
	 * <br><br>A class specific setting overrides the
     * {@link Configuration#activationDepth(int) global setting}
     * <br><br>
     * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param depth the desired maximum activation depth
	 * @see Configuration#activationDepth Why activation?
	 * @see ObjectClass#cascadeOnActivate
     */
    public void maximumActivationDepth (int depth);

    /**
	 * sets the minimum activation depth to the desired value.
	 * <br><br>A class specific setting overrides the
     * {@link Configuration#activationDepth(int) global setting}
	 * <br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param depth the desired minimum activation depth
	 * @see Configuration#activationDepth Why activation?
	 * @see ObjectClass#cascadeOnActivate
     */
    public void minimumActivationDepth (int depth);
    
    /**
     * gets the configured minimum activation depth.
     * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * @return the configured minimum activation depth.
     */
    public int minimumActivationDepth();


    /**
	 * returns an {@link ObjectField ObjectField} object
	 * to configure the specified field.
	 * <br><br>
     * @param fieldName the name of the field to be configured.<br><br>
     * @return an instance of an {@link ObjectField ObjectField}
	 *  object for configuration.
     */
    public ObjectField objectField (String fieldName);
    
    
    /**
     * turns on storing static field values for this class.
     * <br><br>By default, static field values of classes are not stored
     * to the database file. By turning the setting on for a specific class
     * with this switch, all <b>non-simple-typed</b> static field values of this
     * class are stored the first time an object of the class is stored, and
     * restored, every time a database file is opened afterwards, <b>after 
     * class meta information is loaded for this class</b> (which can happen
     * by querying for a class or by loading an instance of a class).<br><br>
     * To update a static field value, once it is stored, you have to the following
     * in this order:<br>
     * (1) open the database file you are working against<br>
     * (2) make sure the class metadata is loaded<br>
     * <code>objectContainer.query().constrain(Foo.class); // Java</code><br>
     * <code>objectContainer.Query().Constrain(typeof(Foo)); // C#</code><br>
     * (3) change the static member<br>
     * (4) store the static member explicitly<br>
     * <code>objectContainer.store(Foo.staticMember); // Java</code>
     * <br><br>The setting will be ignored for simple types.
     * <br><br>Use this setting for constant static object members.
     * <br><br>This option will slow down the process of opening database
     * files and the stored objects will occupy space in the database file.
     * <br><br>In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can NOT be applied to an open object container. <br><br>
     * 
     */
    public void persistStaticFieldValues();

    /**
	 * renames a stored class.
	 * <br><br>Use this method to refactor classes.
     * <br><br>In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can NOT be applied to an open object container. <br><br>
     * @param newName the new fully qualified class name.
     */
    public void rename (String newName);



    /**
	 * allows to specify if transient fields are to be stored.
	 * <br>The default for every class is <code>false</code>.<br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param flag whether or not transient fields are to be stored.
     */
    public void storeTransientFields (boolean flag);



    /**
	 * registers a translator for this class.
     * <br><br>
	 * <br><br>The use of an {@link ObjectTranslator ObjectTranslator} is not
	 * compatible with the use of an 
	 * internal class ObjectMarshaller.<br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * This setting can be applied to an open object container. <br><br>
     * @param translator this may be an {@link ObjectTranslator ObjectTranslator}
     *  or an {@link ObjectConstructor ObjectConstructor}
	 * @see ObjectTranslator
	 * @see ObjectConstructor
     */
    public void translate (ObjectTranslator translator);



    /**
	 * specifies the updateDepth for this class.
	 * <br><br>see the documentation of
	 * {@link com.db4o.ObjectContainer#store(Object)}
	 * for further details.<br><br>
	 * The default setting is 0: Only the object passed to
	 * {@link com.db4o.ObjectContainer#store(Object)} will be updated.<br><br>
	 * In client-server environment this setting should be used on both 
     * client and server. <br><br>
     * @param depth the depth of the desired update for this class.
	 * @see Configuration#updateDepth
	 * @see ObjectClass#cascadeOnUpdate
	 * @see ObjectField#cascadeOnUpdate
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
     */
    public void updateDepth (int depth);
    
}



