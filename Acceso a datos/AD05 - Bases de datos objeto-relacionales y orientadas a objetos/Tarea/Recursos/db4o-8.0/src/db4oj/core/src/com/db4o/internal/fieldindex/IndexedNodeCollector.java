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
package com.db4o.internal.fieldindex;

import com.db4o.foundation.*;
import com.db4o.internal.query.processor.*;

public class IndexedNodeCollector {

	private final Collection4 _nodes;
	
	private final Hashtable4 _nodeCache;

	public IndexedNodeCollector(QCandidates candidates) {
		_nodes = new Collection4();
		_nodeCache = new Hashtable4();
		collectIndexedNodes(candidates);
	}
	
	public Iterator4 getNodes() {
		return _nodes.iterator();
	}
	
	private void collectIndexedNodes(QCandidates candidates) {
		collectIndexedNodes(candidates.iterateConstraints());
		implicitlyAndJoinsOnSameField();
	}

	private void implicitlyAndJoinsOnSameField() {
		final Object[] nodes = _nodes.toArray();
		for (int i = 0; i < nodes.length; i++) {
			Object node = nodes[i];
			if (node instanceof OrIndexedLeaf) {
				OrIndexedLeaf current = (OrIndexedLeaf) node;
				OrIndexedLeaf other = findJoinOnSameFieldAtSameLevel(current);
				if (null != other) {
					nodes[Arrays4.indexOfIdentity(nodes, other)] = null;
					collectImplicitAnd(current.getConstraint(), current, other);
				}
			}
		}
	}

	private OrIndexedLeaf findJoinOnSameFieldAtSameLevel(OrIndexedLeaf join) {
		final Iterator4 i = _nodes.iterator();
		while (i.moveNext()) {
			if (i.current() == join) {
				continue;
			}
			if (i.current() instanceof OrIndexedLeaf) {
				OrIndexedLeaf current = (OrIndexedLeaf) i.current();
				if (current.getIndex() == join.getIndex()
					&& parentConstraint(current) == parentConstraint(join)) {
					return current;
				}
			}
		}
		return null;
	}

	private Object parentConstraint(OrIndexedLeaf node) {
		return node.getConstraint().parent();
	}

	private void collectIndexedNodes(final Iterator4 qcons) {
		
		while (qcons.moveNext()) {
			QCon qcon = (QCon)qcons.current();
			if (isCached(qcon)) {
				continue;
			}
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon.canBeIndexLeaf()) {					
					final QConObject conObject = (QConObject) qcon;
					if (conObject.hasJoins()) {
						collectJoinedNode(conObject);
					} else {
						collectStandaloneNode(conObject);
					}
				}
			} else {
				if (!qcon.hasJoins()) {
					collectIndexedNodes(qcon.iterateChildren());
				}
			}
		}		
	}
    
    private boolean isCached(QCon qcon) {
		return null != _nodeCache.get(qcon);
	}

	private void collectStandaloneNode(final QConObject conObject) {
		IndexedLeaf existing = findLeafOnSameField(conObject);
		if (existing != null) {
			collectImplicitAnd(conObject, existing, new IndexedLeaf(conObject));
		} else {
			_nodes.add(new IndexedLeaf(conObject));
		}
	}

	private void collectJoinedNode(QConObject constraintWithJoins) {
		Collection4 joins = collectTopLevelJoins(constraintWithJoins);
		if (!canJoinsBeSearchedByIndex(joins)) {
			return;
		}
		if (1 == joins.size()) {
			_nodes.add(nodeForConstraint((QCon)joins.singleElement()));
			return;
		}
		collectImplicitlyAndingJoins(joins, constraintWithJoins);
	}

	private boolean allHaveSamePath(Collection4 leaves) {
		final Iterator4 i = leaves.iterator();
		i.moveNext();
		QCon first = (QCon)i.current();
		while (i.moveNext()) {
			if (!haveSamePath(first, (QCon)i.current())) {
				return false;
			}
		}
		return true;
	}

	private boolean haveSamePath(QCon x, QCon y) {
		if (x == y) {
			return true;
		}		
		if (!x.onSameFieldAs(y)) {
			return false;
		}		
		if (!x.hasParent()) {
			return !y.hasParent();
		}
		return haveSamePath(x.parent(), y.parent());
	}

	private Collection4 collectLeaves(Collection4 joins) {
		Collection4 leaves = new Collection4();
		collectLeaves(leaves, joins);
		return leaves;
	}

	private void collectLeaves(Collection4 leaves, Collection4 joins) {
		final Iterator4 i = joins.iterator();
		while (i.moveNext()) {
			final QConJoin join = ((QConJoin)i.current());
			collectLeavesFromJoin(leaves, join);
		}
	}

	private void collectLeavesFromJoin(Collection4 leaves, QConJoin join) {
		collectLeavesFromJoinConstraint(leaves, join.constraint1());
		collectLeavesFromJoinConstraint(leaves, join.constraint2());
	}

	private void collectLeavesFromJoinConstraint(Collection4 leaves, QCon constraint) {
		if (constraint instanceof QConJoin) {
			collectLeavesFromJoin(leaves, (QConJoin) constraint);
		} else {
			if (!leaves.containsByIdentity(constraint)) {
				leaves.add(constraint);
			}
		}
	}

	private boolean canJoinsBeSearchedByIndex(Collection4 joins) {
		Collection4 leaves = collectLeaves(joins);
		return allHaveSamePath(leaves)
			&& allCanBeSearchedByIndex(leaves);
	}

	private boolean allCanBeSearchedByIndex(Collection4 leaves) {
		final Iterator4 i = leaves.iterator();
		while (i.moveNext()) {
			final QCon leaf = ((QCon)i.current());
			if (!leaf.canLoadByIndex()) {
				return false;
			}
		}
		return true;
	}
	
	private void collectImplicitlyAndingJoins(Collection4 joins, QConObject constraintWithJoins) {
		final Iterator4 i = joins.iterator();
		i.moveNext();
		IndexedNodeWithRange last = nodeForConstraint((QCon)i.current());
		while (i.moveNext()) {
			final IndexedNodeWithRange node = nodeForConstraint((QCon)i.current());
			last = new AndIndexedLeaf(constraintWithJoins, node, last);
			_nodes.add(last);
		}
	}

	private Collection4 collectTopLevelJoins(QConObject constraintWithJoins) {
		Collection4 joins = new Collection4();
		collectTopLevelJoins(joins, constraintWithJoins);
		return joins;
	}

	private void collectTopLevelJoins(Collection4 joins, QCon constraintWithJoins) {
		final Iterator4 i = constraintWithJoins.iterateJoins();
		while (i.moveNext()) {
			QConJoin join = (QConJoin)i.current();
			if (!join.hasJoins()) {
				if (!joins.containsByIdentity(join)) {
					joins.add(join);
				}
			} else {
				collectTopLevelJoins(joins, join);
			}
		}
	}
	
	private IndexedNodeWithRange newNodeForConstraint(QConJoin join) {
		final IndexedNodeWithRange c1 = nodeForConstraint(join.constraint1());
		final IndexedNodeWithRange c2 = nodeForConstraint(join.constraint2());
		if (join.isOr()) {
			return new OrIndexedLeaf(findLeafForJoin(join), c1, c2);
		}
		return new AndIndexedLeaf(join.constraint1(), c1, c2);
	}

	private QCon findLeafForJoin(QConJoin join) {
		if (join.constraint1() instanceof QConObject) {
			return join.constraint1();
		}
		QCon con = join.constraint2();
		if (con instanceof QConObject) {
			return con;
		}
		return findLeafForJoin((QConJoin)con);
	}
	
	private IndexedNodeWithRange nodeForConstraint(QCon con) {
		IndexedNodeWithRange node = (IndexedNodeWithRange) _nodeCache.get(con);
		if (null != node || _nodeCache.containsKey(con)) {
			return node;
		}
		node = newNodeForConstraint(con);
		_nodeCache.put(con, node);
		return node;
	}

	private IndexedNodeWithRange newNodeForConstraint(QCon con) {
		if (con instanceof QConJoin) {
			return newNodeForConstraint((QConJoin)con);
		}
		return new IndexedLeaf((QConObject)con);
	}

	private void collectImplicitAnd(final QCon constraint, IndexedNodeWithRange x, final IndexedNodeWithRange y) {
		_nodes.remove(x);
		_nodes.remove(y);
		_nodes.add(new AndIndexedLeaf(constraint, x, y));
	}

	private IndexedLeaf findLeafOnSameField(QConObject conObject) {
		final Iterator4 i = _nodes.iterator();
		while (i.moveNext()) {
			if (i.current() instanceof IndexedLeaf) {
				IndexedLeaf leaf = (IndexedLeaf)i.current();
				if (conObject.onSameFieldAs(leaf.constraint())) {
					return leaf;
				}
			}
		}
		return null;
	}

	private boolean isLeaf(QCon qcon) {
		return !qcon.hasChildren();
	}
}
