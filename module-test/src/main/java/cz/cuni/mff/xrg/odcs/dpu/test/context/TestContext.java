package cz.cuni.mff.xrg.odcs.dpu.test.context;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;

/**
 *
 * @author Petyr
 *
 */
public class TestContext implements DPUContext {

	private static final Logger LOG = LoggerFactory.getLogger(TestContext.class);

	/**
	 * Root directory for execution.
	 */
	private File rootDirectory;

	/**
	 * Date of last execution.
	 */
	private Date lastExecution;

	/**
	 * Jar path.
	 */
	private String jarPath;

	/**
	 * True if DPU that use this context publish warning event.
	 */
	private boolean publishedWarning = false;

	/**
	 * True if DPU that use this context publish error event.
	 */
	private boolean publishedError = false;

	/**
	 * Working directory, if null then the working subdirectory in
	 * {@link #rootDirectory} is used.
	 */
	private File workingDirectory = null;

	/**
	 * Result directory, if null then the result subdirectory in
	 * {@link #rootDirectory} is used.
	 */
	private File resultDirectory = null;

	/**
	 * Global DPU directory, if null then the global subdirectory in
	 * {@link #rootDirectory} is used.
	 */
	private File globalDirectory = null;

	/**
	 * User DPU directory, if null then the user subdirectory in
	 * {@link #rootDirectory} is used.
	 */
	private File userDirectory = null;

	public TestContext() { }
	
	/**
	 * 
	 * @param rootDirectory
	 * @param lastExecution
	 * @param jarPath
	 * @deprecated use empty ctor and setters instead
	 */
	@Deprecated
	public TestContext(File rootDirectory, Date lastExecution, String jarPath) {
		this.rootDirectory = rootDirectory;
		this.lastExecution = lastExecution;
		this.jarPath = jarPath;
	}

	@Override
	public void sendMessage(MessageType type, String shortMessage) {
		sendMessage(type, shortMessage, "");
	}

	@Override
	public void sendMessage(MessageType type,
			String shortMessage,
			String fullMessage) {
		switch (type) {
			case DEBUG:
				LOG.debug("DPU publish message short: '{}' long: '{}'",
						shortMessage,
						fullMessage);
				break;
			case ERROR:
				LOG.error("DPU publish message short: '{}' long: '{}'",
						shortMessage,
						fullMessage);
				publishedError = true;
				break;
			case INFO:
				LOG.info("DPU publish message short: '{}' long: '{}'",
						shortMessage,
						fullMessage);
				break;
			case WARNING:
				LOG.warn("DPU publish message short: '{}' long: '{}'",
						shortMessage,
						fullMessage);
				publishedWarning = true;
				break;
			case TERMINATION_REQUEST:
				LOG.info("DPU publish termination message short: '{}' long: '{}'",
						shortMessage,
						fullMessage);
				break;
		}

	}

	@Override
	public boolean isDebugging() {
		return false;
	}

	@Override
	public boolean canceled() {
		return false;
	}

	@Override
	public File getWorkingDir() {
		File workingDir;
		if (workingDirectory == null) {
			workingDir = new File(rootDirectory, "working");
		} else {
			workingDir = workingDirectory;
		}

		if (!workingDir.exists()) {
			workingDir.mkdirs();
		}
		return workingDir;
	}

	@Override
	public File getResultDir() {
		File resultDir;
		if (resultDirectory == null) {
			resultDir = new File(rootDirectory, "result");
		} else {
			resultDir = resultDirectory;
		}

		if (!resultDir.exists()) {
			resultDir.mkdirs();
		}
		return resultDir;
	}

	@Override
	public File getJarPath() {
		if (jarPath == null) {
			throw new RuntimeException("Jar-path has not been set! Use TestEnvironment.setJarPath");
		} else {
			return new File(jarPath);
		}
	}

	@Override
	public Date getLastExecutionTime() {
		return lastExecution;
	}

	@Override
	public File getGlobalDirectory() {
		File globalDir;
		if (globalDirectory == null) {
			globalDir = new File(rootDirectory, "global");
		} else {
			globalDir = globalDirectory;
		}

		if (!globalDir.exists()) {
			globalDir.mkdirs();
		}
		return globalDir;
	}

	@Override
	public File getUserDirectory() {
		File userDir;
		if (userDirectory == null) {
			userDir = new File(rootDirectory, "user");
		} else {
			userDir = userDirectory;
		}

		if (!userDir.exists()) {
			userDir.mkdirs();
		}
		return userDir;
	}

	public boolean isPublishedWarning() {
		return publishedWarning;
	}

	public boolean isPublishedError() {
		return publishedError;
	}

	/**
	 * @param workingDirectory the workingDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/**
	 * @param resultDirectory the resultDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setResultDirectory(File resultDirectory) {
		this.resultDirectory = resultDirectory;
	}

	/**
	 * @param globalDirectory the globalDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setGlobalDirectory(File globalDirectory) {
		this.globalDirectory = globalDirectory;
	}

	/**
	 * @param userDirectory the userDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setUserDirectory(File userDirectory) {
		this.userDirectory = userDirectory;
	}

	/**
	 * @param rootDirectory the rootDirectory to set
	 */
	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * @param lastExecution the lastExecution to set
	 */
	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	/**
	 * @param jarPath the jarPath to set
	 */
	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

}
