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
package com.db4o.db4ounit.common.io;

import com.db4o.ext.*;
import com.db4o.io.*;


/**
 */
@decaf.Ignore
public class ThrowingStorage extends StorageDecorator {

	private final ThrowCondition _condition;
	
	public ThrowingStorage(Storage storage, ThrowCondition condition) {
		super(storage);
		_condition = condition;
	}

	@Override
	protected Bin decorate(BinConfiguration config, Bin bin) {
		return new ThrowingBin(bin, _condition);
	}

	static class ThrowingBin extends BinDecorator {
		private final ThrowCondition _condition;

		public ThrowingBin(Bin bin, ThrowCondition condition) {
			super(bin);
			_condition = condition;
		}

		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			if(_condition.shallThrow(pos, length)) {
				throw new Db4oIOException("FAIL");
			}
			_bin.write(pos, buffer, length);
		}
	}
}
