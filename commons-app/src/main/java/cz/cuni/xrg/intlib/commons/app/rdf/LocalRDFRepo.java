package cz.cuni.xrg.intlib.commons.app.rdf;

import cz.cuni.xrg.intlib.commons.app.rdf.GraphNotEmptyException;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.data.rdf.WriteGraphType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRDFRepo {

    private static LocalRDFRepo localrepo = null;
    
    /**
     * Logging information about execution of method using openRDF.
     */
    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(LocalRDFRepo.class);
    
    /**
     * How many triples is possible to add to SPARQL endpoind at once.
     */
    protected static final int STATEMENTS_COUNT = 10;
    
    /**
     * Default name for temp directory, where this repository is placed.
     */
    private final static String repoDirName = "intlib-repo";
    
    protected final String encode = "UTF-8";
    
    /**
     * RDF data storage component.
     */
    protected Repository repository = null;
    
    /**
     * If the repository is used only for reading data or not.
     */
    protected boolean isReadOnly;

    /**
     * Create local repository in default path.
     *
     * @return
     */
    public static LocalRDFRepo createLocalRepo() {

        return LocalRDFRepo.createLocalRepoInDirectory(repoDirName);
    }

    public static LocalRDFRepo createLocalRepoInDirectory(String dirName) {
        Path repoPath = null;

        try {
            repoPath = Files.createTempDirectory(dirName);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return LocalRDFRepo.createLocalRepo(repoPath.toString());
    }

    /**
     * Create local repository in defined path.
     *
     * @param path
     * @return
     */
    public static LocalRDFRepo createLocalRepo(String path) {
        localrepo = new LocalRDFRepo(path);
        return localrepo;
    }

    /**
     * Empty constructor - used only for inheritance.
     * TODO: Jirka: if only for inheritance why you have not used protected ?
     */
    public LocalRDFRepo() {
    }

    /**
     * Public constructor - create new instance of repository in defined path.
     *
     * @param repositoryPath
     */
    public LocalRDFRepo(String repositoryPath) {

        callConstructorSetting(repositoryPath);
    }

    private void callConstructorSetting(String repositoryPath) {
        setReadOnly(false);

        long timeToStart = 1000L;
        File dataDir = new File(repositoryPath);
        MemoryStore memStore = new MemoryStore(dataDir);
        memStore.setSyncDelay(timeToStart);

        repository = new SailRepository(memStore);

        try {
            repository.initialize();
            logger.info("New repository incicialized");

        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());

        }
    }

    protected Resource createNewGraph(String graphURI) {
        Resource graph = new URIImpl(graphURI);
        return graph;
    }

    protected void createNewFile(File file) {

        if (file == null) {
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }

    }

    protected Statement createNewStatement(String namespace, String subjectName, String predicateName, String objectName) {

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

            InputStreamReader inputStream;
            try {

                URL urlPath = new URL(path);

                inputStream = new InputStreamReader(urlPath.openStream(), encode);
                RDFFormat format = RDFFormat.forFileName(path, RDFFormat.RDFXML);

                RepositoryConnection connection = repository.getConnection();
                connection.add(inputStream, baseURI, format);

                inputStream.close();

            } catch (IOException | RepositoryException | RDFParseException ex) {
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

        } catch (RepositoryException | IOException | RDFParseException ex) {
            logger.debug("Error by adding file to repository");
        }
    }

    /**
     * Load all triples in repository to defined file in defined RDF format.
     *
     * @param directoryPath
     * @param fileName
     * @param format
     * @throws CannotOverwriteFileException
     */
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName,
            org.openrdf.rio.RDFFormat format) throws CannotOverwriteFileException {

        loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, false, false);
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
    public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format,
            boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException {

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

            if (!dataFile.exists()) {
                createNewFile(dataFile);

            } else {
                if (isNameUnique) {

                    String uniqueFileName = UniqueNameGenerator.getNextName(fileName);

                    dataFile = new File(directoryPath + uniqueFileName);
                    createNewFile(dataFile);

                } else if (canFileOverWrite) {
                    createNewFile(dataFile);
                } else {
                    logger.debug("File existed and cannot be overwritten");
                    throw new CannotOverwriteFileException();
                }

            }


            OutputStreamWriter os;
            try {
                os = new OutputStreamWriter(new FileOutputStream(dataFile.getAbsoluteFile()), encode);

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

        } catch (RDFHandlerException | RepositoryException ex) {
            logger.debug(ex.getMessage());
        }
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the one URI graph
     * without endpoint authentication.
     *
     * @param endpointURL
     * @param defaultGraphURI
     */
    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, WriteGraphType graphType) {
        List<String> endpointGraphsURI = new ArrayList<>();
        endpointGraphsURI.add(defaultGraphURI);

        loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "", graphType);
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the one URI graph
     * with endpoint authentication (name,password).
     *
     * @param endpointURL
     * @param defaultGraphURI
     * @param name
     * @param password
     */
    public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, String name, String password, WriteGraphType graphType) {
        List<String> endpointGraphsURI = new ArrayList<>();
        endpointGraphsURI.add(defaultGraphURI);

        loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, name, password, graphType);
    }

    /**
     * Load RDF data from repository to SPARQL endpointURL to the collection of
     * URI graphs with endpoint authentication (name,password).
     *
     * @param endpointURL
     * @param defaultGraphURI
     * @param userName
     * @param password
     */
    public void loadtoSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String userName,
            String password, WriteGraphType graphType) {
        try {

            final int graphSize = endpointGraphsURI.size();
            List<String> dataParts = getInsertPartsTriplesQuery(STATEMENTS_COUNT, this);
            final int partsCount = dataParts.size();

            HTTPRepository endpointRepo = new HTTPRepository(endpointURL.toString(), "");

            boolean usePassword = (!userName.isEmpty() | !password.isEmpty());

            if (usePassword) {

                final String myName = userName;
                final String myPassword = password;

                Authenticator autentisator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(myName, myPassword.toCharArray());
                    }
                };

                Authenticator.setDefault(autentisator);
                endpointRepo.setUsernameAndPassword(userName, password);

            }

            try {
                endpointRepo.initialize();
            } catch (RepositoryException e) {
                logger.debug("Endpoint reposiotory is failed");
                logger.debug(e.getMessage());
            }

            RepositoryConnection connection = repository.getConnection();

            for (int i = 0; i < graphSize; i++) {

                boolean isOK = true;
                try {
                    switch (graphType) {
                        case MERGE:
                            break;
                        case OVERRIDE: {
                            RepositoryConnection endpointGoal = endpointRepo.getConnection();
                            Resource graphToClear = new URIImpl(endpointGraphsURI.get(i));
                            endpointGoal.clear(graphToClear);
                            endpointGoal.close();
                        }
                        break;
                        case FAIL: {
                            RepositoryConnection endpointGoal = endpointRepo.getConnection();
                            Resource goalGraph = new URIImpl(endpointGraphsURI.get(i));
                            boolean sourceNotEmpty = endpointGoal.size(goalGraph) > 0;
                            endpointGoal.close();

                            if (sourceNotEmpty) {
                                throw new GraphNotEmptyException("Graph " + goalGraph.toString() + "is not empty");
                            }


                        }
                        break;

                    }
                } catch (GraphNotEmptyException | RepositoryException ex) {
                    logger.debug(ex.getMessage());
                    isOK = false;
                }

                if (isOK == false) {
                    continue;
                }

                for (int j = 0; j < partsCount; j++) {

                    final String endpointGraph = endpointGraphsURI.get(i).replace(" ", "+");
                    final String query = dataParts.get(j);
                    final String myquery = URLEncoder.encode(query, encode);

                    URL call = new URL(endpointURL.toString() + "?default-graph-uri=" + endpointGraph + "&query=" + myquery);

                    HttpURLConnection httpConnection = (HttpURLConnection) call.openConnection();
                    httpConnection.setRequestProperty("Content-type", "text/xml");

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                        reader.close();

                        final String processing = String.valueOf(j + 1) + "/" + String.valueOf(partsCount);

                        logger.debug("Data " + processing + " part loaded successful");
                    } catch (IOException ex) {
                        logger.debug(
                                "Cannot open http connection stream at '"
                                + call.toString()
                                + "' - data not loaded");
                        logger.debug(ex.getMessage());
                    } finally {
                        httpConnection.disconnect();
                    }

                }
            }

            connection.close();

        } catch (RepositoryException | IOException ex) {
            logger.debug(ex.getMessage());
        }
    }

    public List<Statement> getRepositoryStatements() {
        List<Statement> statemens = new ArrayList<>();

        if (repository != null) {
            try {
                RepositoryConnection connection = repository.getConnection();

                statemens = connection.getStatements(null, null, null, true).asList();

            } catch (RepositoryException ex) {
                logger.debug(ex.getMessage());
            }
        }

        return statemens;
    }

    protected List<String> getInsertPartsTriplesQuery(int sizeSplit, LocalRDFRepo where) {

        final String insertStart = "INSERT {";
        final String insertStop = "} ";

        List<String> parts = new ArrayList<>();

        StringBuilder builder = new StringBuilder();

        List<Statement> statements = where.getRepositoryStatements();

        if (!statements.isEmpty()) {
            builder.append(insertStart);

            int count = 0;

            for (Statement nextStatement : statements) {

                String subject = nextStatement.getSubject().stringValue();
                String predicate = nextStatement.getPredicate().stringValue();
                String object = nextStatement.getObject().stringValue();

                object = object.replaceAll("<br\\s*/>", "")
                        .replaceAll("\\s+", "_")
                        .replaceAll("<", "â€ą")
                        .replaceAll(">", "â€ş");

                String appendLine = "<" + subject + "> <" + predicate + "> <" + object + "> . ";
                builder.append(appendLine.replaceAll("\\s+", " ").replaceAll("\"", "'"));

                count++;
                if (count == sizeSplit) {
                    builder.append(insertStop);
                    parts.add(builder.toString());

                    builder = new StringBuilder();
                    builder.append(insertStart);
                    count = 0;
                }
            }

            if (count > 0) {
                builder.append(insertStop);
                parts.add(builder.toString());
            }
        }

        return parts;
    }

    /**
     * Extract RDF data from SPARQL endpoint to repository using only data from
     * URI graph without authentication.
     *
     * @param endpointURL
     * @param defaultGraphUri
     * @param query
     */
    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query) {
        List<String> endpointGraphsURI = new ArrayList<>();
        endpointGraphsURI.add(defaultGraphUri);

        extractfromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, "", "");
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
    public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query, String hostName, String password, RDFFormat format) {
        List<String> endpointGraphsURI = new ArrayList<>();
        endpointGraphsURI.add(defaultGraphUri);

        extractfromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, hostName, password);
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
    public void extractfromSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String query, String hostName, String password) {
        try {

            final RDFFormat format = RDFFormat.N3;
            final int graphSize = endpointGraphsURI.size();
            final String myquery = query.replace(" ", "+");
            final String encoder = URLEncoder.encode(format.getDefaultMIMEType(), encode);

            RepositoryConnection connection = repository.getConnection();

            for (int i = 0; i < graphSize; i++) {

                final String endpointGraph = endpointGraphsURI.get(i).replace(" ", "+");

                URL call = new URL(endpointURL.toString() + "?default-graph-uri=" + endpointGraph + "&query=" + myquery + "&format=" + encoder);

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

                try (InputStreamReader inputStreamReader = new InputStreamReader(httpConnection.getInputStream(), encode)) {
                    connection.add(inputStreamReader, endpointGraph, format);

                }
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
            logger.debug("This SPARQL query for transform is valid and prepared for execution:");
            logger.debug(updateQuery);

            myupdate.execute();

            connection.commit();
            connection.close();

            logger.debug("SPARQL query for transform was executed succesfully");

        } catch (RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
            logger.debug("SPARQL query was not executed !!!");
            logger.debug(ex.getMessage());
        }

    }

    /**
     * Return count of triples stored in repository.
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
                logger.debug(ex.getMessage());
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
    public boolean isTripleInRepository(String namespace, String subjectName,
            String predicateName, String objectName) {
        boolean hasTriple = false;

        if (repository.isInitialized()) {
            try {
                Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);
                RepositoryConnection connection = repository.getConnection();

                hasTriple = connection.hasStatement(statement, true);


                connection.close();

            } catch (RepositoryException ex) {
                logger.debug(ex.getMessage());
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

    public void mergeRepositoryData(RDFDataRepository second) {

        if (second == null) {
            return;
        }
        Repository secondRepository = second.getDataRepository();

        try {
            RepositoryConnection sourceConnection = secondRepository.getConnection();

            if (!sourceConnection.isEmpty()) {

                List<Statement> sourceStatemens = second.getRepositoryStatements();

                RepositoryConnection targetConnection = repository.getConnection();

                if (targetConnection != null) {

                    for (Statement nextStatement : sourceStatemens) {
                        targetConnection.add(nextStatement);
                    }
                }
            }
        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());
        }
    }

    /**
     * Add all data from repository to targetRepository.
     *
     * @param targetRepository
     */
    public void copyAllDataToTargetRepository(Repository targetRepo) {

        if (targetRepo == null) {
            return;
        }

        Repository targetRepository = targetRepo; //targetRepo.getDataRepository();

        try {
            RepositoryConnection sourceConnection = repository.getConnection();

            if (!sourceConnection.isEmpty()) {

                List<Statement> sourceStatemens = this.getRepositoryStatements();

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

    public Repository getDataRepository() {
        return repository;
    }    
    
    public boolean isReadOnly() {
    	return isReadOnly;
    }
    
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public List<RDFTriple> getRDFTriplesInRepository() {
     
     List<RDFTriple> triples=new ArrayList<>();
     List<Statement> statements=getRepositoryStatements();
     
     int count=0;
     
     for (Statement next:statements)
     {
         String subject=next.getSubject().stringValue();
         String predicate=next.getPredicate().stringValue();
         String object=next.getObject().stringValue();
         
         count++;
         
         RDFTriple triple=new RDFTriple(count, subject, predicate, object);
         triples.add(triple);
     }
     
     return triples;
    }
    
    /**
     * Save data from repository into given file.
     * @param file
     * @throws CannotOverwriteFileException
     */
    public void save(File file) throws CannotOverwriteFileException {
    	file.mkdirs();
    	loadRDFfromRepositoryToXMLFile(file.getParent(), file.getName(), RDFFormat.NTRIPLES, true, false);
    }
    
    /**
     * Load data from given file into repository.
     * @param file
     */
    public void load(File file) {
    	extractRDFfromXMLFileToRepository(file.getAbsolutePath(), "", "", false);
    }
}

