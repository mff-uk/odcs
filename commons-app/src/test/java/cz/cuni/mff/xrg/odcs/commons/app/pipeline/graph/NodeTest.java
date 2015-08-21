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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;

/**
 * Test suite for {@link Node} class.
 * 
 * @author Jan Vojt
 */
public class NodeTest {

    /**
     * Tested instance.
     */
    private Node instance;

    @Before
    public void setUp() {
        instance = new Node();
    }

    @Test
    public void testCopy() {
        // initialize contained objects
        int posX = 5;
        int posY = 6;
        Position position = new Position(posX, posY);
        DPUInstanceRecord dpu = new DPUInstanceRecord();
        PipelineGraph graph = new PipelineGraph();

        instance.setDpuInstance(dpu);
        instance.setGraph(graph);
        instance.setPosition(position);

        Node copy = new Node(instance);

        assertNotSame(instance, copy);
        assertNotSame(dpu, copy.getDpuInstance());
        assertNotSame(position, copy.getPosition());
        assertEquals(posX, copy.getPosition().getX());
        assertEquals(posY, copy.getPosition().getY());

        // GRAPH CANNOT BE COPIED!!
        assertNull(copy.getGraph());
    }
}
