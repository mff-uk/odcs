package cz.cuni.xrg.intlib.frontend.gui;

/**
 * Store URL for views. The <code>enum</code> name should be the same as a class name 
 * of view.
 * 
 * @author Petyr
 *
 */
public enum ViewNames {
	
	INITIAL(""),
	ADMINISTRATOR("Administrator"),
	DATA_BROWSER("DataBrowser"),
	DPU("DPURecord"),
	EXECUTION_MONITOR("ExecutionMonitor"),
	PIPELINE_LIST("PipelineList"),
	PIPELINE_EDIT("PipelineEdit"),
	PIPELINE_EDIT_NEW("PipelineEdit", "New"),
	SCHEDULER("Scheduler"),
	LOGIN("Login");
	
	/**
	 * String for view.
	 */
	protected String url;
	
	/**
	 * Parameter for view.
	 */
	protected String parametr;
	
	ViewNames(String url) {
		this.url = url;
		this.parametr = "";
	}
	
	ViewNames(String url, String parametr) {
		this.url = url + "/" + parametr;
		this.parametr = parametr;
	}
	
	/**
	 * Return full url to the view.
	 * @return
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * Return view parameter.
	 * @return
	 */
	public String getParametr() {
		return this.parametr;
	}	
	
}
