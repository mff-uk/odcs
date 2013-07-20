package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link DPUInstanceRecord} class.
 *
 * @author Jan Vojt
 */
public class DPUInstanceRecordTest {
	
	/**
	 * Tested instance.
	 */
	private DPUInstanceRecord instance;
	
	@Before
	public void setUp() {
		instance = new DPUInstanceRecord();
	}

	/**
	 * THIS TEST FAILS, BUT IS INTENTIONALLY IGNORED!
	 * It fails because we do not have the implementation of {@link Config} and
	 * so we cannot clone it. We also cannot serialize and unserialize DPU
	 * config, because mocked instance cannot be unserialized.
	 * TODO repair test or redesign configuration
	 */
	@Test
	@Ignore
	public void testCopy() {
		// initialize contained objects
		Config config = mock(Config.class);
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		
		instance.setName("testname");
		instance.setDescription("testdescription");
		instance.setJarPath("testjarpath");
		instance.setConfiguration(config);
		instance.setTemplate(dpu);
		
		DPUInstanceRecord copy = new DPUInstanceRecord(instance);
		
		assertNotSame(instance, copy);
		assertNotNull(copy);
		assertEquals(instance.getName(), copy.getName());
		assertEquals(instance.getDescription(), copy.getDescription());
		assertEquals(instance.getJarPath(), copy.getJarPath());
		assertNotSame(instance.getConfiguration(), copy.getConfiguration());
		
		// DPU template is never copied!!
		assertSame(instance.getTemplate(), copy.getTemplate());
	}
}