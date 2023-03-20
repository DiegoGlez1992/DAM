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
package com.db4o.db4ounit.common.ta;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class MockActivator implements Activator {
	private int _readCount;
	private int _writeCount;
	
	public MockActivator() {
	}
	
	public int count() {
		return _readCount + _writeCount;
	}

	public void activate(ActivationPurpose purpose)  {
		if (purpose == ActivationPurpose.READ) {
			++_readCount;
		} else {
			++_writeCount;
		}
	}

	public int writeCount() {
		return _writeCount;
	}
	
	public int readCount() {
		return _readCount;
	}

	public static MockActivator activatorFor(final Activatable obj) {
		MockActivator activator = new MockActivator();
		obj.bind(activator);
		return activator;
	}

}

