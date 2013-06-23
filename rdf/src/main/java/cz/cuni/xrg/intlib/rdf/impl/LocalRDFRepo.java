
package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;

import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.GraphNotEmptyException;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class LocalRDFRepo implements RDFDataRepository, Closeable {

	/**
	 * Logging information about execution of method using openRDF.
	 */
	protected static Logger logger = LoggerFactory.getLogger(LocalRDFRepo.class);
	/**
	 * How many triples is possible to add to SPARQL endpoind at once.
	 */
	protected static final int STATEMENTS_COUNT = 10;
	/**
	 * Default name for temp directory, where this repository is placed.
	 */
	private final static String repoDirName = "intlib-repo";
	private final static String repoFileName = "localRepository";
	/**
	 * Default name for data file.
	 */
	private final static String dumpName = "dump_dat.ttl";
	/**
	 * Directory root, where is repository stored.
	 */
	private File WorkingRepoDirectory;
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
	 * Graph resource for saving RDF triples.
	 */
	protected Resource graph;

	/**
	 * Create local repository in default path.
	 *
	 * @return
	 */
	public static LocalRDFRepo createLocalRepo() {

		return LocalRDFRepo.createLocalRepoInTempDirectory(repoDirName, repoFileName);
	}

	/**
	 * Create temp directory "dirName", in this directory create file with
	 * "fileName" a there is repository stored.
	 *
	 * @param dirName
	 * @param fileName
	 * @return
	 */
	public static LocalRDFRepo createLocalRepoInTempDirectory(String dirName, String fileName) {
		Path repoPath = null;

		try {
			repoPath = Files.createTempDirectory(dirName);
		} catch (IOException e) {
			// TODO why not throw IOException?
			throw new RuntimeException(e);
		}

		return LocalRDFRepo.createLocalRepo(repoPath.toString(), fileName);
	}

	/**
	 * Create local repository in string path 'repoPath' in the file named
	 * 'fileName', where is repository stored.
	 *
	 * @param repoPath String path to directory where can be repository stored.
	 * @param fileName String file name, where is repository in directory
	 * stored.
	 * @return
	 */
	public static LocalRDFRepo createLocalRepo(String repoPath, String fileName) {
		LocalRDFRepo localrepo = new LocalRDFRepo(repoPath, fileName);
		return localrepo;
	}

	/**
	 * Empty constructor - used only for inheritance.
	 */
	protected LocalRDFRepo() {
	}

	/**
	 * Public constructor - create new instance of repository in defined
	 * repository Path.
	 *
	 * @param repositoryPath
	 * @param fileName
	 */
	public LocalRDFRepo(String repositoryPath, String fileName) {

		callConstructorSetting(repositoryPath, fileName);
	}

	private void callConstructorSetting(String repoPath, String fileName) {
		setReadOnly(false);

		long timeToStart = 1000L;
		File dataFile = new File(repoPath, fileName);
		MemoryStore memStore = new MemoryStore(dataFile);
		memStore.setPersist(true);
		memStore.setSyncDelay(timeToStart);

		repository = new SailRepository(memStore);

		graph = createNewGraph("http://default");
		WorkingRepoDirectory = dataFile.getParentFile();

		try {
			repository.initialize();
			logger.info("New repository incicialized");

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());

		}
	}

	protected Resource createNewGraph(String graphURI) {
		Resource newGraph = new URIImpl(graphURI);
		return newGraph;
	}

	protected void createNewFile(File file) {

		if (file == null) {
			return;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.debug(e.getMessage());
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
	@Override
	public void addTripleToRepository(String namespace, String subjectName, String predicateName, String objectName) {

		Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);
		addStatement(statement, graph);
	}

	protected void addStatement(Statement statement, Resource... graphs) {

		RepositoryConnection connection = null;

		try {

			connection = repository.getConnection();
			if (graphs != null) {

				connection.add(statement, graphs);
			} else {
				connection.add(statement);
			}

			connection.commit();

		} catch (RepositoryException e) {
			logger.debug(e.getMessage());


		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					throw new RuntimeException(ex);
				}
			}
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
	@Override
	public void extractRDFfromXMLFileToRepository(String path, String suffix, String baseURI, boolean useSuffix) throws ExtractException {

		if (path == null) {
			final String message = "Mandatory target path in extractor is null.";

			logger.debug(message);
			throw new ExtractException(message);

		} else if (path.isEmpty()) {

			final String message = "Mandatory target path in extractor have to be not empty.";

			logger.debug(message);
			throw new ExtractException(message);

		}

		File dirFile = new File(path);

		if (path.toLowerCase().startsWith("http")) {

			URL urlPath;
			try {
				urlPath = new URL(path);
			} catch (MalformedURLException ex) {
				throw new ExtractException(ex);
			}

			try (InputStreamReader inputStream = new InputStreamReader(urlPath.openStream(), encode)) {

				RDFFormat format = RDFFormat.forFileName(path, RDFFormat.RDFXML);
				RepositoryConnection connection = repository.getConnection();

				if (graph != null) {
					connection.add(inputStream, baseURI, format, graph);
				} else {
					connection.add(inputStream, baseURI, format);
				}

				inputStream.close();

			} catch (IOException | RepositoryException | RDFParseException ex) {
				throw new ExtractException(ex);
			}
		}

		if (dirFile.isDirectory()) {
			File[] files;

			if (useSuffix) {
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

	private void addFileToRepository(File dataFile, String baseURI, Resource... graphs) throws ExtractException {

		RDFFormat fileFormat = RDFFormat.forFileName(
				dataFile.getAbsolutePath(),
				RDFFormat.RDFXML);

		RepositoryConnection connection = null;

		try (InputStream is = new FileInputStream(dataFile)) {

			connection = repository.getConnection();

			if (graphs != null) {
				connection.add(is, baseURI, fileFormat, graph);
			} else {
				connection.add(is, baseURI, fileFormat);
			}

			connection.commit();

		} catch (IOException | RDFParseException ex) {
			logger.debug(ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			throw new ExtractException("Error by adding file to repository", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository.", ex);
				}
			}
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
	@Override
	public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName,
			org.openrdf.rio.RDFFormat format) throws CannotOverwriteFileException, LoadException {

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
	@Override
	public void loadRDFfromRepositoryToXMLFile(String directoryPath, String fileName, org.openrdf.rio.RDFFormat format,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, LoadException {

		if (directoryPath == null || fileName == null) {

			final String message;

			if (directoryPath == null) {
				message = "Mandatory directory path in File_loader is null.";
			} else {
				message = "Mandatory file name in File_loader is null.";
			}

			logger.debug(message);
			throw new LoadException(message);


		} else if (directoryPath.isEmpty() || fileName.isEmpty()) {

			final String message;

			if (directoryPath.isEmpty()) {
				message = "Mandatory directory path in File_loader is empty.";
			} else {
				message = "Mandatory file name in File_loader is empty.";
			}

			logger.debug(message);
			throw new LoadException(message);
		}

		final String slash = File.separator;

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

		RepositoryConnection connection = null;
		try (OutputStreamWriter os = new OutputStreamWriter(
				new FileOutputStream(dataFile.getAbsoluteFile()), encode)) {

			RDFWriter writer = Rio.createWriter(format, os);

			connection = repository.getConnection();

			if (graph != null) {
				connection.export(writer, graph);
			} else {
				connection.export(writer);
			}

			connection.commit();

		} catch (IOException | RDFHandlerException ex) {
			throw new LoadException(ex);
		} catch (RepositoryException ex) {
			throw new LoadException("Repository connection failed while trying to load into XML file.", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while trying to load into XML file.", ex);
				}
			}
		}

	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * without endpoint authentication.
	 *
	 * @param endpointURL
	 * @param defaultGraphURI
	 */
	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, WriteGraphType graphType) throws LoadException {
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
	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI, String name, String password, WriteGraphType graphType) throws LoadException {
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
	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, List<String> endpointGraphsURI, String userName,
			String password, WriteGraphType graphType) throws LoadException {

		if (endpointURL == null) {
			final String message = "Mandatory URL path in extractor from SPARQL is null.";

			logger.debug(message);
			throw new LoadException(message);

		} else if (!endpointURL.toString().toLowerCase().startsWith("http")) {

			final String message = "Mandatory URL path in extractor from SPARQL "
					+ "have to started with http.";

			logger.debug(message);
			throw new LoadException(message);

		}

		if (endpointGraphsURI == null) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is null.";

			logger.debug(message);
			throw new LoadException(message);

		} else if (endpointGraphsURI.isEmpty()) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is empty.";

			logger.debug(message);
			throw new LoadException(message);
		}

		final int graphSize = endpointGraphsURI.size();
		List<String> dataParts = getInsertPartsTriplesQuery(STATEMENTS_COUNT);
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
			logger.debug("Endpoint repository is failed");
			logger.debug(e.getMessage());

			throw new LoadException(e);
		}

		RepositoryConnection connection = null;

		try {

			connection = repository.getConnection();

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
				} catch (GraphNotEmptyException ex) {
					logger.debug(ex.getMessage());
					isOK = false;

					//throw new LoadException(ex);
				}

				if (isOK == false) {
					continue;
				}

				for (int j = 0; j < partsCount; j++) {

					final String endpointGraph = endpointGraphsURI.get(i).replace(" ", "+");
					final String query = dataParts.get(j);

					String myquery = null;

					try {
						myquery = URLEncoder.encode(query, encode);
					} catch (UnsupportedEncodingException ex) {
						String message = "Encoding " + encode + " is not supported.";
						throw new LoadException(message, ex);
					}

					URL call = null;

					try {
						call = new URL(endpointURL.toString() + "?default-graph-uri=" + endpointGraph + "&query=" + myquery);
					} catch (MalformedURLException e) {
						throw new LoadException("Malfolmed URL exception by construct load from URL", e);
					}

					HttpURLConnection httpConnection = null;

					try {

						httpConnection = (HttpURLConnection) call.openConnection();
						httpConnection.setRequestProperty("Content-type", "text/xml");

					} catch (IOException e) {
						if (httpConnection != null) {
							httpConnection.disconnect();
						}
						throw new LoadException("Endpoint URL stream cannot be opened", e);
					}

					try {
						// check whether given stream is readable
						BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
						reader.close();

						final String processing = String.valueOf(j + 1) + "/" + String.valueOf(partsCount);

						logger.debug("Data " + processing + " part loaded successful");
					} catch (IOException ex) {
						final String message = "Cannot open http connection stream at '"
								+ call.toString()
								+ "' - data not loaded";
						throw new LoadException(message, ex);

					} finally {
						httpConnection.disconnect();
					}

				}
			}

		} catch (RepositoryException ex) {
			throw new LoadException("Repository connection failed.", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository.", ex);
				}
			}

		}
	}

	@Override
	public List<Statement> getRepositoryStatements() {
		List<Statement> statemens = new ArrayList<>();

		if (repository != null) {
			RepositoryConnection connection = null;

			try {
				connection = repository.getConnection();

				if (graph != null) {
					statemens = connection.getStatements(null, null, null, true, graph).asList();
				} else {
					statemens = connection.getStatements(null, null, null, true).asList();
				}

			} catch (RepositoryException ex) {
				logger.debug(ex.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (RepositoryException ex) {
					}
				}
			}

		}

		return statemens;
	}

	protected List<String> getInsertPartsTriplesQuery(int sizeSplit) {

		final String insertStart = "INSERT {";
		final String insertStop = "} ";

		List<String> parts = new ArrayList<>();

		StringBuilder builder = new StringBuilder();

		List<Statement> statements = getRepositoryStatements();

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
	@Override
	public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query) throws ExtractException {
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
	@Override
	public void extractfromSPARQLEndpoint(URL endpointURL, String defaultGraphUri, String query, String hostName, String password, RDFFormat format) throws ExtractException {
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
	@Override
	public void extractfromSPARQLEndpoint(
			URL endpointURL,
			List<String> endpointGraphsURI,
			String query,
			String hostName,
			String password) throws ExtractException {

		if (endpointURL == null) {
			final String message = "Mandatory URL path in extractor from SPARQL is null.";

			logger.debug(message);
			throw new ExtractException(message);

		} else if (!endpointURL.toString().toLowerCase().startsWith("http")) {

			final String message = "Mandatory URL path in extractor from SPARQL "
					+ "have to started with http.";

			logger.debug(message);
			throw new ExtractException(message);

		}

		if (endpointGraphsURI == null) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is null.";

			logger.debug(message);
			throw new ExtractException(message);

		} else if (endpointGraphsURI.isEmpty()) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is empty.";

			logger.debug(message);
			throw new ExtractException(message);
		}

		if (query == null) {
			final String message = "Mandatory construct query in extractor from SPARQL is null.";
			logger.debug(message);
			throw new ExtractException(message);
		}

		final RDFFormat format = RDFFormat.N3;
		final int graphSize = endpointGraphsURI.size();
		final String myquery = query.replace(" ", "+");

		String encoder = null;

		try {
			encoder = URLEncoder.encode(format.getDefaultMIMEType(), encode);
		} catch (UnsupportedEncodingException e) {
			String message = "Encode " + encode + " is not support";
			logger.debug(message);
			throw new ExtractException(message, e);
		}

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();

			for (int i = 0; i < graphSize; i++) {

				final String endpointGraph = endpointGraphsURI.get(i).replace(" ", "+");

				URL call = null;
				try {
					call = new URL(endpointURL.toString() + "?default-graph-uri=" + endpointGraph + "&query=" + myquery + "&format=" + encoder);
				} catch (MalformedURLException e) {
					logger.debug("Malfolmed URL exception by construct extract URL");
					throw new ExtractException(e);
				}

				HttpURLConnection httpConnection = null;
				try {
					httpConnection = (HttpURLConnection) call.openConnection();
					httpConnection.addRequestProperty("Accept", format.getDefaultMIMEType());

				} catch (IOException e) {
					logger.debug("Endpoint URL stream can not open");
					if (httpConnection != null) {
						httpConnection.disconnect();
					}
					throw new ExtractException(e);
				}

				boolean usePassword = !(hostName.isEmpty() && password.isEmpty());

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

				} catch (IOException e) {

					final String message = "Http connection can can not open stream";
					logger.debug(message);

					throw new ExtractException(message, e);

				} catch (RDFParseException e) {
					logger.debug(e.getMessage());

					throw new ExtractException(e);

				}
			}


		} catch (RepositoryException e) {

			final String message = "Repository connection failt: " + e.getMessage();

			logger.debug(message);

			throw new ExtractException(e);

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while extracting from SPQRQL endpoint.", ex);
				}
			}
		}

	}

	private String AddGraphToUpdateQuery(String updateQuery) {

		if (repository instanceof SailRepository) {
			return updateQuery;
		}

		String regex = "(insert|delete)\\s\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(updateQuery.toLowerCase());

		boolean hasResult = matcher.find();

		if (hasResult) {

			int index = matcher.start();

			String first = updateQuery.substring(0, index);
			String second = updateQuery.substring(index, updateQuery.length());

			String graphName = " WITH <" + graph.stringValue() + "> ";

			String newQuery = first + graphName + second;
			return newQuery;


		}
		return updateQuery;


	}

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery
	 */
	@Override
	public void transformUsingSPARQL(String updateQuery) throws TransformException {

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();


			String newUpdateQuery = AddGraphToUpdateQuery(updateQuery);
			Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL,
					newUpdateQuery);


			logger.debug("This SPARQL query for transform is valid and prepared for execution:");
			logger.debug(newUpdateQuery);

			myupdate.execute();
			connection.commit();

			logger.debug("SPARQL query for transform was executed succesfully");

		} catch (MalformedQueryException e) {

			logger.debug(e.getMessage());
			throw new TransformException(e);

		} catch (UpdateExecutionException ex) {

			final String message = "SPARQL query was not executed !!!";
			logger.debug(message);
			logger.debug(ex.getMessage());

			throw new TransformException(message, ex);


		} catch (RepositoryException ex) {
			throw new TransformException("Connection to repository is not available.", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while executing SPARQL transform.", ex);
				}
			}
		}

	}

	/**
	 * Return count of triples stored in repository.
	 *
	 * @return size of triples in repository.
	 */
	@Override
	public long getTripleCountInRepository() {
		long size = 0;

		RepositoryConnection connection = null;

		try {
			connection = repository.getConnection();

			if (graph != null) {
				size = connection.size(graph);
			} else {
				size = connection.size();
			}

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while counting triples.", ex);
				}
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
	@Override
	public boolean isTripleInRepository(String namespace, String subjectName,
			String predicateName, String objectName) {
		boolean hasTriple = false;



		RepositoryConnection connection = null;
		Statement statement = createNewStatement(namespace, subjectName, predicateName, objectName);

		try {
			connection = repository.getConnection();

			if (graph != null) {
				hasTriple = connection.hasStatement(statement, true, graph);
			} else {
				hasTriple = connection.hasStatement(statement, true);
			}

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while looking for triple.", ex);
				}
			}
		}

		return hasTriple;
	}

	/**
	 * Removes all RDF data from repository.
	 */
	@Override
	public void cleanAllRepositoryData() {

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();

			if (graph != null) {
				connection.clear(graph);
			} else {
				connection.clear();
			}

			connection.commit();

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while cleaning up.", ex);
				}
			}
		}

	}

	@Override
	public void mergeRepositoryData(RDFDataRepository second) throws IllegalArgumentException {

		if (second == null) {
			throw new IllegalArgumentException("Instance of RDFDataRepository is null");
		}
		Repository secondRepository = second.getDataRepository();

		RepositoryConnection sourceConnection = null;
		RepositoryConnection targetConnection = null;

		try {
			sourceConnection = secondRepository.getConnection();

			if (!sourceConnection.isEmpty()) {

				List<Statement> sourceStatemens = second.getRepositoryStatements();

				targetConnection = repository.getConnection();

				if (targetConnection != null) {

					for (Statement nextStatement : sourceStatemens) {
						targetConnection.add(nextStatement);
					}
				}
			}
		} catch (RepositoryException ex) {
			logger.error(ex.getMessage());

		} finally {
			if (sourceConnection != null) {
				try {
					sourceConnection.close();
				} catch (RepositoryException ex) {
				}
			}
			if (targetConnection != null) {
				try {
					targetConnection.close();
				} catch (RepositoryException ex) {
				}
			}
		}
	}

	/**
	 * Add all data from repository to targetRepository.
	 *
	 * @param targetRepository
	 */
	@Override
	public void copyAllDataToTargetRepository(RDFDataRepository targetRepo) {

		if (targetRepo == null) {
			return;
		}
		Repository targetRepository = targetRepo.getDataRepository();

		RepositoryConnection sourceConnection = null;
		RepositoryConnection targetConnection = null;

		try {
			sourceConnection = repository.getConnection();

			if (!sourceConnection.isEmpty()) {

				List<Statement> sourceStatemens = this.getRepositoryStatements();

				targetConnection = targetRepository.getConnection();

				for (Statement nextStatement : sourceStatemens) {
					targetConnection.add(nextStatement);
				}

			}
		} catch (RepositoryException ex) {

			logger.debug(ex.getMessage());

		} finally {
			if (sourceConnection != null) {
				try {
					sourceConnection.close();
				} catch (RepositoryException ex) {
				}
			}
			if (targetConnection != null) {
				try {
					targetConnection.close();
				} catch (RepositoryException ex) {
				}
			}
		}

	}

	@Override
	public Repository getDataRepository() {
		return repository;
	}

	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public List<RDFTriple> getRDFTriplesInRepository() {

		List<RDFTriple> triples = new ArrayList<>();
		List<Statement> statements = getRepositoryStatements();

		int count = 0;

		for (Statement next : statements) {
			String subject = next.getSubject().stringValue();
			String predicate = next.getPredicate().stringValue();
			String object = next.getObject().stringValue();

			count++;

			RDFTriple triple = new RDFTriple(count, subject, predicate, object);
			triples.add(triple);
		}

		return triples;
	}

	/**
	 * Save data from repository into given file.
	 *
	 * @param file
	 */
	@Override
	public void save(File file) {

		RDFFormat format = RDFFormat.forFileName(file.getAbsolutePath(), RDFFormat.RDFXML);

		File directory = file.getParentFile();

		if (!directory.exists()) {
			directory.mkdirs();
		}

		logger.debug("saving directory:" + directory.toString());
		logger.debug("saving fileName:" + file.getName());

		try {
			loadRDFfromRepositoryToXMLFile(directory.toString(), file.getName(), format, true, false);
		} catch (CannotOverwriteFileException | LoadException e) {
			throw new RuntimeException(e);
		}
	}

	public File getWorkingRepoDirectory() {
		return WorkingRepoDirectory;
	}

	/**
	 * Load data from given file into repository.
	 *
	 * @param file
	 */
	@Override
	public void load(File directory) {
		File file = new File(directory, dumpName);
		try {
			extractRDFfromXMLFileToRepository(file.getAbsolutePath(), "", "", false);
		} catch (ExtractException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Definitely destroy repository - use after all working in repository.
	 * Another repository using cause exception. For other using you have to
	 * create new instance.
	 */
	public void shutDown() {

		Thread destroyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					repository.shutDown();
					logger.debug("Repository destroyed SUCCESSFULL");
				} catch (RepositoryException ex) {
					logger.debug("Repository was not destroyed - potencial problems with locks ");
					logger.debug(ex.getMessage());
				}
			}
		});

		destroyThread.setDaemon(true);
		destroyThread.start();
	}

	/**
	 * Make query over repository data and return tables as result.
	 *
	 * @param query String representation of query
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 * map key is column name and <code>List&lt;String&gt;</code> are string
	 * values in this column. When query is invalid, return
	 * empty <code>Map</code>.
	 */
	@Override
	public Map<String, List<String>> makeQueryOverRepository(String query) throws InvalidQueryException {

		Map<String, List<String>> map = new HashMap<>();
		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();
			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

			logger.debug("Query " + query + " is valid.");

			TupleQueryResult result = null;
			List<BindingSet> listBindings = new ArrayList<>();
			try {
				result = tupleQuery.evaluate();
				logger.debug("Query " + query + " has not null result.");
				List<String> names = result.getBindingNames();

				for (String name : names) {
					map.put(name, new LinkedList<String>());
				}

				listBindings = result.asList();
			} catch (QueryEvaluationException ex) {
				throw new InvalidQueryException("This query is probably not valid", ex);
			} finally {
				if (result != null) {
					try {
						result.close();
					} catch (QueryEvaluationException ex) {
						logger.warn("Failed to close RDF tuple result.", ex);
					}
				}
			}

			for (BindingSet bindingNextSet : listBindings) {
				for (Binding next : bindingNextSet) {

					String name = next.getName();
					String value = next.getValue().stringValue();

					if (map.containsKey(name)) {
						map.get(name).add(value);
					}

				}
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException("This query is probably not valid", ex);
		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed.", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository while querying.", ex);
				}
			}
		}

		return map;
	}

	/**
	 * {@inheritDoc #shutDown}
	 */
	@Override
	public void close() throws IOException {
		shutDown();
	}
	
	/*public static DataUnit createNewInstance(String id,File workingDir,boolean mergePrepare)
	{
		
	}*/

	@Override
	public void madeReadOnly() {
		setReadOnly(true);
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {

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
	public DataUnitType getType() {
		return DataUnitType.RDF_Local;
	}

	@Override
	public void release() {
		logger.info("Releasing DPU LocalRdf: {}", WorkingRepoDirectory.toString());
		shutDown();
		logger.info("Relelased LocalRdf: {}", WorkingRepoDirectory.toString());
	}

	@Override
	public void save() throws Exception {
		File file = new File(WorkingRepoDirectory, dumpName);
		save(file);
	}

}

/*
class ChunkCommitter implements RDFHandler {

    private RDFInserter inserter;
    private RepositoryConnection connection;
    private long count = 0L;
    
    private long chunksize = 500000L;

    public ChunkCommitter(RepositoryConnection connection) {
        inserter = new RDFInserter(connection);
        this.connection = connection;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        inserter.startRDF();

    }

    @Override
    public void endRDF() throws RDFHandlerException {
        inserter.endRDF();

    }

    @Override
    public void handleNamespace(String prefix, String uri)
            throws RDFHandlerException {

        inserter.handleNamespace(prefix, uri);

    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        inserter.handleStatement(st);
        count++;
        // do an intermittent commit whenever the number of triples
        // has reached a multiple of the chunk size
        if (count % chunksize == 0) {
            try {
                connection.commit();
            } catch (RepositoryException e) {
                throw new RDFHandlerException(e);

            }

        }

    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        inserter.handleComment(comment);

    }
*/