package cz.cuni.xrg.intlib.backend.data.rdf;

import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoRDFRepo extends LocalRDFRepo implements RDFDataRepository {

    private static VirtuosoRDFRepo virtuosoRepo = null;
    /**
     * Logging information about execution of method using openRDF.
     */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(VirtuosoRDFRepo.class);

    public static VirtuosoRDFRepo createVirtuosoRDFRepo()
    {
        final String hostName="localhost";
        final String port="1111";
        final String JDBC="jdbc:virtuoso://"+hostName+":"+port;
        
        final String user="dba";
        final String password="dba";
        final String defautGraph="";
        
        virtuosoRepo=createVirtuosoRDFRepo(JDBC,user, password, defautGraph);
        
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
     *
     */
    public static VirtuosoRDFRepo createVirtuosoRDFRepo(String URL_Host_List, String user, String password, String defaultGraph) {
        if (virtuosoRepo == null) {
            virtuosoRepo = new VirtuosoRDFRepo(URL_Host_List, user, password, defaultGraph);
        }
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

        //repository = new VirtuosoRepository(URL_Host_List, user, password,defaultGraph);

        try {
            repository.initialize();
            logger.info("Virtuoso repository incicialized");

        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());

        }
    }
    
}
