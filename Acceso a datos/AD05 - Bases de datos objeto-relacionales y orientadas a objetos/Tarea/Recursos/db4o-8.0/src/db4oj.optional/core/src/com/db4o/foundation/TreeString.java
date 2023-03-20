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
package com.db4o.foundation;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

public class TreeString extends Tree<String> {
	
	public String _key;
	
	public TreeString(String key) {
		this._key = key;
	}
	
	protected Tree shallowCloneInternal(Tree tree) {
		TreeString ts = (TreeString) super.shallowCloneInternal(tree);
		ts._key = _key;
		return ts;
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new TreeString(_key));
	}
	
	public int compare(Tree to) {
		return StringHandler
			.compare(
				Const4.stringIO.write(_key),
				Const4.stringIO.write(((TreeString) to)._key));
	}
	
	public String key(){
		return _key;
	}
	
}