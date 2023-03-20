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
package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


public class MockReadContext extends MockMarshallingContext implements ReadContext{

    public MockReadContext(ObjectContainer objectContainer) {
        super(objectContainer);
    }

    public MockReadContext(MockWriteContext writeContext) {
        this(writeContext.objectContainer());
        writeContext._header.copyTo(_header, 0, 0, writeContext._header.length());
        writeContext._payLoad.copyTo(_payLoad, 0, 0, writeContext._payLoad.length());
    }
    
    public Object readObject(TypeHandler4 handler) {
        return Handlers4.readValueType(this, handler);
    }

    public BitMap4 readBitMap(int bitCount) {
        BitMap4 map = new BitMap4(_current._buffer, _current._offset, bitCount);
        _current.seek(_current.offset() + map.marshalledLength());
        return map;
    }

}
