package cz.cuni.xrg.intlib.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
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
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
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
    static final String repositoryPath="C:\\intlib\\myRepository\\";
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(LocalRepo.class);

    public static LocalRepo createLocalRepo()
    {
        return LocalRepo.createLocalRepo(repositoryPath);
    }
    public static LocalRepo createLocalRepo(String path) {
        if (localrepo == null) {
            localrepo = new LocalRepo(path);
        }

        return localrepo;
    }
    private Repository repository = null;

    public LocalRepo(String repositoryPath) {

        long timeToStart = 1000L;
        File dataDir = new File(repositoryPath);
        MemoryStore memStore = new MemoryStore(dataDir);
        memStore.setSyncDelay(timeToStart);

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

    private Statement createNewStatement(String namespace, String subjectName, String predicateName, String objectName) {

        ValueFactory valueFaktory = repository.getValueFactory();

        URI subject = valueFaktory.createURI(namespace, subjectName);
        URI predicate = valueFaktory.createURI(namespace, predicateName);
        Literal object = valueFaktory.createLiteral(objectName);

        Statement statement = new StatementImpl(subject, predicate, object);

        return statement;
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
        
        Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);
        addStatement(statement);
    }
    
    private void addStatement(Statement statement) {
        
        try {
            
            RepositoryConnection conection = repository.getConnection();
            conection.add(statement);
            
            conection.commit();
            conection.close();
            
        } catch (RepositoryException ex) {
            System.out.println(ex.getMessage());
            
            
        }
        
    }

    public void extractRDFfromXMLFileToRepository(String path, String suffix, String baseURI, boolean useSuffix) {

        final String aceptedSuffix = suffix.toUpperCase();

        FilenameFilter acceptedFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.toUpperCase().endsWith(aceptedSuffix)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File dirFile = new File(path);

        if (dirFile.isDirectory()) {
            File[] files;
            if (useSuffix) {
                files = dirFile.listFiles(acceptedFileFilter);
            } else {
                files = dirFile.listFiles();
            }

            if (files == null) {
                return;
            }

            for (int i = 0; i < files.length; i++) {
                File nextFile = files[i];
                addFileToRepository(nextFile, baseURI);
            }

        }
        if (dirFile.isFile()) {
            addFileToRepository(dirFile, baseURI);
        }

    }

    private void addFileToRepository(File dataFile, String baseURI) {
        try {
            RDFFormat fileFormat = RDFFormat.forFileName(dataFile.getAbsolutePath());
            RepositoryConnection connection = repository.getConnection();

            connection.add(dataFile, baseURI, fileFormat);

            connection.commit();
            connection.close();

        } catch (Exception ex) {
            System.err.println("Error by adding file to repository");
        }
    }

    //VYRESENO
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format) throws FileCannotOverwriteException {
        loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, false);
    }

    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format, boolean canFileOverWrite) throws FileCannotOverwriteException {

        final String slash = "\\";

        try {
            if (!directoryPath.endsWith(slash)) {
                directoryPath += slash;
            }

            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File dataFile = new File(directoryPath + fileName);

            if (!dataFile.exists() | canFileOverWrite) {
                try {
                    dataFile.createNewFile();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }

            } else {
                System.err.println("File existed and can not be overwritten");
                throw new FileCannotOverwriteException();

            }

            OutputStream os = null;

            try {
                os = new FileOutputStream(dataFile.getAbsoluteFile());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
            RDFWriter writer = Rio.createWriter(format, os);

            RepositoryConnection connection = repository.getConnection();
            connection.export(writer);

            connection.commit();
            connection.close();

        } catch (RDFHandlerException ex) {
//            logger.error(ex.getMessage());
        } catch (RepositoryException ex) {
//            logger.error(ex.getMessage());
        }
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL.
     */
    public void loadtoSPARQLEndpoint(URL endpointURL) {
        try {
            RepositoryConnection connection = repository.getConnection();

            OutputStream os = endpointURL.openConnection().getOutputStream();

            RDFHandler goal = new RDFXMLWriter(os);
            //Resource resource = new URIImpl(endpointURL.toURI().toString());
            //connection.export(handler, resource);
            connection.export(goal);
            connection.commit();
            connection.close();

        } catch (Exception ex) {
//            logger.error(ex.getMessage());
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

            InputStream inputStream = endpointURL.openStream();

            RDFFormat format = RDFFormat.forFileName(endpointURL.toString());

            RDFParser parser = Rio.createParser(format);

            RepositoryConnection connection = repository.getConnection();
            RDFInserter goal = new RDFInserter(connection);

            parser.setRDFHandler(goal);
            parser.parse(inputStream, endpointURL.toString());

            connection.close();

        } catch (Exception ex) {
        }
    }

    //VYRESENO
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

    public long getTripleCountInRepository() {
        long size = 0;

        if (repository.isInitialized()) {
            try {
                RepositoryConnection connection = repository.getConnection();
                size = connection.size();
                connection.close();

            } catch (RepositoryException ex) {
            }

        }
        return size;
    }

    public boolean isTripleInRepository(String namespace, String subjectName, String predicateName, String objectName) {
        boolean hasTriple = false;
        if (repository.isInitialized()) {
            try {
                Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);
                RepositoryConnection connection = repository.getConnection();
                hasTriple = connection.hasStatement(statement, true);
                connection.close();

            } catch (RepositoryException ex) {
            }
        }
        return hasTriple;
    }

    public void cleanAllRepositoryData() {
        try {
            RepositoryConnection connection = repository.getConnection();
            connection.clear();
            connection.commit();
            connection.close();
        } catch (RepositoryException ex) {
        }
    }
}
