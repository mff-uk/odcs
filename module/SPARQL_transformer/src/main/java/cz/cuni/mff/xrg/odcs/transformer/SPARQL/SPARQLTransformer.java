package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SPARQL Transformer.
 *
 * @author Jiri Tomes
 * @author Petyr
 * @author tknap
 */
@AsTransformer
public class SPARQLTransformer
		extends ConfigurableBase<SPARQLTransformerConfig>
		implements ConfigDialogProvider<SPARQLTransformerConfig> {

	private final Logger LOG = LoggerFactory.getLogger(SPARQLTransformer.class);

	public static final String[] DPUNames = {"input", "optional1", "optional2", "optional3"};

	/**
	 * The repository input for SPARQL transformer.
	 */
	@InputDataUnit(name = "input")
	public RDFDataUnit intputDataUnit;

	//three other optional inputs, which may be used in the queries
	/**
	 * The first repository optional input for SPARQL transformer.
	 */
	@InputDataUnit(name = "optional1", optional = true)
	public RDFDataUnit intputOptional1;

	/**
	 * The second repository optional input for SPARQL transformer.
	 */
	@InputDataUnit(name = "optional2", optional = true)
	public RDFDataUnit intputOptional2;

	/**
	 * The third repository optional input for SPARQL transformer.
	 */
	@InputDataUnit(name = "optional3", optional = true)
	public RDFDataUnit intputOptional3;

	/**
	 * The repository output for SPARQL transformer.
	 */
	@OutputDataUnit
	public RDFDataUnit outputDataUnit;

	public SPARQLTransformer() {
		super(SPARQLTransformerConfig.class);
	}

	private Dataset createGraphDataSet(List<RDFDataUnit> inputs) {
		DatasetImpl dataSet = new DatasetImpl();

		for (RDFDataUnit repository : inputs) {
			if (repository != null) {
				URI dataGraphURI = repository.getDataGraph();
				dataSet.addDefaultGraph(dataGraphURI);
				dataSet.addNamedGraph(dataGraphURI);
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
	 * @throws DataUnitException if this DPU fails.
	 * @throws DPUException      if this DPU fails.
	 */
	@Override
	public void execute(DPUContext context)
			throws DPUException, DataUnitException {

		//GET ALL possible inputs
		List<RDFDataUnit> inputs = getInputs();

		final List<SPARQLQueryPair> queryPairs = config.getQueryPairs();

		if (queryPairs == null) {
			context.sendMessage(MessageType.ERROR,
					"All queries for SPARQL transformer are null values");
		} else {
			if (queryPairs.isEmpty()) {
				context.sendMessage(MessageType.ERROR,
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
				context.sendMessage(MessageType.ERROR,
						"Query number " + queryCount + " is not defined");
			} else if (updateQuery.trim().isEmpty()) {
				context.sendMessage(MessageType.ERROR,
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
						Graph graph = intputDataUnit.executeConstructQuery(
								constructQuery, dataSet);
						((ManagableRdfDataUnit) outputDataUnit)
								.addTriplesFromGraph(
								graph);
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
						Dataset dataset = createGraphDataSet(inputs);

						outputDataUnit.executeSPARQLUpdateQuery(
								replacedUpdateQuery, dataset);
//					} else {

//						outputDataUnit.executeSPARQLUpdateQuery(
//								replacedUpdateQuery);
//						TODO michal.klempa this should not be needed anymore
//					}
				}

			} catch (RDFDataUnitException ex) {
				context.sendMessage(MessageType.ERROR, ex.getMessage(), ex
						.fillInStackTrace().toString());
//				TODO michal.klempa this should not be needed anymore
//			} catch (RepositoryException e) {
//                context.sendMessage(MessageType.ERROR, e.getMessage(), e
//                        .fillInStackTrace().toString());
            }
        }
        try {
            RepositoryConnection connection = intputDataUnit.getConnection();
            RepositoryConnection connection2 = outputDataUnit.getConnection();
            final long beforeTriplesCount = connection.size(intputDataUnit.getDataGraph());
            final long afterTriplesCount = connection2.size(outputDataUnit.getDataGraph());
            LOG.info("Transformed thanks {} SPARQL queries {} triples into {}",
                    queryCount, beforeTriplesCount, afterTriplesCount);
        } catch (RepositoryException e) {
            context.sendMessage(MessageType.ERROR,
                    "connection to repository broke down");
        }

	}

	//	TODO michal.klempa this should not be needed anymore
	private void prepareRepository(List<RDFDataUnit> inputs) {
		for (RDFDataUnit input : inputs) {
			((ManagableRdfDataUnit) outputDataUnit).merge(input);
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
}
