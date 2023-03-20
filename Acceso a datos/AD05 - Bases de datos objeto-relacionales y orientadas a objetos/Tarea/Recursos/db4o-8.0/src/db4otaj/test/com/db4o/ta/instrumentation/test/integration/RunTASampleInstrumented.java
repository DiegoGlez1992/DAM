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
package com.db4o.ta.instrumentation.test.integration;

import java.lang.reflect.*;
import java.net.*;

import com.db4o.instrumentation.*;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.ta.instrumentation.*;

public class RunTASampleInstrumented {

	public static void main(String[] args) throws Exception {
		ClassFilter filter = new ByNameClassFilter(new String[] { Project.class.getName(), PrioritizedProject.class.getName(), UnitOfWork.class.getName() });
		BloatClassEdit edit = new InjectTransparentActivationEdit(filter);
		ClassLoader loader = new BloatInstrumentingClassLoader(new URL[]{}, RunTASampleInstrumented.class.getClassLoader(), filter, edit);
		Class mainClass = loader.loadClass(TransparentActivationSampleMain.class.getName());
		Method mainMethod = mainClass.getMethod("main", new Class[]{ String[].class });
		mainMethod.invoke(null, new Object[]{ new String[]{} });
	}
}
