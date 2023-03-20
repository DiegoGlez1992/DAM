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
package com.db4o.internal.qlin;

import com.db4o.qlin.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class QLinField<T> extends QLinSubNode<T>{
	
	private final Query _node;
	
	public QLinField(QLinRoot<T> root, Object expression){
		super(root);
		_node = root.descend(expression);
	}
	
	@Override
	public QLin<T> equal(Object obj) {
		Constraint constraint = _node.constrain(obj);
		constraint.equal();
		return new QLinConstraint<T>(_root, constraint);
	}
	
	@Override
	public QLin<T> startsWith(String string) {
		Constraint constraint = _node.constrain(string);
		constraint.startsWith(true);
		return new QLinConstraint<T>(_root, constraint);
	}
	
	@Override
	public QLin<T> smaller(Object obj) {
		Constraint constraint = _node.constrain(obj);
		constraint.smaller();
		return new QLinConstraint<T>(_root, constraint);
	}
	
	@Override
	public QLin<T> greater(Object obj) {
		Constraint constraint = _node.constrain(obj);
		constraint.greater();
		return new QLinConstraint<T>(_root, constraint);
	}


}
