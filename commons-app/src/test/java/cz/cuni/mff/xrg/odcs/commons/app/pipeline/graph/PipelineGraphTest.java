/*******************************************************************************
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
 *******************************************************************************/
/*******************************************************************************
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
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link PipelineGraph}.
 * 
 * @author Jan Vojt
 */
public class PipelineGraphTest {

    /**
     * Tested instance.
     */
    private PipelineGraph instance;

    @Before
    public void setUp() {
        instance = new PipelineGraph();
    }

    @Test
    public void testSetEdges() {
        Node nodeA = new Node();
        Node nodeB = new Node();
        Edge edge = new Edge(nodeA, nodeB);
        Set<Edge> edges = new HashSet<>(Arrays.asList(edge));
        instance.setEdges(edges);

        assertEquals(edges, instance.getEdges());
        assertEquals(2, instance.getNodes().size());
        assertTrue(instance.getNodes().contains(nodeA));
        assertTrue(instance.getNodes().contains(nodeB));
    }

    @Test
    public void testAddEdge() {
        Node nodeA = new Node();
        Node nodeB = new Node();

        instance.addNode(nodeB);
        instance.addEdge(nodeA, nodeB);

        assertEquals(1, instance.getEdges().size());
        assertEquals(2, instance.getNodes().size());

        // by adding an edge, both its nodes need to be members of graph
        assertTrue(instance.getNodes().contains(nodeA));
        assertTrue(instance.getNodes().contains(nodeB));
    }

    @Test
    public void testCopy() {
        instance.addEdge(new Node(), new Node());

        PipelineGraph copy = new PipelineGraph(instance);

        assertNotSame(instance, copy);
        assertEquals(instance.getEdges().size(), copy.getEdges().size());
        assertEquals(instance.getNodes().size(), copy.getNodes().size());

        for (Node oNode : instance.getNodes()) {
            assertFalse(copy.getNodes().contains(oNode));
        }
        for (Edge oEdge : instance.getEdges()) {
            assertFalse(copy.getEdges().contains(oEdge));
        }
    }
}
