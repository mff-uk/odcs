package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
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
 * Implementation of ExtendedLoadContext.
 *
 * @author Petyr
 * 
 */
class ExtendedLoadContextImpl extends ExtendedCommonImpl implements ExtendedLoadContext {
	
	/**
	 * Context input data units.
	 */
    private List<DataUnit> inputs = new LinkedList<>();
        
	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	private ApplicationEventPublisher eventPublisher;	
	
	/**
	 * Logger class.
	 */
	private static final Logger LOG = Logger.getLogger(ExtendedLoadContextImpl.class);	
	
	public ExtendedLoadContextImpl(String id, PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo context, AppConfig appConfig) throws IOException {
		super(id, execution, dpuInstance, context, appConfig);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<DataUnit> getInputs() {		
		return this.inputs;
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
		for (DataUnit item : inputs) {
			item.release();
		}		
	}	
	
	@Override
	public void save() {
		// we have no mapping from input to indexes .. so we just assign numbers
		Integer index = 0;
		for (DataUnit item : inputs) {		
			try {
				// get directory
				File directory = context.getDataUnitStorage(getDPUInstance(), index++);
				// and save into directory
				item.save(directory);
			} catch (Exception e) {
				LOG.error("Can't save DataUnit", e);
			}
		}
	}	
	
	@Override
	public void sealInputs() {
		for (DataUnit inputDataUnit : inputs) {
			inputDataUnit.madeReadOnly();
		}
	}
	
	@Override
	public void addSource(ProcessingContext context, DataUnitMerger merger, 
			String instruction) throws ContextException {
		// merge custom data
		try {
			customData.putAll(context.getCustomData());
		} catch(Exception e) {
			throw new ContextException("Error while merging custom data.", e);
		}
		// now based on context type ..
		if (context instanceof ExtendedExtractContext) {
			ExtendedExtractContext extractContext = (ExtendedExtractContext)context;
			// primitive merge .. 
			merger.merger(inputs, extractContext.getOutputs(), dataUnitFactory, instruction);
		} else if (context instanceof ExtendedTransformContext) {
			ExtendedTransformContext transformContext = (ExtendedTransformContext)context;
			// primitive merge .. 
			merger.merger(inputs, transformContext.getOutputs(), dataUnitFactory, instruction);
		} else {
			throw new ContextException("Wrong context type: " + context.getClass().getSimpleName());
		}
	}

}
