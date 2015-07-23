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
package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;

/**
 * Test suite for {@link Pipeline} entity.
 * 
 * @author Jan Vojt
 */
public class PipelineTest {

    /**
     * Tested instance.
     */
    private Pipeline instance;

    @Before
    public void setUp() {
        instance = new Pipeline();
    }

    @Test
    public void testCopy() {
        String name = "pplname";
        String description = "ppldescription";

        instance.setName(name);
        instance.setDescription(description);
        instance.setGraph(new PipelineGraph());

        Pipeline copy = new Pipeline(instance);
        assertNotSame(instance, copy);

        assertEquals(name, instance.getName());
        assertEquals(description, instance.getDescription());
        assertSame(instance, instance.getGraph().getPipeline());

        assertEquals(name, copy.getName());
        assertEquals(description, copy.getDescription());
        assertSame(copy, copy.getGraph().getPipeline());

        assertNotSame(instance.getGraph(), copy.getGraph());
    }

}
