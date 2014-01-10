package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.serialization;

import java.util.List;

import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.repository.OdnRepositoryStoreInterface;

/**
 * Stuff common to all OpenData.sk serializers.
 * <p/>
 * Serializer is given harvested data, it converts them into form suitable to a repository and then passes them to the repository.
 * 
 * @param <SerializationInputType>
 *            type of the input data, of the individual record, which will be serialized
 * @param <SerializationOutputType>
 *            type of the output data, the result of serialization
 * @param <RepositoryStoreType>
 *            type of the data pushed to the repository
 */
public abstract class AbstractSerializer<SerializationInputType, SerializationOutputType, RepositoryStoreType> {
    private OdnRepositoryStoreInterface<RepositoryStoreType> repository;

    /**
     * Initialize serializer to use given repository.
     * 
     * @throws IllegalArgumentException
     *             if repository is {@code null}
     */
    public AbstractSerializer(OdnRepositoryStoreInterface<RepositoryStoreType> repository) throws IllegalArgumentException {
        if (repository == null)
            throw new IllegalArgumentException("repository is null");
        this.repository = repository;
    }

    /**
     * Serialize given harvested records into the form suitable for storage in repository.
     * 
     * @param records
     *            to be serialized
     * @return records converted to a form suitable to be stored into repository
     */
    public abstract SerializationOutputType serialize(List<SerializationInputType> records) throws Exception;

    /**
     * Serialize and store given records.
     * 
     * @param records
     *            list of records to serialize and store
     * @throws IllegalArgumentException
     *             if repository with given name does not exists when we fail to store given data into repository
     */
    public abstract void store(List<SerializationInputType> records) throws Exception;

    public OdnRepositoryStoreInterface<RepositoryStoreType> getRepository() {
        return repository;
    }

    public void setRepository(OdnRepositoryStoreInterface<RepositoryStoreType> repository) {
        this.repository = repository;
    }
}
