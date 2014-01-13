package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.repository;

/**
 * This is a Open Data Node Repository interface defining "internal API" between "serialization" and "repository" classes.
 * 
 * @param <RecordType>
 *            type of records which are going to be stored in repository
 */
public interface OdnRepositoryStoreInterface<RecordType> {

    /**
     * Store given record(s) into the repository.
     * 
     * @param records
     *            one or more records to store
     * @throws IllegalArgumentException
     *             when some of the given arguments is not valid
     */
    public void store(RecordType records) throws IllegalArgumentException;

    /**
     * Shut down repository.
     */
    public void shutDown();
}
