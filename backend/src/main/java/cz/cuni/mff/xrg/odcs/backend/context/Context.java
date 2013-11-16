package cz.cuni.mff.xrg.odcs.backend.context;

import java.io.File;
import java.util.Date;
import java.util.List;

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
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
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
	private static final Logger LOG = LoggerFactory.getLogger(Context.class);

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
	
	/**
	 * True if the execution should be stopped on DPU's request. The execution
	 * does not failed instantly by this.
	 */
	private boolean stopExecution;
	
	public Context() {
		this.dpuInstance = null;
		this.contextInfo = null;
		this.lastSuccExec = null;
		this.inputsManager = null;
		this.outputsManager = null;
		this.warningMessage = false;
		this.errorMessage = false;
		this.canceled = false;
		this.stopExecution = false;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - //
	
	public DPUInstanceRecord getDPU() {
		return dpuInstance;
	}
	
	void setDPU(DPUInstanceRecord dpu) {
		this.dpuInstance = dpu;
	}
	
	public ExecutionContextInfo getContextInfo() {
		return contextInfo;
	}
	
	void setContextInfo(ExecutionContextInfo contextInfo) {
		this.contextInfo = contextInfo;
	}
	
	public Date getLastSuccExec() {
		return lastSuccExec;
	}
	
	void setLastSuccExec(Date lastSuccExec) {
		this.lastSuccExec = lastSuccExec;
	}
	
	DataUnitManager getOutputsManager() {
		return outputsManager;
	}
	
	void setOutputsManager(DataUnitManager manager) {
		this.outputsManager = manager;
	}
	
	DataUnitManager getInputsManager() {
		return inputsManager;
	}
	
	void setInputsManager(DataUnitManager manager) {
		this.inputsManager = manager;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - //
	
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
     * @return 
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
	 * True if the execution should be stopped but not failed instantly.
	 * @return 
	 */
	public boolean shouldStopExecution() {
		return stopExecution;
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
		return dpuInstance.getTemplate().getJarDirectory();
	}	

	// - - - - - - - - - - ProcessingContext - - - - - - - - - - //
	
	@Override
	public void sendMessage(MessageType type, String shortMessage) {
        // jest re-call the other function
        sendMessage(type, shortMessage, "");
	}
	
	@Override
	public void sendMessage(MessageType type,
			String shortMessage,
			String fullMessage) {
		eventPublisher.publishEvent(new DPUMessage(shortMessage, fullMessage,
				type, this, this));
		// set warningMessage and errorMessage 
		switch(type) {
			case WARNING:
				this.warningMessage = true;
				break;
			case ERROR:
				this.errorMessage = true;
				break;
			case TERMINATION_REQUEST:
				this.stopExecution = true;
				break;
			default:			
		}
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
		User owner = getExecution().getOwner();
		String userId; 
		if (owner == null) {
			userId = "default";
		} else {
			// user name is unique .. we can use it
			userId = owner.getUsername();
		}
		
		File result = new File(getGeneralWorkingDir(), USER_DIR + File.separator
				+ userId + File.separator + getTemplateIdentification());
		result.mkdirs();
		return result;
	}
		
}
