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

import db4ounit.mocking.*;

public class MockActivatable implements com.db4o.ta.Activatable {
	
	private transient MethodCallRecorder _recorder;
	
	public MethodCallRecorder recorder() {
		if (null == _recorder) {
			_recorder = new MethodCallRecorder();
		}
		return _recorder;
	}

	public void bind(Activator activator) {
		record(new MethodCall("bind", activator));
	}
	
	public void activate(ActivationPurpose purpose) {
		/* need to call with explicit Object[] so it sharpens correctly */
		record(new MethodCall("activate", new Object[] { purpose }));
	}
	
	private void record(MethodCall methodCall) {
		recorder().record(methodCall);
	}
}
