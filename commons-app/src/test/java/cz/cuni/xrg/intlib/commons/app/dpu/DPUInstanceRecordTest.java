package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
	public void setUp() throws ModuleException {
		instance = new DPUInstanceRecord();
	}

	@Test
	public void testCopy() throws ModuleException {
		// initialize contained objects
		DPUConfigObject config = mock(DPUConfigObject.class, withSettings().serializable());
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