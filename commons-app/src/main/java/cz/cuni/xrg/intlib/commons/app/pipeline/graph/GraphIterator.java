package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.Iterator;
import java.util.List;

/**
 * Iterates over dependency graph in an order that satisfies all dependencies.
 * @author Jan Vojt
 *
 */
public class GraphIterator implements Iterator<Node> {
	
	/**
	 * Stack of nodes used to perform breath-first search.
	 */
	private List<DependencyNode> stack;
	
	/**
	 * Constructs iterator from dependency graph.
	 * @param graph
	 */
	public GraphIterator(DependencyGraph graph) {
		this.stack = graph.getExtractors();
	}

	/**
	 * Tells whether there are more node, that have not been used yet.
	 */
	@Override
	public boolean hasNext() {
		return ! stack.isEmpty();
	}

	/**
	 * Returns the next node to be processed.
	 */
	@Override
	public Node next() {
		for (DependencyNode n : stack) {
			if (n.hasMetDependencies()) {
				replaceWithDependencies(n);
				return n.getNode();
			}
		}
		return null;
	}

	/**
	 * Removal of dependencies is not supported for obvious reasons.
	 */
	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
			"Operation remove is forbidden on dependency iterator."
		);
	}
	
	private void replaceWithDependencies(DependencyNode node) {
		node.setExecuted(true);
		stack.remove(node);
		stack.addAll(node.getDependencies());
	}

}
