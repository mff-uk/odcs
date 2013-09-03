package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsTransformer;
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
 * @author tknap
 */
@AsTransformer
public class SPARQLTransformer
		extends ConfigurableBase<SPARQLTransformerConfig>
		implements DPU, ConfigDialogProvider<SPARQLTransformerConfig> {

	@InputDataUnit
	public RDFDataUnit intputDataUnit;
	
	@OutputDataUnit
	public RDFDataUnit outputDataUnit;	
	
	public SPARQLTransformer() {
		super(SPARQLTransformerConfig.class);
	}

	@Override
	public void execute(DPUContext context)
			throws DPUException, DataUnitException {
		
		outputDataUnit.merge(intputDataUnit);

		final String updateQuery = config.SPARQL_Update_Query;
		try {
			outputDataUnit.transform(updateQuery);
		} catch (RDFDataUnitException ex) {
			throw new DPUException(ex.getMessage(), ex);
		}

	}
	@Override
	public AbstractConfigDialog<SPARQLTransformerConfig> getConfigurationDialog() {
		return new SPARQLTransformerDialog();
	}
}
