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
package db4ounit.extensions.fixtures;

public class Db4oFixtures {

	public static Db4oSolo newSolo() {
		return new Db4oSolo();
	}
	
	public static Db4oInMemory newInMemory() {
		return new Db4oInMemory();
	}
	
	public static MultiSessionFixture newEmbedded() {
        return new Db4oEmbeddedSessionFixture();
    }

	public static MultiSessionFixture newEmbedded(String label) {
		return new Db4oEmbeddedSessionFixture(label);
	}
	
	public static Db4oNetworking newNetworkingCS() {
        return new Db4oNetworking();
    }
	
	public static Db4oNetworking newNetworkingCS(String label) {
		return new Db4oNetworking(label);
	}

	@decaf.Ignore
	public static Db4oAndroid newAndroid() {
		return new Db4oAndroid();
	}
}
