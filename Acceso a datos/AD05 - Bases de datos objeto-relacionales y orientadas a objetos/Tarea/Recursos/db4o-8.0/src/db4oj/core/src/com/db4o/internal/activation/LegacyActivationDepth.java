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
 * Activates an object graph to a specific depth respecting any
 * activation configuration settings that might be in effect.
 */
public class LegacyActivationDepth extends ActivationDepthImpl {

	private final int _depth;
	
	public LegacyActivationDepth(int depth) {
		this(depth, ActivationMode.ACTIVATE);
	}

	public LegacyActivationDepth(int depth, ActivationMode mode) {
		super(mode);
		_depth = depth;
	}

	public ActivationDepth descend(ClassMetadata metadata) {
		if (null == metadata) {
			return new LegacyActivationDepth(_depth -1 , _mode);
		}
		return new LegacyActivationDepth(descendDepth(metadata), _mode);
	}

	private int descendDepth(ClassMetadata metadata) {
		int depth = configuredActivationDepth(metadata) - 1;
		if (metadata.isStruct()) {
			// 	We also have to instantiate structs completely every time.
			return Math.max(1, depth);
		}
		return depth;
	}

	private int configuredActivationDepth(ClassMetadata metadata) {
		Config4Class config = metadata.configOrAncestorConfig();
		if (config != null && _mode.isActivate()) {
			return config.adjustActivationDepth(_depth);
		}
		return _depth;
	}

	public boolean requiresActivation() {
		return _depth > 0;
	}

}
