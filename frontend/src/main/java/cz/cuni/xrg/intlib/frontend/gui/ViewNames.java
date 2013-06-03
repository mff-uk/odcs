package cz.cuni.xrg.intlib.frontend.gui;

/**
 * Store url for views. The enum name should be the same as a class name 
 * of view.
 * 
 * @author Petyr
 *
 */
public enum ViewNames {
	Initial(""),
	Administrator("Administrator"),
	DataBrowser("DataBrowser"),
	DPU("DPURecord"),
	ExecutionMonitor("ExecutionMonitor"),
	PipelineList("PipelineList"),
	PipelineEdit("PipelineEdit"),
	PipelineEdit_New("PipelineEdit","New"),
	Scheduler("Scheduler");
	
	/**
	 * String for view.
	 */
	protected String url;
	
	/**
	 * Paramter for view.
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
