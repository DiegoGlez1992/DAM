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
package com.db4o.ta;

import com.db4o.activation.*;

/**
 * Activatable must be implemented by classes in order to support Transparent
 * Activation.<br>
 * <br>
 * 
 * The Activatable interface may be added to persistent classes by hand or by
 * using the db4o enhancer. For further information on the enhancer see the
 * chapter "Enhancement" in the db4o tutorial.<br>
 * <br>
 * 
 * The basic idea for Transparent Activation is as follows:<br>
 * Objects have an activation depth of 0, i.e. by default they are not activated
 * at all. Whenever a method is called on such an object, the first thing to do
 * before actually executing the method body is to activate the object to level
 * 1, i.e. populating its direct members.<br>
 * <br>
 * 
 * To illustrate this approach, we will use the following simple class.<br>
 * <br>
 * <code>
 * public class Item {<br>
 * &#160;&#160;&#160;private Item _next;<br><br>
 *   
 * &#160;&#160;&#160;public Item(Item next) {<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;_next = next;<br>
 * &#160;&#160;&#160;}<br><br>
 *   
 * &#160;&#160;&#160;public Item next() {<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;return _next;<br>
 * &#160;&#160;&#160;}<br>
 * }<br><br></code>
 * 
 * The basic sequence of actions to get the above scheme to work is the
 * following:<br>
 * <br>
 * 
 * - Whenever an object is instantiated from db4o, the database registers an
 * activator for this object. To enable this, the object has to implement the
 * Activatable interface and provide the according bind(Activator) method. The
 * default implementation of the bind method will simply store the given
 * activator reference for later use.<br>
 * <br>
 * <code>
 * public class Item implements Activatable {<br>
 * &#160;&#160;&#160;transient Activator _activator;<br><br>
 * 
 * &#160;&#160;&#160;public void bind(Activator activator) {<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;if (null != _activator) {<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;throw new IllegalStateException();<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;}<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;_activator = activator;<br>
 * &#160;&#160;&#160;}<br><br>
 *   
 * &#160;&#160;&#160;// ...<br>
 * }<br><br></code>
 * 
 * - The first action in every method body of an activatable object should be a
 * call to the corresponding Activator's activate() method. (Note that this is
 * not enforced by any interface, it is rather a convention, and other
 * implementations are possible.)<br>
 * <br>
 * <code>
 * public class Item implements Activatable {<br>
 * &#160;&#160;&#160;public void activate() {<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;if (_activator == null) return;<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;_activator.activate();<br>
 * &#160;&#160;&#160;}<br><br>
 * 
 * &#160;&#160;&#160;public Item next() {<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;activate();<br>
 * &#160;&#160;&#160;&#160;&#160;&#160;return _next;<br>
 * &#160;&#160;&#160;}<br>
 * }<br><br></code>
 * 
 * - The activate() method will check whether the object is already activated.
 * If this is not the case, it will request the container to activate the object
 * to level 1 and set the activated flag accordingly.<br>
 * <br>
 * 
 * To instruct db4o to actually use these hooks (i.e. to register the database
 * when instantiating an object), TransparentActivationSupport has to be
 * registered with the db4o configuration.<br>
 * <br>
 * <code>
 * Configuration config = ...<br>
 * config.add(new TransparentActivationSupport());<br><br>
 * </code>
 * 
 * Java: If you implement this interface manually and intend to pass this class through
 * the db4o bytecode instrumentation process, make sure you also implement the
 * {@link ActivatableInstrumented} marker interface.
 */
public interface Activatable {

	/**
	 * called by db4o upon instantiation. <br>
	 * <br>
	 * The recommended implementation of this method is to store the passed
	 * {@link Activator} in a transient field of the object.
	 * 
	 * @param activator
	 *            the Activator
	 */
	void bind(Activator activator);

	/**
	 * should be called by every reading field access of an object. <br>
	 * <br>
	 * The recommended implementation of this method is to call
	 * {@link Activator#activate(ActivationPurpose)} on the {@link Activator}
	 * that was previously passed to {@link #bind(Activator)}.
	 * 
	 * @param purpose
	 *            TODO
	 */
	void activate(ActivationPurpose purpose);
}