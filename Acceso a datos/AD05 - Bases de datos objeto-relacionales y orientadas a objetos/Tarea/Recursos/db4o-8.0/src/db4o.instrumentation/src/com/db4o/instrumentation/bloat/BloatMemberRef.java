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
package com.db4o.instrumentation.bloat;

import EDU.purdue.cs.bloat.editor.*;

public class BloatMemberRef extends BloatRef {

	protected final MemberRef _member;
	
	public BloatMemberRef(BloatReferenceProvider provider, MemberRef memberRef) {
		super(provider);
		_member = memberRef;
	}
	
	public String name() {
		return _member.name();
	}

	public MemberRef member() {
		return _member;
	}
	
	public String toString() {
		return name();
	}

	public static MemberRef memberRef(Object memberRef) {
		return ((BloatMemberRef)memberRef).member();
	}

}