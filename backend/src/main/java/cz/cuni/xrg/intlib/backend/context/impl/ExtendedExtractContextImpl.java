package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactoryImpl;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextFactory;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextWriter;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author Petyr
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
	 * Application event publisher used to publish messages from DPU.
	 */
	private ApplicationEventPublisher eventPublisher;
	
	public ExtendedExtractContextImpl(String id, PipelineExecution execution, DPUInstance dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextWriter contextWriter) {
		this.extendedImp = new ExtendedCommonImpl(id, execution, dpuInstance, contextWriter);
		this.outputs = new LinkedList<DataUnit>();
		this.eventPublisher = eventPublisher;		
	}
	
	@Override
	public List<DataUnit> getOutputs() {		
		return outputs;
	}
	
	@Override
	public void addOutputDataUnit(DataUnit dataUnit) {
		outputs.add(dataUnit);		
	}

	@Override
	public String storeData(Object object) throws Exception {
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
		// TODO Auto-generated method stub
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
	public DataUnitFactory getDataUnitFactory() {
		return extendedImp.getDataUnitFactory();
	}	
	
	@Override
	public PipelineExecution getPipelineExecution() {		
		return extendedImp.getPipelineExecution();
	}

	@Override
	public DPUInstance getDPUInstance() {
		return extendedImp.getDPUInstance();
	}

	@Override
	public void release() {
		for (DataUnit item : outputs) {
			item.release();
		}		
	}
		
}
