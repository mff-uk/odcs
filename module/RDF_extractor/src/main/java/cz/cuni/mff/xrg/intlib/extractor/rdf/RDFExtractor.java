package cz.cuni.mff.xrg.intlib.extractor.rdf;

import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.message.MessageType;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig>
		implements Extract, ConfigDialogProvider<RDFExtractorConfig> {

	public RDFExtractor() {
		super(new RDFExtractorConfig());
	}

	@Override
	public void extract(ExtractContext context)
			throws ExtractException,
			DataUnitCreateException {

		RDFDataRepository repository = (RDFDataRepository) context
				.addOutputDataUnit(DataUnitType.RDF, "output");

		try {
			final URL endpointURL = new URL(config.SPARQL_endpoint);
			final String hostName = config.Host_name;
			final String password = config.Password;
			final List<String> defaultGraphsUri = config.GraphsUri;
			final String query = config.SPARQL_query;
			final boolean useStatisticHandler = config.UseStatisticalHandler;

			repository.extractFromSPARQLEndpoint(endpointURL, defaultGraphsUri,
					query, hostName, password, RDFFormat.N3, useStatisticHandler);
		} catch (MalformedURLException ex) {
			context.sendMessage(MessageType.ERROR, "MalformedURLException: "
					+ ex.getMessage());
			throw new ExtractException(ex);
		} catch (RDFException ex) {
			throw new ExtractException(ex.getMessage(), ex);
		}
	}

	@Override
	public AbstractConfigDialog<RDFExtractorConfig> getConfigurationDialog() {
		return new RDFExtractorDialog();
	}
}
