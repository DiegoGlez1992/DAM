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

import com.db4o.*;
import com.db4o.qlin.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public abstract class QLinSubNode<T> extends QLinSodaNode<T>{
	
	protected final QLinRoot<T> _root;

	public QLinSubNode(QLinRoot<T> root) {
		_root = root;
	}
	
	protected QLinRoot<T> root(){
		return _root;
	}
	
	protected Query query(){
		return root().query();
	}
	
	public QLin<T> limit(int size){
		root().limit(size);
		return this;
	}
	
	public ObjectSet<T> select() {
		return root().select();
	}


}
