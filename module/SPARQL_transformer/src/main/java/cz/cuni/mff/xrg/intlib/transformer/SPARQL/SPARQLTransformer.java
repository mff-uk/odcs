package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import java.util.List;
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
	public void transform(TransformContext context) throws TransformException {
		
		if (context != null) {

			List<DataUnit> inputs = context.getInputs();

			if (inputs.isEmpty()) {
				throw new TransformException("Missing inputs!");
			}

			DataUnit dataUnit = inputs.get(0);

			RDFDataRepository intputRepository = null;

			if (dataUnit instanceof RDFDataRepository) {
				intputRepository = (RDFDataRepository) dataUnit;
			} else {
				throw new TransformException("Wrong input type " + dataUnit
						.getType().toString() + " expected RDF.");
			}

			// create output repository
			RDFDataRepository outputRepository = null;

			try {
				outputRepository = (RDFDataRepository) context
						.addOutputDataUnit(DataUnitType.RDF);
			} catch (DataUnitCreateException ex) {
				throw new TransformException("Can't create DataUnit", ex);
			}

			if (outputRepository == null) {
				throw new TransformException(
						"DataUnitFactory returned null.");
			}

			final String updateQuery = config.SPARQL_Update_Query;

			intputRepository.copyAllDataToTargetRepository(
					outputRepository);
			outputRepository.transformUsingSPARQL(updateQuery);

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
