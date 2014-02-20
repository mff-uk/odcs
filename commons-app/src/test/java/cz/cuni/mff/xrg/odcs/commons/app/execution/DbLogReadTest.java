package cz.cuni.mff.xrg.odcs.commons.app.execution;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogRead;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for {@link DbLogReader}.
 * 
 * @author Škoda Petr <skodapetr@gmail.com>
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DbLogReadTest {

	@Autowired
	private DbLogRead reader;
	
	@Test
	public void getLastRelativeId() {
		Long lastId = reader.getLastRelativeIndex(1l);
		Assert.assertNotNull(lastId);
		assertEquals(12l, (long)lastId);
	}
	
}
