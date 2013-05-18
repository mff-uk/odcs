package cz.cuni.xrg.intlib.backend.context.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import cz.cuni.xrg.intlib.backend.context.ExtendedContext;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactoryImpl;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextWriter;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;

/**
 * Provide implementation of commons methods for 
 * {@link ExtendedExtractContextImpl}, {@link ExtendedLoadContextImpl} 
 * and {@link ExtendedTransformContextImpl}
 * 
 * @author Petyr
 *
 */
class ExtendedCommonImpl {

	/**
	 * Unique context id.
	 */
	private String id;
	
    /**
     * Custom data holder.
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
    	
	/**
	 * Used factory.
	 */
	private DataUnitFactoryImpl dataUnitFactory;
	
	/**
	 * Manage mapping context into execution's directory. 
	 */
	private ExecutionContextWriter contextWriter;
	
	/**
	 * Counter used to generate unique id for data.
	 */
	private int storeCounter;	
		
	public ExtendedCommonImpl(String id, PipelineExecution execution, DPUInstance dpuInstance, 
			ExecutionContextWriter contextWriter) {
		this.id = id;
		this.customData = new HashMap<String, Object>();
		this.isDebugging = execution.isDebugging();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
		this.dataUnitFactory = new DataUnitFactoryImpl(this.id, contextWriter, dpuInstance);
		this.contextWriter = contextWriter;
		this.storeCounter = 0;
	}	
	
	public String storeData(Object object) throws Exception {
		String id = Integer.toString(this.storeCounter) + ".ser";
		++this.storeCounter;
		// ...
		try
		{
			FileOutputStream fileOut =
				new FileOutputStream( new File(contextWriter.getDirForDPUStorage(dpuInstance), id) );
			ObjectOutputStream outStream =
				new ObjectOutputStream(fileOut);
			outStream.writeObject(object);
			outStream.close();
			fileOut.close();
		} catch(IOException i) {
			Logger.getLogger(ExtendedExtractContextImpl.class).error(i);
		}				
		return null;
	}

	public Object loadData(String id) {
		Object result = null;
		try {
			FileInputStream fileIn = 
					new FileInputStream( new File(contextWriter.getDirForDPUStorage(dpuInstance), id));
			ObjectInputStream inStream = new ObjectInputStream(fileIn);
			result = inStream.readObject();
			inStream.close();
			fileIn.close();
		} catch (IOException e) {
			Logger.getLogger(ExtendedExtractContextImpl.class).error(e);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(ExtendedExtractContextImpl.class).error(e);
		}
		return result;
	}

	public void storeDataForResult(String id, Object object) {
		extendedImp.storeDataForResult(id, object);	
	}

	public boolean isDebugging() {
		return isDebugging;
	}

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public DataUnitFactory getDataUnitFactory() {
		return dataUnitFactory;
	}	
	
	public PipelineExecution getPipelineExecution() {
		return execution;
	}

	public DPUInstance getDPUInstance() {
		return dpuInstance;
	}	
}
