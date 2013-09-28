package cz.cuni.mff.xrg.odcs.backend.context;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.backend.dpu.event.DPUMessage;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;

public class Context implements DPUContext {

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
	private static Logger LOG = LoggerFactory.getLogger(Context.class);

	/**
	 * DPUInstanceRecord as owner of this context.
	 */
	protected DPUInstanceRecord dpuInstance;

	/**
	 * Manage mapping context into execution's directory.
	 */
	protected ExecutionContextInfo contextInfo;

	/**
	 * Time of last successful execution. Null if there is no such execution.
	 */
	private Date lastSuccExec;

	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager inputsManager;

	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager outputsManager;	
	
	/**
	 * Used DataUnit factory.
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
	
	/**
	 * Used to get DPU's directory.
	 */
	@Autowired
	private ModuleFacade moduleFacade;
	
	/**
	 * True if {@link #sendMessage(MessageType, String)} or 
	 * {@link #sendMessage(MessageType, String, String)} has been used to 
	 * publish {@link MessageType#WARNING} message.
	 */
	private boolean warningMessage;
	
	/**
	 * True if {@link #sendMessage(MessageType, String)} or 
	 * {@link #sendMessage(MessageType, String, String)} has been used to 
	 * publish {@link MessageType#ERROR} message.
	 */	
	private boolean errorMessage;
	
	/**
	 * Set to true if the current DPU execution should be stopped 
	 * as soon as possible.
	 */
	private boolean canceled;
	
	public Context() {
		this.dpuInstance = null;
		this.contextInfo = null;
		this.lastSuccExec = null;
		this.inputsManager = null;
		this.outputsManager = null;
		this.warningMessage = false;
		this.errorMessage = false;
		this.canceled = false;
	}
	
	/**
	 * Bind the context to the {@link PipelineExecution} and {@link DPUInstanceRecord}.
	 * Must be called before future using of the {@link Context} class.
	 * @param dpuInstance
	 * @param context
	 * @param lastSuccExec
	 */
	public void bind(DPUInstanceRecord dpuInstance, 
			ExecutionContextInfo contextInfo, Date lastSuccExec) {
		this.dpuInstance = dpuInstance;
		this.contextInfo = contextInfo;
		this.lastSuccExec = lastSuccExec;
		// create DataUnit manager
		this.inputsManager = DataUnitManager.createInputManager(dpuInstance,
				dataUnitFactory, contextInfo, getGeneralWorkingDir(), appConfig);
		// create DataUnit manager
		this.outputsManager = DataUnitManager.createOutputManager(dpuInstance,
				dataUnitFactory, contextInfo, getGeneralWorkingDir(), appConfig);		
	}
	
	/**
	 * Save all data units.
	 */
	public void sealInputs() {
		for (ManagableDataUnit item : inputsManager.getDataUnits()) {
			item.madeReadOnly();
		}
	}	
	
	/**
	 * Save all data units.
	 */
	public void save() {
		inputsManager.save();
		outputsManager.save();
	}
	
	/**
	 * Release all locks from context and DataUnits do not delete data.
	 */
	public void release() {
		inputsManager.release();
		outputsManager.release();		
	}

	/**
	 * Release all lock from context and DataUnits. Also delete all stored
	 * {@link DataUnit}s and related contex's directories.
	 */
	public void delete() {
		inputsManager.delete();
		outputsManager.delete();
		deleteDirectories();		
	}

	/**
	 * Reload DataUnit's if they are not loaded.
	 * 
	 * @throws DataUnitCreateException
	 */
	public void reload() throws DataUnitException {
		inputsManager.reload();
		outputsManager.reload();		
	}

	/**
	 * Add data from given context.
	 * @param context
	 * @param instruction
	 * @throws ContextException
	 */
	public void addContext(Context context, String instruction)
			throws ContextException {
		// create merger class
		DataUnitMerger merger = new DataUnitMerger();
		// merge dataUnits
		merger.merger(inputsManager, 
				context.outputsManager.getDataUnits(), instruction);
	}
	
	/**
	 * Create required {@link ManagableDataUnit} and add it to the context.
	 * @param type Type of {@link ManagableDataUnit} to create.
	 * @param name DataUnit name.
	 * @return Created DataUni.
	 * @throws DataUnitCreateException
	 */
	public ManagableDataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		return outputsManager.addDataUnit(type, name);
	}
	
	/**
	 * Set cancel flag for execution to true. This command DPU to stop
	 * as soon as possible. Can be called from other then DPU's execution
	 * thread.
	 */
	public void cancel() {
		this.canceled = true;
	}
	
	/**
	 * Return respective {@link PipelineExecution}.
	 * @return
	 */
	public PipelineExecution getExecution() {
		return contextInfo.getExecution();
	}
	
	/**
	 * Return respective {@link DPUInstanceRecord}
	 * @return
	 */
	public DPUInstanceRecord getDpuInstance() {
		return dpuInstance;
	}
	
	/**
	 * Return list of all input {@link ManagableDataUnit}s.
	 * @return
	 */
	public List<ManagableDataUnit> getInputs() {
		return inputsManager.getDataUnits();
	}	
	
	/**
	 * Return list of all output {@link ManagableDataUnit}s.
	 * @return
	 */
	public List<ManagableDataUnit> getOutputs() {
		return outputsManager.getDataUnits();
	}	
	
	/**
	 * 	Return true if the warning message has been publish using this context.
	 */
	public boolean warningMessagePublished() {
		return this.warningMessage;
	}
	
	/**
	 * Return true if the error message has been publish using this context.
	 * @return
	 */
	public boolean errorMessagePublished() {
		return this.errorMessage;
	}	
	
	/**
	 * Return engine's general working directory.
	 * 
	 * @return
	 */
	private File getGeneralWorkingDir() {
		return new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
	}	
	
	/**
	 * Return identification of single DPU template shared by all templates with
	 * same name.
	 * 
	 * @return
	 */
	private String getTemplateIdentification() {
		// TODO Petyr: Use something better
		return dpuInstance.getJarPath();
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
	private void deleteDirectories() {
		// DPU' tmp directory
		final File workingDir = getWorkingDir();
		deleteDirectory(workingDir);

		// DataUnit storage directory
		final File dpuStoragePath = new File(getGeneralWorkingDir(),
				contextInfo.getDataUnitRootStoragePath(dpuInstance));
		deleteDirectory(dpuStoragePath);

		// DataUnit tmp directory
		final File dpuTmpPath = new File(getGeneralWorkingDir(),
				contextInfo.getDataUnitRootTmpPath(dpuInstance));
		deleteDirectory(dpuTmpPath);

		// Result directory is shared by whole pipeline
		// and so it's not deleted from here
	}
	
	// - - - - - - - - - - ProcessingContext - - - - - - - - - - //
	
	@Override
	public void sendMessage(MessageType type, String shortMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, "", type,
				this, this));	
	}
	
	@Override
	public void sendMessage(MessageType type,
			String shortMessage,
			String fullMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, fullMessage,
				type, this, this));
		// set warningMessage and errorMessage 
		this.warningMessage = warningMessage || type == MessageType.WARNING;
		this.errorMessage = errorMessage || type == MessageType.ERROR;
	}
	
	@Override
	public boolean isDebugging() {
		return contextInfo.getExecution().isDebugging();
	}

	@Override
	public boolean canceled() {
		return canceled;
	}	
	
	@Override
	public File getWorkingDir() {
		File directory = new File(getGeneralWorkingDir(),
				contextInfo.getDPUTmpPath(dpuInstance));
		directory.mkdirs();
		return directory;
	}
	
	@Override
	public File getResultDir() {
		File directory = new File(getGeneralWorkingDir(),
				contextInfo.getResultPath());
		directory.mkdirs();
		return directory;
	}

	@Override
	public File getJarPath() {
        File path = new File(moduleFacade.getDPUDirectory() + 
        		File.separator + dpuInstance.getJarPath());
		return path;
	}
	
	@Override
	public Date getLastExecutionTime() {
		return lastSuccExec;
	}

	@Override
	public File getGlobalDirectory() {
		File result = new File(getGeneralWorkingDir(), DPU_DIR + File.separator
				+ getTemplateIdentification());
		result.mkdirs();
		return result;
	}

	@Override
	public File getUserDirectory() {
		File result = new File(getGeneralWorkingDir(), DPU_DIR + File.separator
				+ USER_DIR + File.separator + getTemplateIdentification());
		result.mkdirs();
		return result;
	}
	
}
