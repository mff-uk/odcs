package cz.cuni.mff.xrg.odcs.commons.module.dialog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.config.ConfigWrap;

/**
 * Test suite for {@link BaseConfigDialog} class.
 * 
 * @author Petyr
 */
public class BaseConfigDialogTest extends BaseConfigDialog<SimpleTestConfig> {

    /**
     * Actual dialog configuration.
     */
    SimpleTestConfig actualConfiguration;

    public BaseConfigDialogTest() {
        super(SimpleTestConfig.class);
    }

    @Override
    protected void setConfiguration(SimpleTestConfig conf)
            throws ConfigException {
        actualConfiguration = conf;
    }

    @Override
    protected SimpleTestConfig getConfiguration() throws ConfigException {
        return actualConfiguration;
    }

    // - - - - - - - - - - - - - - tests - - - - - - - - - - - - - - - - - - //

    @Test
    public void hasConfigChangeTest() throws ConfigException {
        SimpleTestConfig c = new SimpleTestConfig("hi", 42);
        SimpleTestConfig cEqual = new SimpleTestConfig("hi", 42);
        SimpleTestConfig cNonEqual = new SimpleTestConfig("i", 41);

        // we need serialized forms of this configurations in order to set them
        ConfigWrap<SimpleTestConfig> configWrap = new ConfigWrap<>(
                SimpleTestConfig.class);

        String configString = configWrap.serialize(c);

        // set configuration
        this.setConfig(configString);
        // change configuration in dialog .. 
        this.actualConfiguration = cEqual;

        assertFalse(this.hasConfigChanged());
        // this should remain unchanged by multiple calls
        assertFalse(this.hasConfigChanged());

        // now set different configuration
        this.actualConfiguration = cNonEqual;

        assertTrue(this.hasConfigChanged());
        // this should remain unchanged by multiple calls
        assertTrue(this.hasConfigChanged());

        // and when we update the configuration, it should return false
        // as we do not change it after this
        this.setConfig(configString);
        assertFalse(this.hasConfigChanged());
    }

}
