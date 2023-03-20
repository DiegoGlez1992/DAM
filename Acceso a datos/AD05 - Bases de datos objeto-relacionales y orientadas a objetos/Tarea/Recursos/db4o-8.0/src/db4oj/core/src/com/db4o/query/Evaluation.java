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

/**
 * for implementation of callback evaluations.
 * <br><br>
 * To constrain a {@link Query} node with your own callback
 * <code>Evaluation</code>, construct an object that implements the
 * <code>Evaluation</code> interface and register it by passing it
 * to {@link Query#constrain(Object)}.
 * <br><br>
 * Evaluations are called as the last step during query execution,
 * after all other constraints have been applied. Evaluations in higher
 * level {@link Query} nodes in the query graph are called first.
 * <br><br>Java client/server only:<br>
 * db4o first attempts to use Java Serialization to allow to pass final
 * variables to the server. Please make sure that all variables that are
 * used within the {@link #evaluate(Candidate)} method are Serializable. This may include
 * the class an anonymous Evaluation object is created in. If db4o is
 * not successful at using Serialization, the Evaluation is transported
 * to the server in a db4o {@link com.db4o.io.MemoryBin}. In this case final variables can
 * not be restored. 
 */
public interface Evaluation extends java.io.Serializable {
	
	/**
	 * callback method during {@link Query#execute() query execution}.
	 * @param candidate reference to the candidate persistent object.
	 */
	public void evaluate(Candidate candidate);
	
}
