package cz.cuni.xrg.commons.module.dpu;

import org.junit.Test;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link ConfigurableBase} class.
 * 
 * @author Petyr
 *
 */
public class ConfigurableBaseInstanceTest extends ConfigurableBase<DPUConfigObject> {

	public ConfigurableBaseInstanceTest() {
		super(mock(DPUConfigObject.class, withSettings().serializable()));
	}
	
	/**
	 * Configuration is not changed on configure(null).
	 */
	@Test
	public void nullSet() throws ConfigException {
		DPUConfigObject oldConfig = config;
		assertNotNull(oldConfig);
		
		this.configure(null);
		
		assertEquals(oldConfig, config);
	}
}
