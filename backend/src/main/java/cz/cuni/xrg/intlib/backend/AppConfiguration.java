package cz.cuni.xrg.intlib.backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfiguration;

/**
 * Class with backend application configuration.
 * 
 * @author Petyr
 *
 */
public class AppConfiguration {
	/**
	 * ModuleFacade configuration.
	 */
	private ModuleFacadeConfiguration moduleConfiguration = null;
	
	/**
	 * Port for communicating with backend.
	 */
	private Integer backendPort = 5010;
	
	/**
	 * Working directory.
	 */
	private String workingDirectory;
	
	public AppConfiguration() {
		this.moduleConfiguration = new ModuleFacadeConfiguration();
	}
	
	public AppConfiguration(ModuleFacadeConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}
	
	/**
	 * Load configuration from given properties file.
	 * @param fileName
	 * @throws IOException 
	 * @throws RuntimeException
	 */
	public void Load(String fileName) throws IOException, RuntimeException {
		FileInputStream stream;
		
		stream = new FileInputStream(fileName);
		Load(stream);
		stream.close();
	}
	
	/**
	 * Load configuration from given stream.
	 * @param fileName
	 * @throws IOException
	 * @throws RuntimeException 
	 */
	public void Load(FileInputStream stream) throws IOException, RuntimeException {
		Properties prop = new Properties();
		
		prop.loadFromXML(stream);
		moduleConfiguration.setDpuFolder( prop.getProperty("dpuDirectory") );
		try {
			backendPort = Integer.parseInt( prop.getProperty("backendPort") );
		} catch (NumberFormatException e) {
			throw new RuntimeException("Can't parse port number.", e);
		}
		workingDirectory = prop.getProperty("workingDirectory");
	}
	
	public ModuleFacadeConfiguration getModuleFacadeConfiguration() {
		return this.moduleConfiguration;
	}
	
	public Integer getBackendPort() {
		return this.backendPort;
	}
	
	public void setModuleFacadeConfiguration(ModuleFacadeConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}
}
