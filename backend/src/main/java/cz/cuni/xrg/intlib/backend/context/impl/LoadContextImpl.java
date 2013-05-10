package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.LoadContext;
import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Petyr
 */
public class LoadContextImpl implements LoadContext {

	/**
	 * Context input data units.
	 */
    private List<DataUnit> intputs = new LinkedList<DataUnit>();
    
    /**
     * Storage for custom information.
     */
    private Map<String, Object> customData = null;

    /**
     * True id the related DPU should be run in debug mode.
     */
    private boolean isDebugging = false;
    
    /**
     * PipelineExecution. The one who caused
     * run of this DPU.
     */
	private PipelineExecution execution;

	/**
	 * Instance of DPU for which is this context.
	 */
	private DPUInstance dpuInstance;    
    
	public LoadContextImpl(PipelineExecution execution, DPUInstance dpuInstance) {
		this.intputs = new LinkedList<DataUnit>();
		this.customData = new HashMap<String, Object>();
		this.isDebugging = execution.isDebugging();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
	}

	@Override
	public List<DataUnit> getInputs() {		
		return this.intputs;
	}

	@Override
	public String storeData(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object loadData(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(Type type, String shortMessage) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void sendMessage(Type type, String shortMessage, String fullMessage) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void storeDataForResult(String id, Object object) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean isDebugging() {
		return isDebugging;
	}

	@Override
	public Map<String, Object> getCustomData() {
		return customData;
	}

	@Override
	public PipelineExecution getPipelineExecution() {
		return execution;
	}

	@Override
	public DPUInstance getDPUInstance() {
		return dpuInstance;
	}

	@Override
	public void addSource(ProcessingContext context) throws ContextException {
		// TODO Auto-generated method stub		
	}    
}
