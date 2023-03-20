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
package com.db4o.db4ounit.common.ta.nested;

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;

/**
 * 	@sharpen.partial
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class OuterClass extends ActivatableImpl {
	
	public int _foo;
	
	public int foo() {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return _foo;
	}
	
	public InnerClass createInnerObject() {
		return new InnerClass();
	}
	
	/**
	 * 	@sharpen.partial
 	 */
	@decaf.Ignore(decaf.Platform.JDK11)
    public class InnerClass extends ActivatableImpl {	
	
		public OuterClass getOuterObject(){
			// TA BEGIN
			activate(ActivationPurpose.READ);
			// TA END
			return OuterClass.this;
		}

		public OuterClass getOuterObjectWithoutActivation() {
			return OuterClass.this;
		}
	} 

}
