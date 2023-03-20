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
package db4ounit.fixtures;

import com.db4o.foundation.*;


public class ContextfulIterator extends Contextful implements Iterator4 {

	private final Iterator4 _delegate;
	
	public ContextfulIterator(Iterator4 delegate) {
		_delegate = delegate;
	}

	public Object current() {
		return run(new Closure4() {
			public Object run() {
				return _delegate.current();
			}
		});
	}

	public boolean moveNext() {
		final BooleanByRef result = new BooleanByRef();
		run(new Runnable() {
			public void run() {
				result.value = _delegate.moveNext();
			}
		});
		return result.value;
	}

	public void reset() {
		run(new Runnable() {
			public void run() {
				_delegate.reset();
			}
		});
	}

}
