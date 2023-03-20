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
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


public class MockWriteContext extends MockMarshallingContext implements WriteContext{

    public MockWriteContext(ObjectContainer objectContainer) {
        super(objectContainer);
    }
    
    public void writeObject(TypeHandler4 handler, Object obj) {
        Handlers4.write(handler, this, obj);
    }

    public void writeAny(Object obj) {
        ClassMetadata classMetadata = container().classMetadataForObject(obj);
        writeInt(classMetadata.getID());
        Handlers4.write(classMetadata.typeHandler(), this, obj);
    }

    public ReservedBuffer reserve(int length) {
        ReservedBuffer reservedBuffer = new ReservedBuffer() {
            private final int reservedOffset = offset();
            public void writeBytes(byte[] bytes) {
                int currentOffset = offset();
                seek(reservedOffset);
                MockWriteContext.this.writeBytes(bytes);
                seek(currentOffset);
            }
        };
        seek(offset() + length );
        return reservedBuffer;
    }
    
}
