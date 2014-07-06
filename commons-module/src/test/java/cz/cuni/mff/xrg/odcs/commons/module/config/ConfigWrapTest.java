package cz.cuni.mff.xrg.odcs.commons.module.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.config.DPUConfig;

/**
 * Test suite for {@link ConfigWrap} class.
 * 
 * @author Petyr
 */
public class ConfigWrapTest {

    @SuppressWarnings("rawtypes")
    private ConfigWrap configWrap;

    private DPUConfig configObject;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        configObject = mock(DPUConfig.class, withSettings().serializable());
        configWrap = new ConfigWrap(configObject.getClass());
    }

    @Test
    public void deserialisationNull() throws DPUConfigException {
        DPUConfig newObject = configWrap.deserialize(null);
        assertNull(newObject);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serialisationNull() throws DPUConfigException {
        String serialized = configWrap.serialize(null);
        assertNull(serialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serialisation() throws DPUConfigException {
        String serialized = configWrap.serialize(configObject);

        DPUConfig newObject =
                configWrap.deserialize(serialized);

        assertNotNull(newObject);
        assertNotSame(configObject, newObject);
    }

    @Test
    public void newInstace() {
        DPUConfig newObject = configWrap.createInstance();
        assertNotNull(newObject);
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
