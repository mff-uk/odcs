package cz.cuni.mff.xrg.intlib.loader.rdf;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.module.data.DataUnitList;
import cz.cuni.xrg.intlib.commons.module.data.RDFDataUnitList;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class RDFLoader extends ConfigurableBase<RDFLoaderConfig>
		implements Load, ConfigDialogProvider<RDFLoaderConfig> {

	public RDFLoader() {
		super(new RDFLoaderConfig());
	}

	@Override
	public void load(LoadContext context)
			throws LoadException,
				DataUnitException {
		DataUnitList<RDFDataRepository> dataUnitList = RDFDataUnitList
				.create(context);
		RDFDataRepository repository = dataUnitList.getFirst();

		final String endpoint = config.SPARQL_endpoint;
		URL endpointURL = null;
		try {
			endpointURL = new URL(endpoint);
		} catch (MalformedURLException ex) {

			throw new LoadException(ex);
		}

		final List<String> defaultGraphsURI = config.GraphsUri;
		final String hostName = config.Host_name;
		final String password = config.Password;
		final WriteGraphType graphType = config.Options;

		try {
			repository.loadtoSPARQLEndpoint(endpointURL, defaultGraphsURI,
					hostName, password, graphType);
		} catch (RDFException ex) {
			throw new LoadException(ex.getMessage(), ex);
		}
	}

	@Override
	public AbstractConfigDialog<RDFLoaderConfig> getConfigurationDialog() {
		return new RDFLoaderDialog();
	}

}
