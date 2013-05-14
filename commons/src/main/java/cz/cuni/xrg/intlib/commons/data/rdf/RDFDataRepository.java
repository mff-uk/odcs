package cz.cuni.xrg.intlib.commons.data.rdf;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import java.net.URL;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

/**
 * Enable work with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public interface RDFDataRepository extends DataUnit {

    public void addTripleToRepository(String namespace, String subjectName, String predicateName,
            String objectName, Resource... graphs);

    public void extractRDFfromXMLFileToRepository(String path, String suffix, String baseURI,
            boolean useSuffix, Resource... graphs);

    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName,
            org.openrdf.rio.RDFFormat format, Resource... graphs) throws CannotOverwriteFileException;

    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format,
            boolean canFileOverWrite, boolean isNameUnique, Resource... graphs) throws CannotOverwriteFileException;

    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, Resource... graphs);

    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, String name,
            String password, Resource... graphs);

    public void loadtoSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String userName,
            String password, Resource... graphs);

    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query,
            Resource... graphs);

    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query,
            String hostName, String password, RDFFormat format, Resource... graphs);

    public void extractfromSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI,
            String query, String hostName, String password, Resource... graphs);

    public void transformUsingSPARQL(String updateQuery);

    public long getTripleCountInRepository(Resource... graphs);

    public boolean isTripleInRepository(String namespace, String subjectName, String predicateName,
            String objectName, Resource... graphs);

    public void cleanAllRepositoryData(Resource... graphs);

    public void copyAllDataToTargetRepository(RDFDataRepository targetRepo);

    public void mergeRepositoryData(RDFDataRepository second);

    public Repository getDataRepository();

    public List<Statement> getRepositoryStatements(Resource... graphs);
}
