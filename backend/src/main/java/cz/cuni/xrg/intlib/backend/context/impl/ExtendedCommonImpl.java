package cz.cuni.xrg.intlib.backend.context.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContext;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextReader;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;

/**
 * Provide implementation of commons methods for 
 * {@link ExtendedExtractContextImpl}, {@link ExtendedLoadContextImpl} 
 * and {@link ExtendedTransformContextImpl}
 * 
 * For details about methods see {@link cz.cuni.xrg.intlib.commons.ProcessingContext} 
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
     * True id the related DPURecord should be run in debug mode.
     */
    private boolean isDebugging;
	
    /**
     * PipelineExecution. The one who caused
     * run of this DPURecord.
     */
	private PipelineExecution execution;

	/**
	 * DPUInstanceRecord as owner of this context.
	 */
	private DPUInstanceRecord dpuInstance;
    	
	/**
	 * Used factory.
	 */
	private DataUnitFactory dataUnitFactory;
	
	/**
	 * Manage mapping context into execution's directory. 
	 */
	private ExecutionContext context;
	
	/**
	 * Counter used to generate unique id for data.
	 */
	private int storeCounter;	
	
	/**
	 * Log facade.
	 */
	private static final Logger LOG = Logger.getLogger(ExtendedCommonImpl.class);
	
	/**
	 * 
	 * @param id Context id.
	 * @param execution Associated pipelineExecution.
	 * @param dpuInstance Associated dpuInstanceRecord ~ owner.
	 * @param context Access to context 'manager'.
	 */
	public ExtendedCommonImpl(String id, PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ExecutionContext contextWriter) {
		this.id = id;
		this.customData = new HashMap<String, Object>();
		this.isDebugging = execution.isDebugging();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
		this.dataUnitFactory = new DataUnitFactory(this.id, contextWriter, dpuInstance);
		this.context = contextWriter;
		this.storeCounter = 0;
	}	
	
	public String storeData(Object object) {
		String id = Integer.toString(this.storeCounter) + ".ser";
		++this.storeCounter;
		// ...
		// determine file
		File file = new File(context.createDirForDPUStorage(dpuInstance), id);
		// save data into file
		try (FileOutputStream fileOutStream = new FileOutputStream(file) ) {
			ObjectOutputStream outStream = new ObjectOutputStream(fileOutStream);
			outStream.writeObject(object);
			outStream.close();
		} catch (IOException e) {
			LOG.error("loadData", e);
			throw new RuntimeException("Can't save object.", e);
		}
		return id;
	}

	public Object loadData(String id) {
		Object result = null;
		// determine file
		File file = new File(context.createDirForDPUStorage(dpuInstance), id);
		// try to load data from file
		try (FileInputStream fileInStream = new FileInputStream(file)) {
			ObjectInputStream inStream = new ObjectInputStream(fileInStream);
			result = inStream.readObject();
			inStream.close();
		}  catch (FileNotFoundException e) {
			LOG.error("loadData: FileNotFoundException", e);
		} catch (IOException e) {
			LOG.error("loadData: IOException", e);
		} catch (ClassNotFoundException e) {
			LOG.error("loadData: ClassNotFoundException", e);
		}

		return result;
	}

	public void storeDataForResult(String id, Object object) {
		// TODO Petyr: store data for result
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

	public DPUInstanceRecord getDPUInstance() {
		return dpuInstance;
	}	
	
	public ExecutionContextReader getContext() {
		return context;
	}
}
