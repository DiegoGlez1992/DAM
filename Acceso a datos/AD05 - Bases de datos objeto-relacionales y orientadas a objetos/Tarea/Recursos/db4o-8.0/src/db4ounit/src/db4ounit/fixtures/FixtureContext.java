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
package db4ounit.fixtures;

import com.db4o.foundation.*;

/**
 * Set of live {@link FixtureVariable}/value pairs.
 * 
 */
public class FixtureContext {
	
	private static final DynamicVariable _current = new DynamicVariable() {
		private final FixtureContext EMPTY_CONTEXT = new FixtureContext();
		
		protected Object defaultValue() {
			return EMPTY_CONTEXT;
		}
	};
	
	/**
	 * @sharpen.property
	 */
	public static FixtureContext current() {
		return (FixtureContext)_current.value();
	}
	
	public Object run(Closure4 closure) {
		return _current.with(this, closure);
	}

	public void run(Runnable block) {
		_current.with(this, block);
	}
	
	static class Found {
		public final Object value;
		
		public Found(Object value_) {
			value = value_;
		}
	}
	
	Found get(FixtureVariable fixture) {
		return null;
	}
	
	public FixtureContext combine(final FixtureContext parent) {
		return new FixtureContext() {
			Found get(FixtureVariable fixture) {
				Found found = FixtureContext.this.get(fixture);
				if (null != found) return found;
				return parent.get(fixture);
			}
		};
	}

	FixtureContext add(final FixtureVariable fixture, final Object value) {
		return new FixtureContext() {
			Found get(FixtureVariable key) {
				if (key == fixture) {
					return new Found(value);
				}
				return FixtureContext.this.get(key);
			}
		};
	}
}
