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

import com.db4o.internal.*;
import com.db4o.internal.activation.*;

import db4ounit.mocking.*;

/**
 * An ActivationDepthProvider that records ActivationDepthProvider calls and
 * delegates to another provider.
 */
public class MockActivationDepthProvider extends MethodCallRecorder implements ActivationDepthProvider {
	
	private final ActivationDepthProvider _delegate;
	
	public MockActivationDepthProvider() {
		_delegate = LegacyActivationDepthProvider.INSTANCE;
	}

	public ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode) {
		record(new MethodCall("activationDepthFor", classMetadata, mode));
		return _delegate.activationDepthFor(classMetadata, mode);
	}

	public ActivationDepth activationDepth(int depth, ActivationMode mode) {
		record(new MethodCall("activationDepth", new Integer(depth), mode));
		return _delegate.activationDepth(depth, mode);
	}
}
