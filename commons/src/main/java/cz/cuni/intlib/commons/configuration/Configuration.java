package cz.cuni.intlib.commons.configuration;

/**
 * DPU' configuration.
 * @author Jiri Tomes
 */
public interface Configuration {

    public Object getValue(String parameter);
    
    public void setValue(String parameter, Object value);
}
