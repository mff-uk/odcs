package cz.cuni.xrg.intlib.backend.loader.events;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jiri Tomes
 */
public class LoadContextImpl implements LoadContext {

    private List<DataUnit> intputs = new ArrayList<>();
    
    private final String id;
    private final Map<String, Object> customData;
    private long duration;

    public LoadContextImpl(String id, Map<String, Object> customData) {
        this.id = id;
        this.customData = customData;
    }

    @Override
    public List<DataUnit> getInputs() {
        return intputs;
    }

    @Override
    public String storeData(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object loadData(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void storeDataForResult(String id, Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Map<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
