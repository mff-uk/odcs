package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.*;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.validators.RepositoryDataValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
@AsLoader
public class RDFLoader extends ConfigurableBase<RDFLoaderConfig>
		implements ConfigDialogProvider<RDFLoaderConfig> {

	private final Logger logger = LoggerFactory.getLogger(RDFLoader.class);

	@InputDataUnit
	public RDFDataUnit rdfDataUnit;

	public RDFLoader() {
		super(RDFLoaderConfig.class);
	}

	@Override
	public void execute(DPUContext context)
			throws DPUException,
			DataUnitException {

		final String endpoint = config.getSPARQLEndpoint();
		URL endpointURL = null;
		try {
			endpointURL = new URL(endpoint);
		} catch (MalformedURLException ex) {

			throw new DPUException(ex);
		}

		final List<String> defaultGraphsURI = config.getGraphsUri();
		final String hostName = config.getHostName();
		final String password = config.getPassword();
		final WriteGraphType graphType = config.getGraphOption();
		final InsertType insertType = config.getInsertOption();
		final long chunkSize = config.getChunkSize();
		final boolean validateDataBefore = config.isValidDataBefore();

		final int retrySize = config.getRetrySize();
		final long retryTime = config.getRetryTime();

		if (validateDataBefore) {
			DataValidator dataValidator = new RepositoryDataValidator(
					rdfDataUnit);

			if (!dataValidator.areDataValid()) {
				final String message = "RDF Data to load are not valid - LOADING to SPARQL FAIL";
				logger.error(dataValidator.getErrorMessage());

				context.sendMessage(MessageType.WARNING, message, dataValidator
						.getErrorMessage());

				throw new RDFException(message);
			} else {
				context.sendMessage(MessageType.INFO,
						"RDF Data for loading are valid");
				context.sendMessage(MessageType.INFO,
						"Loading data to SPARQL endpoint STARTS JUST NOW");
			}
		}
		final long triplesCount = rdfDataUnit.getTripleCount();
		logger.info("Loading {} triples", triplesCount);

		try {
			SPARQLoader loader = new SPARQLoader(rdfDataUnit, context, retrySize,
					retryTime);

			loader.loadToSPARQLEndpoint(endpointURL, defaultGraphsURI,
					hostName, password, graphType, insertType, chunkSize);

			context.sendMessage(MessageType.INFO,
					"Loading data to SPARQL endpoint ends SUCCESSFULLY");

		} catch (RDFDataUnitException ex) {
			context.sendMessage(MessageType.ERROR, ex.getMessage());
			throw new DPUException(ex.getMessage(), ex);
		}
	}

	@Override
	public AbstractConfigDialog<RDFLoaderConfig> getConfigurationDialog() {
		return new RDFLoaderDialog();
	}
}
