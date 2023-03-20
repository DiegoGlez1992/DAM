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
 * Factory for ActivationDepth strategies.
 */
public interface ActivationDepthProvider {

	/**
	 * Returns an ActivationDepth suitable for the specified class and activation mode.
	 * 
	 * @param classMetadata root class that's being activated
	 * @param mode activation mode
	 * @return an appropriate ActivationDepth for the class and activation mode
	 */
	ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode);

	/**
	 * Returns an ActivationDepth that will activate at most *depth* levels.
	 * 
	 * A special case is Integer.MAX_VALUE (int.MaxValue for .net) for which a
	 * FullActivationDepth object must be returned.
	 * 
	 * @param depth 
	 * @param mode
	 * 
	 * @return
	 */
	ActivationDepth activationDepth(int depth, ActivationMode mode);
}
