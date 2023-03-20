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
package com.db4o.test.nativequery.diagnostics;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.diagnostic.NativeQueryNotOptimized;
import com.db4o.query.Predicate;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @sharpen.partial
 */
public class NativeQueryOptimizerDiagnosticsTestCase extends AbstractDb4oTestCase {
	private boolean _failed = false; 
//	private Object _reason = null;
	
	protected void configure(Configuration config) {
		config.objectClass(Subject.class).objectField("_name").indexed(true);
		
		config.diagnostic().addListener(
				new DiagnosticListener() {
					public void onDiagnostic(Diagnostic d) {
						if (d.getClass() == NativeQueryNotOptimized.class) {
//							_reason = ((NativeQueryNotOptimized) d).reason();
							_failed = true;
						}
					}					
				});
	}
	
	protected void store() {
		db().store(new Subject("Test"));
		db().store(new Subject("Test2"));
	}
	
	public void testNativeQueryNotOptimized() {
		ObjectSet items = db().query(
								new Predicate<Subject>(){
									public boolean match(final Subject subject) {
										return subject.complexName().startsWith("Test");
									}
								});
		Assert.isNotNull(items);
		Assert.isTrue(_failed);
	}
	
	private static class Subject {
		private String _name;

		public Subject(String name) {
			_name = name;
		}
		
		public String complexName() {
			StringBuffer sb = new StringBuffer(_name);			
			for(int i =0; i < 10; i++) {
				sb.append(i);
			}
			
			return sb.toString();
		}
	}
}
