package cz.cuni.xrg.intlib.backend.context.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Provide implementation of commons methods for 
 * {@link ExtendedExtractContextImpl}, {@link ExtendedLoadContextImpl} 
 * and {@link ExtendedTransformContextImpl}
 * 
 * For details about methods see {@link cz.cuni.xrg.intlib.commons.context.ProcessingContext} 
 * 
 * @author Petyr
 *
 */
class ExtendedCommonImpl {

	/**
	 * Unique context id.
	 */
	@Deprecated
	protected String id;
	
    /**
     * Custom data holder.
     */
	protected Map<String, Object> customData;

    /**
     * True id the related DPURecord should be run in debug mode.
     */
	@Deprecated
	protected boolean isDebugging;
	
    /**
     * PipelineExecution. The one who caused
     * run of this DPURecord.
     */
	@Deprecated
	protected PipelineExecution execution;

	/**
	 * DPUInstanceRecord as owner of this context.
	 */
	protected DPUInstanceRecord dpuInstance;
    	
	/**
	 * Used factory.
	 */
	protected DataUnitFactory dataUnitFactory;
	
	/**
	 * Manage mapping context into execution's directory. 
	 */
	protected ExecutionContextInfo context;
	
	/**
	 * App configuration.
	 */
	protected AppConfig appConfig;
	
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
	 * @param appCofig Application's configuration.
	 * @param jarFile Path to the jar file.
	 * @throws IOException 
	 */
	public ExtendedCommonImpl(String id, PipelineExecution execution, DPUInstanceRecord dpuInstance, 
			ExecutionContextInfo context, AppConfig appConfig) throws IOException {
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
		this.dataUnitFactory = new DataUnitFactory(this.id, dpuInstance, context, appConfig);
		this.context = context;
		this.appConfig = appConfig;
	}	
	
	/**
	 * Check required type based on application configuration and return
	 * {@link DataUnitType} that should be created. Can thrown
	 * {@link DataUnitCreateException} in case of unknown {@link DataUnitType}.
	 * 
	 * @param type Required type.
	 * @return Type to create.
	 * @throws DataUnitCreateException
	 */
	protected DataUnitType checkType(DataUnitType type)
			throws DataUnitCreateException {
		if (type == DataUnitType.RDF) {
			// select other DataUnit based on configuration
			String defRdfRepo = appConfig
					.getString(ConfigProperty.BACKEND_DEFAULTRDF);
			if (defRdfRepo == null) {
				// use local
				type = DataUnitType.RDF_Local;
			} else {
				// choose based on value in appConfig
				if (defRdfRepo.compareToIgnoreCase("virtuoso") == 0) {
					// use virtuoso
					type = DataUnitType.RDF_Virtuoso;
				} else if (defRdfRepo.compareToIgnoreCase("localRDF") == 0) {
					// use local
					type = DataUnitType.RDF_Local;
				} else {
					throw new DataUnitCreateException(
							"The data unit type is unknown."
									+ "Check the value of the parameter "
									+ "backend.defaultRDF in config.properties");
				}
			}
		}
		return type;
	}	
	
	public File getWorkingDir() {
		File directory = context.getTmp(dpuInstance);
		directory.mkdirs();
		return directory;
	}

	/**
	 * Return pipeline's execution root directory.
	 * 
	 * @return
	 */
	public File getExecutionDir() {
		return null;
	}
	
	public File getResultDir() {
		File directory = context.getResult(dpuInstance);
		directory.mkdirs();
		return directory;

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
	
	public File getJarPath() {
		File path = new File (appConfig.getString(ConfigProperty.MODULE_PATH) + dpuInstance.getJarPath());
		return path;
	}
}
