package cz.cuni.xrg.intlib.commons.app;

/**
 * Contains application configuration.
 * 
 * @author Petyr
 */
public class AppConfiguration {

	/**
	 * Path to the dpu directory folder.
	 */
	private String dpuDirectory = "C:/Users/Bogo/Documents/NetBeansProjects/intlib";
	
	/**
	 * Backend instance IP address.
	 */
	private String backendAddress = "127.0.0.1";
	
	/**
	 * Port for communicating with backend.
	 */
	private Integer backendPort = 5010;
	
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