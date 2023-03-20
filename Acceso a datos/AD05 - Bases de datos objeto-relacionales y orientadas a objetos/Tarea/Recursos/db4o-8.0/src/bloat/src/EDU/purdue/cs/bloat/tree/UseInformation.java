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
package EDU.purdue.cs.bloat.tree;

/**
 * UseInformation stores information about a use of a local variable.
 * 
 * @author Thomas VanDrunen
 */
public class UseInformation {

	int type;

	int type0s;

	int type1s;

	int type0_x1s;

	int type0_x2s;

	int type1_x1s;

	int type1_x2s;

	public UseInformation() {

		type = 2; // assume type > 1 unless discovered otherwise
		type0s = 0;
		type1s = 0;
		type0_x1s = 0;
		type0_x2s = 0;
		type1_x1s = 0;
		type1_x2s = 0;
	}
}
