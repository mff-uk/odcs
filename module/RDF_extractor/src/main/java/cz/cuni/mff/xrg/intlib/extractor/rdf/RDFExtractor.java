package cz.cuni.mff.xrg.intlib.extractor.rdf;

import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsExtractor;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.xrg.intlib.commons.message.MessageType;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@AsExtractor
public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig>
		implements DPU, ConfigDialogProvider<RDFExtractorConfig> {

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
			final URL endpointURL = new URL(config.SPARQL_endpoint);
			final String hostName = config.Host_name;
			final String password = config.Password;
			final List<String> defaultGraphsUri = config.GraphsUri;
			final String query = config.SPARQL_query;
			final boolean useStatisticHandler = config.UseStatisticalHandler;
			final boolean extractFail = config.ExtractFail;

			rdfDataUnit.extractFromSPARQLEndpoint(endpointURL,
					defaultGraphsUri,
					query, hostName, password, RDFFormat.N3,
					useStatisticHandler,extractFail);
		} catch (MalformedURLException ex) {
			context.sendMessage(MessageType.ERROR, "MalformedURLException: "
					+ ex.getMessage());
			throw new DPUException(ex);
		} catch (RDFDataUnitException ex) {
			throw new DPUException(ex.getMessage(), ex);
		}
	}

	@Override
	public AbstractConfigDialog<RDFExtractorConfig> getConfigurationDialog() {
		return new RDFExtractorDialog();
	}

	@Override
	public void cleanUp() {	}
	
}
