package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of ExtendedExtractContext. 
 *
 * @author Petyr
 * 
 */
class ExtendedExtractContextImpl extends ExtendedCommonImpl implements ExtendedExtractContext {
   
	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager dataUnitManager;
	
	public ExtendedExtractContextImpl(PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo context, AppConfig appConfig, Date lastSuccExec) throws IOException {
		super(execution, dpuInstance, context, appConfig, lastSuccExec);
		this.eventPublisher = eventPublisher;
		// create DataUnit manager
		this.dataUnitManager = DataUnitManager.createOutputManager(
				dpuInstance, 
				dataUnitFactory, 
				context, 
				getGeneralWorkingDir(), 
				appConfig);
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
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		return dataUnitManager.addDataUnit(type, name);	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name, Object config)
			throws DataUnitCreateException {	
		return dataUnitManager.addDataUnit(type, name, config);
	}

	@Override
	public List<DataUnit> getOutputs() {
		return dataUnitManager.getDataUnits();
	}

}
