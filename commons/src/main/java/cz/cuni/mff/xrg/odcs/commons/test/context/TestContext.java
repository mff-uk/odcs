package cz.cuni.mff.xrg.odcs.commons.test.context;

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
	
	private boolean publishedWarning = false; 
	
	private boolean publishedError = false;
	
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
		switch(type) {
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
		File workingDir = new File(rootDirectory, "working");
		if(!workingDir.exists()) {
			workingDir.mkdirs();
		}		
		return workingDir;
	}

	@Override
	public File getResultDir() {
		File resultDir = new File(rootDirectory, "result");
		if(!resultDir.exists()) {
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
		File globalDir = new File(rootDirectory, "global");
		if(!globalDir.exists()) {
			globalDir.mkdirs();
		}
		return globalDir;
	}

	@Override
	public File getUserDirectory() {
		File userDir = new File(rootDirectory, "user");
		if(!userDir.exists()) {
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

}
