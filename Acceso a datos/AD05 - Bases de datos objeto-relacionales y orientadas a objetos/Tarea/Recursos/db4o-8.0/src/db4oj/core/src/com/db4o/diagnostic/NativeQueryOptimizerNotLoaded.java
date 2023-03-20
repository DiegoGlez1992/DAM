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
package com.db4o.diagnostic;

public class NativeQueryOptimizerNotLoaded extends DiagnosticBase {

	private int _reason;
	private final Exception _details;
	public final static int NQ_NOT_PRESENT 			= 1;
	public final static int NQ_CONSTRUCTION_FAILED 	= 2;

	
	public NativeQueryOptimizerNotLoaded(int reason, Exception details) {
		_reason = reason;
		_details = details;
	}
	public String problem() {
		return "Native Query Optimizer could not be loaded";
	}

	public Object reason() {
		switch (_reason) {
		case NQ_NOT_PRESENT:
			return AppendDetails("Native query not present.");
		case NQ_CONSTRUCTION_FAILED:
			return AppendDetails("Native query couldn't be instantiated.");
		default:
			return AppendDetails("Reason not specified.");
		}
	}

	public String solution() {
		return "If you to have the native queries optimized, please check that the native query jar is present in the class-path.";
	}

	private Object AppendDetails(String reason) {
		if (_details == null) {
			return reason;
		}
		
		return reason + "\n" + _details.toString();
	}
}
