package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.help.LazyTriples;
import cz.cuni.mff.xrg.odcs.rdf.help.PlaceHolder;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * It allows for SPARQL CONSTRUCT queries to replace "graph ?g_XX" (XX is DPU
 * name used graph, which name we need to replace in query) in WHERE SPARQL part
 * to original URI of this used graph mapping
 *
 * (for example: XX -> http://myGraph).
 *
 * If mapping between DPU name and graph URI (graph name) not exist, in query is
 * used generated temp graph name as http://graphForDataUnit_XXXX, where XXXX is
 * name of DPU.
 *
 * Example:
 *
 * SPARQL contruct query:
 *
 * construct {?s ?p ?o. ?s ?p ?y} where { graph ?g_AA {?s ?p ?o} graph
 * ?G_ABECEDA {?s ?p ?y}}
 *
 * DPU names for graph we need to replace:
 *
 * AA, ABECEDA
 *
 * Mapping of DPU names to original URIs (graph name):
 *
 * AA -> http://myGraph1 ABECEDA -> http://myGraph2
 *
 * Modified query after using placeholders (with original URI graph names):
 *
 * construct {?s ?p ?o. ?s ?p ?y} where { graph
 * <http://myGraph1> {?s ?p ?o} graph <http://myGraph2> {?s ?p ?y}}
 *
 * In case when DPU name ABECEDA has not graph URI mapping we get modified query
 * like: construct {?s ?p ?o. ?s ?p ?y} where { graph <http://myGraph1> {?s ?p
 * ?o} graph <http://graphForDataUnit_ABECEDA> {?s ?p ?y}}
 *
 * @author Jiri Tomes
 */
public class PlaceholdersHelper {

	/**
	 * Given DPU Context used in case for sending message if not finding mapping
	 * between DPU names keep in placeHolders and really used DPU names in
	 * application.
	 *
	 */
	private DPUContext context;

	private List<RDFDataUnit> usedRepositories = new LinkedList<>();

	public PlaceholdersHelper() {
		this.context = null;
	}

	public PlaceholdersHelper(DPUContext context) {
		this.context = context;
	}

	/**
	 * @return true if some of repository in collection of {@link RDFDataUnit}
	 *         need for 'special' SPARQL contruct query based on DPU name is
	 *         type of {@link DataUnitType#RDF_Local}, false otherwise. If
	 *         method returns true, is necessary to create temp
	 *         repository(calling method {@link #getExecutableTempRepository()},
	 *         where this 'special' SPARQL contruct can be executed.
	 */
	public boolean needExecutableRepository() {
		boolean hasLocal = false;
		for (RDFDataUnit nextRepository : usedRepositories) {
			if (nextRepository.getType() == DataUnitType.RDF_Local) {
				hasLocal = true;
				break;
			}
		}
		return hasLocal;
	}

	/**
	 * Create new instance of {@link ManagableRdfDataUnit} where 'special'
	 * SPARQL contruct based on DPU name can be executed. Need only if method
	 * {@link #needExecutableRepository()} returns TRUE;
	 *
	 * @return new instance of {@link ManagableRdfDataUnit} where 'special'
	 *         SPARQL contruct based on DPU name can be executed. Need only if
	 *         method {@link #needExecutableRepository()} returns TRUE;
	 */
	public ManagableRdfDataUnit getExecutableTempRepository() {
		LocalRDFRepo tempRepository = RDFDataUnitFactory.createLocalRDFRepo(
				"executable");
		for (RDFDataUnit repository : usedRepositories) {
			URI dataGraph = repository.getDataGraph();
			LazyTriples iterator = repository.getTriplesIterator();

			while (iterator.hasNextTriples()) {
				List<Statement> triples = iterator.getTriples();
				tempRepository.addStatementsToGraph(triples, dataGraph);
			}
		}
		return tempRepository;
	}

	/**
	 *
	 * @param constructQuery original SPARQL contruct query where we can find
	 *                       DPU names to graph names(graph URI).
	 * @return List as collection of DPUNames - each keep String DPU name
	 *         extracted from SPARQL query.
	 */
	public List<String> getExtractedDPUNames(String constructQuery) {

		String regex = "graph\\s+\\?[gG]_[\\w-_]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(constructQuery);

		boolean hasResult = matcher.find();

		List<String> DPUNames = new ArrayList<>();

		while (hasResult) {

			int start = matcher.start();
			int end = matcher.end();

			int partIndex = constructQuery.substring(start, end).indexOf("_") + 1;

			start += partIndex;

			String DPUName = constructQuery.substring(start, end);

			DPUNames.add(DPUName);

			hasResult = matcher.find();
		}

		return DPUNames;
	}

	/**
	 *
	 * @param constructQuery original SPARQL contruct query where we can replace
	 *                       DPU names to graph names(graph URI).
	 * @return List as collection of PlaceHolder - each keep DPU name extracted
	 *         from SPARQL query.
	 */
	private List<PlaceHolder> getPlaceHolders(String constructQuery) {

		List<String> DPUNames = getExtractedDPUNames(constructQuery);


		List<PlaceHolder> placeholders = new ArrayList<>();

		for (String dpuName : DPUNames) {
			PlaceHolder placeHolder = new PlaceHolder(dpuName);
			placeholders.add(placeHolder);
		}

		return placeholders;
	}

	/**
	 *
	 * @param inputs       Set of RDFDataUnit for checking if DPU name in query
	 *                     correspondent to at least one DPU name in given
	 *                     inputs. Is necessary for mapping DPU_name -> graph
	 *                     URI.
	 * @param placeHolders List as collection of PlaceHolder - each keep DPU
	 *                     name extracted from SPARQL query.
	 * @throws DPUException if DPU name in placeHolders in not in any DPU names
	 *                      used in application - there can not exist mapping
	 *                      DPU name in query to graph URI (graph name).
	 */
	private void replaceAllPlaceHolders(List<RDFDataUnit> inputs,
			List<PlaceHolder> placeHolders) throws DPUException {

		for (PlaceHolder next : placeHolders) {
			boolean isReplased = false;

			for (RDFDataUnit input : inputs) {
				if (input.getDataUnitName().equals(next.getDPUName())) {
					usedRepositories.add(input);
					//set RIGHT data graph for DPU
					next.setGraphName(input.getDataGraph().toString());
					isReplased = true;

					break;
				}
			}

			if (!isReplased) {
				usedRepositories.clear();
				String DPUName = next.getDPUName();
				final String message = "Graph for DPU name " + DPUName + " was not replased";

				if (context != null) {
					context.sendMessage(MessageType.ERROR, message);
				}
				throw new DPUException(message);
			}

		}

	}

	/**
	 *
	 * @param originalConstructQuery Original SPARQL contruct query where we can
	 *                               replace DPU names to graph names(graph
	 *                               URI).
	 * @param inputs                 Set of RDFDataUnit for checking if DPU name
	 *                               in query correspondent to at least one DPU
	 *                               name in given inputs. Is necessary for
	 *                               mapping DPU_name -> graph URI.
	 * @return Modified query after using placeholders (with original URI graph
	 *         names)
	 * @throws DPUException if there can not exist mapping DPU name in query to
	 *                      graph URI (graph name).
	 */
	public String getContructQuery(String originalConstructQuery,
			List<RDFDataUnit> inputs) throws DPUException {

		String result = originalConstructQuery;

		List<PlaceHolder> placeHolders = getPlaceHolders(originalConstructQuery);

		if (!placeHolders.isEmpty()) {
			replaceAllPlaceHolders(inputs, placeHolders);
		}

		for (PlaceHolder next : placeHolders) {

			String graphName = "<" + next.getGraphName() + ">";

			result = result.replaceAll("\\?[g|G]_" + next
					.getDPUName(), graphName);
		}

		return result;
	}
}
