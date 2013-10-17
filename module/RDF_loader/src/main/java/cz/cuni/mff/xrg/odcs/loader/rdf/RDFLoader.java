package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.*;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFDataUnitException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

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

	private final Logger LOG = LoggerFactory.getLogger(RDFLoader.class);
	
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

		final long triplesCount = rdfDataUnit.getTripleCount();
		LOG.info("Loading {} triples", triplesCount);		
		
		try {

			rdfDataUnit.loadToSPARQLEndpoint(endpointURL, defaultGraphsURI,
					hostName, password, graphType, insertType, chunkSize);
		} catch (RDFDataUnitException ex) {
			throw new DPUException(ex.getMessage(), ex);
		}
	}

	@Override
	public AbstractConfigDialog<RDFLoaderConfig> getConfigurationDialog() {
		return new RDFLoaderDialog();
	}

}
