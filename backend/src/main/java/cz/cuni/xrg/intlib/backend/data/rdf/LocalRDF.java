package cz.cuni.xrg.intlib.backend.data.rdf;

import cz.cuni.xrg.intlib.commons.app.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.NotValidQueryException;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.data.rdf.WriteGraphType;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRDF implements RDFDataRepository {

    private LocalRDFRepo impl;
    private File workingDirectory;
    private static String dumpName = "dump_dat.ttl";

    public static LocalRDF createLocalRepoInDirectory(String dirName,String fileName) {
        LocalRDF result = new LocalRDF();
        result.impl = LocalRDFRepo.createLocalRepoInTempDirectory(dirName,fileName);
        return result;
    }

    /**
     * Create local repository in defined path.
     *
     * @param path
     * @return
     */
    public static LocalRDF createLocalRepo(String repoPath,String fileName) {
        LocalRDF result = new LocalRDF();
        result.impl = new LocalRDFRepo(repoPath,fileName);
        return result;
    }

    /**
     * Empty constructor
     */
    public LocalRDF() {
    }

    /**
     * Public constructor - create new instance of repository in defined path.
     *
     * @param repositoryPath
     */
    public LocalRDF(String repositoryPath,String fileName) {
        this.impl = new LocalRDFRepo(repositoryPath,fileName);
    }

    /**
     * Add tripple RDF (statement) to the repository.
     *
     * @param namespace
     * @param subjectName
     * @param predicateName
     * @param objectName
     */
    @Override
    public void addTripleToRepository(String namespace, String subjectName, String predicateName, String objectName) {
        impl.addTripleToRepository(namespace, subjectName, predicateName, objectName);
    }

    /**
     * Extract RDF triples from RDF file to repository.
     *
     * @param path
     * @param suffix
     * @param baseURI
     * @param useSuffix
     */
    @Override
    public void extractRDFfromXMLFileToRepository(String path, String suffix, String baseURI, boolean useSuffix) throws ExtractException {
        impl.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useSuffix);
    }

    /**
     * Load all triples in repository to defined file in defined RDF format.
     *
     * @param directoryPath
     * @param fileName
     * @param format
     * @throws CannotOverwriteFileException
     */
    @Override
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName,
            org.openrdf.rio.RDFFormat format) throws CannotOverwriteFileException, LoadException {
        impl.loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format);
    }

    /**
     * Load all triples in repository to defined file in defined RDF format.
     *
     * @param directoryPath
     * @param fileName
     * @param format
     * @param canFileOverWrite
     * @throws CannotOverwriteFileException
     */
    @Override
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format,
            boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException,LoadException {
        impl.loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, canFileOverWrite, isNameUnique);
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the one URI graph
     * without endpoint authentisation.
     *
     * @param endpointURL
     * @param defaultGraphURI
     */
    @Override
    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, WriteGraphType graphType) throws LoadException {
        impl.loadtoSPARQLEndpoint(endpointURL, defaultGraphURI, graphType);
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the one URI graph
     * with endpoint authentisation (name,password).
     *
     * @param endpointURL
     * @param defaultGraphURI
     * @param name
     * @param password
     */
    @Override
    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, String name, String password, WriteGraphType graphType) throws LoadException {
        impl.loadtoSPARQLEndpoint(endpointURL, defaultGraphURI, name, password, graphType);
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the collection of
     * URI graphs with endpoint authentisation (name,password).
     *
     * @param endpointURL
     * @param defaultGraphURI
     * @param userName
     * @param password
     */
    @Override
    public void loadtoSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String userName,
            String password, WriteGraphType graphType) throws LoadException {
        impl.loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, userName, password, graphType);
    }

    @Override
    public List<Statement> getRepositoryStatements() {
        return impl.getRepositoryStatements();
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph withnout authentication.
     *
     * @param endpointURL
     * @param defaultGraphUri
     * @param query
     */
    @Override
    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query) throws ExtractException {
        impl.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query);
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph using authentication (name,password).
     *
     * @param endpointURL
     * @param defaultGraphUri
     * @param query
     * @param hostName
     * @param password
     * @param format
     */
    @Override
    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query, String hostName, String password, RDFFormat format) throws ExtractException {
        impl.extractfromSPARQLEndpoint(endpointURL, defaultGraphUri, query, hostName, password, format);
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * collection of URI graphs using authentication (name,password).
     *
     * @param endpointURL
     * @param defaultGraphsUri
     * @param query
     * @param hostName
     * @param password
     * @param format
     */
    @Override
    public void extractfromSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String query, String hostName, String password) throws ExtractException {
        impl.extractfromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, hostName, password);
    }

    /**
     * Transform RDF in repository by SPARQL updateQuery.
     *
     * @param updateQuery
     */
    @Override
    public void transformUsingSPARQL(String updateQuery) throws TransformException {
        impl.transformUsingSPARQL(updateQuery);
    }

    /**
     * Return count of triples stored in repository.
     *
     * @return size of triples in repository.
     */
    @Override
    public long getTripleCountInRepository() {
        return impl.getTripleCountInRepository();
    }

    /**
     * Return if RDF triple is in repository.
     *
     * @param namespace
     * @param subjectName
     * @param predicateName
     * @param objectName
     * @return
     */
    @Override
    public boolean isTripleInRepository(String namespace, String subjectName,
            String predicateName, String objectName) {
        return impl.isTripleInRepository(namespace, subjectName, predicateName, objectName);
    }

    /**
     * Removes all RDF data from repository.
     */
    @Override
    public void cleanAllRepositoryData() {
        impl.cleanAllRepositoryData();
    }

    @Override
    public void mergeRepositoryData(RDFDataRepository second) {
        impl.mergeRepositoryData(second);
    }
    
    @Override
    public Map<String,List<String>> makeQueryOverRepository(String query) throws NotValidQueryException
    {
        return impl.makeQueryOverRepository(query);
    }

    /**
     * Add all data from repository to targetRepository.
     *
     * @param targetRepository
     */
    @Override
    public void copyAllDataToTargetRepository(RDFDataRepository targetRepo) {
        impl.copyAllDataToTargetRepository(targetRepo.getDataRepository());
    }

    @Override
    public DataUnitType getType() {
        return DataUnitType.RDF_Local;
    }

    @Override
    public void madeReadOnly() {
        setReadOnly(true);
    }

    @Override
    public void merge(DataUnit unit) {
        if (unit != null) {
            if (unit instanceof RDFDataRepository) {
                RDFDataRepository rdfRepository = (RDFDataRepository) unit;
                mergeRepositoryData(rdfRepository);

            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        return impl.isReadOnly();
    }

    protected void setReadOnly(boolean isReadOnly) {
        impl.setReadOnly(isReadOnly);
    }

    public void createNew(String id, File workingDirectory, boolean mergePrepare) {
        if (!workingDirectory.exists()) {
            workingDirectory.mkdirs();
        }
        // we need inforamtion about working directory
        this.workingDirectory = workingDirectory;
        impl = new LocalRDFRepo(workingDirectory.getAbsolutePath(),id);
    }

    @Override
    public Repository getDataRepository() {
        return impl.getDataRepository();
    }

    @Override
    public void release() {
        cleanAllRepositoryData();
        impl.shutDown();
    }

    @Override
    public void save() throws Exception {

        File file = new File(workingDirectory, dumpName);
        impl.save(file);
    }

    @Override
    public void load(File directory) throws FileNotFoundException, Exception {

        File file = new File(workingDirectory, dumpName);
        impl.load(file);
    }

	@Override
	public void save(File directory) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
