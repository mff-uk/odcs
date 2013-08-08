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
public class ConfigurableBaseTest {

	/**
	 * Dummy implementation of {@link ConfigurableBase}
	 * @author Petyr
	 *
	 */
	private class ConfigurableDummy extends ConfigurableBase<DPUConfigObject> {

		public ConfigurableDummy() {
			super(mock(DPUConfigObject.class, withSettings().serializable()));
		}
		
	}
	
	/**
	 * Test not null default configuration.
	 */
	@Test
	public void notNullInit() throws ConfigException {
		ConfigurableDummy configurable = new ConfigurableDummy();
		assertNotNull(configurable.getConf());		
	}
	
	/**
	 * When null is set the configuration should not change.
	 */	
	@Test
	public void nullSet() throws ConfigException {
		ConfigurableDummy configurable = new ConfigurableDummy();
		byte[] oldConfig = configurable.getConf();
		assertNotNull(oldConfig);
		configurable.configure(null);
		assertNotNull(configurable.getConf());
		byte[] newConfig = configurable.getConf();
		// configuration is unchanged
		assertArrayEquals(oldConfig, newConfig);
	}
	
}
