/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph;

import java.util.Iterator;
import java.util.Set;

/**
 * Iterates over dependency graph in an order that satisfies all dependencies.
 * If final node is set, iteration may end before all nodes are iterated, immediately after final node is iterated.
 * 
 * @author Jan Vojt
 */
public class GraphIterator implements Iterator<Node> {

    /**
     * Stack of nodes used to perform breath-first search.
     */
    private final Set<DependencyNode> stack;

    /**
     * Constructs iterator from dependency graph.
     * 
     * @param graph
     */
    public GraphIterator(DependencyGraph graph) {
        this.stack = graph.getStarters();
    }

    /**
     * Tells whether there are more node, that have not been used yet.
     */
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    /**
     * Returns the next node to be processed.
     */
    @Override
    public Node next() {
        for (DependencyNode n : stack) {
            if (n.hasMetDependencies()) {
                replaceWithDependants(n);
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
                "Operation remove is forbidden on dependency iterator.");
    }

    /**
     * Remove given node from stack and replace it with its dependants
     * 
     * @param node
     */
    private void replaceWithDependants(DependencyNode node) {
        node.setExecuted(true);
        stack.remove(node);

        // we need to make sure dependant is not already in the stack
        for (DependencyNode n : node.getDependants()) {
            if (!stack.contains(n)) {
                stack.add(n);
            }
        }
    }

}
