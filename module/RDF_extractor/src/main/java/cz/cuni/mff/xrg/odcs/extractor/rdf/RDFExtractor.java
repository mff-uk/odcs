package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@AsExtractor
public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig>
		implements ConfigDialogProvider<RDFExtractorConfig> {

	private final Logger LOG = LoggerFactory.getLogger(RDFExtractor.class);

	@OutputDataUnit
	public RDFDataUnit rdfDataUnit;

	public RDFExtractor() {
		super(RDFExtractorConfig.class);
	}

	@Override
	public void execute(DPUContext context)
			throws DPUException,
			DataUnitCreateException {

		try {
			final URL endpointURL = new URL(config.getSPARQLEndpoint());
			final String hostName = config.getHostName();
			final String password = config.getPassword();
			final List<String> defaultGraphsUri = config.getGraphsUri();
			String constructQuery = config.getSPARQLQuery();
			if (constructQuery.isEmpty()) {
				constructQuery = "construct {?x ?y ?z} where {?x ?y ?z}";
			}

			boolean useStatisticHandler = config.isUsedStatisticalHandler();
			boolean failWhenErrors = config.isFailWhenErrors();

			HandlerExtractType handlerExtractType = HandlerExtractType
					.getHandlerType(useStatisticHandler, failWhenErrors);

			final boolean extractFail = config.isExtractFail();

			LOG.debug("endpointURL: {}", endpointURL);
			LOG.debug("defaultGraphsUri: {}", defaultGraphsUri);
			LOG.debug("constructQuery: {}", constructQuery);
			LOG.debug("hostName: {}", hostName);
			LOG.debug("password: {}", password);
			LOG.debug("useStatisticHandler: {}", useStatisticHandler);
			LOG.debug("extractFail: {}", extractFail);

			rdfDataUnit.extractFromSPARQLEndpoint(endpointURL,
					defaultGraphsUri,
					constructQuery, hostName, password, RDFFormat.N3,
					handlerExtractType, extractFail);

			if (useStatisticHandler && StatisticalHandler.hasParsingProblems()) {

				String problems = StatisticalHandler
						.getFindedGlobalProblemsAsString();
				StatisticalHandler.clearParsingProblems();

				context.sendMessage(MessageType.WARNING,
						"Statistical and error handler has found during parsing problems triples (these triples were not added)",
						problems);
			}

		} catch (MalformedURLException ex) {
			LOG.debug("RDFDataUnitException", ex);
			context.sendMessage(MessageType.ERROR, "MalformedURLException: "
					+ ex.getMessage());
			throw new DPUException(ex);
		} catch (RDFDataUnitException ex) {
			context.sendMessage(MessageType.ERROR, ex.getMessage());
			throw new DPUException(ex.getMessage(), ex);
		}

		final long triplesCount = rdfDataUnit.getTripleCount();
		LOG.info("Extracted {} triples", triplesCount);
	}

	@Override
	public AbstractConfigDialog<RDFExtractorConfig> getConfigurationDialog() {
		return new RDFExtractorDialog();
	}
}
