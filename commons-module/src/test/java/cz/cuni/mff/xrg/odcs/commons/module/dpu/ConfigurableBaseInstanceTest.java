package cz.cuni.mff.xrg.odcs.commons.module.dpu;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

/**
 * Test suite for {@link ConfigurableBase} class.
 * 
 * @author Petyr
 */
public class ConfigurableBaseInstanceTest extends ConfigurableBase {

    public ConfigurableBaseInstanceTest() {
        super(ConfigDummy.class);
    }

    /**
     * Test that initial configuration has been set properly.
     */
    @Test
    public void initialConfigNotNull() {
        assertNotNull(config);
    }

     /**
     *
     * @param context
     * @throws DPUException
     * @throws DataUnitException
     * @throws InterruptedException
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException,
            InterruptedException {

    }

}
