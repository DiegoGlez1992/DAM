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
package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.ext.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore
public class ArrayList4TATestCaseBase extends TransparentActivationTestCaseBase {
	
	@Override
	protected void store() throws Exception {
		List<Integer> list = new ArrayList4<Integer>();
		ArrayList4Asserter.createList(list);
		store(list);
	}
	
	protected ArrayList4<Integer> retrieveAndAssertNullArrayList4() throws Exception{
		return CollectionsUtil.retrieveAndAssertNullArrayList4(db(), reflector());
	}
	
	protected ArrayList4<Integer> retrieveAndAssertNullArrayList4(ExtObjectContainer oc) throws Exception{
		return CollectionsUtil.retrieveAndAssertNullArrayList4(oc, reflector());
	}

}
