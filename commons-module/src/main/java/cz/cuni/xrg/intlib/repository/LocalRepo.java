package cz.cuni.xrg.intlib.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import javax.management.Query;
import org.openrdf.model.*;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;

import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sesame.admin.StdOutAdminListener;
import org.openrdf.sesame.config.AccessDeniedException;
import org.openrdf.sesame.config.ConfigurationException;
import org.openrdf.sesame.config.RepositoryConfig;
import org.openrdf.sesame.constants.RDFFormat;
import org.openrdf.sesame.query.GraphQueryResultListener;
import org.openrdf.sesame.query.MalformedQueryException;
import org.openrdf.sesame.query.QueryEvaluationException;
import org.openrdf.sesame.query.StdOutGraphQueryResultWriter;
import org.openrdf.sesame.repository.local.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRepo {

    static LocalRepo localrepo=null;
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(LocalRepo.class);
    
    /**
     * 
     * Main for debuggin - will be deleted 
     */
    public static void main(String[]args)
    {
        LocalRepo myRepository=LocalRepo.createLocalRepo();
        myRepository.addTripleToRepository("http://namespace/intlib/", "subject", "object","predicate");
    }
    
    public static LocalRepo createLocalRepo()
    {
        if (localrepo==null)
        {
            localrepo=new LocalRepo("C:\\intlib\\myRepository");
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
        RepositoryConnection conection;
        try {

            //ERROR by getConnection. 
            // WHY ????
            conection = repository.getConnection();
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
    public void loadRDFfromXMLFileToRepository(File dataFile, String baseURI) {
        try {
            //boolean verify = true;
            //AdminListener listener = new StdOutAdminListener();
            //pository.addData(dataFile, baseURI, RDFFormat.RDFXML, verify, listener);
            // repository.getConnection().add(dataFile, baseURI, RDFFormat.RDFXML);

            RepositoryConnection connection = repository.getConnection();
            connection.add(dataFile, baseURI, org.openrdf.rio.RDFFormat.RDFXML);
            connection.commit();
            connection.close();

        } catch (Exception ex) {
        }
    }

    
    public void saveRDFfromRepositoryToXMLFile(File dataFile) throws IOException, AccessDeniedException {
        boolean ontology = true;
        boolean instances = true;
        boolean explicitOnly = true;
        boolean niceOutput = true;
        try {
            RepositoryConnection connection = repository.getConnection();
            /*TODO - resolve print to FILE*/

            connection.commit();
            connection.close();

            /*
             InputStream stream = repository.extractRDF(RDFFormat.RDFXML, ontology, instances, explicitOnly, niceOutput);
             OutputStream os = new FileOutputStream(dataFile);

             for (int i = stream.read(); i != -1; i = stream.read()) {
             os.write(i);
             }*/
        } catch (RepositoryException ex) {
        }

    }

    /**
     * Load RDF data from repository to SPARQL endpoint.
     */
    public void loadtoSPARQLEndpoint() {
        try {
            RepositoryConnection connection = repository.getConnection();
            /*TODO - resolve load from FILE*/

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
    public void extractfromSPARQLEndpoint(URL endpoint, String dataBaseURI, RDFHandler handler) {
        try {

            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            RDFParser parser = Rio.createParser(org.openrdf.rio.RDFFormat.RDFXML);
            parser.setRDFHandler(handler);
            parser.parse(connection.getInputStream(), dataBaseURI);


        } catch (Exception ex) {
        }
    }

    /**
     * Transform RDF in repository by SPARQL query.
     * @param query 
     */
    public void transformSPARQL(String query) {

        try {
            RepositoryConnection connection = repository.getConnection();
            Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL, query);
            myupdate.execute();
            connection.commit();
            connection.close();
            /*
             GraphQueryResultListener listener = new StdOutGraphQueryResultWriter();

             try {
             repository.performGraphQuery(org.openrdf.sesame.constants.QueryLanguage.SERQL, query, listener);
             // repository.performGraphQuery(org.openrdf.query.QueryLanguage.SPARQL,query,listener);
             } catch (IOException ex) {
             Logger.getLogger(LocalRepo.class.getName()).log(Level.SEVERE, null, ex);
             } catch (MalformedQueryException ex) {
             Logger.getLogger(LocalRepo.class.getName()).log(Level.SEVERE, null, ex);
             } catch (QueryEvaluationException ex) {
             Logger.getLogger(LocalRepo.class.getName()).log(Level.SEVERE, null, ex);
             } catch (AccessDeniedException ex) {
             Logger.getLogger(LocalRepo.class.getName()).log(Level.SEVERE, null, ex);
             }

             }*/
        } catch (Exception ex) {
        }

    }
}
