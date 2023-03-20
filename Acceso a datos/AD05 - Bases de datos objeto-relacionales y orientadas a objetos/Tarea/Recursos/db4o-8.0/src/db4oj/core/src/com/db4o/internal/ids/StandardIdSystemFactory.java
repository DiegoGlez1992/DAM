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
package com.db4o.internal.ids;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class StandardIdSystemFactory {
	
	public static final byte LEGACY = 0;
	
	public static final byte POINTER_BASED = 1;
	
	public static final byte STACKED_BTREE = 2;
	
	public static final byte DEFAULT = STACKED_BTREE;
	
	public static final byte IN_MEMORY = 3;
	
	public static final byte CUSTOM = 4;
	
	public static final byte SINGLE_BTREE = 5;

	public static IdSystem newInstance(LocalObjectContainer localContainer) {
		SystemData systemData = localContainer.systemData();
		byte idSystemType = systemData.idSystemType();
		
        switch(idSystemType){
	    	case LEGACY:
	    		return new PointerBasedIdSystem(localContainer);
	    	case POINTER_BASED:
	    		return new PointerBasedIdSystem(localContainer);
			case STACKED_BTREE:
				InMemoryIdSystem inMemoryIdSystem = new InMemoryIdSystem(localContainer);
				BTreeIdSystem bTreeIdSystem = new BTreeIdSystem(localContainer, inMemoryIdSystem);
				systemData.freespaceIdSystem(bTreeIdSystem.freespaceIdSystem());
				return new BTreeIdSystem(localContainer, bTreeIdSystem);
			case SINGLE_BTREE:
				InMemoryIdSystem smallInMemoryIdSystem = new InMemoryIdSystem(localContainer);
				BTreeIdSystem smallBTreeIdSystem = new BTreeIdSystem(localContainer, smallInMemoryIdSystem);
				systemData.freespaceIdSystem(smallBTreeIdSystem.freespaceIdSystem());
				return smallBTreeIdSystem;
	    	case IN_MEMORY:
	    		return new InMemoryIdSystem(localContainer);
	    	case CUSTOM:
	    		IdSystemFactory customIdSystemFactory = localContainer.configImpl().customIdSystemFactory();
	    		if(customIdSystemFactory == null){
	    			throw new Db4oFatalException("Custom IdSystem configured but no factory was found. See IdSystemConfiguration#useCustomSystem()");
	    		}
	    		return customIdSystemFactory.newInstance(localContainer);
	        default:
	        	return new PointerBasedIdSystem(localContainer);
        }
	            
    }
	

}
