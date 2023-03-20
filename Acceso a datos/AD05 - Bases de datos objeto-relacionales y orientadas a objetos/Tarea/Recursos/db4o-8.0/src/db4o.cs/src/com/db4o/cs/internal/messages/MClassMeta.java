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
package com.db4o.cs.internal.messages;

import com.db4o.*;
import com.db4o.cs.internal.*;
import com.db4o.internal.*;
import com.db4o.reflect.generic.*;

public class MClassMeta extends MsgObject implements MessageWithResponse {
	public Msg replyFromServer() {
		unmarshall();
		try{
			synchronized (containerLock()) {
	            ClassInfo classInfo = (ClassInfo) readObjectFromPayLoad();
	            ClassInfoHelper classInfoHelper = serverMessageDispatcher().classInfoHelper();
	            GenericClass genericClass = classInfoHelper.classMetaToGenericClass(container().reflector(), classInfo);
	            if (genericClass != null) {
	                
    				Transaction trans = container().systemTransaction();
    
    				ClassMetadata classMetadata = container().produceClassMetadata(genericClass);
    				if (classMetadata != null) {
    					container().checkStillToSet();
    					classMetadata.setStateDirty();
    					classMetadata.write(trans);
    					trans.commit();
    					StatefulBuffer returnBytes = container()
    							.readStatefulBufferById(trans, classMetadata.getID());
    					return Msg.OBJECT_TO_CLIENT.getWriter(returnBytes);
    				}
    			}
			}
		}catch(Exception e){
			if(Debug4.atHome){
				e.printStackTrace();
			}
		}
		return Msg.FAILED;
	}

}
