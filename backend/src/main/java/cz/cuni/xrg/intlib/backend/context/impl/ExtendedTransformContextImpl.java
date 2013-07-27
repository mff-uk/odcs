package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of ExtendedTransformContext. 
 *
 * @author Petyr
 * 
 */
class ExtendedTransformContextImpl extends ExtendedCommonImpl implements ExtendedTransformContext {
	
	/**
	 * Context input data units.
	 */
    private List<DataUnit> inputs = new LinkedList<DataUnit>();
    
    /**
     * Context output data units.
     */
    private List<DataUnit> outputs = new LinkedList<DataUnit>();
    
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
	private final static Logger LOG = Logger.getLogger(ExtendedTransformContextImpl.class);	
	
	public ExtendedTransformContextImpl(PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ApplicationEventPublisher eventPublisher, ExecutionContextInfo context, AppConfig appConfig) throws IOException {
		super(execution, dpuInstance, context, appConfig);
		this.inputs = new LinkedList<>();
		this.outputs = new LinkedList<>();
		this.indexes = new HashMap<>();
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<DataUnit> getInputs() {
		return inputs;
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
		for (DataUnit item : outputs) {
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
				File directory = new File(getWorkingDir(),
						context.getDataUnitStoragePath(dpuInstance, ++index));

				// and save into directory
				item.save(directory);
			} catch (Exception e) {
				LOG.error("Can't save DataUnit", e);
			}
		}
		
		for (DataUnit item : outputs) {		
			try {
				// get directory
				File directory = new File(getWorkingDir(),
						context.getDataUnitStoragePath(dpuInstance, indexes.get(item)));

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

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		// check for type changes
		type = checkType(type);
		// gather information for new DataUnit
// TODO Petyr Use single class for DataUnit information		
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
// TODO Petyr Use single class for DataUnit information		
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
