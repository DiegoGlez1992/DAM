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

public class Contextful {

	protected final FixtureContext _context;

	public Contextful() {
		_context = currentContext();
	}

	protected Object run(final Closure4 closure4) {
		return combinedContext().run(closure4);
	}
	
	protected void run(final Runnable runnable) {
		combinedContext().run(runnable);
	}

	private FixtureContext combinedContext() {
		return currentContext().combine(_context);
	}

	private FixtureContext currentContext() {
		return FixtureContext.current();
	}
}