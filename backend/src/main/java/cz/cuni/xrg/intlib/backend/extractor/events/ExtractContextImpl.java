package cz.cuni.xrg.intlib.backend.extractor.events;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jiri Tomes
 */
public class ExtractContextImpl implements ExtractContext {

    private List<DataUnit> outputs = new ArrayList<>();
    
    private final String id;
    private final Map<String, Object> customData;
    private long duration;

    public ExtractContextImpl(String id, Map<String, Object> customData) {
        this.id = id;
        this.customData = customData;
    }

    @Override
    public List<DataUnit> getOutputs() {
        return outputs;
    }

    @Override
    public void addOutputDataUnit(DataUnit dataUnit) {
        outputs.add(dataUnit);
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
