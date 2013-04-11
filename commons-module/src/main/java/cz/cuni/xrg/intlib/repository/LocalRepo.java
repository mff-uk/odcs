package cz.cuni.xrg.intlib.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.openrdf.model.*;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;

import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRepo {

    static LocalRepo localrepo = null;
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(LocalRepo.class);

    /**
     *
     * Main for debuggin - will be deleted
     */
    public static void main(String[] args) {
        LocalRepo myRepository = LocalRepo.createLocalRepo();
        myRepository.addTripleToRepository("http://namespace/intlib/", "subject", "object", "predicate");
    }

    public static LocalRepo createLocalRepo() {
        if (localrepo == null) {
            localrepo = new LocalRepo("C:\\intlib\\myRepository");
        }

        return localrepo;
    }
    private Repository repository = null;

    public LocalRepo(String repositoryPath) {

        long timeToStart = 1000L;
        File dataDir = new File(repositoryPath);
        MemoryStore memStore = new MemoryStore(dataDir);

        repository = new SailRepository(memStore);

        try {
            repository.initialize();
            System.out.println("Repository incicialized");
            logger.info("Repository incicialized");

        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
        }
    }

    /*
     private LocalRepository createLocalRepository(String repositoryName) {

     LocalService service = Sesame.getService();
     boolean inferenced = true;

     try {
     repository = service.createRepository(repositoryName, inferenced);
     } catch (ConfigurationException ex) {
     Logger.getLogger(LocalRepo.class.getName()).log(Level.SEVERE, ex.getMessage());

     }

     return repository;
     }*/
    public Graph creatNewGraph() {
        Graph graph = new GraphImpl();
        return graph;
    }

    /**
     * Add tripple RDF (statement) to the repository.
     *
     * @param namespace
     * @param subjectName
     * @param predicateName
     * @param objectName
     */
    public void addTripleToRepository(String namespace, String subjectName, String predicateName, String objectName) {

        ValueFactory valueFaktory = repository.getValueFactory();

        URI subject = valueFaktory.createURI(namespace, subjectName);
        URI predicate = valueFaktory.createURI(namespace, predicateName);
        Literal object = valueFaktory.createLiteral(objectName);

        Statement statement = new StatementImpl(subject, predicate, object);

        addStatement(statement);
    }

    private void addStatement(Statement statement) {

        try {

            //ERROR by getConnection. 
            // WHY ????
            RepositoryConnection conection = repository.getConnection();
            conection.add(statement);

            conection.commit();
            conection.close();

        } catch (RepositoryException ex) {
            System.out.println(ex.getMessage());


        }

    }

    /**
     * Add RDF triples from XML file to repository.
     *
     * @param dataFile
     * @param baseURI
     */
    public void extractRDFfromXMLFileToRepository(File dataFile, String baseURI) {
        try {
            //boolean verify = true;
            //AdminListener listener = new StdOutAdminListener();
            //pository.addData(dataFile, baseURI, RDFFormat.RDFXML, verify, listener);
            // repository.getConnection().add(dataFile, baseURI, RDFFormat.RDFXML);

            RepositoryConnection connection = repository.getConnection();

            connection.add(dataFile, baseURI, RDFFormat.forFileName(dataFile.getName()));

            connection.commit();
            connection.close();

        } catch (Exception ex) {
        }
    }

    public void loadRDFfromRepositoryToXMLFile(File dataFile, org.openrdf.rio.RDFFormat format) {

        try {
            RepositoryConnection connection = repository.getConnection();

            OutputStream os = new FileOutputStream(dataFile);

            RDFHandler handler = new RDFXMLWriter(os);

            Resource resource = new URIImpl(dataFile.toURI().toString());

            connection.export(handler, resource);
            connection.commit();
            connection.close();

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        /*
         InputStream stream = repository.extractRDF(RDFFormat.RDFXML, ontology, instances, explicitOnly, niceOutput);
         OutputStream os = new FileOutputStream(dataFile);

         for (int i = stream.read(); i != -1; i = stream.read()) {
         os.write(i);
         }
         */
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL.
     */
    public void loadtoSPARQLEndpoint(URL endpointURL) {
        try {
            RepositoryConnection connection = repository.getConnection();

            OutputStream os = endpointURL.openConnection().getOutputStream();

            RDFHandler handler = new RDFXMLWriter(os);
            Resource resource = new URIImpl(endpointURL.toURI().toString());

            connection.export(handler, resource);
            connection.commit();
            connection.close();

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Add RDF data from SPARQL endpoint to repository.
     *
     * @param endpoint
     * @param dataBaseURI
     * @param handler
     */
    public void extractfromSPARQLEndpoint(URL endpointURL) {
        try {

            RDFInserter goal = new RDFInserter(repository.getConnection());

            InputStream inputStream = endpointURL.openStream();

            RDFFormat format = RDFFormat.forFileName(endpointURL.toString());

            RDFParser parser = Rio.createParser(format);
            parser.setRDFHandler(goal);
            parser.parse(inputStream, endpointURL.toString());


        } catch (Exception ex) {
        }
    }

    /**
     * Transform RDF in repository by SPARQL updateQuery.
     *
     * @param updateQuery
     */
    public void transformUsingSPARQL(String updateQuery) {

        try {
            RepositoryConnection connection = repository.getConnection();

            Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL, updateQuery);
            myupdate.execute();

            connection.commit();
            connection.close();

        } catch (Exception ex) {
        }

    }
}
