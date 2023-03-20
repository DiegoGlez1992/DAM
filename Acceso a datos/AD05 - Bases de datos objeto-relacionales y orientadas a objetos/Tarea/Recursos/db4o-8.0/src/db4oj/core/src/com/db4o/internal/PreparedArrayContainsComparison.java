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
package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class PreparedArrayContainsComparison implements PreparedComparison {
	
	private final ArrayHandler _arrayHandler;
	
	private final PreparedComparison _preparedComparison; 
	
	private ObjectContainerBase _container;
	
	public PreparedArrayContainsComparison(Context context, ArrayHandler arrayHandler, TypeHandler4 typeHandler, Object obj){
		_arrayHandler = arrayHandler;
		_preparedComparison = Handlers4.prepareComparisonFor(typeHandler, context, obj);
		_container = context.transaction().container();
	}

	public int compareTo(Object obj) {
		// We never expect this call
		// TODO: The callers of this class should be refactored to pass a matcher and
		//       to expect a PreparedArrayComparison.
		throw new IllegalStateException();
	}
	
    public boolean IsEqual(Object array) {
    	return isMatch(array, IntMatcher.ZERO);
    }

    public boolean isGreaterThan(Object array) {
    	return isMatch(array, IntMatcher.POSITIVE);
    }

    public boolean isSmallerThan(Object array) {
    	return isMatch(array, IntMatcher.NEGATIVE);
    }
    
    private boolean isMatch(Object array, IntMatcher matcher){
        if(array == null){
            return false;
        }
        Iterator4 i = _arrayHandler.allElements(_container, array);
        while (i.moveNext()) {
        	if(matcher.match(_preparedComparison.compareTo(i.current()))){
        		return true;
        	}
        }
        return false;
    }

}
