package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.help.PlaceHolder;

/**
 * This class allows for SPARQL CONSTRUCT/UPDATE queries possible to replace
 * "graph ?g_XX" (XX is DPU name of used graph, which name we need to replace in
 * query) in WHERE clause to original URI thanks used graph mapping.
 * (for example: XX -> http://myGraph).
 * If mapping between DPU name and graph URI (graph name) don´t exist, in query
 * is used generated temp graph name as http://graphForDataUnit_XXXX, where XXXX
 * is the name of DPU.
 * Example:
 * SPARQL construct query:
 * construct {?s ?p ?o. ?s ?p ?y} where { graph ?g_AA {?s ?p ?o} graph
 * ?G_ABECEDA {?s ?p ?y}}
 * DPU names for graph we need to replace:
 * AA, ABECEDA
 * Mapping of DPU names to original URIs (graph name):
 * AA -> http://myGraph1 ABECEDA -> http://myGraph2
 * Modified query after using placeholders (with original URI graph names):
 * construct {?s ?p ?o. ?s ?p ?y} where { graph
 * <http://myGraph1> {?s ?p ?o} graph <http://myGraph2> {?s ?p ?y}}
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
     * Returns true if some of repository in collection of {@link RDFDataUnit} need for SPARQL construct/update query based on DPU name is type of
     * {@link DataUnitType#RDF_Local}, false otherwise.
     * If method returns true, is necessary to create temp repository(calling
     * method {@link #getExecutableTempRepository()}, where this SPARQL
     * construct/update query can be executed.
     * 
     * @return true if some of repository in collection of {@link RDFDataUnit} need for SPARQL construct/update query based on DPU name is type
     *         of {@link DataUnitType#RDF_Local}, false otherwise.
     */
//	TODO michal.klempa this should not be needed anymore
//	public boolean needExecutableRepository() {
//		boolean hasLocal = false;
//		for (RDFDataUnit nextRepository : usedRepositories) {
//			if (nextRepository.getType() == DataUnitType.RDF_Local) {
//				hasLocal = true;
//				break;
//			}
//		}
//		return hasLocal;
//	}

    /**
     * Create new instance of {@link ManagableRdfDataUnit} where SPARQL
     * construct/update query based on DPU name can be executed. Need only if
     * method {@link #needExecutableRepository()} returns TRUE;
     * 
     * @return new instance of {@link ManagableRdfDataUnit} where SPARQL
     *         construct/update query based on DPU name can be executed. Need
     *         only if method {@link #needExecutableRepository()} returns TRUE;
     */
//	TODO michal.klempa this should not be needed anymore
//	public ManagableRdfDataUnit getExecutableTempRepository() throws RepositoryException {
//		LocalRDFDataUnit tempRepository = RDFDataUnitFactory.createLocalRDFRepo(
//				"executable");
//        RepositoryConnection tempRepositoryConnection = tempRepository.getConnection();
//        for (RDFDataUnit repository : usedRepositories) {
//            URI dataGraph = repository.getDataGraph();
//            RepositoryConnection usedRepositoryConnection = repository.getConnection();
//            RepositoryResult<Statement> triples = usedRepositoryConnection.getStatements(null, null, null, true, repository.getDataGraph());
//            tempRepositoryConnection.add(triples, dataGraph);
//		}
//		return tempRepository;
//	}

    /**
     * @param query
     *            original SPARQL construct/update query where we can find DPU
     *            names need for graph names(graph URI).
     * @return List as collection of DPUNames - each keep DPU name as string
     *         extracted from SPARQL query.
     */
    public List<String> getExtractedDPUNames(String query) {

        String regex = "graph\\s+\\?[gG]_[\\w-_]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        boolean hasResult = matcher.find();

        List<String> DPUNames = new ArrayList<>();

        while (hasResult) {

            int start = matcher.start();
            int end = matcher.end();

            int partIndex = query.substring(start, end).indexOf("_") + 1;

            start += partIndex;

            String DPUName = query.substring(start, end);

            DPUNames.add(DPUName);

            hasResult = matcher.find();
        }

        return DPUNames;
    }

    /**
     * @param query
     *            original SPARQL construct/update query where we can replace
     *            DPU names to graph names(graph URI).
     * @return List as collection of PlaceHolder - each keep DPU name extracted
     *         from SPARQL query.
     */
    private List<PlaceHolder> getPlaceHolders(String query) {

        List<String> DPUNames = getExtractedDPUNames(query);

        List<PlaceHolder> placeholders = new ArrayList<>();

        for (String dpuName : DPUNames) {
            PlaceHolder placeHolder = new PlaceHolder(dpuName);
            placeholders.add(placeHolder);
        }

        return placeholders;
    }

    /**
     * @param inputs
     *            Set of RDFDataUnit for checking if DPU name in query
     *            correspondent to at least one DPU name in given
     *            inputs. It is necessary for mapping DPU_name -> graph
     *            URI.
     * @param placeHolders
     *            List as collection of PlaceHolder - each keep DPU
     *            name extracted from SPARQL query.
     * @throws DPUException
     *             if DPU name in placeHolders in not in any DPU names
     *             used in application - there can not exist mapping
     *             DPU name in query to graph URI (graph name).
     */
    private void replaceAllPlaceHolders(List<RDFDataUnit> inputs,
            List<PlaceHolder> placeHolders) throws DPUException {

        for (PlaceHolder next : placeHolders) {
            boolean isReplased = false;

            for (RDFDataUnit input : inputs) {
                if (input.getDataUnitName().equals(next.getDPUName())) {
                    usedRepositories.add(input);
                    //set RIGHT data graph for DPU
                    next.setGraphName(input.getContexts().toString());
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
     * Returns modified SPARQL construct/update query after using placeholders
     * (with original URI graph names).
     * 
     * @param originalQuery
     *            Original SPARQL construct/update query where we can
     *            replace DPU names to graph names(graph URI).
     * @param inputs
     *            Set of RDFDataUnit for checking if DPU name in query
     *            correspondent to at least one DPU name in given
     *            inputs. Is necessary for mapping DPU_name -> graph
     *            URI.
     * @return Modified SPARQL construct/update query after using placeholders
     *         (with original URI graph names).
     * @throws DPUException
     *             if there can not exist mapping DPU name in query to
     *             graph URI (graph name).
     */
    public String getReplacedQuery(String originalQuery,
            List<RDFDataUnit> inputs) throws DPUException {

        String result = originalQuery;

        List<PlaceHolder> placeHolders = getPlaceHolders(originalQuery);

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
