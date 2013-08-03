package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of ExtendedLoadContext.
 * 
 * @author Petyr
 * 
 */
class ExtendedLoadContextImpl extends ExtendedCommonImpl
		implements ExtendedLoadContext {

	/**
	 * Manager for input DataUnits.
	 */
	private DataUnitManager dataUnitManager;

	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	private ApplicationEventPublisher eventPublisher;

	public ExtendedLoadContextImpl(PipelineExecution execution,
			DPUInstanceRecord dpuInstance,
			ApplicationEventPublisher eventPublisher,
			ExecutionContextInfo context,
			AppConfig appConfig,
			Date lastSuccExec) throws IOException {
		super(execution, dpuInstance, context, appConfig, lastSuccExec);
		this.eventPublisher = eventPublisher;
		// create DataUnit manager
		this.dataUnitManager = DataUnitManager.createInputManager(dpuInstance,
				dataUnitFactory, context, getGeneralWorkingDir(), appConfig);
	}

	@Override
	public List<DataUnit> getInputs() {
		return dataUnitManager.getDataUnits();
	}

	@Override
	public void sendMessage(MessageType type, String shortMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, "", type,
				this, this));
	}

	@Override
	public void sendMessage(MessageType type,
			String shortMessage,
			String fullMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, fullMessage,
				type, this, this));
	}

	@Override
	public void delete() {
		dataUnitManager.delete();
	}	
	
	@Override
	public void release() {
		dataUnitManager.release();
	}

	@Override
	public void save() {
		dataUnitManager.save();
	}

	@Override
	public void sealInputs() {
		for (DataUnit inputDataUnit : dataUnitManager.getDataUnits()) {
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

}
