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
package com.db4o.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.tree.*;

public class OpSymmetryUtil {

	private final static Map<Integer,Integer> OP_SYMMETRY = new HashMap<Integer,Integer>();

	static {
		OP_SYMMETRY.put(IfStmt.EQ, IfStmt.EQ);
		OP_SYMMETRY.put(IfStmt.NE, IfStmt.NE);
		OP_SYMMETRY.put(IfStmt.LT, IfStmt.GT);
		OP_SYMMETRY.put(IfStmt.GT, IfStmt.LT);
		OP_SYMMETRY.put(IfStmt.LE, IfStmt.GE);
		OP_SYMMETRY.put(IfStmt.GE, IfStmt.LE);
	}

	public static int counterpart(int op) {
		return OP_SYMMETRY.get(op);
	}
}
