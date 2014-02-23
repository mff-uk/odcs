package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;

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
		try {
			ManagableRdfDataUnit repo
					= RDFDataUnitHelper.getVirtuosoRepository("http://Virtuoso");
			message = repo.deleteApplicationGraphs();
		} finally {
			running = false;
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
