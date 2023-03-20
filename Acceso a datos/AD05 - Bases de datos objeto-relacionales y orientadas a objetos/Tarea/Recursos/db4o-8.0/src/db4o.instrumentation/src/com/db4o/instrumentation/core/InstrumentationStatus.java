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
package com.db4o.instrumentation.core;

/**
 * Used to report success/status after applying an instrumentation step.
 */
public class InstrumentationStatus {

	public final static InstrumentationStatus FAILED = new InstrumentationStatus(false, false, "FAILED");
	public final static InstrumentationStatus INSTRUMENTED = new InstrumentationStatus(true, true, "INSTRUMENTED");
	public final static InstrumentationStatus NOT_INSTRUMENTED = new InstrumentationStatus(true, false, "NOT INSTRUMENTED");
	
	private final boolean _canContinue;
	private final boolean _isInstrumented;
	private final String _name;
	
	private InstrumentationStatus(final boolean canContinue, final boolean isInstrumented, String name) {
		_canContinue = canContinue;
		_isInstrumented = isInstrumented;
		_name = name;
	}
	
	public boolean canContinue() {
		return _canContinue;
	}
	
	public boolean isInstrumented() {
		return _isInstrumented;
	}
	
	public InstrumentationStatus aggregate(InstrumentationStatus status, boolean ignoreFailure) {
		if(!(ignoreFailure || (_canContinue && status._canContinue))) {
			return FAILED;
		}
		if(_isInstrumented || status._isInstrumented) {
			return INSTRUMENTED;
		}
		return NOT_INSTRUMENTED;
	}
	
	public String toString() {
		return _name;
	}
}
