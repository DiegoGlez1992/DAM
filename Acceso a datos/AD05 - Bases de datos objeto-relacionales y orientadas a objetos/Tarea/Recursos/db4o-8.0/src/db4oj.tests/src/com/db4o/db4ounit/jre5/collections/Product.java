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
package com.db4o.db4ounit.jre5.collections;

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;


/**
 */
@decaf.Ignore
public class Product extends ActivatableImpl {
	private String _code;
	private String _description;
	
	public Product(String code, String description) {
		_code = code;
		_description = description;
	}
	
	public String code() {
		activate(ActivationPurpose.READ);
		return _code;
	}
	
	public String description() {
		activate(ActivationPurpose.READ);
		return _description;
	}
	
	public boolean equals(Object p) {
		activate(ActivationPurpose.READ);
		
		if (p == null) return false;
		if (p.getClass() != this.getClass()) return false;
		
		Product rhs = (Product) p;
		return  rhs._code == _code;
	}
}
