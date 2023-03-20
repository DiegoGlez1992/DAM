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
package EDU.purdue.cs.bloat.util;

import java.util.*;

/**
 * Graph represents a graph of nodes with directed edges between them.
 * GraphNodes are created and are added to the Graph before the edges can be
 * constructed. Each GraphNode as a unique key associated with it. For instance,
 * if a Graph represents a control flow graph, each GraphNode would be
 * associated with a basic block.
 * 
 * @see #addNode
 * @see #addEdge
 * 
 * @see GraphNode
 * @see EDU.purdue.cs.bloat.cfg.FlowGraph
 */
public class Graph {
	private NodeMap nodes; // The nodes in this Graph

	private NodeList preOrder; // 

	private NodeList postOrder;

	private Collection roots; // The root nodes of this Graph (no
								// predacessors)

	private Collection revRoots; // Reverse root nodes of this Graph (no
									// successors)

	// These counts are used to determine when certain actions (such as updating
	// the
	// roots Collection) should or should not be performed.
	protected int rootEdgeModCount = 0;

	protected int revRootEdgeModCount = 0;

	protected int nodeModCount = 0; // Number of nodes that have been modified

	protected int edgeModCount = 0; // Number of edges that have been modified

	protected int removingNode = 0;

	protected int removingEdge = 0;

	/**
	 * Constructor.
	 */
	public Graph() {
		nodes = new NodeMap();
		preOrder = null;
		postOrder = null;
		roots = null;
		revRoots = null;
	}

	/**
	 * @return The roots of this Graph. That is, the nodes with no predacessors.
	 */
	public Collection roots() {
		if ((roots == null) || (rootEdgeModCount != edgeModCount)) {
			rootEdgeModCount = edgeModCount;
			roots = new ArrayList();
			buildRootList(roots, false);
		}

		return roots;
	}

	/**
	 * @return The reverse roots of this Graph. That is, the nodes with no
	 *         successors.
	 */
	public Collection reverseRoots() {
		if ((roots == null) || (revRootEdgeModCount != edgeModCount)) {
			revRootEdgeModCount = edgeModCount;
			revRoots = new ArrayList();
			buildRootList(revRoots, true);
		}

		return revRoots;
	}

	/**
	 * Compile a collection of root nodes in this Graph. If reverse is true, the
	 * collection will contain those nodes with no predacessor nodes. If reverse
	 * is false, the collection will contain those nodes with no sucessor nodes.
	 * 
	 * @param c
	 *            A Collection that will contain the roots in the graph.
	 * @param reverse
	 *            Do we make a reverse traversal of the graph?
	 */
	private void buildRootList(final Collection c, final boolean reverse) {
		final HashSet visited = new HashSet(nodes.size() * 2);
		final ArrayList stack = new ArrayList();

		final Iterator iter = nodes.values().iterator();

		while (iter.hasNext()) {
			final GraphNode node = (GraphNode) iter.next();

			if (!visited.contains(node)) {
				visited.add(node);
				stack.add(node);

				while (!stack.isEmpty()) {
					final GraphNode v = (GraphNode) stack
							.remove(stack.size() - 1);
					boolean pushed = false;

					final Iterator preds = reverse ? v.succs.iterator()
							: v.preds.iterator();

					while (preds.hasNext()) {
						final GraphNode w = (GraphNode) preds.next();

						if (!visited.contains(w)) {
							visited.add(w);
							stack.add(w);
							pushed = true;
						}
					}

					if (!pushed) {
						c.add(v);
					}
				}
			}
		}
	}

	/**
	 * Return the successors of a given node.
	 */
	public Collection succs(final GraphNode v) {
		return new EdgeSet(v, v.succs);
	}

	/**
	 * Returns the predacessors of a given node.
	 */
	public Collection preds(final GraphNode v) {
		return new EdgeSet(v, v.preds);
	}

	/**
	 * Determines whether or not a node v is an ancestor (has a lower pre-order
	 * index and a higher post-order index) of a node w.
	 * 
	 * @param v
	 *            Candidate ancestor node.
	 * @param w
	 *            Candidate descendent node.
	 * 
	 * @return True, if v is an ancestor of w.
	 */
	public boolean isAncestorToDescendent(final GraphNode v, final GraphNode w) {
		return (preOrderIndex(v) <= preOrderIndex(w))
				&& (postOrderIndex(w) <= postOrderIndex(v));
	}

	/**
	 * Returns the index of a given node in a pre-order ordering of this Graph.
	 */
	public int preOrderIndex(final GraphNode node) {
		if ((preOrder == null) || (edgeModCount != preOrder.edgeModCount)) {
			buildLists();
		}

		return node.preOrderIndex();
	}

	/**
	 * Returns the index of a given node in a post-order ordering of this Graph.
	 */
	public int postOrderIndex(final GraphNode node) {
		if ((postOrder == null) || (edgeModCount != postOrder.edgeModCount)) {
			buildLists();
		}

		return node.postOrderIndex();
	}

	/**
	 * Returns the nodes in this Graph ordered by their pre-order index.
	 */
	public List preOrder() {
		if ((preOrder == null) || (edgeModCount != preOrder.edgeModCount)) {
			buildLists();
		}

		return preOrder;
	}

	/**
	 * Return the nodes in this Graph ordered by their post-order index.
	 */
	public List postOrder() {
		if ((postOrder == null) || (edgeModCount != postOrder.edgeModCount)) {
			buildLists();
		}

		return postOrder;
	}

	/**
	 * Constructs lists of nodes in both pre-order and post-order order.
	 */
	private void buildLists() {
		Iterator iter = roots().iterator();

		preOrder = new NodeList();
		postOrder = new NodeList();

		final Set visited = new HashSet();

		// Calculate the indices of the nodes.
		while (iter.hasNext()) {
			final GraphNode root = (GraphNode) iter.next();

			Assert.isTrue(nodes.containsValue(root), "Graph does not contain "
					+ root);

			number(root, visited);
		}

		// Mark all nodes that were not numbered as having an index of -1. This
		// information is used when removing unreachable nodes.
		iter = nodes.values().iterator();

		while (iter.hasNext()) {
			final GraphNode node = (GraphNode) iter.next();

			if (!visited.contains(node)) {
				node.setPreOrderIndex(-1);
				node.setPostOrderIndex(-1);
			} else {
				Assert.isTrue(node.preOrderIndex() >= 0);
				Assert.isTrue(node.postOrderIndex() >= 0);
			}
		}
	}

	/**
	 * Removes all nodes from this Graph that cannot be reached in a pre-order
	 * traversal of the Graph. These nodes have a pre-order index of -1.
	 */
	public void removeUnreachable() {
		if ((preOrder == null) || (edgeModCount != preOrder.edgeModCount)) {
			buildLists();
		}

		final Iterator iter = nodes.entrySet().iterator();

		while (iter.hasNext()) {
			final Map.Entry e = (Map.Entry) iter.next();

			final GraphNode v = (GraphNode) e.getValue();

			if (v.preOrderIndex() == -1) {
				iter.remove();
			}
		}
	}

	/**
	 * Sets the pre-order and post-order indices of a node.
	 * 
	 * @param node
	 *            The node to number.
	 * @param visited
	 *            The nodes that have been visited already.
	 */
	private void number(final GraphNode node, final Set visited) {
		visited.add(node);

		// Visit in pre-order
		node.setPreOrderIndex(preOrder.size());
		preOrder.addNode(node);

		final Iterator iter = succs(node).iterator();

		while (iter.hasNext()) {
			final GraphNode succ = (GraphNode) iter.next();
			if (!visited.contains(succ)) {
				number(succ, visited);
			}
		}

		// Visit in post-order
		node.setPostOrderIndex(postOrder.size());
		postOrder.addNode(node);
	}

	/**
	 * Insertes a node (and its associated key) into this Graph.
	 * 
	 * @param key
	 *            A unique value associated with this node. For instance, if
	 *            this Graph represented a control flow graph, the key would be
	 *            a basic block.
	 * @param node
	 *            The node to be added.
	 */
	// This method is NOT guaranteed to be called whenever a node is added.
	public void addNode(final Object key, final GraphNode node) {
		Assert.isTrue(nodes.get(key) == null);
		nodes.putNodeInMap(key, node);
		preOrder = null;
		postOrder = null;
		nodeModCount++;
		edgeModCount++;
	}

	/**
	 * Returns the node in this Graph with a given key.
	 */
	public GraphNode getNode(final Object key) {
		return (GraphNode) nodes.get(key);
	}

	/**
	 * Returns a Set of the keys used to uniquely identify the nodes in this
	 * Graph.
	 */
	public Set keySet() {
		return nodes.keySet();
	}

	/**
	 * Removes a node with a given key from the Graph.
	 * 
	 * @param key
	 *            The key associated with the node to remove.
	 */
	// This method is guaranteed to be called whenever a node is deleted.
	// If removingNode != 0, the node is NOT deleted when this method returns.
	// It is the callers responsibility to delete the node AFTER this method
	// is called. An exception will be thrown if the node is not present
	// in the graph.
	public void removeNode(final Object key) {
		final GraphNode node = getNode(key);
		Assert.isTrue(node != null, "No node for " + key);

		succs(node).clear();
		preds(node).clear();

		if (removingNode == 0) {
			nodes.removeNodeFromMap(key);
		} else if (removingNode != 1) {
			throw new RuntimeException();
		}

		// Removing a node invalidates the orderings
		preOrder = null;
		postOrder = null;

		nodeModCount++;
		edgeModCount++;
	}

	/**
	 * Adds a directed edge from node v to node w.
	 * 
	 * @param v
	 *            Source node.
	 * @param w
	 *            Destination node.
	 */
	// This method is NOT guaranteed to be called whenever an edge is added.
	public void addEdge(final GraphNode v, final GraphNode w) {
		Assert.isTrue(nodes.containsValue(v), "Graph does not contain " + v);
		Assert.isTrue(nodes.containsValue(w), "Graph does not contain " + w);

		succs(v).add(w);
		edgeModCount++;
	}

	// This method is guaranteed to be called whenever an edge is deleted.
	// If removingEdge != 0, the edge is NOT deleted when this method returns.
	// It is the callers responsibility to delete the edge AFTER this method
	// is called. An exception will be thrown if the edge is not present
	// in the graph.
	public void removeEdge(final GraphNode v, final GraphNode w) {
		Assert.isTrue(nodes.containsValue(v), "Graph does not contain " + v);
		Assert.isTrue(nodes.containsValue(w), "Graph does not contain " + w);
		Assert.isTrue(v.succs().contains(w));

		if (removingEdge == 0) {
			succs(v).remove(w);
		} else if (removingEdge != 1) {
			throw new RuntimeException();
		}

		edgeModCount++;
	}

	public String toString() {
		String s = "";

		final Iterator iter = nodes.values().iterator();

		while (iter.hasNext()) {
			final GraphNode node = (GraphNode) iter.next();
			s += "[" + node;
			s += " succs = " + node.succs();
			s += " preds = " + node.preds();
			s += "]\n";
		}

		return s;
	}

	/**
	 * Searchs this Graph for a given GraphNode.
	 * 
	 * @param v
	 *            GraphNode to search for.
	 * 
	 * @return True, if this Graphs contains v.
	 */
	public boolean hasNode(final GraphNode v) {
		return nodes.containsValue(v);
	}

	/**
	 * Searches this Graph for an (directed) edge between two GraphNodes.
	 * 
	 * @param v
	 *            Source node of desired edge.
	 * @param w
	 *            Destination node of desired edge.
	 * 
	 * @return True, if an edge exists between nodes v and w.
	 */
	public boolean hasEdge(final GraphNode v, final GraphNode w) {
		Assert.isTrue(nodes.containsValue(v), "Graph does not contain " + v);
		Assert.isTrue(nodes.containsValue(w), "Graph does not contain " + w);
		return succs(v).contains(w);
	}

	/**
	 * Returns all the nodes in this Graph.
	 */
	public Collection nodes() {
		return nodes.values();
	}

	/**
	 * Returns the number of nodes in this Graph.
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * A NodeMap that stores nodes. I guess we use this data structure to make
	 * it easier to ensure that there are not duplicate nodes in the Graph. A
	 * HashMap is used as the underlying stored mechanism.
	 */
	class NodeMap extends AbstractMap {
		HashMap map = new HashMap();

		void removeNodeFromMap(final Object key) {
			map.remove(key);
		}

		void putNodeInMap(final Object key, final Object value) {
			map.put(key, value);
		}

		public Object remove(final Object key) {
			final GraphNode v = (GraphNode) map.get(key);

			if (v != null) {
				Graph.this.removeNode(v);
			}

			return v;
		}

		public Object put(final Object key, final Object value) {
			final GraphNode v = (GraphNode) remove(key);
			Graph.this.addNode(key, (GraphNode) value);
			return v;
		}

		public void clear() {
			final Iterator iter = entrySet().iterator();

			while (iter.hasNext()) {
				final Map.Entry e = (Map.Entry) iter.next();
				removingNode++;
				Graph.this.removeNode(e.getKey());
				removingNode--;
				iter.remove();
			}
		}

		public Set entrySet() /* Modified for final JDK1.2 API */
		{
			final Collection entries = map.entrySet();

			return new AbstractSet() {
				public int size() {
					return entries.size();
				}

				public boolean contains(final Object a) {
					return entries.contains(a);
				}

				public boolean remove(final Object a) {
					final Map.Entry e = (Map.Entry) a;

					removingNode++;
					Graph.this.removeNode(e.getKey());
					removingNode--;

					return entries.remove(a);
				}

				public void clear() {
					final Iterator iter = entries.iterator();

					while (iter.hasNext()) {
						final Map.Entry e = (Map.Entry) iter.next();
						removingNode++;
						Graph.this.removeNode(e.getKey());
						removingNode--;
						iter.remove();
					}
				}

				public Iterator iterator() {
					final Iterator iter = entries.iterator();

					return new Iterator() {
						int nodeModCount = Graph.this.nodeModCount;

						Map.Entry last;

						public boolean hasNext() {
							if (nodeModCount != Graph.this.nodeModCount) {
								throw new ConcurrentModificationException();
							}

							return iter.hasNext();
						}

						public Object next() {
							if (nodeModCount != Graph.this.nodeModCount) {
								throw new ConcurrentModificationException();
							}

							last = (Map.Entry) iter.next();
							return last;
						}

						public void remove() {
							if (nodeModCount != Graph.this.nodeModCount) {
								throw new ConcurrentModificationException();
							}

							removingNode++;
							Graph.this.removeNode(last.getKey());
							removingNode--;
							iter.remove();

							nodeModCount = Graph.this.nodeModCount;
						}
					};
				}
			};
		}
	}

	/**
	 * NodeList represents a list of nodes. Special provisions must be made for
	 * methods such as indexOf() and iterator(). A NodeList is used to store the
	 * pre-order and post-order travsersals of the Graph.
	 */
	class NodeList extends ArrayList implements List {
		int edgeModCount;

		NodeList() {
			super(Graph.this.size());
			edgeModCount = Graph.this.edgeModCount;
		}

		boolean addNode(final GraphNode a) {
			return super.add(a);
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public boolean add(final Object a) {
			throw new UnsupportedOperationException();
		}

		public boolean remove(final Object a) {
			throw new UnsupportedOperationException();
		}

		// This works only if each node is in the list at most once.
		public int indexOf(final Object a) {
			if (edgeModCount != Graph.this.edgeModCount) {
				throw new ConcurrentModificationException();
			}

			final GraphNode v = (GraphNode) a;

			if (this == Graph.this.preOrder) {
				return v.preOrderIndex();
			}

			if (this == Graph.this.postOrder) {
				return v.postOrderIndex();
			}

			return super.indexOf(a);
		}

		// This works only if each node is in the list at most once.
		public int indexOf(final Object a, final int index) {
			final int i = indexOf(a);

			if (i >= index) {
				return i;
			}

			return -1;
		}

		// This works only if each node is in the list at most once.
		public int lastIndexOf(final Object a) {
			if (edgeModCount != Graph.this.edgeModCount) {
				throw new ConcurrentModificationException();
			}

			final GraphNode v = (GraphNode) a;

			if (this == Graph.this.preOrder) {
				return v.preOrderIndex();
			}

			if (this == Graph.this.postOrder) {
				return v.postOrderIndex();
			}

			return super.lastIndexOf(a);
		}

		// This works only if each node is in the list at most once.
		public int lastIndexOf(final Object a, final int index) {
			final int i = indexOf(a);

			if (i <= index) {
				return i;
			}

			return -1;
		}

		public Iterator iterator() {
			if (Graph.this.edgeModCount != edgeModCount) {
				throw new ConcurrentModificationException();
			}

			final Iterator iter = super.iterator();

			return new Iterator() {
				int edgeModCount = NodeList.this.edgeModCount;

				Object last;

				public boolean hasNext() {
					if (Graph.this.edgeModCount != edgeModCount) {
						throw new ConcurrentModificationException();
					}

					return iter.hasNext();
				}

				public Object next() {
					if (Graph.this.edgeModCount != edgeModCount) {
						throw new ConcurrentModificationException();
					}

					last = iter.next();
					return last;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	/**
	 * A set of edges. Recall that a Set cannot contain duplicate entries.
	 */
	class EdgeSet extends AbstractSet {
		GraphNode node;

		Set set;

		int nodeModCount;

		/**
		 * 
		 */
		public EdgeSet(final GraphNode node, final Set set) {
			this.node = node;
			this.set = set;
			this.nodeModCount = Graph.this.nodeModCount;
		}

		public int size() {
			if (nodeModCount != Graph.this.nodeModCount) {
				throw new ConcurrentModificationException();
			}

			return set.size();
		}

		/**
		 * Removes all nodes in this set except for those found in collection.
		 * 
		 * @param c
		 *            Nodes that are to be retained.
		 */
		public boolean retainAll(final Collection c) {
			return super.retainAll(new ArrayList(c));
		}

		/**
		 * Removes all of the nodes in this set that are specified in a given
		 * Collection.
		 * 
		 * @param c
		 *            The nodes to remove.
		 */
		public boolean removeAll(final Collection c) {
			return super.removeAll(new ArrayList(c));
		}

		/**
		 * Adds all of the nodes in a Collection to this set.
		 */
		public boolean addAll(final Collection c) {
			return super.addAll(new ArrayList(c));
		}

		public boolean add(final Object a) {
			if (nodeModCount != Graph.this.nodeModCount) {
				throw new ConcurrentModificationException();
			}

			Assert.isTrue(nodes.containsValue(a));
			Assert.isTrue(nodes.containsValue(node));

			final GraphNode v = (GraphNode) a;

			if (set.add(v)) {
				Graph.this.edgeModCount++;

				if (set == node.succs) {
					v.preds.add(node);
				} else {
					v.succs.add(node);
				}

				return true;
			}

			return false;
		}

		public boolean remove(final Object a) {
			if (nodeModCount != Graph.this.nodeModCount) {
				throw new ConcurrentModificationException();
			}

			final GraphNode v = (GraphNode) a;

			if (set.contains(v)) {
				Graph.this.edgeModCount++;

				if (set == node.succs) {
					removingEdge++;
					Graph.this.removeEdge(node, v);
					removingEdge--;
					v.preds.remove(node);
				} else {
					removingEdge++;
					Graph.this.removeEdge(v, node);
					removingEdge--;
					v.succs.remove(node);
				}

				set.remove(v);

				return true;
			}

			return false;
		}

		public boolean contains(final Object a) {
			if (nodeModCount != Graph.this.nodeModCount) {
				throw new ConcurrentModificationException();
			}

			Assert.isTrue(nodes.containsValue(a));
			Assert.isTrue(nodes.containsValue(node));

			if (a instanceof GraphNode) {
				return set.contains(a);
			}

			return false;
		}

		public void clear() {
			if (nodeModCount != Graph.this.nodeModCount) {
				throw new ConcurrentModificationException();
			}

			final Iterator iter = set.iterator();

			while (iter.hasNext()) {
				final GraphNode v = (GraphNode) iter.next();

				if (set == node.succs) {
					removingEdge++;
					Graph.this.removeEdge(node, v);
					removingEdge--;
					v.preds.remove(node);
				} else {
					removingEdge++;
					Graph.this.removeEdge(v, node);
					removingEdge--;
					v.succs.remove(node);
				}
			}

			Graph.this.edgeModCount++;

			set.clear();
		}

		public Iterator iterator() {
			if (nodeModCount != Graph.this.nodeModCount) {
				throw new ConcurrentModificationException();
			}

			final Iterator iter = set.iterator();

			return new Iterator() {
				GraphNode last;

				int edgeModCount = Graph.this.edgeModCount;

				int nodeModCount = EdgeSet.this.nodeModCount;

				public boolean hasNext() {
					if (nodeModCount != Graph.this.nodeModCount) {
						throw new ConcurrentModificationException();
					}
					if (edgeModCount != Graph.this.edgeModCount) {
						throw new ConcurrentModificationException();
					}

					return iter.hasNext();
				}

				public Object next() {
					if (nodeModCount != Graph.this.nodeModCount) {
						throw new ConcurrentModificationException();
					}
					if (edgeModCount != Graph.this.edgeModCount) {
						throw new ConcurrentModificationException();
					}

					last = (GraphNode) iter.next();

					Assert.isTrue(nodes.containsValue(last), last
							+ " not found in graph");
					Assert.isTrue(nodes.containsValue(node), node
							+ " not found in graph");

					return last;
				}

				public void remove() {
					if (nodeModCount != Graph.this.nodeModCount) {
						throw new ConcurrentModificationException();
					}
					if (edgeModCount != Graph.this.edgeModCount) {
						throw new ConcurrentModificationException();
					}

					if (set == node.succs) {
						removingEdge++;
						Graph.this.removeEdge(node, last);
						removingEdge--;
						last.preds.remove(node);
					} else {
						removingEdge++;
						Graph.this.removeEdge(last, node);
						removingEdge--;
						last.succs.remove(node);
					}

					Graph.this.edgeModCount++;
					edgeModCount = Graph.this.edgeModCount;

					iter.remove();
				}
			};
		}
	}
}
