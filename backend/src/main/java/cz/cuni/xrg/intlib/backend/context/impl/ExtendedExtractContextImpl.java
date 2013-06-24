package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author Petyr
 * 
 */
public class ExtendedExtractContextImpl implements ExtendedExtractContext {

	/**
	 * Provide implementation for some common context methods.
	 */
	ExtendedCommonImpl extendedImp;
		
	/**
	 * Context output data units.
	 */
    private List<DataUnit> outputs;
    	
	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	private ApplicationEventPublisher eventPublisher;
	
	/**
	 * Logger class.
	 */
	private static final Logger LOG = Logger.getLogger(ExtendedExtractContextImpl.class);
	
	public ExtendedExtractContextImpl(String id, PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo contextWriter) throws IOException {
		this.extendedImp = new ExtendedCommonImpl(id, execution, dpuInstance, contextWriter);
		this.outputs = new LinkedList<>();
		this.eventPublisher = eventPublisher;
	}
	
	@Override
	public String storeData(Object object) {
		return extendedImp.storeData(object);
	}

	@Override
	public Object loadData(String id) {
		return extendedImp.loadData(id);
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
	public void storeDataForResult(String id, Object object) {
		extendedImp.storeDataForResult(id, object);
	}

	@Override
	public boolean isDebugging() {		
		return extendedImp.isDebugging();
	}

	@Override
	public Map<String, Object> getCustomData() {
		return extendedImp.getCustomData();
	}
	
	@Override
	public PipelineExecution getPipelineExecution() {		
		return extendedImp.getPipelineExecution();
	}

	@Override
	public DPUInstanceRecord getDPUInstance() {
		return extendedImp.getDPUInstance();
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
				item.save();
			} catch (Exception e) {
				LOG.error("Can't save DataUnit", e);
			}
		}
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type)
			throws DataUnitCreateException {
		return extendedImp.getDataUnitFactory().createOutput(type);
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, Object config)
			throws DataUnitCreateException {
		return extendedImp.getDataUnitFactory().createOutput(type, config);
	}

	@Override
	public List<DataUnit> getOutputs() {
		return outputs;
	}
		
}
