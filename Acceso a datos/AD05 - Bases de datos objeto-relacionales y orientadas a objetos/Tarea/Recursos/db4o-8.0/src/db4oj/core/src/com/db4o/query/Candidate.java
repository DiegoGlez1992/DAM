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
package com.db4o.query;

import com.db4o.*;

/**
 * candidate for {@link Evaluation} callbacks.
 * <br><br>
 * During {@link Query#execute() query execution} all registered {@link Evaluation} callback
 * handlers are called with  {@link Candidate} proxies that represent the persistent objects that
 * meet all other {@link Query} criteria.
 * <br><br>
 * A {@link Candidate} provides access to the persistent object it
 * represents and allows to specify, whether it is to be included in the 
 * {@link ObjectSet} resultset.
 */
public interface Candidate {
	
	/**
	 * returns the persistent object that is represented by this query 
	 * {@link Candidate}.
	 * @return Object the persistent object.
	 */
	public Object getObject();
	
	/**
	 * specify whether the Candidate is to be included in the 
	 * {@link ObjectSet} resultset.
	 * <br><br>
	 * This method may be called multiple times. The last call prevails.
	 * @param flag inclusion.
	 */
	public void include(boolean flag);
	
	
	/**
	 * returns the {@link ObjectContainer} the Candidate object is stored in.
	 * @return the {@link ObjectContainer}
	 */
	public ObjectContainer objectContainer();
	
}
