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
package com.db4o.internal.cluster;

import com.db4o.cluster.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

/**
 * 
 * @exclude
 */
public class ClusterConstraints  extends ClusterConstraint implements Constraints{
    
    public ClusterConstraints(Cluster cluster, Constraint[] constraints){
        super(cluster, constraints);
    }

    public Constraint[] toArray() {
        synchronized(_cluster){
            Collection4 all = new Collection4();
            for (int i = 0; i < _constraints.length; i++) {
                ClusterConstraint c = (ClusterConstraint)_constraints[i];
                for (int j = 0; j < c._constraints.length; j++) {
                    all.add(c._constraints[j]);
                }
            }
            Constraint[] res = new Constraint[all.size()];
            all.toArray(res);
            return res;
        }
    }
}

