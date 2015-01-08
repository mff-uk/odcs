package eu.unifiedviews.commons.dataunit;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Provide additional functionality to the
 * {@link eu.unifiedviews.dataunit.DataUnit} that enable management in sense of
 * load, store, merge and delete. It is separated from the DataUnit interface
 * because DPU developer do not need to see methods needed by the engine and
 * defined in ManagableDataUnit
 *
 * @author Petyr
 */
public interface ManagableDataUnit extends DataUnit {

    /**
     * Types of DataUnit interface implementation.
     *
     * @author Petyr
     */
    public enum Type {

        /**
         * General RDF type, the repository will be selected by the application.
         */
        RDF,
        /**
         * Represent files data unit.
         */
        FILES
    }

    /**
     * Return type of data unit interface implementation.
     *
     * @return DataUnit type.
     */
    ManagableDataUnit.Type getType();

    /**
     * Check my type against provided.
     *
     * @param dataUnitType
     * @return True if equals
     */
    boolean isType(ManagableDataUnit.Type dataUnitType);

    /**
     * Return dataUnit's URI. The DataUnit URI should be set in constructor.
     * Otherwise it should be immutable.
     *
     * @return String name of data unit.
     */
    String getName();

    /**
     * Delete all the data from the DataUnit but does not close or destroy it.
     * After this call the state of data inside DataUnit should be the same as
     * if it was newly created. Reset the data in data unit to a clean state.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    void clear() throws DataUnitException;

    /**
     * Check for data unit consistency. It's called right after the DPU execution.
     * If throws, then the execution fill not continue and fail.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    void checkConsistency() throws DataUnitException;

    /**
     * Release all locks, prepare for destroy in memory representation of
     * DataUnit. DataUnit is not usable anymore after calling this.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    void release() throws DataUnitException;

    /**
     * Merge (add) data from given DataUnit into this DataUnit. If the unit has
     * wrong type then the {@link IllegalArgumentException} should be thrown.
     * The method must not modify the current parameter (unit). The given
     * DataUnit is not in read-only mode.
     *
     * @param dataUnit {@link DataUnit} to merge with
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    void merge(DataUnit dataUnit) throws IllegalArgumentException, DataUnitException;

    /**
     * Persist data in data unit.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    void store() throws DataUnitException;

    /**
     * Load data associated with given data unit.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    void load() throws DataUnitException;

}
