package cz.cuni.intlib.commons.app.data.dpu;

import java.util.HashMap;
import java.util.Map;

/**
 * Base configuration setting for concrete DPU type.
 *
 * @author Jiri Tomes
 */
public class TemplateConfiguration implements cz.cuni.intlib.commons.configuration.Configuration {

    /*
     * For relation parameter(String)->Value(Object)
     */
    private Map<String, Object> map;

    public TemplateConfiguration() {
        map = new HashMap<String, Object>();
    }

    /*
     * Return null - no object for parameter
     */
    public Object getValue(String parameter) {
        return map.get(parameter);
    }

    public void setValue(String parameter, Object value) {
        map.put(parameter, value);
    }
}
