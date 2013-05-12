package cz.cuni.xrg.intlib.backend.data.rdf;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoRDFRepo extends LocalRDFRepo implements RDFDataRepository {

    private static VirtuosoRDFRepo virtuosoRepo = null;
    
    private String URL_Host_List;
    private String user;
    private String password;
    private String defaultGraph;
    
    static
    {
        logger=LoggerFactory.getLogger(VirtuosoRDFRepo.class);
    }
    
    public static VirtuosoRDFRepo createVirtuosoRDFRepo()
    {
        final String hostName="localhost";
        final String port="1111";
        final String user="dba";
        final String password="dba";
        final String defautGraph="";
        
        return createVirtuosoRDFRepo(hostName,port,user, password, defautGraph);
        
    }
    
    public static VirtuosoRDFRepo createVirtuosoRDFRepo(String hostName,String port,String user,String password,String defaultGraph)
    {
        final String JDBC="jdbc:virtuoso://"+hostName+":"+port;
        return createVirtuosoRDFRepo(JDBC, user, password, defaultGraph);
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

        this.URL_Host_List=URL_Host_List;
        this.user=user;
        this.password=password;
        this.defaultGraph=defaultGraph;
        
        //repository = new VirtuosoRepository(URL_Host_List, user, password,defaultGraph);

        try {
            repository.initialize();
            logger.info("Virtuoso repository incicialized");

        } catch (RepositoryException ex) {
            logger.debug(ex.getMessage());

        }
    }

    /**
     * 
     * @return the Virtuoso JDBC URL connection string or hostlist
     * for poolled connection.
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
    
    private VirtuosoRDFRepo getCopyOfVirtuosoReposiotory()
    {
        VirtuosoRDFRepo copy=new VirtuosoRDFRepo(URL_Host_List, user, password, defaultGraph);
        copyAllDataToTargetRepository(copy.getDataRepository());
        
        return copy;
    }

    
    /**
     * Creates read only copy of instance Virtuoso repository.
     */
    @Override
    public DataUnit createReadOnlyCopy() {
       VirtuosoRDFRepo newCopy=getCopyOfVirtuosoReposiotory();
       newCopy.setReadOnly(true);
       
       return newCopy;
    }
    

   
    
    
    
    
    
    
    
    
}
