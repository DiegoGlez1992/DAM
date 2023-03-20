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

/**
 * constraint to limit the objects returned upon
 * {@link Query#execute() query execution}.
 * <br><br>
 * Constraints are constructed by calling 
 * {@link Query#constrain(Object)}.
 * <br><br>
 * Constraints can be joined with the methods {@link #and}
 * and {@link #or}.
 * <br><br>
 * The methods to modify the constraint evaluation algorithm may
 * be merged, to construct combined evaluation rules.
 * Examples:
 * <ul>
 *   <li> <code>Constraint#smaller().equal()</code> for "smaller or equal" </li>
 *   <li> <code>Constraint#not().like()</code> for "not like" </li>
 *   <li> <code>Constraint#not().greater().equal()</code> for "not greater or equal" </li>
 * </ul>
 */
public interface Constraint {

    /**
	 * links two Constraints for AND evaluation.
	 * For example:<br>
	 * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("points").constrain(101).smaller().and(query.descend("name").constrain("Test Pilot0"));	</code><br>
	 * will retrieve all pilots with points less than 101 and name as "Test Pilot0"<br>
     * @param with the other {@link Constraint}
     * @return a new {@link Constraint}, that can be used for further calls
	 * to {@link #and(Constraint)} and {@link #or(Constraint)}
     */
    public Constraint and (Constraint with);


    /**
	 * links two Constraints for OR evaluation.
	 * For example:<br><br>
	 * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("points").constrain(101).greater().or(query.descend("name").constrain("Test Pilot0"));</code><br>
	 * will retrieve all pilots with points more than 101 or pilots with the name "Test Pilot0"<br>
     * @param with the other {@link Constraint}
     * @return a new {@link Constraint}, that can be used for further calls
     * to {@link #and(Constraint)} and {@link #or(Constraint)}
     */
    public Constraint or (Constraint with);


    /**
     * Used in conjunction with {@link #smaller()} or {@link #greater()} to create constraints
     * like "smaller or equal", "greater or equal".
     * For example:<br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("points").constrain(101).smaller().equal();</code><br>
	 * will return all pilots with points &lt;= 101.<br>
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint equal ();


    /**
     * sets the evaluation mode to <code>&gt;</code>.
     * For example:<br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("points").constrain(101).greater()</code><br>
	 * will return all pilots with points &gt; 101.<br>
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint greater ();

    /**
     * sets the evaluation mode to <code>&lt;</code>.
     * For example:<br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("points").constrain(101).smaller()</code><br>
	 * will return all pilots with points &lt; 101.<br>
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint smaller ();


    /**
     * sets the evaluation mode to identity comparison. In this case only 
     * objects having the same database identity will be included in the result set.
     * For example:<br>
     * <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br>
	 * <code>Car car = new Car("BMW", pilot);</code><br>
	 * <code>container.store(car);</code><br>
	 * <code>// Change the name, the pilot instance stays the same</code><br>
	 * <code>pilot.setName("Test Pilot2");</code><br>
	 * <code>// create a new car</code><br>
	 * <code>car = new Car("Ferrari", pilot);</code><br>
	 * <code>container.store(car);</code><br>
	 * <code>Query query = container.query();</code><br>
	 * <code>query.constrain(Car.class);</code><br>
	 * <code>// All cars having pilot with the same database identity</code><br>
	 * <code>// will be retrieved. As we only created Pilot object once</code><br>
	 * <code>// it should mean all car objects</code><br>
	 * <code>query.descend("_pilot").constrain(pilot).identity();</code><br><br>
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint identity ();
	
    /**
     * set the evaluation mode to object comparison (query by example).
     * 
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
	public Constraint byExample();
	
    /**
     * sets the evaluation mode to "like" comparison. This mode will include 
     * all objects having the constrain expression somewhere inside the string field.
     * For example:<br>
     * <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br>
	 * <code>container.store(pilot);</code><br>
	 * <code> ...</code><br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>// All pilots with the name containing "est" will be retrieved</code><br>
	 * <code>query.descend("name").constrain("est").like();</code><br>
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint like ();
	
	
    /**
     * sets the evaluation mode to string contains comparison.
     * The contains comparison is case sensitive.
     * For example:<br>
     * <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br>
     * <code>container.store(pilot);</code><br>
     * <code> ...</code><br>
     * <code>query.constrain(Pilot.class);</code><br>
     * <code>// All pilots with the name containing "est" will be retrieved</code><br>
     * <code>query.descend("name").constrain("est").contains();</code><br>
     * @see #like() like() for case insensitive string comparison
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint contains ();

    /**
     * sets the evaluation mode to string startsWith comparison.
     * For example:<br>
     * <code>Pilot pilot = new Pilot("Test Pilot0", 100);</code><br>
     * <code>container.store(pilot);</code><br>
	 * <code> ...</code><br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("name").constrain("Test").startsWith(true);</code><br>
     * @param caseSensitive comparison will be case sensitive if true, case insensitive otherwise
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint startsWith(boolean caseSensitive);

    /**
     * sets the evaluation mode to string endsWith comparison.
     * For example:<br>
     * <code>Pilot pilot = new Pilot("Test Pilot0", 100);</code><br>
     * <code>container.store(pilot);</code><br>
	 * <code> ...</code><br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("name").constrain("T0").endsWith(false);</code><br>
     * @param caseSensitive comparison will be case sensitive if true, case insensitive otherwise
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint endsWith(boolean caseSensitive);


    /**
     * turns on not() comparison. All objects not fullfilling the constrain condition will be returned.
     *  For example:<br>
     * <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br>
     * <code>container.store(pilot);</code><br>
	 * <code> ...</code><br>
     * <code>query.constrain(Pilot.class);</code><br>
	 * <code>query.descend("name").constrain("t0").endsWith(true).not();</code><br>
     * @return this {@link Constraint} to allow the chaining of method calls.
     */
    public Constraint not ();
    
    
    /**
     * returns the Object the query graph was constrained with to
     * create this {@link Constraint}.
     * @return Object the constraining object.
     */
    public Object getObject();

}
