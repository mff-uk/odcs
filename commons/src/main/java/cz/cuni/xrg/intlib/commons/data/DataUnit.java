package cz.cuni.xrg.intlib.commons.data;

import java.io.File;
import org.openrdf.repository.Repository;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information.
 *
 * @author Petyr
 *
 */
public interface DataUnit {

    /**
     * Create new context with given id. The context must not be read only.
     * Because it's not guaranteed that workingDirectory exist, 
     * call mkdirs before first use. 
     *
     * @param id unique identification
     * @param workingDirectory Path to the directory where DataUnit can store files.
     */
    // TODO: Jirka I thing its not in Repository - we will speak about practive of this using.
    public void createNew(String id, File workingDirectory);
    /**
     * Return read only copy of data unit. This copy will be used as a input for
     * next DPU. The copy can be used multiple times. It's guaranteed that the
     * original class will not be used anymore after this call.
     *
     * @return
     */
    public DataUnit createReadOnlyCopy();

    /**
     * Merge (add) data from given DataUnit into this DataUnit.
     *
     * @param unit
     * @return DataUnit
     */
    public void merge(DataUnit unit);

    /**
     * Return type of data unit interface implementation.
     *
     * @return
     */
    public DataUnitType getType();

    /**
     * Return true if DataUnit is in read only state.
     *
     * @return
     */
    public boolean isReadOnly();

    /**
     * Set value, if this DataUnit is in read only state.
     * @param isReadOnly 
     */
    public void setReadOnly(boolean isReadOnly);
    /**
     * Return data storage repository for this type.
     *
     * @return
     */
    public Repository getDataRepository();
}
