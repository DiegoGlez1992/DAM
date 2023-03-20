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
package com.db4o.ta.instrumentation;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;

/**
 * @exclude
 */
class CheckApplicabilityEdit implements BloatClassEdit {

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		try {
			Class clazz = BloatUtil.classForEditor(ce, origLoader);
			if (clazz.isInterface()) {
				return InstrumentationStatus.FAILED;
			}
			if (clazz.isEnum()) {
				return InstrumentationStatus.FAILED;
			}
			if (!isApplicableClass(clazz)) {
				return InstrumentationStatus.FAILED;
			}
		} catch (ClassNotFoundException e) {
			return InstrumentationStatus.FAILED;
		}
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

	private boolean isApplicableClass(Class clazz) {
		Class curClazz = clazz;
		while (curClazz != Object.class && curClazz != null && !isApplicablePlatformClass(curClazz)) {
			if (BloatUtil.isPlatformClassName(curClazz.getName())) {
				return false;
			}
			curClazz = curClazz.getSuperclass();
		}
		return true;
	}

	private boolean isApplicablePlatformClass(Class curClazz) {
		return isEnum(curClazz) || isSupportedCollection(curClazz);
	}

	private boolean isSupportedCollection(Class curClazz) {
		return curClazz == ArrayList.class;
	}

	private boolean isEnum(Class curClazz) {
		// FIXME string reference to Enum is rather fragile
		return curClazz.getName().equals("java.lang.Enum");
	}

	
}
