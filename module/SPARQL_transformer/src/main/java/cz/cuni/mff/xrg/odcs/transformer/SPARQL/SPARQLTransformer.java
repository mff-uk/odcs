package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.CleverDataset;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;

/**
 * SPARQL Transformer.
 *
 * @author Jiri Tomes
 * @author Petyr
 * @author tknap
 */
@DPU.AsTransformer
public class SPARQLTransformer
        extends ConfigurableBase<SPARQLTransformerConfig>
        implements ConfigDialogProvider<SPARQLTransformerConfig> {

    private final Logger LOG = LoggerFactory.getLogger(SPARQLTransformer.class);

    public static final String[] DPUNames = {"input", "optional1", "optional2", "optional3"};

    /**
     * The repository input for SPARQL transformer.
     */
    @DataUnit.AsInput(name = "input")
    public RDFDataUnit intputDataUnit;

    //three other optional inputs, which may be used in the queries
    /**
     * The first repository optional input for SPARQL transformer.
     */
    @DataUnit.AsInput(name = "optional1", optional = true)
    public RDFDataUnit intputOptional1;

    /**
     * The second repository optional input for SPARQL transformer.
     */
    @DataUnit.AsInput(name = "optional2", optional = true)
    public RDFDataUnit intputOptional2;

    /**
     * The third repository optional input for SPARQL transformer.
     */
    @DataUnit.AsInput(name = "optional3", optional = true)
    public RDFDataUnit intputOptional3;

    /**
     * The repository output for SPARQL transformer.
     */
    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outputDataUnit;

    public SPARQLTransformer() {
        super(SPARQLTransformerConfig.class);
    }

    private Dataset createGraphDataSet(List<RDFDataUnit> inputs) throws DataUnitException {
        CleverDataset dataSet = new CleverDataset();

        for (RDFDataUnit repository : inputs) {
            if (repository != null) {
                dataSet.addDefaultGraphs(repository.getDataGraphnames());
                dataSet.addNamedGraphs(repository.getDataGraphnames());
            }
        }
        return dataSet;
    }

    private List<RDFDataUnit> getInputs() {
        List<RDFDataUnit> inputs = new ArrayList<>();

        addInput(inputs, intputDataUnit);
        addInput(inputs, intputOptional1);
        addInput(inputs, intputOptional2);
        addInput(inputs, intputOptional3);

        return inputs;
    }

    private void addInput(List<RDFDataUnit> inputs, RDFDataUnit nextInput) {
        if (inputs != null && nextInput != null) {
            inputs.add(nextInput);
        }
    }

    /**
     * Execute the SPARQL transformer.
     *
     * @param context SPARQL transformer context.
     * @throws DPUException if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException {

        //GET ALL possible inputs
        List<RDFDataUnit> inputs = getInputs();

        final List<SPARQLQueryPair> queryPairs = config.getQueryPairs();

        if (queryPairs == null) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "All queries for SPARQL transformer are null values");
        } else {
            if (queryPairs.isEmpty()) {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Queries for SPARQL transformer are empty",
                        "SPARQL transformer must constains at least one SPARQL query");
            }
        }

        //if merge input - depend on type of quries.
        boolean isFirstUpdateQuery = true;

        int queryCount = 0;

        for (SPARQLQueryPair nextPair : queryPairs) {

            queryCount++;
            String updateQuery = nextPair.getSPARQLQuery();
            boolean isConstructQuery = nextPair.isConstructType();

            if (updateQuery == null) {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Query number " + queryCount + " is not defined");
            } else if (updateQuery.trim().isEmpty()) {
                context.sendMessage(DPUContext.MessageType.ERROR,
                        "Query number " + queryCount + " is not defined",
                        "SPARQL transformer must constain at least one SPARQL (Update) query");
            }

            try {
                if (isConstructQuery) {
                    isFirstUpdateQuery = false;
                    //creating newConstruct replaced query
                    PlaceholdersHelper placeHolders = new PlaceholdersHelper(
                            context);
                    String constructQuery = placeHolders.getReplacedQuery(
                            updateQuery,
                            inputs);

                    //execute given construct query
                    Dataset dataSet = createGraphDataSet(inputs);
//	TODO michal.klempa this should not be needed anymore
//					if (placeHolders.needExecutableRepository()) {
//						ManagableRdfDataUnit tempDataUnit = placeHolders
//								.getExecutableTempRepository();
//						Graph graph = tempDataUnit.executeConstructQuery(
//								constructQuery, dataSet);
//
//						((ManagableRdfDataUnit) outputDataUnit)
//								.addTriplesFromGraph(graph);
//
//						tempDataUnit.clear();
//						tempDataUnit.release();
//
//					} else {

                    RepositoryConnection connectionInput = null;
                    Graph graph = null;
                    try {
                        connectionInput = intputDataUnit.getConnection();
                        graph = executeConstructQuery(connectionInput, constructQuery, dataSet);
                    } catch ( DataUnitException ex) {
                        LOG.error("Could not add triples from graph", ex);
                    } finally {
                        if (connectionInput != null) {
                            try {
                                connectionInput.close();
                            } catch (RepositoryException ex) {
                                context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                            }
                        }
                    }

                    if (graph != null) {
                        RepositoryConnection connection = null;
                        try {
                            connection = outputDataUnit.getConnection();
                            connection.add(graph, outputDataUnit.getWriteDataGraph());
                        } catch (RepositoryException | DataUnitException ex) {
                            LOG.error("Could not add triples from graph", ex);

                        } finally {
                            if (connection != null) {
                                try {
                                    connection.close();
                                } catch (RepositoryException ex) {
                                    context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                                }
                            }
                        }
                    }
//					}

                } else {

                    PlaceholdersHelper placeHolders = new PlaceholdersHelper(
                            context);

                    String replacedUpdateQuery = placeHolders.getReplacedQuery(
                            updateQuery,
                            inputs);

//					TODO michal.klempa this should not be needed anymore
//					boolean needRepository = placeHolders
//							.needExecutableRepository();
                    if (isFirstUpdateQuery) {

                        isFirstUpdateQuery = false;

//						TODO michal.klempa this should not be needed anymore
//						if (needRepository) {
                        prepareRepository(inputs);
//						} else {
//							((ManagableRdfDataUnit) outputDataUnit)
//									.merge(intputDataUnit);
//							TODO michal.klempa this should not be needed anymore
//						}

                    }

//					TODO michal.klempa this should not be needed anymore
//					if (needRepository) {
                    CleverDataset dataset = new CleverDataset();
                    dataset.addDefaultGraph(outputDataUnit.getWriteDataGraph());
                    dataset.addNamedGraph(outputDataUnit.getWriteDataGraph());

                    RepositoryConnection connection = null;
                    try {
                        connection = outputDataUnit.getConnection();
                        executeSPARQLUpdateQuery(connection, replacedUpdateQuery, dataset, outputDataUnit.getWriteDataGraph());

                    } catch (DataUnitException ex) {
                        LOG.error("Could not add triples from graph", ex);

                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (RepositoryException ex) {
                                context.sendMessage(DPUContext.MessageType.WARNING,
                                        ex.getMessage(), ex.fillInStackTrace().toString());
                            }
                        }
                    }

//					} else {
//						outputDataUnit.executeSPARQLUpdateQuery(
//								replacedUpdateQuery);
//						TODO michal.klempa this should not be needed anymore
//					}
                }

            } catch (DataUnitException ex) {
                context.sendMessage(DPUContext.MessageType.ERROR, ex.getMessage(), ex
                        .fillInStackTrace().toString());
//				TODO michal.klempa this should not be needed anymore
//			} catch (RepositoryException e) {
//                context.sendMessage(MessageType.ERROR, e.getMessage(), e
//                        .fillInStackTrace().toString());
            }
        }
        RepositoryConnection connection = null;
        try {
            connection = intputDataUnit.getConnection();
            final long beforeTriplesCount = connection.size(intputDataUnit.getDataGraphnames().toArray(new URI[0]));
            final long afterTriplesCount = connection.size(outputDataUnit.getWriteDataGraph());
            LOG.info("Transformed thanks {} SPARQL queries {} triples into {}",
                    queryCount, beforeTriplesCount, afterTriplesCount);
        } catch (RepositoryException e) {
            context.sendMessage(DPUContext.MessageType.ERROR,
                    "connection to repository broke down");
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DataUnit exception " + ex.getMessage(), ex.fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }

    }

    //	TODO michal.klempa this should not be needed anymore
    private void prepareRepository(List<RDFDataUnit> inputs) throws DataUnitException {
        for (RDFDataUnit input : inputs) {
            outputDataUnit.addAll(input);
        }
    }

    /**
     * Returns the configuration dialogue for SPARQL transformer.
     *
     * @return the configuration dialogue for SPARQL transformer.
     */
    @Override
    public AbstractConfigDialog<SPARQLTransformerConfig> getConfigurationDialog() {
        return new SPARQLTransformerDialog();
    }

    /**
     * Transform RDF in repository by SPARQL updateQuery.
     *
     * @param updateQuery String value of update SPARQL query.
     * @param dataset Set of graph URIs used for update query.
     * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException when
     * transformation fault.
     */
    public void executeSPARQLUpdateQuery(RepositoryConnection connection, String updateQuery, Dataset dataset, URI dataGraph)
            throws RDFException {

        try {
            String newUpdateQuery = AddGraphToUpdateQuery(updateQuery, dataGraph);
            Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL,
                    newUpdateQuery);
            myupdate.setDataset(dataset);

            LOG.debug(
                    "This SPARQL update query is valid and prepared for execution:");
            LOG.debug(newUpdateQuery);

            myupdate.execute();
            //connection.commit();

            LOG.debug("SPARQL update query for was executed successfully");

        } catch (MalformedQueryException e) {

            LOG.debug(e.getMessage());
            throw new RDFException(e.getMessage(), e);

        } catch (UpdateExecutionException ex) {

            final String message = "SPARQL query was not executed !!!";
            LOG.debug(message);
            LOG.debug(ex.getMessage());

            throw new RDFException(message + ex.getMessage(), ex);

        } catch (RepositoryException ex) {
            throw new RDFException(
                    "Connection to repository is not available. "
                    + ex.getMessage(), ex);
        }

    }

    /**
     * @param updateQuery String value of SPARQL update query.
     * @return String extension of given update query works with set repository
     * GRAPH.
     */
    public String AddGraphToUpdateQuery(String updateQuery, URI dataGraph) {

        String regex = "(insert|delete)\\s\\{";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(updateQuery.toLowerCase());

        boolean hasResult = matcher.find();
        boolean hasWith = updateQuery.toLowerCase().contains("with");

        if (hasResult && !hasWith) {

            int index = matcher.start();

            String first = updateQuery.substring(0, index);
            String second = updateQuery.substring(index, updateQuery.length());

            String graphName = " WITH <" + dataGraph.stringValue() + "> ";

            String newQuery = first + graphName + second;
            return newQuery;

        } else {

            LOG.debug("WITH graph clause was not added, "
                    + "because the query was: {}", updateQuery);

            regex = "(insert|delete)\\sdata\\s\\{";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(updateQuery.toLowerCase());

            hasResult = matcher.find();

            if (hasResult) {

                int start = matcher.start();
                int end = matcher.end();

                String first = updateQuery.substring(0, start);
                String second = updateQuery.substring(end, updateQuery.length());

                String myString = updateQuery.substring(start, end);
                String graphName = myString.replace("{",
                        "{ GRAPH <" + dataGraph.stringValue() + "> {");

                second = second.replaceFirst("}", "} }");
                String newQuery = first + graphName + second;

                return newQuery;

            }
        }
        return updateQuery;
    }

    /**
     * Make construct query over graph URIs in dataSet and return interface
     * Graph as result contains iterator for statements (triples).
     *
     * @param constructQuery String representation of SPARQL query.
     * @param dataSet Set of graph URIs used for construct query.
     * @return Interface Graph as result of construct SPARQL query.
     * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException when
     * query is not valid.
     */
    public Graph executeConstructQuery(RepositoryConnection connection, String constructQuery, Dataset dataSet)
            throws InvalidQueryException {

        try {

            GraphQuery graphQuery = connection.prepareGraphQuery(
                    QueryLanguage.SPARQL,
                    constructQuery);

            graphQuery.setDataset(dataSet);

            LOG.debug("Query {} is valid.", constructQuery);

            try {
                GraphQueryResult result = graphQuery.evaluate();
                LOG.debug("Query {} has not null result.", constructQuery);
                return QueryResults.asModel(result);

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        "This query is probably not valid. " + ex
                        .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    "This query is probably not valid. "
                    + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            LOG.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                "Getting GraphQueryResult using SPARQL construct query failed.");
    }

}
