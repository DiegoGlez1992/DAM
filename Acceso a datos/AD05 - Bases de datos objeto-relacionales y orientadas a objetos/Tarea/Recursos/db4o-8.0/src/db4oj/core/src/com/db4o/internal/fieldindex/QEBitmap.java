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
package com.db4o.internal.fieldindex;

import com.db4o.internal.query.processor.*;

class QEBitmap {
	public static QEBitmap forQE(QE qe) {
    	boolean[] bitmap = new boolean[4];
    	qe.indexBitMap(bitmap);
    	return new QEBitmap(bitmap);
    }
	
	private QEBitmap(boolean[] bitmap) {
		_bitmap = bitmap;
	}
	
	private boolean[] _bitmap;

	public boolean takeGreater() {
		return _bitmap[QE.GREATER];
	}
	
	public boolean takeEqual() {
		return _bitmap[QE.EQUAL];
	}

	public boolean takeSmaller() {
		return _bitmap[QE.SMALLER];
	}
}