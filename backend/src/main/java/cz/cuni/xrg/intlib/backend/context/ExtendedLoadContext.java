package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.context.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;

/**
 * Extended load context.
 * 
 * @author Petyr
 * 
 */
public class ExtendedLoadContext
	extends ExtendedContext implements LoadContext, MergableContext {

	/**
	 * Manager for input DataUnits.
	 */
	private DataUnitManager dataUnitManager;	
	
	public ExtendedLoadContext() {
		super();
		this.dataUnitManager = DataUnitManager.createInputManager(dpuInstance,
				dataUnitFactory, context, getGeneralWorkingDir(), appConfig);		
	}
	
	/**
	 * Made inputs read only. It's called just before it's passed to the
	 * DPURecord.
	 */
	public void sealInputs() {
		for (DataUnit inputDataUnit : dataUnitManager.getDataUnits()) {
			inputDataUnit.madeReadOnly();
		}
	}

	@Override
	public void addSource(ProcessingContext context,
			String instruction) throws ContextException {
		// create merger class
		DataUnitMerger merger = new DataUnitMerger();
		// merge custom data
		try {
			customData.putAll(context.getCustomData());
		} catch (Exception e) {
			throw new ContextException("Error while merging custom data.", e);
		}
		// now based on context type ..
		if (context instanceof ExtendedExtractContext) {
			ExtendedExtractContext extractContext = (ExtendedExtractContext) context;
			// primitive merge ..
			merger.merger(dataUnitManager, extractContext.getOutputs(),
					instruction);
		} else if (context instanceof ExtendedTransformContext) {
			ExtendedTransformContext transformContext = (ExtendedTransformContext) context;
			// primitive merge ..
			merger.merger(dataUnitManager, transformContext.getOutputs(),
					instruction);
		} else {
			throw new ContextException("Wrong context type: "
					+ context.getClass().getSimpleName());
		}
	}

	@Override
	public List<DataUnit> getInputs() {
		return dataUnitManager.getDataUnits();
	}

	@Override
	public void save() {
		dataUnitManager.save();		
	}

	@Override
	public void release() {
		dataUnitManager.release();		
	}

	@Override
	public void delete() {
		dataUnitManager.delete();
		deleteDirectories();		
	}

	@Override
	public void reload() throws DataUnitException {
		dataUnitManager.reload();
	}
	
}
