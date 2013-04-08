package cz.cuni.xrg.intlib.commons.app.pipeline.event;

/**
 *
 * @author Jiri Tomes
 */
public interface Disableable {
    
    public void setDisabled(boolean disabled);
    public boolean isDisabled();
}
