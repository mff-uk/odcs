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

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;

/**
 * Test suite for {@link ConfigWrap} class.
 * 
 * @author Petyr
 */
public class ConfigWrapTest {

    @SuppressWarnings("rawtypes")
    private ConfigWrap configWrap;

    private DPUConfigObject configObject;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        configObject = mock(DPUConfigObject.class, withSettings().serializable());
        configWrap = new ConfigWrap(configObject.getClass());
    }

    @Test
    public void deserialisationNull() throws ConfigException {
        DPUConfigObject newObject = configWrap.deserialize(null);
        assertNull(newObject);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serialisationNull() throws ConfigException {
        String serialized = configWrap.serialize(null);
        assertNull(serialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serialisation() throws ConfigException {
        String serialized = configWrap.serialize(configObject);

        DPUConfigObject newObject =
                configWrap.deserialize(serialized);

        assertNotNull(newObject);
        assertNotSame(configObject, newObject);
    }

    @Test
    public void newInstace() {
        DPUConfigObject newObject = configWrap.createInstance();
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
