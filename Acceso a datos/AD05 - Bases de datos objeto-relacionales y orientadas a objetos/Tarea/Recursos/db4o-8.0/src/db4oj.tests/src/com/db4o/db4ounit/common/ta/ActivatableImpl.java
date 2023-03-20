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
package com.db4o.db4ounit.common.ta;

import com.db4o.activation.*;

public class ActivatableImpl /* TA BEGIN */ implements com.db4o.ta.Activatable /* TA END */{

	// TA BEGIN
	private transient Activator _activator;
	// TA END

	//	 TA BEGIN
	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }

		_activator = activator;
	}
	
	public void activate(ActivationPurpose purpose) {
		if (_activator == null) return;
		_activator.activate(purpose);
	}
	
	/**
	 * @sharpen.ignore 
	 */
	protected Object clone() throws CloneNotSupportedException {
		// clone must remember to reset the _activator field
		final ActivatableImpl clone = (ActivatableImpl)super.clone();
		clone._activator = null;
		return clone;
	}
	// TA END
}
