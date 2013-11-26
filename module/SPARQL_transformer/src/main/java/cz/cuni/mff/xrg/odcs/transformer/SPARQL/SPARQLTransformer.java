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
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.impl.DatasetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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

	@InputDataUnit(name = "input")
	public RDFDataUnit intputDataUnit;

	//two other optional inputs, which may be used in the queries
	@InputDataUnit(name = "optional1", optional = true)
	public RDFDataUnit intputOptional1;

	@InputDataUnit(name = "optional2", optional = true)
	public RDFDataUnit intputOptional2;

	@InputDataUnit(name = "optional3", optional = true)
	public RDFDataUnit intputOptional3;

	@OutputDataUnit
	public RDFDataUnit outputDataUnit;

	public SPARQLTransformer() {
		super(SPARQLTransformerConfig.class);
	}

	private Dataset createGraphDataSet(List<RDFDataUnit> inputs) {
		DatasetImpl dataSet = new DatasetImpl();

		for (RDFDataUnit repository : inputs) {
			URI dataGraphURI = repository.getDataGraph();
			dataSet.addDefaultGraph(dataGraphURI);
		}
		return dataSet;
	}

	private List<RDFDataUnit> getInputs() {
		List<RDFDataUnit> inputs = new ArrayList<>();

		inputs.add(intputDataUnit);
		inputs.add(intputOptional1);
		inputs.add(intputOptional2);
		inputs.add(intputOptional3);

		return inputs;
	}

	@Override
	public void execute(DPUContext context)
			throws DPUException, DataUnitException {

		final String updateQuery = config.SPARQL_Update_Query;
		final boolean isConstructQuery = config.isConstructType;

		try {
			if (isConstructQuery) {

				//GET ALL inputs
				List<RDFDataUnit> inputs = getInputs();

				//creating newConstruct replaced query
				PlaceholdersHelper placeHolders = new PlaceholdersHelper(context);
				String constructQuery = placeHolders.getContructQuery(
						updateQuery,
						inputs);

				//execute given construct query
				Dataset dataSet = createGraphDataSet(inputs);
				Graph graph = intputDataUnit.executeConstructQuery(
						constructQuery, dataSet);
				outputDataUnit.addTriplesFromGraph(graph);

			} else {
				outputDataUnit.merge(intputDataUnit);
				outputDataUnit.executeSPARQLUpdateQuery(updateQuery);
			}

		} catch (RDFDataUnitException ex) {
			context.sendMessage(MessageType.ERROR, ex.getMessage());
			throw new DPUException(ex.getMessage(), ex);
		}

		final long beforeTriplesCount = intputDataUnit.getTripleCount();
		final long afterTriplesCount = outputDataUnit.getTripleCount();
		LOG.info("Transformed {} triples into {}", beforeTriplesCount,
				afterTriplesCount);
	}

	@Override
	public AbstractConfigDialog<SPARQLTransformerConfig> getConfigurationDialog() {
		return new SPARQLTransformerDialog();
	}
}
