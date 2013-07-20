package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.configuration.Config;
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
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DPUInstanceRecordTest {
	
	@Autowired
	private ModuleFacade mf;
	
	/**
	 * Tested instance.
	 */
	private DPUInstanceRecord instance;
	
	@Before
	public void setUp() throws ModuleException {
		instance = new DPUInstanceRecord();
		try {
			// TODO THIS IS EXTREMELY BAD DESIGN -> REDESIGN
			instance.loadInstance(mf);
		} catch (FileNotFoundException ex) {
			// ignore, loadInstance is used just to set moduleFacade ...
		}
	}

	@Test
	public void testCopy() throws ModuleException {
		// initialize contained objects
		Config config = mock(Config.class, withSettings().serializable());
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		
		instance.setName("testname");
		instance.setDescription("testdescription");
//		instance.setJarPath("testjarpath"); // TODO must be loadable jar to pass
		instance.setConfiguration(config);
		instance.setTemplate(dpu);
		
		DPUInstanceRecord copy = new DPUInstanceRecord(instance);
		try {
			// TODO THIS IS EXTREMELY BAD DESIGN -> REDESIGN
			copy.loadInstance(mf);
		} catch (FileNotFoundException ex) {
			// ignore, loadInstance is used just to set moduleFacade ...
		}
		
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