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
package com.db4o.db4ounit.common.activation;

import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.internal.activation.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationDepthTestCase extends AbstractDb4oTestCase {
	
	public static final class NonTAAware {
	}
	
	public static final class TAAware implements Activatable {
		public void activate(ActivationPurpose purpose) {
		}

		public void bind(Activator activator) {
		}
	}
	
	protected void configure(Configuration config) throws Exception {
		// configured depth should be ignored by ta provider
		config.objectClass(TAAware.class).minimumActivationDepth(42);
		config.objectClass(NonTAAware.class).minimumActivationDepth(42);
	}
	
	protected void store() throws Exception {
		store(new TAAware());
		store(new NonTAAware());
	}
	
	public void testDescendingFromNonTAAwareToTAAware() {		
		ActivationDepth depth = nonTAAwareDepth();
		
		ActivationDepth child = depth.descend(classMetadataFor(TAAware.class));
		Assert.isFalse(child.requiresActivation());
	}

	public void testDefaultActivationNonTAAware() {
		ActivationDepth depth = nonTAAwareDepth();
		Assert.isTrue(depth.requiresActivation());
		
		ActivationDepth child = depth.descend(classMetadataFor(NonTAAware.class));
		Assert.isTrue(child.requiresActivation());
	}
	
	public void testDefaultActivationTAAware() {
		ActivationDepth depth = TAAwareDepth();
		Assert.isFalse(depth.requiresActivation());
	}
	
	public void testSpecificActivationDepth() {
		ActivationDepth depth = provider().activationDepth(3, ActivationMode.ACTIVATE);
		assertDescendingDepth(3, depth, TAAware.class);
		assertDescendingDepth(3, depth, NonTAAware.class);
	}	
	
	public void testIntegerMaxValueMeansFull() {
		assertFullActivationDepthForMaxValue(ActivationMode.PEEK);
		assertFullActivationDepthForMaxValue(ActivationMode.ACTIVATE);
	}

	private void assertFullActivationDepthForMaxValue(final ActivationMode mode) {
		Assert.isInstanceOf(
			FullActivationDepth.class,
			provider().activationDepth(Integer.MAX_VALUE, mode));
	}

	private void assertDescendingDepth(int expectedDepth, ActivationDepth depth, Class clazz) {
		if (expectedDepth < 1) {
			Assert.isFalse(depth.requiresActivation());
			return;
		}
		Assert.isTrue(depth.requiresActivation());
		assertDescendingDepth(expectedDepth-1, depth.descend(classMetadataFor(clazz)), clazz);
	}

	private ActivationDepth nonTAAwareDepth() {
		return transparentActivationDepthFor(NonTAAware.class);
	}
	
	private ActivationDepth transparentActivationDepthFor(Class clazz) {
		return provider().activationDepthFor(classMetadataFor(clazz), ActivationMode.ACTIVATE);
	}

	private TransparentActivationDepthProviderImpl provider() {
		return new TransparentActivationDepthProviderImpl();
	}

	private ActivationDepth TAAwareDepth() {
		return transparentActivationDepthFor(TAAware.class);
	}
}
