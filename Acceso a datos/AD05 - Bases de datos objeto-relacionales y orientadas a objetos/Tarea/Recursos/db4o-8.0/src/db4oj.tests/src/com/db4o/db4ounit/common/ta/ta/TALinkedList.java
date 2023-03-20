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
package com.db4o.db4ounit.common.ta.ta;

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;

/**
 * @exclude
 */
public class TALinkedList extends ActivatableImpl {
	
	public static TALinkedList newList(int depth) {
		if (depth == 0) {
			return null;
		}
		TALinkedList head = new TALinkedList(depth);
		head.next = newList(depth - 1);
		return head;
	}

	public TALinkedList nextN(int depth) {
		TALinkedList node = this;
		for (int i = 0; i < depth; ++i) {
			node = node.next();
		}
		return node;
	}
	
	public TALinkedList next;
	
	public int value;

	public TALinkedList(int v) {
		value = v;
	}

	public int value() {
		activate(ActivationPurpose.READ);
		return value;
	}
	
	public TALinkedList next() {
		activate(ActivationPurpose.READ);
		return next;
	}
	
	public boolean equals(Object other) {
		activate(ActivationPurpose.READ);
		TALinkedList otherList = (TALinkedList) other;
		if( value != otherList.value()) {
			return false;
		}
		if(next == otherList.next()) {
			return true;
		}
		if(otherList.next() == null) {
			return false;
		}
		return next.equals(otherList.next());
	}
}
