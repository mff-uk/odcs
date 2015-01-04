package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.rdf.RepositoryManager;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.commons.rdf.repository.ManagableRepository;
import eu.unifiedviews.commons.rdf.repository.RDFException;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.impl.FilesDataUnitFactory;
import eu.unifiedviews.dataunit.rdf.impl.RDFDataUnitFactory;

/**
 * 
 *
 * @author Škoda Petr
 */
class DataUnitFactoryImpl implements DataUnitFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataUnitFactoryImpl.class);

    @Autowired
    private RepositoryManager repositoryManager;

    private final RDFDataUnitFactory rdfDataUnitFactory = new RDFDataUnitFactory();

    private final FilesDataUnitFactory filesDataUnitFactory = new FilesDataUnitFactory();

    @Override
    public ManagableDataUnit create(ManagableDataUnit.Type type, Long executionId, String dataUnitUri, String dataUnitName, File dataUnitDirectory) throws RDFException, DataUnitException {
        LOG.info("create({}, {}, {}, {}, {})", type, executionId, dataUnitUri, dataUnitName, dataUnitDirectory);
        // Get repository.
        final ManagableRepository repository = repositoryManager.get(executionId);
        // Create DataUnit.
        switch (type) {
            case FILES:
                return filesDataUnitFactory.create(dataUnitName, dataUnitUri, repository.getConnectionSource(), dataUnitDirectory);
            case RDF:
                return rdfDataUnitFactory.create(dataUnitName, dataUnitUri, repository.getConnectionSource(), dataUnitDirectory);
            default:
                throw new DataUnitException("Unknwon DataUnit type: " + type.toString());
        }
    }
}
