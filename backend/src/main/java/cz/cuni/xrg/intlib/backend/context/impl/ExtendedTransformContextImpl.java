package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextWriter;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.message.MessageType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author Petyr
 */
public class ExtendedTransformContextImpl implements ExtendedTransformContext {

	/**
	 * Provide implementation for some common context methods.
	 */
	ExtendedCommonImpl extendedImp;
	
	/**
	 * Context input data units.
	 */
    private List<DataUnit> intputs = new LinkedList<DataUnit>();
    
    /**
     * Context output data units.
     */
    private List<DataUnit> outputs = new LinkedList<DataUnit>();
     
	/**
	 * Application event publisher used to publish messages from DPU.
	 */
	private ApplicationEventPublisher eventPublisher;	

	public ExtendedTransformContextImpl(String id, PipelineExecution execution, DPUInstance dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextWriter contextWriter) {
		this.extendedImp = new ExtendedCommonImpl(id, execution, dpuInstance, contextWriter);
		this.intputs = new LinkedList<DataUnit>();
		this.outputs = new LinkedList<DataUnit>();
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<DataUnit> getInputs() {
		return intputs;
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
		for (DataUnit item : outputs) {
			item.release();
		}
	}	
	
	@Override
	public void save() {
		Logger.getLogger(ExtendedTransformContextImpl.class).debug("saving DataUnits");
		for (DataUnit item : intputs) {		
			try {
				item.save();
			} catch (Exception e) {
				Logger.getLogger(ExtendedTransformContextImpl.class).error("Can't save DataUnit", e);
			}
		}
		
		for (DataUnit item : outputs) {		
			try {
				item.save();
			} catch (Exception e) {
				Logger.getLogger(ExtendedTransformContextImpl.class).error("Can't save DataUnit", e);
			}
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
