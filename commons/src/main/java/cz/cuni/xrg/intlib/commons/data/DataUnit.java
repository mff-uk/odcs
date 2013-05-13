package cz.cuni.xrg.intlib.commons.data;

import java.io.File;

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
     * @param mergePrepare If true the merge method will be called immediately after this method.
     */
    // TODO: Jirka I thing its not in Repository - we will speak about practice of this using.
    public void createNew(String id, File workingDirectory, boolean mergePrepare);
    
    /**
     * Made this DataUnit read only. This copy will be used as a input for
     * next DPU. The copy can be used multiple times. It's guaranteed that the
     * original class will not be used anymore after this call.
	 *
     */
    public void madeReadOnly();

    /**
     * Merge (add) data from given DataUnit into this DataUnit. If the
     * unit has wrong type then the {@link IllegalArgumentException} should be thrown.
     * The method must not modify the content parameter (unit). 
     *
     * @param unit {@link DataUnit} to merge wit
     * @throws {@link IllegalArgumentException} In case of unsupported unit type.
     */
    public void merge(DataUnit unit) throws IllegalArgumentException;
        
    /**
     * Return type of data unit interface implementation.
     *
     * @return
     */
    public DataUnitType getType();

    /**
     * Return true if DataUnit is in read only state.
     * @see {@link #madeReadOnly}
     * @return
     */
    public boolean isReadOnly();
    
    /**
     * Release all lock, prepare for being deleted.
     * Can be called even when the DataUnit is in read only 
     * mode.
     */
    public void release();    
    
    /**
     * Set value, if this DataUnit is in read only state.
     * @param isReadOnly 
     */
    //public void setReadOnly(boolean isReadOnly);
    // TODO: Petyr -> Jirka : no way .. 
    
    /**
     * Return data storage repository for this type.
     *
     * @return
     */
    //public Repository getDataRepository();
    // TODO: Petyr -> Jirka : why should this be here ?
}
