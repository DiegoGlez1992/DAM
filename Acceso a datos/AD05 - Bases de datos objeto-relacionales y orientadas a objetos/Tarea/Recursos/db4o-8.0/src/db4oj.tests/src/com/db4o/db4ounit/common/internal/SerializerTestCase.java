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
package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SerializerTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
		new SerializerTestCase().runAll();
	}
	
    /**
     * @sharpen.if !SILVERLIGHT
     */
	public void testExceptionMarshalling() throws Exception {
		ReflectException e = new ReflectException(new NullPointerException());
		SerializedGraph marshalled = Serializer.marshall(stream().container(), e);
		Assert.isTrue(marshalled.length() > 0);
	}
}
