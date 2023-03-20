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

import db4ounit.*;

final class FixtureDecorator implements TestDecorator {
	private final Object _fixture;
	private final FixtureVariable _provider;
	private final int _fixtureIndex;

	FixtureDecorator(FixtureVariable provider, Object fixture, int fixtureIndex) {
		_fixture = fixture;
		_provider = provider;
		_fixtureIndex = fixtureIndex;
	}

	public Test decorate(final Test test) {
		final String label = label();
		return test.transmogrify(new Function4<Test, Test>() {
			public Test apply(Test innerTest) {
				return new TestWithFixture(innerTest, label, _provider, _fixture);
			}
		});
		
	}

	private String label() {
		String label = _provider.label() + "[" + _fixtureIndex + "]";
		if(_fixture instanceof Labeled) {
			label += ":" + ((Labeled)_fixture).label();
		}
		return label;
	}
}