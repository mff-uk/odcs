package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.data.DataUnitContainer;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of ExtendedExtractContext. 
 *
 * @author Petyr
 * 
 */
class ExtendedExtractContextImpl extends ExtendedCommonImpl implements ExtendedExtractContext {
		
	/**
	 * Context output data units.
	 */
    private List<DataUnit> outputs;
    
    /**
     * Mapping from {@link outputs} to indexes.
     */
    private Map<DataUnit, Integer> indexes;
    
	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	private ApplicationEventPublisher eventPublisher;
	
	/**
	 * Logger class.
	 */
	private static final Logger LOG = Logger.getLogger(ExtendedExtractContextImpl.class);
	
	public ExtendedExtractContextImpl(String id, PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo context, AppConfig appConfig) throws IOException {
		super(id, execution, dpuInstance, context, appConfig);
		this.outputs = new LinkedList<>();
		this.indexes = new HashMap<>();
		this.eventPublisher = eventPublisher;
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
		for (DataUnit item : outputs) {
			item.release();
		}
	}

	@Override
	public void save() {
		for (DataUnit item : outputs) {		
			try {
				// get directory
				File directory = context.getDataUnitStorage(getDPUInstance(), indexes.get(item));
				// and save into directory
				item.save(directory);
			} catch (Exception e) {
				LOG.error("Can't save DataUnit", e);
			}
		}
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		// check for type changes
		type = checkType(type);
		// gather information for new DataUnit
		Integer index = context.createOutput(dpuInstance, name, type);
		String id = context.generateDataUnitId(dpuInstance, index);
		File directory = new File(getWorkingDir(),
				context.getDataUnitTmpPath(dpuInstance, index) );
		// create instance
		return dataUnitFactory.createInput(type, id, name, directory);
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name, Object config)
			throws DataUnitCreateException {		
		// check for type changes
		type = checkType(type);
		// gather information for new DataUnit
		Integer index = context.createOutput(dpuInstance, name, type);
		String id = context.generateDataUnitId(dpuInstance, index);
		File directory = new File(getWorkingDir(),
				context.getDataUnitTmpPath(dpuInstance, index) );
		// create instance
		return dataUnitFactory.createInput(type, id, name, directory, config);
	}

	@Override
	public List<DataUnit> getOutputs() {
		return outputs;
	}

		
}
