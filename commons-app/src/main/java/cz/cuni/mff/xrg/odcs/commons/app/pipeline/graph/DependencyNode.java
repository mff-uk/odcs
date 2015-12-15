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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents node in the dependency graph. Contains links to its dependencies.
 * 
 * @author Jan Vojt
 */
public class DependencyNode {

    /**
     * The node in pipeline graph represented by this dependency.
     */
    private Node node;

    /**
     * List of dependencies of this node.
     */
    private List<DependencyNode> dependencies = new ArrayList<>();

    /**
     * List of nodes that depend on this node
     */
    private List<DependencyNode> dependants = new ArrayList<>();

    /**
     * Tells whether this dependency is already satisfied.
     */
    private boolean executed = false;

    /**
     * Constructs dependency from node in pipeline graph.
     * 
     * @param node
     */
    public DependencyNode(Node node) {
        this.node = node;
    }

    /**
     * @return whether all dependencies for this node are already met.
     */
    public boolean hasMetDependencies() {
        for (DependencyNode d : dependencies) {
            if (!d.isExecuted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the dependencies
     */
    public List<DependencyNode> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies
     */
    public void setDependencies(List<DependencyNode> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Checks whether this node already has given dependency
     * 
     * @param node
     *            dependency to check
     * @return whether this node already has given dependency
     */
    public boolean hasDependency(DependencyNode node) {
        return dependencies.contains(node);
    }

    /**
     * Add a new dependency
     * 
     * @param node
     *            dependency to add
     */
    public void addDependency(DependencyNode node) {
        if (!hasDependency(node)) {
            dependencies.add(node);
        }
    }

    /**
     * @return the dependants
     */
    public List<DependencyNode> getDependants() {
        return dependants;
    }

    /**
     * @param dependants
     *            the dependants to set
     */
    public void setDependants(List<DependencyNode> dependants) {
        this.dependants = dependants;
    }

    /**
     * Adds dependent node
     * 
     * @param node
     */
    public void addDependant(DependencyNode node) {
        if (!hasDependant(node)) {
            dependants.add(node);
        }
    }

    /**
     * Tells whether this node is directly dependent on given node
     * 
     * @param node
     * @return whether this node is directly dependent on given node
     */
    public boolean hasDependant(DependencyNode node) {
        return dependants.contains(node);
    }

    /**
     * @return the executed
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * @param executed
     *            the executed to set
     */
    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }
}
