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

/**
 * Diagnostic to recommend Defragment when needed.
 */
public class DefragmentRecommendation extends DiagnosticBase{
	
	private final DefragmentRecommendationReason _reason;
	
	public DefragmentRecommendation(DefragmentRecommendationReason reason){
		_reason = reason;
	}
	
	public static class DefragmentRecommendationReason{
		
		final String _message;

		public DefragmentRecommendationReason(String reason) {
			_message = reason;
		}

		public static final DefragmentRecommendationReason DELETE_EMBEDED = 
			new DefragmentRecommendationReason("Delete Embedded not supported on old file format."); 
		
	}
	
	public String problem() {
		return "Database file format is old or database is highly fragmented.";
	}
	
	public Object reason() {
		return _reason._message;
	}
	
	public String solution() {
		return "Defragment the database.";
	}
	
}
