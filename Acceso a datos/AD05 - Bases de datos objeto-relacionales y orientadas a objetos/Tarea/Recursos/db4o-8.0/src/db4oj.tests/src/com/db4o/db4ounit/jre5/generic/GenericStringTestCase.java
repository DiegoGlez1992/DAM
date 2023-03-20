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
package com.db4o.db4ounit.jre5.generic;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 */
@decaf.Ignore
public class GenericStringTestCase extends AbstractDb4oTestCase {
	public static void main(String[] args) {
		new GenericStringTestCase().runAll();
	}

	public void test1() throws Exception {
		store(new StringWrapper1<String>("hello"));
		StringWrapper1 sw = (StringWrapper1) retrieveOnlyInstance(StringWrapper1.class);
		Assert.areEqual("hello", sw.str);
	}
	
	public void test2() throws Exception {
		store(new StringWrapper1<String>("hello"));
		reopen();
		StringWrapper1 sw = (StringWrapper1) retrieveOnlyInstance(StringWrapper1.class);
		Assert.areEqual("hello", sw.str);
	}
	
	public void test3() throws Exception {
		store(new StringWrapper2<String>("hello"));
		StringWrapper2 sw = (StringWrapper2) retrieveOnlyInstance(StringWrapper2.class);
		Assert.areEqual("hello", sw.str);
	}
	
	public void test4() throws Exception {
		store(new StringWrapper2<String>("hello"));
		reopen();
		StringWrapper2 sw = (StringWrapper2) retrieveOnlyInstance(StringWrapper2.class);
		Assert.areEqual("hello", sw.str);
	}

	static class StringWrapper1<T> {
		public T str;

		public StringWrapper1(T s) {
			str = s;
		}
	}

	static class StringWrapper2<T extends Comparable> {
		public T str;
		
		public StringWrapper2() {

		}

		public StringWrapper2(T s) {
			str = s;
		}
	}
}
