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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
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
	private ExecutionContextInfo context;
	
	/**
	 * Counter used to generate unique id for data.
	 */
	private int storeCounter;	
	
	/**
	 * Log facade.
	 */
	private static final Logger LOG = Logger.getLogger(ExtendedCommonImpl.class);
	
	/**
	 * If the working directory for Context already exist then the ctor try to delete it.
	 * @param id Context id.
	 * @param execution Associated pipelineExecution.
	 * @param dpuInstance Associated dpuInstanceRecord ~ owner.
	 * @param context Access to context 'manager'.
	 * @throws IOException 
	 */
	public ExtendedCommonImpl(String id, PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ExecutionContextInfo context) throws IOException {
		this.id = id;
		this.customData = new HashMap<String, Object>();
		this.isDebugging = execution.isDebugging();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
		File workingDir = new File(context.getWorkingDirectory(), id);
		// if working directory exist, try to delete it 
		if (workingDir.exists()) {
			if (workingDir.isDirectory()) {
				FileUtils.deleteDirectory(workingDir);
			} else {
				FileUtils.forceDelete(workingDir);
			}
		}
		this.dataUnitFactory = new DataUnitFactory(this.id, workingDir);
		this.context = context;
		this.storeCounter = 0;
	}	
	
	public String storeData(Object object) {
		String id = Integer.toString(this.storeCounter) + ".tmp";
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
		File resultDir = context.createDirForDPUResult(dpuInstance);
		File file = new File(resultDir, id);
		// save data into file
		try (FileOutputStream fileOutStream = new FileOutputStream(file) ) {
			ObjectOutputStream outStream = new ObjectOutputStream(fileOutStream);
			outStream.writeObject(object);
			outStream.close();
		} catch (IOException e) {
			LOG.error("loadData", e);
			throw new RuntimeException("Can't save object.", e);
		}		
		
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
	
	public ExecutionContextInfo getContext() {
		return context;
	}
}
