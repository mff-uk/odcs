package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.rdf.impl.SPARQLoader;
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

		final String endpoint = config.SPARQL_endpoint;
		URL endpointURL = null;
		try {
			endpointURL = new URL(endpoint);
		} catch (MalformedURLException ex) {

			throw new DPUException(ex);
		}

		final List<String> defaultGraphsURI = config.GraphsUri;
		final String hostName = config.Host_name;
		final String password = config.Password;
		final WriteGraphType graphType = config.graphOption;
		final InsertType insertType = config.insertOption;
		final long chunkSize = config.chunkSize;
		final boolean validateDataBefore = config.validDataBefore;

		final int retrySize = config.retrySize;
		final long retryTime = config.retryTime;

		if (validateDataBefore) {
			DataValidator dataValidator = new RepositoryDataValidator(
					rdfDataUnit);

			if (!dataValidator.areDataValid()) {
				final String message = "RDF Data to load are not valid - LOAD to SPARQL FAIL";
				logger.info(message);
				logger.error(dataValidator.getErrorMessage());
				context.sendMessage(MessageType.WARNING, dataValidator
						.getErrorMessage());

				throw new RDFException(message);
			} else {
				logger.info("RDF Data for loading are VALID");
				logger.info("Loading to SPARQL endpoint start just now");
			}
		}
		final long triplesCount = rdfDataUnit.getTripleCount();
		logger.info("Loading {} triples", triplesCount);

		try {
			SPARQLoader loader = new SPARQLoader(rdfDataUnit, context, retrySize,
					retryTime);

			loader.loadToSPARQLEndpoint(endpointURL, defaultGraphsURI,
					hostName, password, graphType, insertType, chunkSize);

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
