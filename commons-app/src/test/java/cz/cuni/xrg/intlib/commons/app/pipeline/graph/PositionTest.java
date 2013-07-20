package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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