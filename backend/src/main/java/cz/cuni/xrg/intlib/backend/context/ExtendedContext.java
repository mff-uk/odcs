package cz.cuni.xrg.intlib.backend.context;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.context.ProcessingContext;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * 
 * 
 * @author Petyr
 * 
 */
@Component
public abstract class ExtendedContext implements ProcessingContext {

	/**
	 * Name of directory for shared DPU's data.
	 */
	private static final String DPU_DIR = "dpu";

	/**
	 * Name of sub-directory in {@link #DPU_DIR} for user related data storage.
	 */
	private static final String USER_DIR = "user";

	/**
	 * Logger class for ExtendedCommonImpl class.
	 */
	private static Logger LOG = LoggerFactory.getLogger(ExtendedContext.class);

	/**
	 * Custom data holder.
	 */
	protected Map<String, Object> customData;

	/**
	 * PipelineExecution. The one who caused run of this DPURecord.
	 */
	private PipelineExecution execution;

	/**
	 * DPUInstanceRecord as owner of this context.
	 */
	protected DPUInstanceRecord dpuInstance;

	/**
	 * Manage mapping context into execution's directory.
	 */
	protected ExecutionContextInfo context;

	/**
	 * Time of last successful execution. Null if there is no such execution.
	 */
	private Date lastSuccExec;

	/**
	 * Used factory.
	 */
	@Autowired
	protected DataUnitFactory dataUnitFactory;

	/**
	 * Application configuration.
	 */
	@Autowired
	protected AppConfig appConfig;

	/**
	 * Application event publisher used to publish messages from DPURecord.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public ExtendedContext() {
		this.customData = new HashMap<>();
		this.execution = null;
		this.dpuInstance = null;
		this.context = null;
		this.lastSuccExec = null;
	}

	// --------------- new abstract methods in ExtendedContext -------------- //

	/**
	 * Save all data units.
	 */
	public abstract void save();

	/**
	 * Release all locks from context and DataUnits do not delete data.
	 */
	public abstract void release();

	/**
	 * Release all lock from context and DataUnits. Also delete all stored
	 * {@link DataUnit}s and related contex's directories.
	 */
	public abstract void delete();

	/**
	 * Reload DataUnit's if they are not loaded.
	 * 
	 * @throws DataUnitCreateException
	 */
	public abstract void reload() throws DataUnitException;

	// ------------------ new methods in ExtendedContext -------------------- //

	/**
	 * Bind context to the execution and dpuInstance.
	 * 
	 * @param execution
	 * @param dpuInstance
	 * @param context
	 * @param lastSuccExec
	 */
	public void init(PipelineExecution execution,
			DPUInstanceRecord dpuInstance,
			ExecutionContextInfo context,
			Date lastSuccExec) {
		this.execution = execution;
		this.dpuInstance = dpuInstance;
		this.context = context;
		this.lastSuccExec = lastSuccExec;
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

	/**
	 * Delete directory if exist. If error occur is logged and silently ignored.
	 * 
	 * @param directory
	 */
	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			try {
				FileUtils.deleteDirectory(directory);
			} catch (IOException e) {
				LOG.error("Can't delete directory {}", directory.toString(), e);
			}
		}
	}

	/**
	 * Delete all DPU context's directories.
	 */
	protected void deleteDirectories() {
		// DPU' tmp directory
		final File workingDir = getWorkingDir();
		deleteDirectory(workingDir);

		// DataUnit storage directory
		final File dpuStoragePath = new File(getGeneralWorkingDir(),
				context.getDataUnitRootStoragePath(dpuInstance));
		deleteDirectory(dpuStoragePath);

		// DataUnit tmp directory
		final File dpuTmpPath = new File(getGeneralWorkingDir(),
				context.getDataUnitRootTmpPath(dpuInstance));
		deleteDirectory(dpuTmpPath);

		// Result directory is shared by whole pipeline
		// and so it's not deleted from here
	}

	/**
	 * Return engine's general working directory.
	 * 
	 * @return
	 */
	public File getGeneralWorkingDir() {
		return new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
	}

	/**
	 * Return existing DPU's execution working directory.
	 * 
	 * @return
	 */
	public File getWorkingDir() {
		File directory = new File(getGeneralWorkingDir(),
				context.getDPUTmpPath(dpuInstance));
		directory.mkdirs();
		return directory;
	}

	/**
	 * Return existing execution's result directory.
	 * 
	 * @return
	 */
	public File getResultDir() {
		File directory = new File(getGeneralWorkingDir(),
				context.getResultPath());
		directory.mkdirs();
		return directory;
	}

	/**
	 * Return identification of single DPU template shared by all templates with
	 * same name.
	 * 
	 * @return
	 */
	private String getTemplateIdentification() {
		return dpuInstance.getJarPath();
	}

	// --- implementation of methods from backend.context.ExtendedContext --- //

	/**
	 * Return related pipeline execution.
	 * 
	 * @return
	 */
	public PipelineExecution getPipelineExecution() {
		return execution;
	}

	/**
	 * Return owner of the context. (The one who work with it.)
	 * 
	 * @return
	 */
	public DPUInstanceRecord getDPUInstance() {
		return dpuInstance;
	}

	// -- implementation of methods from commons.context.ProcessingContext -- //

	public void sendMessage(MessageType type, String shortMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, "", type,
				this, this));
	}

	public void sendMessage(MessageType type,
			String shortMessage,
			String fullMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, fullMessage,
				type, this, this));
	}

	public boolean isDebugging() {
		return execution.isDebugging();
	}

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public File getJarPath() {
		File path = new File(appConfig.getString(ConfigProperty.MODULE_PATH)
				+ dpuInstance.getJarPath());
		return path;
	}

	public Date getLastExecutionTime() {
		return lastSuccExec;
	}

	public File getGlobalDirectory() {
		File result = new File(getGeneralWorkingDir(), DPU_DIR + File.separator
				+ getTemplateIdentification());
		result.mkdirs();
		return result;
	}

	public File getUserDirectory() {
		File result = new File(getGeneralWorkingDir(), DPU_DIR + File.separator
				+ USER_DIR + File.separator + getTemplateIdentification());
		result.mkdirs();
		return result;
	}

}
