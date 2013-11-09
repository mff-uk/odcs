package cz.cuni.mff.xrg.odcs.rdf;

/**
 * Help class for creating graph's url.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class GraphUrl {

	private static final String prefix = "http://linked.opendata.cz/resource/odcs/internal/pipeline/";

	private GraphUrl() {
	}

	/**
	 * Translate the dataUnit id in to graph url format.
	 *
	 * @param dataUnitId
	 * @return
	 */
	public static String translateDataUnitId(String dataUnitId) {
		return prefix + dataUnitId.replace('_', '/');
	}

	public static String getGraphPrefix() {
		return prefix;
	}
}
