package cz.cuni.mff.xrg.odcs.commons.data;

/**
 * Provide additional functionality to the {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} that enable management in
 * sense of load, store, merge and delete. It is separated from the DataUnit
 * interface because DPU developer do not need to see methods needed by the
 * engine and defined in ManagableDataUnit
 * 
 * @author Petyr
 */
public interface ManagableDataUnit extends DataUnit {

    /**
     * Delete all the data from the DataUnit but does not close or destroy it.
     * After this call the state of data inside DataUnit should be the same as if it was
     * newly created. Reset the data in data unit to a clean state.
     */
    void clear();

    /**
     * Dry-run, check all locks.
     */
    void isReleaseReady();

    /**
     * Release all locks, prepare for destroy in memory representation of
     * DataUnit.
     * DataUnit is not usable anymore after calling this.
     */
    void release();

    /**
     * Merge (add) data from given DataUnit into this DataUnit. If the unit has
     * wrong type then the {@link IllegalArgumentException} should be thrown.
     * The method must not modify the current parameter (unit). The given
     * DataUnit is not in read-only mode.
     * 
     * @param unit
     *            {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} to merge
     *            with
     */
    void merge(DataUnit unit) throws IllegalArgumentException;
}
