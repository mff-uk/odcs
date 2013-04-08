package cz.cuni.xrg.intlib.frontend;

import com.vaadin.navigator.Navigator;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.frontend.OSGi.Framework;
import cz.cuni.xrg.intlib.frontend.data.DataAccess;
import cz.cuni.xrg.intlib.frontend.gui.MenuLayout;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.views.*;

/**
 * Frontend application entry point. 
 * Also provide access to the application services like database connection.
 * To access the class use ((AppEntry)UI.getCurrent()).
 * 
 * @author Petyr
 *
 */
public class AppEntry extends com.vaadin.ui.UI {
	
	/**
	 * Used to resolve url request and select active view.
	 */
	private com.vaadin.navigator.Navigator navigator;
	
	/**
	 * Data access service.
	 */
	private DataAccess dataAccess;
	
	// TODO: Move to backend
	private Framework osgiFramework;
	
	/**
	 * Facade interface providing services for managing pipelines.
	 */
	private PipelineFacade pipelines = new PipelineFacade();
	
	/**
	 * Return service for data access.
	 * @see DataAccess
	 */
	public DataAccess getDataAccess() {
		return this.dataAccess;
	}
	
	/**
	 * Returns facade, which provides services for managing pipelines.
	 * @return pipeline facade
	 */
	public PipelineFacade getPipelines() {
		return pipelines;
	}
	
	/**
	 * Return application navigator.
	 */
	public Navigator getNavigator() {
		return this.navigator;
	}
	
	/**
	 * Return acces to OSGi framework class.
	 * @return
	 */
	public Framework getFrameWork() {
		return this.osgiFramework;
	}	
	
	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {
		this.osgiFramework = new Framework(); 
		this.osgiFramework.start();
		// on detach event
		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				osgiFramework.stop();
			}} );
		
		// init data access
		this.dataAccess = new DataAccess();
		
		// create main application uber-view and set it as app. content
		MenuLayout main = new MenuLayout();
		setContent(main);
		
        // create a navigator to control the views
        this.navigator = new com.vaadin.navigator.Navigator(this, main.getViewLayout());
        
		// add views to the navigator
        this.navigator.addView("", new Initial());
        // TODO: check rights !!
        this.navigator.addView(ViewNames.Administrator.getUrl(), new Administrator());
        this.navigator.addView(ViewNames.DataBrowser.getUrl(), new DataBrowser());
        this.navigator.addView(ViewNames.DPU.getUrl(), new DPU());
        this.navigator.addView(ViewNames.ExecutionMonitor.getUrl(), new ExecutionMonitor());
        this.navigator.addView(ViewNames.PipelineList.getUrl(), new PipelineList());
        this.navigator.addView(ViewNames.PipelineEdit.getUrl(), new PipelineEdit());
        this.navigator.addView(ViewNames.Scheduler.getUrl(), new Scheduler());
        // TODO: remove !
        this.navigator.addView("expDialog", new DPUDialog());
        
        /* You can create new views dynamically using a view provider 
         * that implements the  ViewProvider interface. 
         * A provider is registered in Navigator with  addProvider().
         */
        
        /* View Change Listeners
         * You can handle view changes also by implementing a  ViewChangeListener 
         * and adding it to a Navigator. When a view change occurs, a listener receives 
         * a ViewChangeEvent object, which has references to the old and the activated view,
         * the name of the activated view, as well as the fragment parameters.
         */
	}	
}
