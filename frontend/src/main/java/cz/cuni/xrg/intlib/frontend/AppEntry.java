package cz.cuni.xrg.intlib.frontend;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Panel;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfiguration;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfiguration;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.frontend.gui.MenuLayout;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.views.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
	 * Spring application context.
	 */
	private ApplicationContext context;

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
	private DPUFacade dpus;

	/**
	 * Add a single view to {@link #navigator}.
	 * @param view Name of the view.
	 */
	private void initNavigatorAddSingle(ViewNames view) {
		this.navigator.addView(view.getUrl(), ViewsFactory.create(view));
	}
	
    /**
     * Add url-view association into navigator.
     */
    private void initNavigator() {
    	initNavigatorAddSingle(ViewNames.Initial);
        // TODO: check rights !!
        initNavigatorAddSingle(ViewNames.Administrator);
        initNavigatorAddSingle(ViewNames.DataBrowser);
        initNavigatorAddSingle(ViewNames.DPU);
        initNavigatorAddSingle(ViewNames.ExecutionMonitor);
        initNavigatorAddSingle(ViewNames.PipelineList);
        initNavigatorAddSingle(ViewNames.PipelineEdit);
        initNavigatorAddSingle(ViewNames.Scheduler);

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
		
		context = new ClassPathXmlApplicationContext("frontend-context.xml");
		this.appConfig = (AppConfiguration) getBean("configuration");
        
		this.modules = new ModuleFacade((ModuleFacadeConfiguration) getBean("moduleFacadeConfiguration"));
		// add vaadin to export package list
		this.modules.start();
		
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
		this.dpus = new DPUFacade();
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
        @Override
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
    public DPUFacade getDPUs() {
        return this.dpus;
    }	
    
    /**
     * Return application configuration class.
     * @return
     */
    public AppConfiguration getAppConfiguration() {
    	return appConfig;
    }
	
	/**
	 * Fetches spring bean.
	 * 
	 * @param name
	 * @return bean
	 */
	public Object getBean(String name) {
		return context.getBean(name);
	}
}
