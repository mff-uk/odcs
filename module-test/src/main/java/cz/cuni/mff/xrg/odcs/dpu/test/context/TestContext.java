package cz.cuni.mff.xrg.odcs.dpu.test.context;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;

/**
 * Special implementation of {@link DPUContext} that enables testing.
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

	public TestContext() {
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
				LOG.info(
						"DPU publish termination message short: '{}' long: '{}'",
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
			throw new RuntimeException(
					"Jar-path has not been set! Use TestEnvironment.setJarPath");
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

	/**
	 *
	 * @return True if the warning message has been sent via this context.
	 */
	public boolean isPublishedWarning() {
		return publishedWarning;
	}

	/**
	 *
	 * @return True if the error message has been sent via this context.
	 */
	public boolean isPublishedError() {
		return publishedError;
	}

	/**
	 * @param workingDirectory Working directory, use null to use
	 *                         subdirectory in {@link #rootDirectory}.
	 */
	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/**
	 * @param resultDirectory Result directory, use null to use
	 *                        subdirectory in {@link #rootDirectory}.
	 */
	public void setResultDirectory(File resultDirectory) {
		this.resultDirectory = resultDirectory;
	}

	/**
	 * @param globalDirectory Global directory, use null to use
	 *                        subdirectory in {@link #rootDirectory}.
	 */
	public void setGlobalDirectory(File globalDirectory) {
		this.globalDirectory = globalDirectory;
	}

	/**
	 * @param userDirectory User directory, use null to use
	 *                      subdirectory in {@link #rootDirectory}.
	 */
	public void setUserDirectory(File userDirectory) {
		this.userDirectory = userDirectory;
	}

	/**
	 * @param rootDirectory Root directory.
	 */
	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * @param lastExecution Date of last execution.
	 */
	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	/**
	 * @param jarPath Path to the jar file.
	 */
	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

}
