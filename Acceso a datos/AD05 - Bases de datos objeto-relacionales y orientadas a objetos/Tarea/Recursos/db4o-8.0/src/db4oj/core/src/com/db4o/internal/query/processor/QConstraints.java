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
package com.db4o.internal.query.processor;

import com.db4o.internal.*;
import com.db4o.query.*;


/**
 * 
 * Array of constraints for queries.
 * 
 * Necessary to be returned to Query#constraints()
 * 
 * @exclude
 */
public class QConstraints extends QCon implements Constraints {

	private Constraint[] i_constraints;

	QConstraints(Transaction a_trans, Constraint[] constraints) {
		super(a_trans);
		i_constraints = constraints;
	}

	Constraint join(Constraint a_with, boolean a_and) {
		synchronized(streamLock()){
			if (!(a_with instanceof QCon)) {
				return null;
			}
			// resolving multiple constraints happens in QCon for
			// a_with, so we simply turn things around
			return ((QCon) a_with).join1(this, a_and);
		}
	}
	
	public Constraint[] toArray() {
		synchronized(streamLock()){
			return i_constraints;
		}
	}

	public Constraint contains() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].contains();
			}
			return this;
		}
	}

	public Constraint equal() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].equal();
			}
			return this;
		}
	}

	public Constraint greater() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].greater();
			}
			return this;
		}
	}

	public Constraint identity() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].identity();
			}
			return this;
		}
	}

	public Constraint not() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].not();
			}
			return this;
		}
	}

	public Constraint like() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].like();
			}
			return this;
		}
	}

	public Constraint startsWith(boolean caseSensitive) {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].startsWith(caseSensitive);
			}
			return this;
		}
	}

	public Constraint endsWith(boolean caseSensitive) {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].endsWith(caseSensitive);
			}
			return this;
		}
	}

	public Constraint smaller() {
		synchronized(streamLock()){
			for (int i = 0; i < i_constraints.length; i++) {
				i_constraints[i].smaller();
			}
			return this;
		}
	}

	public Object getObject() {
		synchronized(streamLock()){
			Object[] objects = new Object[i_constraints.length];
			for (int i = 0; i < i_constraints.length; i++) {
				objects[i] = i_constraints[i].getObject();
			}
			return objects;
		}
	}
}
