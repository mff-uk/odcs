package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.module.data.InputHelper;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class SPARQLTransformer implements Transform,
		Configurable<SPARQLTransformerConfig>, ConfigDialogProvider<SPARQLTransformerConfig> {

	/**
	 * Logger class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(
			SPARQLTransformer.class);

	private SPARQLTransformerConfig config = new SPARQLTransformerConfig();

	@Override
	public void transform(TransformContext context) throws TransformException, DataUnitCreateException {

		if (context != null) {

			// get input repository
			RDFDataRepository intputRepository =
					InputHelper.getInput(context.getInputs(), 0,
					RDFDataRepository.class);

			// create output repository
			RDFDataRepository outputRepository =
					(RDFDataRepository) context.addOutputDataUnit(
					DataUnitType.RDF, "output");

			final String updateQuery = config.SPARQL_Update_Query;

			intputRepository.copyAllDataToTargetRepository(
					outputRepository);
			try {
				outputRepository.transformUsingSPARQL(updateQuery);
			} catch (RDFException ex) {
				throw new TransformException(ex.getMessage(), ex);
			}

		} else {
			throw new TransformException(
					"Transform context " + context + " is null");
		}

	}

	@Override
	public void configure(SPARQLTransformerConfig c) throws ConfigException {
		config = c;
	}

	@Override
	public SPARQLTransformerConfig getConfiguration() {
		return config;
	}

	@Override
	public AbstractConfigDialog<SPARQLTransformerConfig> getConfigurationDialog() {
		return new SPARQLTransformerDialog();
	}
}
