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
package  com.db4o.query;

import java.util.*;

import com.db4o.*;


/**
 * handle to a node in a S.O.D.A. query graph.
 * <br/><br/>
 * A node in the query graph can represent multiple 
 * classes, one class or an attribute of a class.<br/><br/>The graph 
 * is automatically extended with attributes of added constraints 
 * (see {@link #constrain(Object)}) and upon calls to  {@link #descend(java.lang.String)} that request nodes that do not yet exist.
 * <br/><br/>
 * References to joined nodes in the query graph can be obtained
 * by "walking" along the nodes of the graph with the method 
 * {@link #descend(String)}.
 * <br/><br/>
 * {@link #execute()}
 * evaluates the entire graph against all persistent objects. 
 * <br/><br/>
 * {@link #execute()} can be called from any {@link Query} node
 * of the graph. It will return an {@link ObjectSet} filled with
 * objects of the class/classes that the node, it was called from,
 * represents.<br/><br/>
 * <b>Note:<br/>
 * {@link Predicate Native queries} are the recommended main query 
 * interface of db4o.</b> 
 */
public interface Query {


    /**
	 * adds a constraint to this node.
	 * <br/><br/>
	 * If the constraint contains attributes that are not yet
	 * present in the query graph, the query graph is extended
	 * accordingly.
	 * <br/><br/>
	 * Special behaviour for:
	 * <ul>
	 * <li> class {@link Class}: confine the result to objects of one
	 * class or to objects implementing an interface.</li>
	 * <li> interface {@link Evaluation}: run
	 * evaluation callbacks against all candidates.</li>
	 * </ul>
     * @param constraint the constraint to be added to this Query.
     * @return {@link Constraint} a new {@link Constraint} for this
     * query node or <code>null</code> for objects implementing the 
     * {@link Evaluation} interface.
     */
    public Constraint constrain (Object constraint);

    
    /**
     * returns a {@link Constraints}
     * object that holds an array of all constraints on this node.
     * @return {@link Constraints} on this query node.
     */
    public Constraints constraints();


    /**
	 * returns a reference to a descendant node in the query graph.
	 * <br/><br/>If the node does not exist, it will be created.
	 * <br/><br/>
	 * All classes represented in the query node are tested, whether
	 * they contain a field with the specified field name. The
	 * descendant Query node will be created from all possible candidate
	 * classes.
     * @param fieldName path to the descendant.
     * @return descendant {@link Query} node
     */
    public Query descend (String fieldName);


    /**
	 * executes the {@link Query}.
     * @return {@link ObjectSet} - the result of the {@link Query}.
     */
    public <T> ObjectSet<T> execute ();

    
    /**
	 * adds an ascending ordering criteria to this node of
	 * the query graph. 
	 * <p>
	 * If multiple ordering criteria are applied, the chronological
	 * order of method calls is relevant: criteria created by 'earlier' calls are
	 * considered more significant, i.e. 'later' criteria only have an effect
	 * for elements that are considered equal by all 'earlier' criteria.
	 * </p>
	 * <p>
	 * As an example, consider a type with two int fields, and an instance set
	 * {(a:1,b:3),(a:2,b:2),(a:1,b:2),(a:2,b:3)}. The call sequence [orderAscending(a),
	 * orderDescending(b)] will result in [(<b>a:1</b>,b:3),(<b>a:1</b>,b:2),(<b>a:2</b>,b:3),(<b>a:2</b>,b:2)].
	 * </p>
     * @return this {@link Query} object to allow the chaining of method calls.
     */
    public Query orderAscending ();


    /**
	 * adds a descending order criteria to this node of
	 * the query graph. 
	 * <br/><br/>
	 * For semantics of multiple calls setting ordering criteria, see {@link #orderAscending()}.
     * @return this {@link Query} object to allow the chaining of method calls.
     */
    public Query orderDescending ();
    
    /**
     * Sort the resulting ObjectSet by the given comparator.
     * 
     * @param comparator The comparator to apply.
     * @return this {@link Query} object to allow the chaining of method calls.
     */
    public Query sortBy(QueryComparator<?> comparator);


    /**
     * Sort the resulting ObjectSet by the given comparator.
     * 
     * @param comparator The comparator to apply.
     * @return this {@link Query} object to allow the chaining of method calls.
     * @sharpen.ignore
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public Query sortBy(Comparator comparator);
}

