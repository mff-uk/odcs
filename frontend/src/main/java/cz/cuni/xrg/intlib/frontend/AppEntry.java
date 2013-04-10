package cz.cuni.xrg.intlib.frontend;

import java.util.Date;

import com.vaadin.navigator.Navigator;

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
public class AppEntry extends com.vaadin.ui.UI {

	/**
	 * Used to resolve url request and select active view.
	 */
	private com.vaadin.navigator.Navigator navigator;

	/**
	 * Provide service to manipulate modules.
	 */
	private ModuleFacade modules;

	/**
	 * Facade interface providing services for managing pipelines.
	 */
	private PipelineFacade pipelines = new PipelineFacade();
	
	/**
	 * Facade interface providing services for managing DPUs.
	 */
	private DpuFacade dpus = new DpuFacade();

	protected void finalize ()  {


System.out.println((new Date()).toString() + ": AppEntry::finalize");
		//modules.stop();
		//modules = null;
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

	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {
		this.modules = new ModuleFacade();
		this.modules.start();

System.out.println((new Date()).toString() + ": AppEntry::init");

		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				modules.stop();
				modules = null;
System.out.println((new Date()).toString() + ": AppEntry::detach");
			}} );

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
