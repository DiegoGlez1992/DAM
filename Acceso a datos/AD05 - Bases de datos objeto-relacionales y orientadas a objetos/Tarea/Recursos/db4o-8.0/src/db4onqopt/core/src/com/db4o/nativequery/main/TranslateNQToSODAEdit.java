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
package com.db4o.nativequery.main;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;
import com.db4o.internal.query.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class TranslateNQToSODAEdit implements BloatClassEdit {

	private final NativeQueryEnhancer _enhancer = new NativeQueryEnhancer();
	
	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		try {
		    Class enhancedPredicateClass = origLoader.loadClass(Db4oEnhancedFilter.class.getName());
            Class clazz = BloatUtil.classForEditor(ce, origLoader);
            if(enhancedPredicateClass.isAssignableFrom(clazz)) {
                return InstrumentationStatus.FAILED;
            }

			Type type=ce.superclass();
			while(type!=null) {
				if(BloatUtil.normalizeClassName(type.className()).equals(Predicate.class.getName())) {
					boolean success = _enhancer.enhance(loaderContext,ce,PredicatePlatform.PREDICATEMETHOD_NAME,null,origLoader, new DefaultClassSource());
					return (success ? InstrumentationStatus.INSTRUMENTED : InstrumentationStatus.NOT_INSTRUMENTED);
				}
				type = loaderContext.superType(type);
			}
			//System.err.println("Bypassing "+ce.name());
		} catch (Exception exc) {
//			throw new RuntimeException(exc);
			return InstrumentationStatus.FAILED;
		}
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

}
