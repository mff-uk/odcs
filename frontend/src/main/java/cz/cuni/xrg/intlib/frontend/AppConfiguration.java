package cz.cuni.xrg.intlib.frontend;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfiguration;

/**
 * Contains application configuration.
 * 
 * @author Petyr
 */
public class AppConfiguration {

	/**
	 * ModuleFacade configuration.
	 */
	private ModuleFacadeConfiguration moduleConfiguration = new ModuleFacadeConfiguration();
	
	/**
	 * Backend instance IP address.
	 */
	private String backendAddress = "127.0.0.1";
	
	/**
	 * Port for communicating with backend.
	 */
	private Integer backendPort = 5010;
		
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
		backendAddress = prop.getProperty("backendAddress");
		try {
			backendPort = Integer.parseInt( prop.getProperty("backendPort") );
		} catch (NumberFormatException e) {
			throw new RuntimeException("Can't parse port number.", e);
		}
	}
	
	public ModuleFacadeConfiguration getModuleFacadeConfiguration() {
		return this.moduleConfiguration;
	}
	
	public String getBackendAddress() {
		return this.backendAddress;
	}
	
	public Integer getBackendPort() {
		return this.backendPort;
	}
	
	public void setModuleFacadeConfiguration(ModuleFacadeConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}
}
