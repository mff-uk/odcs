package dpu;

import common.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiri Tomes
 */
public class TemplateConfiguration implements Configuration {

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

    public void addValue(String parameter, Object value) {
        map.put(parameter, value);
    }
}
