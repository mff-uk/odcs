package cz.cuni.xrg.intlib.frontend;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Panel;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
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
@Push(PushMode.MANUAL)
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

		// create Spring context
		context = new ClassPathXmlApplicationContext("frontend-context.xml");

		// add vaadin to export package list
		ModuleFacade modules = (ModuleFacade) context.getBean("moduleFacade");
		modules.start();

		// TODO: set module relative path .. ?
//		this.modules.installDirectory(App.getWebAppDirectory() + "/OSGI/libs/");
//		cz.cuni.xrg.intlib.commons.app.dpu.DPURecord.HACK_basePath = App.getWebAppDirectory() + "/OSGI";

		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				getModules().stop();
			}} );

		initNavigator();
	}

	/**
	 * Returns facade, which provides services for managing pipelines.
	 * @return pipeline facade
	 */
	public PipelineFacade getPipelines() {
		return (PipelineFacade) context.getBean("pipelineFacade");
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
		return (ModuleFacade) context.getBean("moduleFacade");
	}

    /**
     * Return facade, which provide services for manipulating with DPUs.
     * @return dpus facade
     */
    public DPUFacade getDPUs() {
		return (DPUFacade) context.getBean("dpuFacade");
    }

    /**
     * Return application configuration class.
     * @return
     */
    public AppConfig getAppConfiguration() {
		return (AppConfig) context.getBean("configuration");
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
