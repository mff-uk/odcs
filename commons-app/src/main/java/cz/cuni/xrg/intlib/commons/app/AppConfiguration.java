package cz.cuni.xrg.intlib.commons.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Contains application configuration.
 * 
 * @author Petyr
 */
public class AppConfiguration {

	/**
	 * Path to the dpu directory folder.
	 */
	private String dpuDirectory = "./";
	
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
		dpuDirectory = prop.getProperty("dpuDirectory");
		backendAddress = prop.getProperty("backendAddress");
		try {
			backendPort = Integer.parseInt( prop.getProperty("backendPort") );
		} catch (NumberFormatException e) {
			throw new RuntimeException("Can't parse port number.", e);
		}
	}
	
	public String getDpuDirectory() {
		return this.dpuDirectory;
	}
	
	public String getBackendAddress() {
		return this.backendAddress;
	}
	
	public Integer getBackendPort() {
		return this.backendPort;
	}
	
	public void setDpuDirectory(String dpuDirectory) {
		this.dpuDirectory = dpuDirectory;
	}
}
