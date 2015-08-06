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

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link Position} class.
 * 
 * @author Jan Vojt
 */
public class PositionTest {

    /**
     * Tested instance.
     */
    private Position instance;

    private int x = 3;

    private int y = 4;

    @Before
    public void setUp() {
        instance = new Position(x, y);
    }

    @Test
    public void testCopy() {
        Position copy = new Position(instance);
        assertNotSame(instance, copy);
        assertEquals(x, copy.getX());
        assertEquals(y, copy.getY());
    }
}
