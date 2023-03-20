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
package EDU.purdue.cs.bloat.cfg;

import java.util.*;

/**
 * <tt>DominanceFrontier</tt> is used to calculate the <i>dominance frontier</i>
 * of each node in a control flow graph.
 * <p>
 * The <i>dominance frontier</i> of a node x is the set of all nodes w such
 * that x dominates a predacessor of w, but does not strictly dominate w.
 * Basically, nodes in the dominance frontier have one parent that <b>is</b>
 * dominated by x and at least one parent that <b>is not</b> dominated by x.
 * <p>
 * <tt>DominanceFrontier</tt> can be used to calculate both the dominance
 * (forward) and the postdominance (reverse) frontiers for a control flow graph.
 * 
 * @see FlowGraph
 */

public class DominanceFrontier {
	/**
	 * Calculates the dominance frontier for a cfg and notifies the blocks in it
	 * appropriately.
	 * 
	 * @param graph
	 *            The cfg to operate on
	 * @param reverse
	 *            Do we calculate the postdominance frontier?
	 */
	public static void buildFrontier(final FlowGraph graph, boolean reverse) {
		if (!reverse) {
			DominanceFrontier.calcFrontier(graph.source(), graph, reverse);
		} else {
			DominanceFrontier.calcFrontier(graph.sink(), graph, reverse);
		}
	}

	/**
	 * Recursively traverses the cfg and builds up the dominance frontier.
	 * <p>
	 * A block n's dominance frontier is the union of two sets of nodes. The
	 * first set is the nodes in the dominance frontier of the nodes that n
	 * dominates that are not dominated by n's immediate dominator. The second
	 * set consists of the successors of n that are not strictly dominated by n.
	 * 
	 * @param block
	 *            The block to start from (either source or sink)
	 * @param graph
	 *            The cfg from which to get blocks
	 * @param reverse
	 *            Do we calculate the dominance or postdominance frontier?
	 * 
	 * @return The blocks in the (post)dominance frontier of block
	 */
	private static LinkedList calcFrontier(final Block block,
			final FlowGraph graph, boolean reverse) {
		// local is an array of Blocks that are in block's dominance
		// frontier. It is indexed by the block's pre-order index. I
		// suppose an array is used so that no block is added to the
		// dominance frontier twice.
		final Block[] local = new Block[graph.size()];

		Iterator children; // The blocks that are dominated by block

		if (!reverse) {
			children = block.domChildren().iterator();
		} else {
			children = block.pdomChildren().iterator();
		}

		// Recursively calculate the nodes in the dominance frontier of
		// block that are not dominated by block's immediate dominator
		while (children.hasNext()) {
			final Block child = (Block) children.next();

			final LinkedList df = DominanceFrontier.calcFrontier(child, graph,
					reverse);

			final Iterator e = df.iterator();

			while (e.hasNext()) {
				final Block dfChild = (Block) e.next();

				if (!reverse) {
					if (block != dfChild.domParent()) {
						local[graph.preOrderIndex(dfChild)] = dfChild;
					}

				} else {
					if (block != dfChild.pdomParent()) {
						local[graph.preOrderIndex(dfChild)] = dfChild;
					}
				}
			}
		}

		final Iterator succs = reverse ? graph.preds(block).iterator() : graph
				.succs(block).iterator();

		// Caculate the successors of block that are not strictly
		// dominated by block.
		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			// If block is not the immediate (post)dominator of its
			// successor, add it to block's dominance frontier.
			if (!reverse) {
				if (block != succ.domParent()) {
					local[graph.preOrderIndex(succ)] = succ;
				}

			} else {
				if (block != succ.pdomParent()) {
					local[graph.preOrderIndex(succ)] = succ;
				}
			}
		}

		final LinkedList v = new LinkedList(); // The dominance frontier

		for (int i = 0; i < local.length; i++) {
			if (local[i] != null) {
				v.add(local[i]);
			}
		}

		// Set block's (post)dominance frontier
		if (!reverse) {
			block.domFrontier().clear();
			block.domFrontier().addAll(v);
		} else {
			block.pdomFrontier().clear();
			block.pdomFrontier().addAll(v);
		}

		return v;
	}
}
