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
	private String dpuFolder;
	
	public String getDpuFolder() {
		return dpuFolder;
	}

	public void setDpuFolder(String dpuFolder) {
		this.dpuFolder = dpuFolder;
	}	
}
