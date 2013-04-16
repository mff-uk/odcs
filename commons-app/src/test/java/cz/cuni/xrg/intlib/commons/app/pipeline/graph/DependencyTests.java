package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;


/**
 * Test suite for testing pipeline graph dependency resolving algorithms.
 * @author Jan Vojt
 */
public class DependencyTests {
	
	private Pipeline pipe;
	
	private Node[] nodes;
	
	/**
	 * Prepare some nodes
	 */
	@Before
	public void setUp() {

		PipelineGraph graph = new PipelineGraph();
		
		pipe = new Pipeline();
		pipe.setGraph(graph);
		
		nodes = new Node[5];
		for (int i = 0; i<5; i++) {
			Node node = new Node();
			nodes[i] = node;
			graph.addNode(node);
		}
	}
	
	/**
	 * Test dependency resolution for serialized DPU setup.
	 * Scenario: E -> T -> T -> T -> L
	 */
	@Test
	public void testInlineDependencyResolution() {
		
		PipelineGraph graph = pipe.getGraph();
		graph.addEdge(nodes[2], nodes[3]);
		graph.addEdge(nodes[0], nodes[1]);
		graph.addEdge(nodes[1], nodes[2]);
		graph.addEdge(nodes[3], nodes[4]);
		
		DependencyGraph dGraph = new DependencyGraph(graph);

		int i = 0;
		for (Node n : dGraph) {
			assertSame(nodes[i++], n);
		}
	}
	
	/**
	 * Test more complex setup of DPUs.
	 * Scenario: E0 -> T1 -> T2 -> L3
	 * 				   E4 ---^
	 */
	@Test
	public void testComplexDependencyResolution() {
		
		PipelineGraph graph = pipe.getGraph();
		graph.addEdge(nodes[0], nodes[1]);
		graph.addEdge(nodes[1], nodes[2]);
		graph.addEdge(nodes[2], nodes[3]);
		graph.addEdge(nodes[4], nodes[2]);

		DependencyGraph dGraph = new DependencyGraph(graph);

		int i = 0;
		for (Node n : dGraph) {
			if (i==0) {
				// first must be E0 or E4
				assertTrue(n==nodes[0] || n==nodes[4]);
			} else if (i==1) {
				// second may be any of E0, E4, T1
				assertTrue(n==nodes[0] || n==nodes[1]
						|| n==nodes[4]);
			} else if (i==2) {
				// third may be E4 or T1
				assertTrue(n==nodes[1] || n==nodes[4]);
			} else {
				// otherwise T2 or L3
				assertSame(nodes[i], n);
			}
			i++;
		}
	}
	
	/**
	 * Test circular dependency resolution.
	 * Scenario: E -> T -> T -> T -> T
	 * 				  ^---------'
	 */
	@Test
	public void testCircularDependencyResolution() {
		
		PipelineGraph graph = pipe.getGraph();
		graph.addEdge(nodes[0], nodes[1]);
		graph.addEdge(nodes[1], nodes[2]);
		graph.addEdge(nodes[2], nodes[3]);
		graph.addEdge(nodes[3], nodes[4]);
		graph.addEdge(nodes[3], nodes[1]);

		DependencyGraph dGraph = new DependencyGraph(graph);
		GraphIterator iter = dGraph.iterator();
		
		// first node is not in the circle
		assertTrue(iter.hasNext());
		assertSame(nodes[0], iter.next());
		
		// second node is in the circle
		assertFalse(iter.hasNext());
		assertNull(iter.next());
	}

}
