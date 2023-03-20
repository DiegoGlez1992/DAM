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
package db4ounit;

import java.io.*;

import com.db4o.foundation.*;

public class TestFailureCollection extends Printable implements Iterable4 {
	
	private final Collection4 _failures = new Collection4();
	
	public Iterator4 iterator() {
		return _failures.iterator();
	}
	
	/**
	 * @sharpen.property Count
	 */
	public int size() {
		return _failures.size();
	}
	
	public void add(TestFailure failure) {
		_failures.add(failure);
	}
	
	public void print(Writer writer) throws IOException {
		printSummary(writer);
		printDetails(writer);
	}

	private void printSummary(Writer writer) throws IOException {
		int index = 1;
		Iterator4 e = iterator();
		while (e.moveNext()) {
			writer.write(String.valueOf(index));
			writer.write(") ");
			writer.write(((TestFailure)e.current()).testLabel());
			writer.write(TestPlatform.NEW_LINE);
			++index;
		}
	}

	private void printDetails(Writer writer) throws IOException {
		int index = 1;
		Iterator4 e = iterator();
		while (e.moveNext()) {
			writer.write(TestPlatform.NEW_LINE);
			writer.write(String.valueOf(index));
			writer.write(") ");
			((Printable)e.current()).print(writer);
			writer.write(TestPlatform.NEW_LINE);
			++index;
		}
	}
}
