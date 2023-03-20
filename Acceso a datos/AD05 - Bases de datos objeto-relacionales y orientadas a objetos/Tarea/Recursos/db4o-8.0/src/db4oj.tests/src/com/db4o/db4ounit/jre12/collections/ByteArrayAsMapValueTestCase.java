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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Ignore(decaf.Platform.JDK11)
public class ByteArrayAsMapValueTestCase extends AbstractDb4oTestCase{
	
	public static class Item{
		
		public Map<String, Object> _map;
		
	}
	
	public static class ByteArrayHolder{
		
		byte[] _bytes;
		
		public ByteArrayHolder(byte[] bytes) {
			_bytes = bytes;
		
		}
	}
	
	public void test() throws Exception {
		Item item = new Item();
		item._map = new HashMap();
		Map<String, Object> map = item._map;
		store(item);
		long initialLength = fileSession().fileLength();
		map.put("one", new ByteArrayHolder(newByteArray()));
		store(map);
		db().commit();
		long lengthAfterStoringHolder = fileSession().fileLength();
		map.put("two", newByteArray());
		store(map);
		db().commit();
		long lengthAfterStoringByteArray = fileSession().fileLength();
		long increaseForHolder = lengthAfterStoringHolder - initialLength;
		long increaseForByteArray = lengthAfterStoringByteArray - lengthAfterStoringHolder;
		Assert.isSmaller(2, increaseForByteArray / increaseForHolder);
	}

	private byte[] newByteArray() {
		return new byte[1000];
	}
	

}
