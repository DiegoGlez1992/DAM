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
package com.db4o.internal.activation;

import com.db4o.internal.*;

/**
 * Activates a fixed depth of the object graph regardless of
 * any existing activation depth configuration settings.
 */
public class FixedActivationDepth extends ActivationDepthImpl {

	private final int _depth;

	public FixedActivationDepth(int depth, ActivationMode mode) {
		super(mode);
		_depth = depth;
	}
	
	public FixedActivationDepth(int depth) {
		this(depth, ActivationMode.ACTIVATE);
	}
	
	public boolean requiresActivation() {
		return _depth > 0;
	}
	
	public ActivationDepth descend(ClassMetadata metadata) {
		if (_depth < 1) {
			return this;
		}
		return new FixedActivationDepth(_depth-1, _mode);
	}

	// TODO code duplication in fixed activation/update depth
	public FixedActivationDepth adjustDepthToBorders() {
		return new FixedActivationDepth(DepthUtil.adjustDepthToBorders(_depth));
	}

}
