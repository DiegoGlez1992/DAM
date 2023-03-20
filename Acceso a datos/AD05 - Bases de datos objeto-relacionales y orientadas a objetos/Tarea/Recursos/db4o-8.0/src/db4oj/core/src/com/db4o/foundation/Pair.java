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
package com.db4o.foundation;


public class Pair<TFirst, TSecond> {
	
	public static <TFirst, TSecond> Pair<TFirst, TSecond> of(TFirst first, TSecond second) {
		return new Pair(first, second);
	}
	
	public TFirst first;
	public TSecond second;
	
	public Pair(TFirst first, TSecond second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "Pair.of(" + first + ", " + second + ")";
	}

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((first == null) ? 0 : first.hashCode());
	    result = prime * result + ((second == null) ? 0 : second.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Pair other = (Pair) obj;
	    if (first == null) {
		    if (other.first != null)
			    return false;
	    } else if (!first.equals(other.first))
		    return false;
	    if (second == null) {
		    if (other.second != null)
			    return false;
	    } else if (!second.equals(other.second))
		    return false;
	    return true;
    }


}
