package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.module.data.DataUnitList;
import cz.cuni.xrg.intlib.commons.module.data.RDFDataUnitList;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

/**
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public class SPARQLTransformer
		extends ConfigurableBase<SPARQLTransformerConfig>
		implements Transform, ConfigDialogProvider<SPARQLTransformerConfig> {

	public SPARQLTransformer() {
		super(SPARQLTransformerConfig.class);
	}

	@Override
	public void transform(TransformContext context)
			throws TransformException,
			DataUnitException {
		DataUnitList<RDFDataUnit> dataUnitList = RDFDataUnitList
				.create(context);
		RDFDataUnit intputDataUnit = dataUnitList.getFirst();

		// create output repository
		RDFDataUnit outputDataUnit = (RDFDataUnit) context
				.addOutputDataUnit(DataUnitType.RDF, "output");

		final String updateQuery = config.SPARQL_Update_Query;

		outputDataUnit.merge(intputDataUnit);

		try {
			outputDataUnit.transformUsingSPARQL(updateQuery);
		} catch (RDFDataUnitException ex) {
			throw new TransformException(ex.getMessage(), ex);
		}

	}

	@Override
	public AbstractConfigDialog<SPARQLTransformerConfig> getConfigurationDialog() {
		return new SPARQLTransformerDialog();
	}
}
