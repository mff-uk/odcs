package cz.cuni.xrg.intlib.commons.app.module;

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
	private String packagesToExpose; 
	
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
}
