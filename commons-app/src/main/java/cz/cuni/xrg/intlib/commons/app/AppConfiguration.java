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

	public String getDpuDirectory() {
		return this.dpuDirectory;
	}

	public void setDpuDirectory(String dpuDirectory) {
		this.dpuDirectory = dpuDirectory;
	}
}
