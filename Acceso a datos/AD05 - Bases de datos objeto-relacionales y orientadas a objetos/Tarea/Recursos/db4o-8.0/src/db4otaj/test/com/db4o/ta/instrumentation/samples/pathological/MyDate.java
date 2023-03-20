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
package com.db4o.ta.instrumentation.samples.pathological;


import com.db4o.activation.*;
import com.db4o.ta.*;


public class MyDate extends SuperDate implements Activatable {
	
    public boolean after(SuperDate date) {
    	activate(ActivationPurpose.READ);
    	if(date instanceof Activatable) {
    		((Activatable)date).activate(ActivationPurpose.READ);
    	}
    	return super.after(date);
    }

	public void activate(ActivationPurpose purpose) {
		// TODO Auto-generated method stub
		
	}

	public void bind(Activator activator) {
		// TODO Auto-generated method stub
		
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}

