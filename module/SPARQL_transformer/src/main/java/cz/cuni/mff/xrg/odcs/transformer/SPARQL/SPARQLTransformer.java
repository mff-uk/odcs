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
import cz.cuni.mff.xrg.odcs.rdf.impl.PlaceHolder;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openrdf.model.Graph;
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
	
	@InputDataUnit
	public RDFDataUnit intputDataUnit;

	@OutputDataUnit
	public RDFDataUnit outputDataUnit;

	public SPARQLTransformer() {
		super(SPARQLTransformerConfig.class);
	}

	private List<PlaceHolder> getPlaceHolders(String constructQuery) {

		String regex = "graph\\s+\\?[gG]_[\\w-_]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(constructQuery);

		boolean hasResult = matcher.find();

		List<PlaceHolder> placeholders = new ArrayList<>();

		while (hasResult) {

			int start = matcher.start();
			int end = matcher.end();

			int partIndex = constructQuery.substring(start, end).indexOf("_") + 1;

			start += partIndex;

			String DPUName = constructQuery.substring(start, end);

			PlaceHolder placeHolder = new PlaceHolder(DPUName);
			placeholders.add(placeHolder);

			hasResult = matcher.find();
		}

		return placeholders;
	}

	private void replaceAllPlaceHolders(List<RDFDataUnit> inputs,
			List<PlaceHolder> placeHolders, DPUContext context) throws DPUException {

		for (PlaceHolder next : placeHolders) {
			boolean isReplased = false;

			for (RDFDataUnit input : inputs) {
				if (input.getDataUnitName().equals(next.getDPUName())) {

					//set RIGHT data graph for DPU
					next.setGraphName(input.getDataGraph().toString());
					isReplased = true;
					break;
				}
			}

			if (!isReplased) {
				String DPUName = next.getDPUName();
				final String message = "Graph for DPU name " + DPUName + " was not replased";

				context.sendMessage(MessageType.ERROR, message);
				throw new DPUException(message);
			}

		}

	}

	private String getContructQuery(String originalConstructQuery,
			List<RDFDataUnit> inputs, DPUContext context) throws DPUException {

		String result = originalConstructQuery;

		List<PlaceHolder> placeHolders = getPlaceHolders(originalConstructQuery);

		if (!placeHolders.isEmpty()) {
			replaceAllPlaceHolders(inputs, placeHolders, context);
		}

		for (PlaceHolder next : placeHolders) {

			String graphName = "<" + next.getGraphName() + ">";

			result = result.replaceAll("\\?[g|G]_" + next
					.getDPUName(), graphName);
		}

		return result;
	}

	@Override
	public void execute(DPUContext context)
			throws DPUException, DataUnitException {

		final String updateQuery = config.SPARQL_Update_Query;
		final boolean isConstructQuery = config.isConstructType;

				
		try {
			if (isConstructQuery) {

				//TODO - update for more inputs
				List<RDFDataUnit> inputs = new ArrayList<>();
				inputs.add(intputDataUnit);

				//creating newConstruct replaced query
				String constructQuery = getContructQuery(updateQuery, inputs,
						context);

				//execute given construct query
				Graph graph = intputDataUnit.executeConstructQuery(
						constructQuery);
				outputDataUnit.addTriplesFromGraph(graph);

			} else {
				outputDataUnit.merge(intputDataUnit);
				outputDataUnit.executeSPARQLUpdateQuery(updateQuery);
			}

		} catch (RDFDataUnitException ex) {
			throw new DPUException(ex.getMessage(), ex);
		}

		final long beforeTriplesCount = intputDataUnit.getTripleCount();
		final long afterTriplesCount = outputDataUnit.getTripleCount();
		LOG.info("Transformed {} triples into {}", beforeTriplesCount, afterTriplesCount);
	}

	@Override
	public AbstractConfigDialog<SPARQLTransformerConfig> getConfigurationDialog() {
		return new SPARQLTransformerDialog();
	}
}
