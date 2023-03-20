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
 * @exclude
 */
public class ClusterConstraint implements Constraint{
    
    final Cluster _cluster;
    final Constraint[] _constraints;
    
    public ClusterConstraint(Cluster cluster, Constraint[] constraints){
        _cluster = cluster; 
        _constraints = constraints;
    }
    
    private ClusterConstraint compatible (Constraint with){
        if(! (with instanceof ClusterConstraint)){
            throw new IllegalArgumentException();
        }
        ClusterConstraint other = (ClusterConstraint)with;
        if(other._constraints.length != _constraints.length){
            throw new IllegalArgumentException();
        }
        return other;
    }

    public Constraint and(Constraint with) {
        return join(with, true);
    }

    public Constraint or(Constraint with) {
        return join(with, false);
    }
    
    private Constraint join(Constraint with, boolean isAnd){
        synchronized(_cluster){
            ClusterConstraint other = compatible(with);
            Constraint[] newConstraints = new Constraint[_constraints.length];
            for (int i = 0; i < _constraints.length; i++) {
                newConstraints[i] = isAnd ? _constraints[i].and(other._constraints[i]) : _constraints[i].or(other._constraints[i]);
            }
            return new ClusterConstraint(_cluster, newConstraints);
        }
    }
    

    public Constraint equal() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].equal();
            }
            return this;
        }
    }

    public Constraint greater() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].greater();
            }
            return this;
        }
    }

    public Constraint smaller() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].smaller();
            }
            return this;
        }
    }

    public Constraint identity() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].identity();
            }
            return this;
        }
    }
    
    public Constraint byExample() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].byExample();
            }
            return this;
        }
    }

    public Constraint like() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].like();
            }
            return this;
        }
    }

    public Constraint startsWith(boolean caseSensitive) {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].startsWith(caseSensitive);
            }
            return this;
        }
    }

    public Constraint endsWith(boolean caseSensitive) {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].endsWith(caseSensitive);
            }
            return this;
        }
    }

    public Constraint contains() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].contains();
            }
            return this;
        }
    }

    public Constraint not() {
        synchronized(_cluster){
            for (int i = 0; i < _constraints.length; i++) {
                _constraints[i].not();
            }
            return this;
        }
    }

    public Object getObject() {
        throw new NotSupportedException();
    }
}
