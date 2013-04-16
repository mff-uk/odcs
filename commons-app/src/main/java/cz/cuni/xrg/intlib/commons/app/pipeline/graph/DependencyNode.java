package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents node in the dependency graph. Contains links to its dependencies. 
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
	private List<DependencyNode> dependencies = new ArrayList<DependencyNode>();
	
	/**
	 * Tells whether this dependency is already satisfied.
	 */
	private boolean executed = false;

	/**
	 * Constructs dependency from node in pipeline graph.
	 * @param node
	 */
	public DependencyNode(Node node) {
		this.node = node;
	}
	
	/**
	 * Tells whether all dependencies for this node are already met.
	 * @return
	 */
	public boolean hasMetDependencies() {
		for (DependencyNode d : dependencies) {
			if (!d.isExecuted()) return false;
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
	 * @param node dependency to check
	 * @return
	 */
	public boolean hasDependency(DependencyNode node) {
		return dependencies.contains(node);
	}
	
	/**
	 * Add a new dependency
	 * @param node dependency to add
	 */
	public void addDependency(DependencyNode node) {
		if (!hasDependency(node)) dependencies.add(node);
	}

	/**
	 * @return the executed
	 */
	public boolean isExecuted() {
		return executed;
	}

	/**
	 * @param executed the executed to set
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
