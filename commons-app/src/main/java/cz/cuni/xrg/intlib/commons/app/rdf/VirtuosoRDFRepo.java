package cz.cuni.xrg.intlib.commons.app.rdf;

import cz.cuni.xrg.intlib.commons.app.rdf.GraphNotEmptyException;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
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
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoRDFRepo extends LocalRDFRepo {

    private static VirtuosoRDFRepo virtuosoRepo = null;
    
    private String URL_Host_List;
    
    private String user;
    
    private String password;
    
    private String defaultGraph;
    
    private Resource graph;

    static {
        logger = LoggerFactory.getLogger(VirtuosoRDFRepo.class);
    }

    public static VirtuosoRDFRepo createVirtuosoRDFRepo() {
    	// TODO: Jirka: load from AppConfiguration .., ask Petyr about more details 
        final String hostName = "localhost";
        final String port = "1111";
        final String user = "dba";
        final String password = "dba";
        final String defautGraph = "http://default";

        return createVirtuosoRDFRepo(hostName, port, user, password, defautGraph);
    }

    public static VirtuosoRDFRepo createVirtuosoRDFRepo(String hostName, String port, String user, String password, String defaultGraph) {
        final String JDBC = "jdbc:virtuoso://" + hostName + ":" + port + "/charset=UTF-8/log_enable=2";

        virtuosoRepo = new VirtuosoRDFRepo(JDBC, user, password, defaultGraph);
        return virtuosoRepo;
    }

    /**
     * Construct a VirtuosoRepository with a specified parameters.
     *
     * @param URL_Host_List the Virtuoso JDBC URL connection string or hostlist
     * for poolled connection.
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
    public VirtuosoRDFRepo(String URL_Host_List, String user, String password, String defaultGraph) {

        this.URL_Host_List = URL_Host_List;
        this.user = user;
        this.password = password;
        this.defaultGraph = defaultGraph;

        graph = createNewGraph(defaultGraph);

        repository = new VirtuosoRepository(URL_Host_List, user, password, defaultGraph);

        try {
            repository.initialize();
            logger.info("Virtuoso repository successfully incicialized");

        } catch (RepositoryException ex) {
            logger.warn("Your Virtuoso is maybe turn off.");
            logger.debug(ex.getMessage());

        }
    }

    /**
     *
     * @return the Virtuoso JDBC URL connection string or hostlist for poolled
     * connection.
     */
    public String getURL_Host_List() {
        return URL_Host_List;
    }

    /**
     *
     * @return User name to Virtuoso connection.
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @return Password to virtuoso connection.
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return Default graph name
     */
    public String getDefaultGraph() {
        return defaultGraph;
    }

    /**
     *
     * @return defaultGraphURI
     */
    public Resource getGraph() {
        return graph;
    }

    private VirtuosoRDFRepo getCopyOfVirtuosoReposiotory() {

        VirtuosoRDFRepo newCopy = new VirtuosoRDFRepo(URL_Host_List, user, password, defaultGraph);
        copyAllDataToTargetRepository(newCopy);

        return newCopy;
    }

    @Override
    public void addTripleToRepository(String namespace, String subjectName, String predicateName, String objectName) {

        Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);
        addStatement(statement, graph);
    }

    private void addStatement(Statement statement, Resource... graphs) {

        try {

            RepositoryConnection conection = repository.getConnection();
            if (graphs != null) {
                conection.add(statement, graphs);
            } else {
                conection.add(statement);
            }

            conection.commit();
            conection.close();

        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());


        }

    }

    @Override
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
                if (graph != null) {
                    connection.add(inputStream, baseURI, format, graph);
                } else {
                    connection.add(inputStream, baseURI, format);
                }


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
                addFileToRepository(nextFile, baseURI, graph);
            }

        }
        if (dirFile.isFile()) {
            addFileToRepository(dirFile, baseURI, graph);
        }

    }

    private void addFileToRepository(File dataFile, String baseURI, Resource... graphs) {
        try {
            RDFFormat fileFormat = RDFFormat.forFileName(dataFile.getAbsolutePath(), RDFFormat.RDFXML);
            RepositoryConnection connection = repository.getConnection();

            if (graphs != null) {
                connection.add(dataFile, baseURI, fileFormat, graphs);
            } else {
                connection.add(dataFile, baseURI, fileFormat);
            }

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
     * @param canFileOverWrite
     * @throws CannotOverwriteFileException
     */
    @Override
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
                if (graph != null) {
                    connection.export(writer, graph);
                } else {
                    connection.export(writer);
                }


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

    @Override
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
                    if (graph != null) {
                        connection.add(inputStreamReader, endpointGraph, format, graph);
                    } else {
                        connection.add(inputStreamReader, endpointGraph, format);
                    }
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

    @Override
    public long getTripleCountInRepository() {
        long size = 0;

        if (repository != null) {
            try {
                RepositoryConnection connection = repository.getConnection();
                if (graph != null) {
                    size = connection.size(graph);
                } else {
                    size = connection.size();
                }
                connection.close();

            } catch (RepositoryException ex) {
                logger.debug(ex.getMessage());
            }

        }
        return size;
    }

    @Override
    public void cleanAllRepositoryData() {
        try {
            RepositoryConnection connection = repository.getConnection();
            if (graph != null) {
                connection.clear(graph);
            } else {
                connection.clear();
            }

            connection.commit();
            connection.close();

        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());
        }
    }

    @Override
    public boolean isTripleInRepository(String namespace, String subjectName,
            String predicateName, String objectName) {
        boolean hasTriple = false;

        if (repository != null) {
            try {
                Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);
                RepositoryConnection connection = repository.getConnection();
                if (graph != null) {
                    hasTriple = connection.hasStatement(statement, true, graph);
                } else {
                    hasTriple = connection.hasStatement(statement, true);
                }

                connection.close();

            } catch (RepositoryException ex) {
                logger.debug(ex.getMessage());
            }
        }
        return hasTriple;
    }

    @Override
    public List<Statement> getRepositoryStatements() {
        List<Statement> statemens = new ArrayList<>();

        if (repository != null) {
            try {
                RepositoryConnection connection = repository.getConnection();
                if (graph != null) {
                    statemens = connection.getStatements(null, null, null, true, graph).asList();
                } else {
                    statemens = connection.getStatements(null, null, null, true).asList();
                }

            } catch (RepositoryException ex) {
                logger.debug(ex.getMessage());
            }
        }

        return statemens;
    }

}
