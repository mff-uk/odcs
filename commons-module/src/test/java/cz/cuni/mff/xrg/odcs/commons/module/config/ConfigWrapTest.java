package cz.cuni.mff.xrg.odcs.commons.module.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Test suite for {@link ConfigWrap} class.
 * 
 * @author Petyr
 */
public class ConfigWrapTest {

    @SuppressWarnings("rawtypes")
    private ConfigWrap configWrap;

    private Object configObject;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        configObject = mock(Object.class, withSettings().serializable());
        configWrap = new ConfigWrap(configObject.getClass());
    }

 
    @SuppressWarnings("unchecked")
    @Test
    public void serialisationNull() throws DPUConfigException {
        String serialized = configWrap.serialize(null);
        assertNull(serialized);
    }

    @Test
    public void fieldCopy() {
        SampleConfig source = new SampleConfig();
        source.setFirst("1");
        source.setSecond("2");
        SampleConfig target = new SampleConfig();

        LinkedList<String> fieldNames = new LinkedList<>();
        fieldNames.add("second");

        configWrap = new ConfigWrap(SampleConfig.class);
        configWrap.copyFields(source, target, fieldNames);

        assertEquals("f", target.getFirst());
        assertEquals("2", target.getSecond());
    }

}
