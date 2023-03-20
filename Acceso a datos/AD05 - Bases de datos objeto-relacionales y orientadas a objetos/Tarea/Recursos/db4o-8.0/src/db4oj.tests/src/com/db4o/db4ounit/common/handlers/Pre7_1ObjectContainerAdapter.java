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
package com.db4o.db4ounit.common.handlers;

import java.lang.reflect.*;

import db4ounit.*;

/** @sharpen.partial */
public class Pre7_1ObjectContainerAdapter extends AbstractObjectContainerAdapter {

	public void store(Object obj) {
		storeObject(obj);
	}

	public void store(Object obj, int depth) {
		storeObject(obj, depth);
	}

	private void storeObject(Object obj) {
		try {
			storeInternal(resolveSetMethod(), new Object[] { obj });
		} catch (Exception e) {
			Assert.fail("Call to set method failed.", e);
		}
	}

	private void storeObject(Object obj, int depth) {
		try {
			storeInternal(resolveSetWithDepthMethod(), new Object[] { obj, depth });
		} catch (Exception e) {
			Assert.fail("Call to set method failed.", e);
		}
	}

	public void storeInternal(Method method, Object[] args) {

		try {
			method.invoke(db, args);
		} catch (Exception e) {
			Assert.fail(e.toString());
			e.printStackTrace();
		}
	}

	 
	private Method resolveSetWithDepthMethod() throws Exception {
		if (setWithDepthMethod != null) return setWithDepthMethod;
		
		setWithDepthMethod = db.getClass().getMethod(setMethodName(), new Class[] { Object.class, Integer.TYPE });
		
		return setWithDepthMethod;
	}

	private Method resolveSetMethod() throws Exception {
		if (setMethod != null) return setMethod;
		
		setMethod = db.getClass().getMethod(setMethodName(), new Class[] { Object.class });
		
		return setMethod;
	}
	
	/** @sharpen.ignore */
	private String setMethodName() {
		return "set";
	}
	
	private Method setWithDepthMethod = null;
	private Method setMethod = null;
}
