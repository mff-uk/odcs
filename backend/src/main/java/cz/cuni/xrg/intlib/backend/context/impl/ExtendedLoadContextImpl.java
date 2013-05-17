package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactoryImpl;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
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
public class ExtendedLoadContextImpl implements ExtendedLoadContext {

	/**
	 * Provide implementation for some common context methods.
	 */
	ExtendedCommonImpl extendedImp;
	
	/**
	 * Context input data units.
	 */
    private List<DataUnit> intputs = new LinkedList<DataUnit>();
        
	/**
	 * Application event publisher used to publish messages from DPU.
	 */
	private ApplicationEventPublisher eventPublisher;	
	
	public ExtendedLoadContextImpl(String id, PipelineExecution execution, DPUInstance dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextWriter contextWriter) {
		this.extendedImp = new ExtendedCommonImpl(id, execution, dpuInstance, contextWriter);
		this.intputs = new LinkedList<DataUnit>();
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<DataUnit> getInputs() {		
		return this.intputs;
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
		for (DataUnit item : intputs) {
			item.release();
		}		
	}	
	
	@Override
	public void sealInputs() {
		for (DataUnit inputDataUnit : intputs) {
			inputDataUnit.madeReadOnly();
		}
	}
	
	@Override
	public void addSource(ProcessingContext context, DataUnitMerger merger) throws ContextException {
		// merge custom data
		try {
			extendedImp.getCustomData().putAll(context.getCustomData());
		} catch(Exception e) {
			throw new ContextException("Error while merging custom data.", e);
		}
		// now based on context type ..
		if (context instanceof ExtendedExtractContext) {
			ExtendedExtractContext extractContext = (ExtendedExtractContext)context;
			// primitive merge .. 
			merger.merger(intputs, extractContext.getOutputs(), extendedImp.getDataUnitFactory());
		} else if (context instanceof ExtendedTransformContext) {
			ExtendedTransformContext transformContext = (ExtendedTransformContext)context;
			// primitive merge .. 
			merger.merger(intputs, transformContext.getOutputs(), extendedImp.getDataUnitFactory());
		} else {
			throw new ContextException("Wrong context type: " + context.getClass().getSimpleName());
		}
	}

}
