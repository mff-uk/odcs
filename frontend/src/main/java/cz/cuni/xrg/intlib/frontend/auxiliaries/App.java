package cz.cuni.xrg.intlib.frontend.auxiliaries;

import com.vaadin.ui.UI;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.frontend.AppEntry;

/**
 * Class provide access to application data.
 * The purpose of the class is to wrap some function class
 * and made writing code easier.
 * 
 * Class can not be instantiated.
 * 
 * @author Petyr
 *
 */
public class App {

	/**
	 * Private ctor, class should not be instantiated.
	 */
	private App() {
	}

	/**
	 * Return instance of current AppEntry class.
	 * @return
	 */
	public static AppEntry getApp() {
		return (AppEntry)UI.getCurrent();
	}
	
	/**
	 * Returns facade, which provides services for managing pipelines.
	 * @return pipeline facade
	 */	
	public static PipelineFacade getPipelines() {
		return getApp().getPipelines();
	}
	
	/**
	 * Returns facade, which provides services for managing DPUs.
	 * @return dpu facade
	 */	
	public static DPUFacade getDPUs() {
		return getApp().getDPUs();
	}	
}
