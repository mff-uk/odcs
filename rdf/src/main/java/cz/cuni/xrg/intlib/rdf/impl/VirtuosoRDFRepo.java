package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoRDFRepo extends LocalRDFRepo implements RDFDataRepository {

	private String URL_Host_List;

	private String user;

	private String password;

	private String defaultGraph;
	
	private static AppConfig config;

	static {

		logger = LoggerFactory.getLogger(VirtuosoRDFRepo.class);
		config=new AppConfig();
	}

	public static VirtuosoRDFRepo createVirtuosoRDFRepo() {
				
		final String hostName = config.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
		final String port = config.getString(ConfigProperty.VIRTUOSO_PORT);
		final String user = config.getString(ConfigProperty.VIRTUOSO_USER);
		final String password = config.getString(ConfigProperty.VIRTUOSO_PASSWORD);
		final String defautGraph = config.getString(ConfigProperty.VIRTUOSO_DEFAULT_GRAPH);

		return createVirtuosoRDFRepo(hostName, port, user, password, defautGraph);
	}

	public static VirtuosoRDFRepo createVirtuosoRDFRepo(String hostName,
			String port, String user, String password, String defaultGraph) {
		final String JDBC = "jdbc:virtuoso://" + hostName + ":" + port + "/charset=UTF-8/log_enable=2";

		VirtuosoRDFRepo virtuosoRepo = new VirtuosoRDFRepo(JDBC, user, password,
				defaultGraph);
		return virtuosoRepo;
	}

	/**
	 * Construct a VirtuosoRepository with a specified parameters.
	 *
	 * @param URL_Host_List the Virtuoso JDBC URL connection string or hostlist
	 *                      for poolled connection.
	 *
	 * @param user          the database user on whose behalf the connection is
	 *                      being made.
	 *
	 * @param password      the user's password.
	 *
	 * @param defaultGraph  a default Graph name, used for Sesame calls, when
	 *                      contexts list is empty, exclude exportStatements,
	 *                      hasStatement, getStatements methods.
	 */
	public VirtuosoRDFRepo(String URL_Host_List, String user, String password,
			String defaultGraph) {

		this.URL_Host_List = URL_Host_List;
		this.user = user;
		this.password = password;
		this.defaultGraph = defaultGraph;

		graph = createNewGraph(defaultGraph);

		repository = new VirtuosoRepository(URL_Host_List, user, password,
				defaultGraph);

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
	 *         connection.
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

	public void setDefaultGraph(String defaultGraph) {
		this.defaultGraph = defaultGraph;
		setGraph(createNewGraph(defaultGraph));
	}

	public void setGraph(Resource graph) {
		this.graph = graph;
	}

	private VirtuosoRDFRepo getCopyOfVirtuosoReposiotory() {

		VirtuosoRDFRepo newCopy = new VirtuosoRDFRepo(URL_Host_List, user,
				password, defaultGraph);
		copyAllDataToTargetRepository(newCopy);

		return newCopy;
	}

	@Override
	public void release() {
		shutDown();
	}
}
