package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.annotation.InputDataUnit;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
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

	@InputDataUnit
	public RDFDataUnit intputDataUnit;
	
	@OutputDataUnit
	public RDFDataUnit outputDataUnit;	
	
	public SPARQLTransformer() {
		super(SPARQLTransformerConfig.class);
	}

	@Override
	public void transform(TransformContext context)
			throws TransformException,
			DataUnitException {
		
		outputDataUnit.merge(intputDataUnit);

		final String updateQuery = config.SPARQL_Update_Query;
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
