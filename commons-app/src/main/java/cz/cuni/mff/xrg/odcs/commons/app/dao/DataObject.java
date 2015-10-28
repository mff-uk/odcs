package cz.cuni.mff.xrg.odcs.commons.app.dao;

import java.io.Serializable;

/**
 * Marker abstract class for objects that can be used with {@link DataAccess} and {@link DataAccessRead}.
 * 
 * @author Petyr
 * @author Jan Vojt
 */
public interface DataObject extends Serializable {

    /**
     * @return object's id
     */
    public abstract int getId();

}
