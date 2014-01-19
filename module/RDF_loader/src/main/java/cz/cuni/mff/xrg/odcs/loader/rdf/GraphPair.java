package cz.cuni.mff.xrg.odcs.loader.rdf;

/**
 * Responsible for keeping pairs (graph_name,temp_graph_name).
 *
 * @author Jiri Tomes
 */
public class GraphPair {

	private String graphName;

	private String tempGraphName;

	/**
	 * Create new graph pair based on given parameters.
	 *
	 * @param graphName           String value of graph name (URI type)
	 * @param tempGraphNameString String value of temp graph name (URI type)
	 */
	public GraphPair(String graphName, String tempGraphName) {
		this.graphName = graphName;
		this.tempGraphName = tempGraphName;
	}

	/**
	 * Returns String value of graph name (URI type).
	 *
	 * @return String value of graph name (URI type).
	 */
	public String getGraphName() {
		return graphName;
	}

	/**
	 * Returns String value of temp graph name (URI type).
	 *
	 * @return String value of temp graph name (URI type).
	 */
	public String getTempGraphName() {
		return tempGraphName;
	}
}
