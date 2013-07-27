package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.io.IOException;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of ExtendedTransformContext. 
 *
 * @author Petyr
 * 
 */
class ExtendedTransformContextImpl extends ExtendedCommonImpl implements ExtendedTransformContext {
	
	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager inputsManager;
	
	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager outputsManager;	
	
	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	private ApplicationEventPublisher eventPublisher;	

	public ExtendedTransformContextImpl(PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo context, AppConfig appConfig) throws IOException {
		super(execution, dpuInstance, context, appConfig);
		this.eventPublisher = eventPublisher;
		// create DataUnit manager
		this.inputsManager = DataUnitManager.createInputManager(dpuInstance,
				dataUnitFactory, context, getWorkingDir(), appConfig);
		// create DataUnit manager
		this.outputsManager = DataUnitManager.createOutputManager(dpuInstance,
				dataUnitFactory, context, getWorkingDir(), appConfig);
	}

	@Override
	public List<DataUnit> getInputs() {
		return inputsManager.getDataUnits();
	}

	@Override
	public void sendMessage(MessageType type, String shortMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, "", type, this, this) );
	}

	@Override
	public void sendMessage(MessageType type, String shortMessage, String fullMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, fullMessage, type, this, this) );		
	}

	@Override
	public void release() {
		inputsManager.release();
		outputsManager.release();
	}	
	
	@Override
	public void save() {
		inputsManager.save();
		outputsManager.save();
	}	
	
	@Override
	public void sealInputs() {
		for (DataUnit inputDataUnit : inputsManager.getDataUnits()) {
			inputDataUnit.madeReadOnly();
		}
	}
	
	@Override
	public void addSource(ProcessingContext context, 
			String instruction) throws ContextException {
		// create merger class
		DataUnitMerger merger = new PrimitiveDataUnitMerger();		
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
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		return outputsManager.addDataUnit(type, name);	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name, Object config)
			throws DataUnitCreateException {	
		return outputsManager.addDataUnit(type, name, config);
	}

	@Override
	public List<DataUnit> getOutputs() {
		return outputsManager.getDataUnits();
	}
      
}
