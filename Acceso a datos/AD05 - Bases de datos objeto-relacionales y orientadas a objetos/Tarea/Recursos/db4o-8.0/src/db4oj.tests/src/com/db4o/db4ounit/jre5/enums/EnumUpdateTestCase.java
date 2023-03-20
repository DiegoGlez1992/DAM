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
package com.db4o.db4ounit.jre5.enums;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
@decaf.Ignore
public class EnumUpdateTestCase extends AbstractDb4oTestCase {
	
	public enum MyEnum {
		
	    A("A"),
	    B("B");
	    
	    private String _name;
	    
	    private boolean _modified;
	    
	    private MyEnum(String name){
	    	_name = name;
	    }
	    
	    public void modify(){
	    	_modified = true;
	    }
	    
	    public boolean isModified(){
	    	return _modified;
	    }
	}
	
	public void test(){
		db().store(MyEnum.A);
		MyEnum.A.modify();
		db().store(MyEnum.A);
		db().commit();
		MyEnum committedMyEnumA = db().peekPersisted(MyEnum.A, Integer.MAX_VALUE, true);
		Assert.areNotSame(MyEnum.A, committedMyEnumA);
		Assert.isTrue(committedMyEnumA.isModified());
	}
	
	public static void main(String[] args) {
		new EnumUpdateTestCase().runSolo();
	}

}
