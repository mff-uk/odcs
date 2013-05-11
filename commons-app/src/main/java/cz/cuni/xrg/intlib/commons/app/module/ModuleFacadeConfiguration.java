package cz.cuni.xrg.intlib.commons.app.module;

import java.util.Properties;

/**
 * Contains settings for ModuleFacade;
 * 
 * @author Petyr
 *
 */
public class ModuleFacadeConfiguration {

	/**
	 * Folder with dpu to load during execution.
	 */
	private String dpuFolder = "FILL YOUR PATH HERE";
	
	/**
	 * List package that should be expose from application.
	 */
	private String packagesToExpose = ""; 
	
	/**
	 * Folder with dpu libraries. 
	 */
	private String dpuLibsFolder = "";
	
	/**
	 * Load configuration from property file.
	 * @param prop
	 */
	public void load(Properties prop) {
		dpuFolder = prop.getProperty("dpuFolder");
		packagesToExpose = prop.getProperty("packagesToExpose");
		dpuLibsFolder = prop.getProperty("dpuLibsFolder");
	}
	
	public String getDpuFolder() {
		return dpuFolder;
	}

	public void setDpuFolder(String dpuFolder) {
		this.dpuFolder = dpuFolder;
	}

	public String getPackagesToExpose() {
		return packagesToExpose;
	}

	public void setPackagesToExpose(String packagesToExpose) {
		this.packagesToExpose = packagesToExpose;
	}

	public String getDpuLibsFolder() {
		return dpuLibsFolder;
	}

	public void setDpuLibsFolder(String dpuLibsFolder) {
		this.dpuLibsFolder = dpuLibsFolder;
	}	
}
