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
package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class AddressKeySlotHandler extends SlotHandler{
	
	public int compareTo(Object obj) {
		return _current.compareByAddress((Slot)obj);
	}
	
	public PreparedComparison prepareComparison(Context context, Object slot) {
		final Slot sourceSlot = (Slot)slot;
		return new PreparedComparison() {
			public int compareTo(Object obj) {
				final Slot targetSlot = (Slot)obj;
				
				// FIXME: The comparison method in #compareByAddress is the wrong way around.
				
				// Fix there and here after other references are fixed.
				
				return - sourceSlot.compareByAddress(targetSlot);
			}
		};
	}

}
