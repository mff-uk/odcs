package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.data.DataUnitContainer;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
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
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo context) throws IOException {
		super(id, execution, dpuInstance, context);
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
	public DataUnit addOutputDataUnit(DataUnitType type)
			throws DataUnitCreateException {
		// create data unit
		DataUnitContainer dataUnitContainer = dataUnitFactory.createOutput(type);
		// store mapping
		indexes.put(dataUnitContainer.getDataUnit(), dataUnitContainer.getIndex());
		// add to outputs
		outputs.add(dataUnitContainer.getDataUnit());
		// return new DataUnit
		return dataUnitContainer.getDataUnit();
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, Object config)
			throws DataUnitCreateException {		
		// create data unit
		DataUnitContainer dataUnitContainer = dataUnitFactory.createOutput(type, config);
		// store mapping
		indexes.put(dataUnitContainer.getDataUnit(), dataUnitContainer.getIndex());
		// add to outputs
		outputs.add(dataUnitContainer.getDataUnit());
		// return new DataUnit
		return dataUnitContainer.getDataUnit();
	}

	@Override
	public List<DataUnit> getOutputs() {
		return outputs;
	}

		
}
