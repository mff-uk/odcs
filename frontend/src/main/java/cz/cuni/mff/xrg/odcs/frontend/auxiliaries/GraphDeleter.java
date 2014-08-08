package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.container.rdf.RepositoryFrontendHelper;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;

/**
 * Component for deleting the graphs for virtuoso.
 * 
 * @author Petyr
 */
public class GraphDeleter implements Runnable {

    private static String message = "-";

    private static GraphDeleter instance = null;

    /**
     * Lock object for operations.
     */
    private static final Object lock = new Object();

    private static boolean running = false;

    private GraphDeleter() {
        // we do not allow construction of this
    }

    @Override
    public void run() {
        RDFDataUnitFactory rdfDataUnitFactory = ((AppEntry) UI.getCurrent()).getBean(
                RDFDataUnitFactory.class);
        RepositoryConnection connection = null;

        try {
            ManagableRdfDataUnit repo =
                    //					RDFDataUnitHelper.getVirtuosoRepository("http://Virtuoso");
                    rdfDataUnitFactory.create("", "", "");
            connection = repo.getConnection();
            message = RepositoryFrontendHelper.deleteApplicationGraphs(connection,  RDFHelper.getGraphsURISet(repo));

        } catch (Throwable ex) {
            message = ex.toString();
        } finally {
            running = false;
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    message = ex.toString();
                }
            }

        }
    }

    /**
     * Return last message from last deletion.
     * 
     * @return Last message from last deletion.
     */
    public static String getMessage() {
        return message;
    }

    /**
     * True it the graphs are currently being deleted.
     * 
     * @return If currently deleting graphs.
     */
    public static boolean isRunning() {
        return running;
    }

    /**
     * Delete graphs.
     */
    public static void deleteGraphs() {
        synchronized (lock) {
            if (running == true) {
                // already running
                return;
            } else {
                // continue, start the execution
                running = true;
            }
        }

        // create new instance of runner if need
        if (instance == null) {
            instance = new GraphDeleter();
        }

        Thread thread = new Thread(instance, "Graph deleter");
        thread.setDaemon(true);
        // execute ..
        thread.start();
        // and return .. we get the message as the paralel execution ends
    }

}
