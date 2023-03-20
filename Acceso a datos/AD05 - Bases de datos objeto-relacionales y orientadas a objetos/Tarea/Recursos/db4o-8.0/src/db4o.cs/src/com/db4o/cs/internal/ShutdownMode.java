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
package com.db4o.cs.internal;

public abstract class ShutdownMode {
	
	public static class NormalMode extends ShutdownMode {
		
		NormalMode() {
		}
		
		@Override
		public boolean isFatal() {
			return false;
		}
	}
	
	public static class FatalMode extends ShutdownMode {
		
		private Throwable _exc;
		
		FatalMode(Throwable exc) {
			_exc = exc;
		}
		
		public Throwable exc() {
			return _exc;
		}

		@Override
		public boolean isFatal() {
			return true;
		}
	}
	
	public final static ShutdownMode NORMAL = new NormalMode();
	
	public static ShutdownMode fatal(Throwable exc) {
		return new FatalMode(exc);
	}

	public abstract boolean isFatal();
	
	private ShutdownMode() {
	}
}