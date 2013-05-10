package cz.cuni.xrg.intlib.backend.context.impl;

import cz.cuni.xrg.intlib.backend.context.ExtractContext;
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
public class ExtractContextImpl implements ExtractContext {

	/**
	 * Context output data units.
	 */
    private List<DataUnit> outputs;
    
    /**
     * Storage for custom information.
     */
    private Map<String, Object> customData;

    /**
     * True id the related DPU should be run in debug mode.
     */
    private boolean isDebugging;
    
    /**
     * PipelineExecution. The one who caused
     * run of this DPU.
     */
	private PipelineExecution execution;

	/**
	 * Instance of DPU for which is this context.
	 */
	private DPUInstance dpuInstance;
	
	@Override
	public List<DataUnit> getOutputs() {		
		return outputs;
	}

	public ExtractContextImpl(PipelineExecution execution, DPUInstance dpuInstance) {
		this.outputs = new LinkedList<DataUnit>();
		this.customData = new HashMap<String, Object>();
		this.isDebugging = execution.isDebugging();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
	}
	
	@Override
	public void addOutputDataUnit(DataUnit dataUnit) {
		outputs.add(dataUnit);		
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

}
