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
package db4ounit.extensions.tests;

import com.db4o.config.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.mocking.*;

public class SimpleDb4oTestCase extends AbstractDb4oTestCase {
	
	public static final DynamicVariable<MethodCallRecorder> RECORDER_VARIABLE = DynamicVariable.newInstance();
	
	public static class Data {}
	
	protected void configure(Configuration config) {
		record(new MethodCall("fixture", fixture()));
		record(new MethodCall("configure", config));
	}

	private void record(final MethodCall call) {
	    recorder().record(call);
    }

	private MethodCallRecorder recorder() {
		return RECORDER_VARIABLE.value();
	}
	
	protected void store() {
		record(new MethodCall("store"));
		fixture().db().store(new Data());
	}
	
	public void testResultSize() {
		record(new MethodCall("testResultSize"));
		Assert.areEqual(1, fixture().db().queryByExample(Data.class).size());
	}
}
