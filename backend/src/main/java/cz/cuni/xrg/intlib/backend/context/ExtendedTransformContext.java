package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.context.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;

/**
 * Extended transform context.
 * 
 * @author Petyr
 * 
 */
public class ExtendedTransformContext
	extends ExtendedContext implements TransformContext, MergableContext {

	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager inputsManager;

	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager outputsManager;	
	
	public ExtendedTransformContext() {
		// create DataUnit manager
		this.inputsManager = DataUnitManager.createInputManager(dpuInstance,
				dataUnitFactory, context, getGeneralWorkingDir(), appConfig);
		// create DataUnit manager
		this.outputsManager = DataUnitManager.createOutputManager(dpuInstance,
				dataUnitFactory, context, getGeneralWorkingDir(), appConfig);		
	}
	
	/**
	 * Made inputs read only. It's called just before it's passed to the
	 * DPURecord.
	 */
	public void sealInputs() {
		for (DataUnit inputDataUnit : inputsManager.getDataUnits()) {
			inputDataUnit.madeReadOnly();
		}		
	}

	/**
	 * Return access to list of all output DataUnits.
	 * 
	 * @return
	 */
	public List<DataUnit> getOutputs() {
		return outputsManager.getDataUnits();
	}

	@Override
	public List<DataUnit> getInputs() {
		return inputsManager.getDataUnits();
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		return outputsManager.addDataUnit(type, name);
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type,
			String name,
			Object config) throws DataUnitCreateException {
		return outputsManager.addDataUnit(type, name, config);
	}

	@Override
	public void addSource(ProcessingContext context, String instruction)
			throws ContextException {
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
			merger.merger(inputsManager, extractContext.getOutputs(),
					instruction);
		} else if (context instanceof ExtendedTransformContext) {
			ExtendedTransformContext transformContext = (ExtendedTransformContext) context;
			// primitive merge ..
			merger.merger(inputsManager, transformContext.getOutputs(),
					instruction);
		} else {
			throw new ContextException("Wrong context type: "
					+ context.getClass().getSimpleName());
		}		
	}

	@Override
	public void save() {
		inputsManager.save();
		outputsManager.save();
	}

	@Override
	public void release() {
		inputsManager.release();
		outputsManager.release();
	}

	@Override
	public void delete() {
		inputsManager.delete();
		outputsManager.delete();
		deleteDirectories();		
	}

	@Override
	public void reload() throws DataUnitException {
		inputsManager.reload();
		outputsManager.reload();
	}
	
}
