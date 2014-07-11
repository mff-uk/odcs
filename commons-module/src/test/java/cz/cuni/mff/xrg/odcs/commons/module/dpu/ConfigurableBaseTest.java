package cz.cuni.mff.xrg.odcs.commons.module.dpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

/**
 * Test suite for {@link ConfigurableBase} class.
 * 
 * @author Petyr
 */
public class ConfigurableBaseTest {

    /**
     * Dummy implementation of {@link ConfigurableBase}
     * 
     * @author Petyr
     */
    private class ConfigurableDummy extends ConfigurableBase<ConfigDummy> {
        public ConfigurableDummy() {
            super(ConfigDummy.class);
        }

        @Override
        public void execute(DPUContext context)
                throws DPUException,
                InterruptedException {

        }
    }

    /**
     * Test not null default configuration.
     * 
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    @Test
    public void notNullInit() throws DPUConfigException {
        ConfigurableDummy configurable = new ConfigurableDummy();
        assertNotNull(configurable.getDefaultConfiguration());
    }

    /**
     * When null is set the configuration should not change.
     * 
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    @Test
    public void nullSet() throws DPUConfigException {
        ConfigurableDummy configurable = new ConfigurableDummy();
        String oldConfig = configurable.getDefaultConfiguration();
        assertNotNull(oldConfig);
        String nullByteConfig = null;
        configurable.configure(nullByteConfig);
        assertNotNull(configurable.getDefaultConfiguration());
        String newConfig = configurable.getDefaultConfiguration();
        // configuration is unchanged
        assertEquals(oldConfig, newConfig);
    }

}
