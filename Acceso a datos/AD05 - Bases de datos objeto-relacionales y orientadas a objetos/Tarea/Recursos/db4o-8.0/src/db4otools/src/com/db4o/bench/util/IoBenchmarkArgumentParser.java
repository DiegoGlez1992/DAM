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
package com.db4o.bench.util;

/**
 * 
 * @sharpen.ignore
 *
 */
public class IoBenchmarkArgumentParser {
	
	private String _resultsFile2;
	private String _resultsFile1;
	private int _objectCount;
	private boolean _delayed;

	public IoBenchmarkArgumentParser(String[] arguments) {
		validateArguments(arguments);
	}
		
	private void validateArguments(String[] arguments) {
		if (arguments.length != 1 && arguments.length != 3) {
			System.out.println("Usage: IoBenchmark <object-count> [<results-file-1> <results-file-2>]");
			throw new RuntimeException("Usage: IoBenchmark <object-count> [<results-file-1> <results-file-2>]");
		}
		_objectCount = java.lang.Integer.parseInt(arguments[0]);
		if (arguments.length > 1) {
			_resultsFile1 = arguments[1];
			_resultsFile2 = arguments[2];
			_delayed = true;
		}
	}


	public int objectCount() {
		return _objectCount;
	}
	
	public String resultsFile1() {
		return _resultsFile1;
	}
	
	public String resultsFile2() {
		return _resultsFile2;
	}
	
	public boolean delayed() {
		return _delayed;
	}
}
