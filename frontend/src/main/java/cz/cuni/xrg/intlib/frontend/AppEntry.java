package cz.cuni.xrg.intlib.frontend;

import java.io.File;
import java.util.Enumeration;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Panel;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.app.AppConfiguration;
import cz.cuni.xrg.intlib.commons.app.dpu.DpuFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
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
@Theme("IntLibTheme")
public class AppEntry extends com.vaadin.ui.UI {

	/**
	 * Used to resolve url request and select active view.
	 */
	private com.vaadin.navigator.Navigator navigator;

	/**
	 * Application configuration.
	 */
	private AppConfiguration appConfig;		
	
	/**
	 * Provide service to manipulate modules.
	 */
	private ModuleFacade modules;

	/**
	 * Facade interface providing services for managing pipelines.
	 */
	private PipelineFacade pipelines;

	/**
	 * Facade interface providing services for managing DPUs.
	 */
	private DpuFacade dpus;

    /**
     * Add url-view association into navigator.
     */
    protected void initNavigator() {
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
        this.navigator.addView(ViewNames.OSGiSupport.getUrl(), new OSGiSupport());

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
    
	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {
		// create main application uber-view and set it as app. content
        // in panel, for possible vertical scrolling
		MenuLayout main = new MenuLayout();
        Panel mainPanel = new Panel(main);
		setContent(mainPanel);

        // create a navigator to control the views
        this.navigator = new com.vaadin.navigator.Navigator(this, main.getViewLayout());		
		
        this.appConfig = new AppConfiguration();
        
		this.modules = new ModuleFacade(appConfig);
		// add vaadin to export package list
		this.modules.start(
				",com.vaadin.ui" +
				",com.vaadin.data" +
				",com.vaadin.data.util" +
				",com.vaadin.data.util.converter" +
				",com.vaadin.shared.ui.combobox" +
				",com.vaadin.server" +
				// OpenRdf
				",org.openrdf.rio"
						);
		
		// TODO: set module relative path .. ? 
//		this.modules.installDirectory(App.getWebAppDirectory() + "/OSGI/libs/");
//		cz.cuni.xrg.intlib.commons.app.dpu.DPU.HACK_basePath = App.getWebAppDirectory() + "/OSGI";

		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				modules.stop();
				modules = null;
			}} );

		this.pipelines = new PipelineFacade();
		this.dpus = new DpuFacade();
		this.appConfig = new AppConfiguration();

		initNavigator();
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
	 * @return application navigator
	 */
	public Navigator getNavigator() {
		return this.navigator;
	}

	/**
	 * Return facade, which provide services for manipulating with modules.
	 * @return  modules facade
	 */
	public ModuleFacade getModules() {
		return this.modules;
	}

    /**
     * Return facade, which provide services for manipulating with DPUs.
     * @return dpus facade
     */
    public DpuFacade getDPUs() {
        return this.dpus;
    }	
}
