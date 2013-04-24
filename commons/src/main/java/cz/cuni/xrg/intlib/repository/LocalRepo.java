package cz.cuni.xrg.intlib.repository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRepo {

    static LocalRepo localrepo = null;
    static final String repositoryPath = "C:\\intlib\\myRepository\\";
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(LocalRepo.class);
    private final String encode = "UTF-8";

    public static LocalRepo createLocalRepo() {
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
            logger.info("Repository incicialized");

        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());

        }
    }

    public Resource creatNewGraph(String graphURI) {
        Resource graph = new URIImpl(graphURI);
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
            logger.debug(ex.getMessage());


        }

    }

    /**
     * Extract RDF triples from RDF file to repository.
     *
     * @param path
     * @param suffix
     * @param baseURI
     * @param useSuffix
     */
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

        if (path.toLowerCase().startsWith("http")) {

            try {

                URL urlPath = new URL(path);
                InputStream inputStream = urlPath.openStream();
                RDFFormat format = RDFFormat.forFileName(path, RDFFormat.RDFXML);

                RepositoryConnection connection = repository.getConnection();
                connection.add(inputStream, baseURI, format);

                inputStream.close();

            } catch (Exception ex) {
                logger.debug(ex.getMessage());
            }

        }

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
            RDFFormat fileFormat = RDFFormat.forFileName(dataFile.getAbsolutePath(), RDFFormat.RDFXML);
            RepositoryConnection connection = repository.getConnection();

            connection.add(dataFile, baseURI, fileFormat);

            connection.commit();
            connection.close();

        } catch (Exception ex) {
            logger.debug("Error by adding file to repository");
        }
    }

    /**
     * Load all triples in repository to defined file in defined RDF format.
     *
     * @param directoryPath
     * @param fileName
     * @param format
     * @throws FileCannotOverwriteException
     */
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format) throws FileCannotOverwriteException {
        loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, false);
    }

    /**
     * Load all triples in repository to defined file in defined RDF format.
     *
     * @param directoryPath
     * @param fileName
     * @param format
     * @param canFileOverWrite
     * @throws FileCannotOverwriteException
     */
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format, boolean canFileOverWrite) throws FileCannotOverwriteException {

        final String slash = File.separator;

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
                    logger.debug(ex.getMessage());
                }

            } else {
                logger.debug("File existed and can not be overwritten");
                throw new FileCannotOverwriteException();

            }

            try {
                OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(dataFile.getAbsoluteFile()),encode);
                
                RDFWriter writer = Rio.createWriter(format, os);

                RepositoryConnection connection = repository.getConnection();
                connection.export(writer);

                connection.commit();
                connection.close();
                os.close();

            } catch (FileNotFoundException ex) {
                logger.debug(ex.getMessage());
            } catch (IOException ex) {
                logger.debug(ex.getMessage());
            }

        } catch (RDFHandlerException ex) {
            logger.debug(ex.getMessage());
        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI) {
        List<String> graphs = new ArrayList<String>();
        graphs.add(defaultGraphURI);

        loadtoSPARQLEndpoint(endpointURL, graphs, "", "");
    }

    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, String name, String password) {
        List<String> graphs = new ArrayList<String>();
        graphs.add(defaultGraphURI);

        loadtoSPARQLEndpoint(endpointURL, graphs, name, password);
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL.
     *
     * @param endpointURL
     * @param defaultGraphURI
     * @param userName
     * @param password
     */
    public void loadtoSPARQLEndpoint(URL endpointURL, List<String> defaultGraphsURI, String userName, String password) {
        try {

            final int graphSize = defaultGraphsURI.size();
            final RDFFormat format = RDFFormat.N3;

            Resource[] graphs = new Resource[graphSize];

            for (int i = 0; i < graphSize; i++) {
                graphs[i] = new URIImpl(defaultGraphsURI.get(i));
            }


            RepositoryConnection connection = repository.getConnection();

            boolean autentize = !(userName.isEmpty() && password.isEmpty());

            HttpURLConnection httpConnection = (HttpURLConnection) endpointURL.openConnection();

            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.addRequestProperty("Accept", format.getDefaultMIMEType());

            if (autentize) {

                final String myName = userName;
                final String myPassword = password;

                Authenticator autentisator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(myName, myPassword.toCharArray());
                    }
                };

                Authenticator.setDefault(autentisator);

            }

            OutputStream outputStream = new BufferedOutputStream(httpConnection.getOutputStream());

            RDFWriter goal = Rio.createWriter(format, outputStream);

            for (int i = 0; i < graphSize; i++) {
                connection.export(goal, graphs[i]);
                connection.commit();
            }

            connection.close();
            httpConnection.disconnect();
            outputStream.close();

        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query) {
        List<String> graphs = new ArrayList<String>();
        graphs.add(defaultGraphUri);

        extractfromSPARQLEndpoint(endpointURL, graphs, query, "", "");
    }

    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query, String hostName, String password, RDFFormat format) {
        List<String> graphs = new ArrayList<String>();
        graphs.add(defaultGraphUri);

        extractfromSPARQLEndpoint(endpointURL, graphs, query, hostName, password);
    }

    /**
     * Add RDF data from SPARQL endpoint to repository.
     *
     * @param endpointURL
     * @param defaultGraphsUri
     * @param query
     * @param hostName
     * @param password
     * @param format
     */
    public void extractfromSPARQLEndpoint(URL endpointURL, List<String> defaultGraphsUri, String query, String hostName, String password) {
        try {

            final RDFFormat format = RDFFormat.N3;
            final int graphSize = defaultGraphsUri.size();
            final String myquery = query.replace(" ", "+");
            final String encoder = URLEncoder.encode(format.getDefaultMIMEType(), encode);

            RepositoryConnection connection = repository.getConnection();

            for (int i = 0; i < graphSize; i++) {

                final String graph = defaultGraphsUri.get(i).replace(" ", "+");

                URL call = new URL(endpointURL.toString() + "?default-graph-uri=" + graph + "&query=" + myquery + "&format=" + encoder);

                HttpURLConnection httpConnection = (HttpURLConnection) call.openConnection();
                httpConnection.addRequestProperty("Accept", format.getDefaultMIMEType());

                boolean usePassword = (!hostName.isEmpty() | !password.isEmpty());

                if (usePassword) {

                    final String myName = hostName;
                    final String myPassword = password;

                    Authenticator autentisator = new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(myName, myPassword.toCharArray());
                        }
                    };

                    Authenticator.setDefault(autentisator);

                }

                InputStreamReader inputStreamReader=new InputStreamReader(httpConnection.getInputStream(), encode);

                connection.add(inputStreamReader, graph, format);
                inputStreamReader.close();
            }

            connection.close();


        } catch (IOException ex) {
            logger.debug("Can not open http connection stream");
            logger.debug(ex.getMessage());
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
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
            logger.debug("SPARQL query for transform is valid and prepared for execution");
            
            myupdate.execute();

            connection.commit();
            connection.close();
            
            logger.debug("SPARQL query for transform was executed succesfully");

        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }

    }

    /**
     *
     * @return size of triples in repository.
     */
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

    /**
     * Return if RDF triple is in repository.
     *
     * @param namespace
     * @param subjectName
     * @param predicateName
     * @param objectName
     * @return
     */
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

    /**
     * Removes all RDF data from repository.
     */
    public void cleanAllRepositoryData() {
        try {
            RepositoryConnection connection = repository.getConnection();
            connection.clear();
            connection.commit();
            connection.close();
        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());
        }
    }
    
    /**
     * Add all data from repository to targetRepository.
     * 
     * @param targetRepository 
     */
    public void copyAllDataToTargetRepository(Repository targetRepository) {
        try {
            RepositoryConnection sourceConnection = repository.getConnection();

            if (!sourceConnection.isEmpty()) {

                List<Statement> sourceStatemens = sourceConnection.getStatements(null, null, null, true).asList();

                if (targetRepository != null) {
                    
                    RepositoryConnection targetConnection = targetRepository.getConnection();

                    for (Statement nextStatement : sourceStatemens) {
                        targetConnection.add(nextStatement);
                    }
                }
            }
        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());
        }

    }
}
