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
package EDU.purdue.cs.bloat.editor;

import java.util.*;

import EDU.purdue.cs.bloat.tree.*;

public class UseMap {

	public Hashtable map;

	public UseMap() {
		map = new Hashtable();
	}

	public void add(final LocalExpr use, final Instruction inst) {

		final Node def = use.def();
		if (def != null) {
			map.put(inst, def);
		}
	}

	public boolean hasDef(final Instruction inst) {

		return map.containsKey(inst);
	}

	public boolean hasSameDef(final Instruction a, final Instruction b) {
		return map.containsKey(a) && map.containsKey(b)
				&& (map.get(a) == map.get(b));
	}

}
