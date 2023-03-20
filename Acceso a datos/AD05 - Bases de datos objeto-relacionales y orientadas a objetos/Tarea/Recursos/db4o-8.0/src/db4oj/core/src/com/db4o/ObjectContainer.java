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
package  com.db4o;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.qlin.*;
import com.db4o.query.*;


/**
 * the interface to a db4o database, stand-alone or client/server.
 * <br><br>The ObjectContainer interface provides methods
 * to store, query and delete objects and to commit and rollback
 * transactions.<br><br>
 * An ObjectContainer can either represent a stand-alone database
 * or a connection to a {@link Db4o#openServer(String, int) db4o server}.
 * <br><br>An ObjectContainer also represents a transaction. All work
 * with db4o always is transactional. Both {@link #commit()} and
 * {@link #rollback()} start new transactions immediately. For working 
 * against the same database with multiple transactions, open a db4o server
 * with {@link Db4o#openServer(String, int)} and 
 * {@link ObjectServer#openClient() connect locally} or
 * {@link Db4o#openClient(String, int, String, String) over TCP}.
 * @see ExtObjectContainer ExtObjectContainer for extended functionality.
 * @sharpen.ignore
 */
public interface ObjectContainer {
	
    /**
     * activates all members on a stored object to the specified depth.
     * <br><br>
     * See {@link com.db4o.config.Configuration#activationDepth(int) "Why activation"}
     * for an explanation why activation is necessary.<br><br>
     * The activate method activates a graph of persistent objects in memory.
     * Only deactivated objects in the graph will be touched: their
     * fields will be loaded from the database. 
     * The activate methods starts from a
     * root object and traverses all member objects to the depth specified by the
     * depth parameter. The depth parameter is the distance in "field hops" 
     * (object.field.field) away from the root object. The nodes at 'depth' level
     * away from the root (for a depth of 3: object.member.member) will be instantiated
     * but deactivated, their fields will be null.
     * The activation depth of individual classes can be overruled
     * with the methods
     * {@link com.db4o.config.ObjectClass#maximumActivationDepth maximumActivationDepth()} and
     * {@link com.db4o.config.ObjectClass#minimumActivationDepth minimumActivationDepth()} in the
     * {@link com.db4o.config.ObjectClass ObjectClass interface}.<br><br>
     * A successful call to activate triggers Activating and Activated callbacks,
     * which can be used for cascaded activation.<br><br>
     * @see com.db4o.config.Configuration#activationDepth Why activation?
     * @see ObjectCallbacks Using callbacks
     * @param obj the object to be activated.
     * @param depth the member {@link com.db4o.config.Configuration#activationDepth depth}
     *  to which activate is to cascade.
     *  @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
	 *  @throws DatabaseClosedException db4o database file was closed or failed to open.
     */
    public void activate (Object obj, int depth) throws Db4oIOException, DatabaseClosedException;
    
    /**
     * closes the <code>ObjectContainer</code>.
     * <br><br>A call to <code>close()</code> automatically performs a 
     * {@link #commit commit()}.
     * <br><br>Note that every session opened with Db4o.openFile() requires one
     * close()call, even if the same filename was used multiple times.<br><br>
     * Use <code>while(!close()){}</code> to kill all sessions using this container.<br><br>
     * @return success - true denotes that the last used instance of this container
     * and the database file were closed.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     */
	public boolean close () throws Db4oIOException;

    /**
     * commits the running transaction.
     * <br><br>Transactions are back-to-back. A call to commit will starts
     * a new transaction immedidately.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     * @throws DatabaseReadOnlyException database was configured as read-only.
     */
    public void commit () throws Db4oIOException, DatabaseClosedException, DatabaseReadOnlyException;
    

    /**
     * deactivates a stored object by setting all members to <code>NULL</code>.
     * <br>Primitive types will be set to their default values.
     * Calls to this method save memory.
     * The method has no effect, if the passed object is not stored in the
     * <code>ObjectContainer</code>.<br><br>
     * <code>deactivate()</code> triggers Deactivating and Deactivated callbacks.
     * <br><br>
     * Be aware that calling this method with a depth parameter greater than 
     * 1 sets members on member objects to null. This may have side effects 
     * in other places of the application.<br><br>
	 * @see ObjectCallbacks Using callbacks
  	 * @see com.db4o.config.Configuration#activationDepth Why activation?
     * @param obj the object to be deactivated.
	 * @param depth the member {@link com.db4o.config.Configuration#activationDepth depth} 
	 * to which deactivate is to cascade.
	 * @throws DatabaseClosedException db4o database file was closed or failed to open.
	*/
    public void deactivate (Object obj, int depth) throws DatabaseClosedException;

    /**
     * deletes a stored object permanently.
     * <br><br>Note that this method has to be called <b>for every single object
     * individually</b>. Delete does not recurse to object members. Simple
     * and array member types are destroyed.
     * <br><br>Object members of the passed object remain untouched, unless
     * cascaded deletes are  
     * {@link com.db4o.config.ObjectClass#cascadeOnDelete configured for the class}
     * or for {@link com.db4o.config.ObjectField#cascadeOnDelete one of the member fields}.
     * <br><br>The method has no effect, if
     * the passed object is not stored in the <code>ObjectContainer</code>.
     * <br><br>A subsequent call to
     * <code>set()</code> with the same object newly stores the object
     * to the <code>ObjectContainer</code>.<br><br>
     * <code>delete()</code> triggers Deleting and Deleted callbacks,
     * which can be also used for cascaded deletes.<br><br>
	 * @see com.db4o.config.ObjectClass#cascadeOnDelete
	 * @see com.db4o.config.ObjectField#cascadeOnDelete
	 * @see ObjectCallbacks Using callbacks
     * @param obj the object to be deleted from the
     * <code>ObjectContainer</code>.<br>
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     * @throws DatabaseReadOnlyException database was configured as read-only.
     */
    public void delete (Object obj) throws Db4oIOException, DatabaseClosedException, DatabaseReadOnlyException;
    
    /**
     * returns an ObjectContainer with extended functionality.
     * <br><br>Every ObjectContainer that db4o provides can be casted to
     * an ExtObjectContainer. This method is supplied for your convenience
     * to work without a cast.
     * <br><br>The ObjectContainer functionality is split to two interfaces
     * to allow newcomers to focus on the essential methods.<br><br>
     * @return this, casted to ExtObjectContainer
     */
    public ExtObjectContainer ext();
	   
	/**
     * Query-By-Example interface to retrieve objects.
     * <br><br><code>queryByExample()</code> creates an
     * {@link ObjectSet ObjectSet} containing
     * all objects in the <code>ObjectContainer</code> that match the passed
     * template object.<br><br>
	 * Calling <code>queryByExample(NULL)</code> returns all objects stored in the
     * <code>ObjectContainer</code>.<br><br><br>
     * <b>Query Evaluation</b>
     * <br>All non-null members of the template object are compared against
     * all stored objects of the same class.
     * Primitive type members are ignored if they are 0 or false respectively.
     * <br><br>Arrays and all supported <code>Collection</code> classes are
     * evaluated for containment. Differences in <code>length/size()</code> are
     * ignored.
     * <br><br>Consult the documentation of the Configuration package to
     * configure class-specific behaviour.<br><br><br>
     * <b>Returned Objects</b><br>
     * The objects returned in the
     * {@link ObjectSet ObjectSet} are instantiated
     * and activated to the preconfigured depth of 5. The
	 * {@link com.db4o.config.Configuration#activationDepth activation depth}
	 * may be configured {@link com.db4o.config.Configuration#activationDepth globally} or
     * {@link com.db4o.config.ObjectClass individually for classes}.
	 * <br><br>
     * db4o keeps track of all instantiatied objects. Queries will return
     * references to these objects instead of instantiating them a second time.
     * <br><br>
	 * Objects newly activated by <code>queryByExample()</code> can respond to the Activating callback
	 * method.
     * <br><br>
     * @param template object to be used as an example to find all matching objects.<br><br>
     * @return {@link ObjectSet ObjectSet} containing all found objects.<br><br>
	 * @see com.db4o.config.Configuration#activationDepth Why activation?
	 * @see ObjectCallbacks Using callbacks
	 * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
	 * @throws DatabaseClosedException db4o database file was closed or failed to open.
	 */
    public <T> ObjectSet<T> queryByExample (Object template) throws Db4oIOException, DatabaseClosedException;
    
    /**
     * creates a new S.O.D.A. {@link Query Query}.
     * <br><br>
     * Use {@link #queryByExample(Object)} for simple Query-By-Example.<br><br>
     * {@link #query(Predicate) Native queries } are the recommended main db4o query
     * interface. 
     * <br><br>
     * @return a new Query object
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     */
    public Query query () throws DatabaseClosedException;
    
    /**
     * queries for all instances of a class.
     * @param clazz the class to query for.
     * @return the {@link ObjectSet} returned by the query.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     */
    public <TargetType> ObjectSet <TargetType> query(Class<TargetType> clazz) throws Db4oIOException, DatabaseClosedException;

    
    /**
     * Native Query Interface.
     * <br><br>Native Queries allow typesafe, compile-time checked and refactorable 
     * querying, following object-oriented principles. Native Queries expressions
     * are written as if one or more lines of code would be run against all
     * instances of a class. A Native Query expression should return true to mark 
     * specific instances as part of the result set. 
     * db4o will  attempt to optimize native query expressions and execute them 
     * against indexes and without instantiating actual objects, where this is 
     * possible.<br><br>
     * The syntax of the enclosing object for the native query expression varies,
     * depending on the language version used. Here are some examples:<br><br>
     * 
     * <code>
     * <b>// Java JDK 5</b><br>
     * List &lt;Cat&gt; cats = db.query(new Predicate&lt;Cat&gt;() {<br>
     * &#160;&#160;&#160;public boolean match(Cat cat) {<br>
     * &#160;&#160;&#160;&#160;&#160;&#160;return cat.getName().equals("Occam");<br>
     * &#160;&#160;&#160;}<br>
     * });<br>
     * <br>
     * <br>
     * <b>// Java JDK 1.2 to 1.4</b><br>
     * List cats = db.query(new Predicate() {<br>
     * &#160;&#160;&#160;public boolean match(Cat cat) {<br>
     * &#160;&#160;&#160;&#160;&#160;&#160;return cat.getName().equals("Occam");<br>
     * &#160;&#160;&#160;}<br>
     * });<br>
     * <br>
     * <br>
     * <b>// Java JDK 1.1</b><br>
     * ObjectSet cats = db.query(new CatOccam());<br>
     * <br>
     * public static class CatOccam extends Predicate {<br>
     * &#160;&#160;&#160;public boolean match(Cat cat) {<br>
     * &#160;&#160;&#160;&#160;&#160;&#160;return cat.getName().equals("Occam");<br>
     * &#160;&#160;&#160;}<br>
     * });<br>
     * </code>
     *
     * <br>
     * Summing up the above:<br>
     * In order to run a Native Query, you can extend the Predicate class for all other language dialects<br><br>
     * A class that extends Predicate is required to 
     * implement the #match() method, following the native query
     * conventions:<br>
     * - The name of the method is "#match()".<br>
     * - The method must be public.<br>
     * - The method returns a boolean.<br>
     * - The method takes one parameter.<br>
     * - The Class (Java) of the parameter specifies the extent.<br>
     * - For all instances of the extent that are to be included into the
     * resultset of the query, the match method should return true. For all
     * instances that are not to be included, the match method should return
     * false.<br><br>  
     *   
     * @param predicate the {@link Predicate} containing the native query expression.
     * @return the {@link ObjectSet} returned by the query.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     */
    public <TargetType> ObjectSet <TargetType> query(Predicate<TargetType> predicate) throws Db4oIOException, DatabaseClosedException;

    /**
     * Native Query Interface. Queries as with {@link com.db4o.ObjectContainer#query(com.db4o.query.Predicate)},
     * but will sort the resulting {@link com.db4o.ObjectSet} according to the given {@link com.db4o.query.QueryComparator}.
     * 
     * @param predicate the {@link Predicate} containing the native query expression.
     * @param comparator the {@link QueryComparator} specifying the sort order of the result
     * @return the {@link ObjectSet} returned by the query.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     */
    public <TargetType> ObjectSet <TargetType> query(Predicate<TargetType> predicate,QueryComparator<TargetType> comparator) throws Db4oIOException, DatabaseClosedException;

    /**
     * Native Query Interface. Queries as with {@link com.db4o.ObjectContainer#query(com.db4o.query.Predicate)},
     * but will sort the resulting {@link com.db4o.ObjectSet} according to the given {@link Comparator}.
     * 
     * @param predicate the {@link Predicate} containing the native query expression.
     * @param comparator the java.util.Comparator specifying the sort order of the result
     * @return the {@link ObjectSet} returned by the query.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     * @sharpen.ignore
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public <TargetType> ObjectSet <TargetType> query(Predicate<TargetType> predicate, Comparator<TargetType> comparator) throws Db4oIOException, DatabaseClosedException;

    /**
     * rolls back the running transaction.
     * <br><br>Modified application objects im memory are not restored.
     * Use combined calls to {@link #deactivate deactivate()}
     * and {@link #activate activate()} to reload an objects member values.
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseClosedException db4o database file was closed or failed to open.
     * @throws DatabaseReadOnlyException database was configured as read-only.
     */
    public void rollback() throws Db4oIOException, DatabaseClosedException, DatabaseReadOnlyException;
   

	/**
     * newly stores objects or updates stored objects.
     * <br><br>An object not yet stored in the <code>ObjectContainer</code> will be
     * stored when it is passed to <code>store()</code>. An object already stored
     * in the <code>ObjectContainer</code> will be updated.
     * <br><br><b>Updates</b><br>
	 * - will affect all simple type object members.<br>
     * - links to object members that are already stored will be updated.<br>
	 * - new object members will be newly stored. The algorithm traverses down
	 * new members, as long as further new members are found.<br>
     * - object members that are already stored will <b>not</b> be updated
     * themselves.<br>Every object member needs to be updated individually with a
	 * call to <code>store()</code> unless a deep
	 * {@link com.db4o.config.Configuration#updateDepth global} or 
     * {@link com.db4o.config.ObjectClass#updateDepth class-specific}
     * update depth was configured or cascaded updates were 
     * {@link com.db4o.config.ObjectClass#cascadeOnUpdate defined in the class}
     * or in {@link com.db4o.config.ObjectField#cascadeOnUpdate one of the member fields}.
	 * Depending if the passed object is newly stored or updated, Creating/Created 
	 * or Updating/Updated callback method is triggered.
	 * Callbacks
	 * might also be used for cascaded updates.<br><br>
     * @param obj the object to be stored or updated.
	 * @see ExtObjectContainer#store(java.lang.Object, int) ExtObjectContainer#set(object, depth)
	 * @see com.db4o.config.Configuration#updateDepth
	 * @see com.db4o.config.ObjectClass#updateDepth
	 * @see com.db4o.config.ObjectClass#cascadeOnUpdate
	 * @see com.db4o.config.ObjectField#cascadeOnUpdate
	 * @see ObjectCallbacks Using callbacks
	 * @throws DatabaseClosedException db4o database file was closed or failed to open.
	 * @throws DatabaseReadOnlyException database was configured as read-only.
     */
    public void store (Object obj) throws DatabaseClosedException, DatabaseReadOnlyException;
    
    
    
}



