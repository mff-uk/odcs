package cz.cuni.xrg.intlib.backend.data.rdf;

import cz.cuni.xrg.intlib.commons.app.rdf.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.data.rdf.WriteGraphType;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoRDF implements RDFDataRepository {

	private VirtuosoRDFRepo impl;
	
    public static VirtuosoRDF createVirtuosoRDFRepo() {

        final String hostName = "localhost";
        final String port = "1111";
        final String user = "dba";
        final String password = "dba";
        final String defautGraph = "http://default";

        return createVirtuosoRDFRepo(hostName, port, user, password, defautGraph);

    }

    public static VirtuosoRDF createVirtuosoRDFRepo(String hostName, String port, String user, String password, String defaultGraph) {
    	VirtuosoRDF result = new VirtuosoRDF();
    	result.impl = VirtuosoRDFRepo.createVirtuosoRDFRepo(hostName, port, user, password, defaultGraph);
        return result;
    }

    /**
     * Private ctor for {@link #createVirtuosoRDFRepo}
     */
    private VirtuosoRDF() {
    	
    }
    
    /**
     * Construct a VirtuosoRepository with a specified parameters.
     *
     * @param URL_Host_List the Virtuoso JDBC URL connection string or hostlist
     * for pooled connection.
     *
     * @param user the database user on whose behalf the connection is being
     * made.
     *
     * @param password the user's password.
     *
     * @param defaultGraph a default Graph name, used for Sesame calls, when
     * contexts list is empty, exclude exportStatements, hasStatement,
     * getStatements methods.
     */
    public VirtuosoRDF(String URL_Host_List, String user, String password, String defaultGraph) {
    	impl = new VirtuosoRDFRepo(URL_Host_List, user, password, defaultGraph);
    }

    /**
     *
     * @return the Virtuoso JDBC URL connection string or hostlist for poolled
     * connection.
     */
    public String getURL_Host_List() {
        return impl.getURL_Host_List();
    }

    /**
     *
     * @return User name to Virtuoso connection.
     */
    public String getUser() {
        return impl.getUser();
    }

    /**
     *
     * @return Password to virtuoso connection.
     */
    public String getPassword() {
        return impl.getPassword();
    }

    /**
     *
     * @return Default graph name
     */
    public String getDefaultGraph() {
        return impl.getDefaultGraph();
    }

    /**
     *
     * @return defaultGraphURI
     */
    public Resource getGraph() {
        return impl.getGraph();
    }

    @Override
    public void addTripleToRepository(String namespace, String subjectName, String predicateName, String objectName) {
    	impl.addTripleToRepository(namespace, subjectName, predicateName, objectName);
    }

    @Override
    public void extractRDFfromXMLFileToRepository(String path, String suffix, String baseURI, boolean useSuffix) {
    	impl.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useSuffix);
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
            boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException {
    	impl.loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, canFileOverWrite, isNameUnique);
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
            String password, WriteGraphType graphType) {
    	impl.loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, userName, password, graphType);
    }

    @Override
    public void extractfromSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String query, String hostName, String password) {
    	impl.extractfromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, hostName, password);
    }

    @Override
    public long getTripleCountInRepository() {
    	return impl.getTripleCountInRepository();
    }

    @Override
    public void cleanAllRepositoryData() {
    	impl.cleanAllRepositoryData();
    }

    @Override
    public boolean isTripleInRepository(String namespace, String subjectName,
            String predicateName, String objectName) {
    	return impl.isTripleInRepository(namespace, subjectName, predicateName, objectName);
    }

    @Override
    public List<Statement> getRepositoryStatements() {
    	return impl.getRepositoryStatements();
    }

    /**
     * Creates read only copy of instance Virtuoso repository.
     */
    @Override
    public void madeReadOnly() {
    	impl.setReadOnly(true);
    }

	@Override
	public void createNew(String id, File workingDirectory, boolean mergePrepare) {
		// TODO Jirka: Virtuoso.createNew
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {
		// TODO Jirka: Virtuoso.merge
	}

	@Override
	public DataUnitType getType() {
		return DataUnitType.RDF_Virtuoso;
	}

	@Override
	public boolean isReadOnly() {
		return impl.isReadOnly();
	}

	@Override
	public void release() {
		// TODO Jirka: Virtuoso.release
	}

	@Override
	public void save(File directory) {
		// TODO Jirka		
	}

	@Override
	public void load(File directory) throws FileNotFoundException, Exception {
		// TODO Jirka		
	}

	@Override
	public void loadRDFfromRepositoryToXMLFile(String directoryPath,
			String fileName, RDFFormat format)
			throws CannotOverwriteFileException {
		// TODO Jirka		
	}

	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType) {
		// TODO Jirka		
	}

	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name, String password, WriteGraphType graphType) {
		// TODO Jirka		
	}

	@Override
	public void extractfromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query) {
		// TODO Jirka		
	}

	@Override
	public void extractfromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query, String hostName,
			String password, RDFFormat format) {
		// TODO Jirka		
	}

	@Override
	public void transformUsingSPARQL(String updateQuery) {
		// TODO Jirka		
	}

	@Override
	public void copyAllDataToTargetRepository(RDFDataRepository targetRepo) {
		// TODO Jirka		
	}

	@Override
	public void mergeRepositoryData(RDFDataRepository second) {
		// TODO Jirka		
	}

	@Override
	public Repository getDataRepository() {
		// TODO Jirka
		return null;
	}
}
