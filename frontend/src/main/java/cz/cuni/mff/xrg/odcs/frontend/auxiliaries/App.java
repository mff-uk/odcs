package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;

/**
 * Class provide access to application data. The purpose of the class is to wrap
 * some function class and made writing code easier.
 *
 * Class cannot be instantiated.
 *
 * @author Petyr
 * @deprecated use spring's autowired functionality instead
 */
@Deprecated
public class App {

	public static final int MAX_TABLE_COLUMN_LENGTH = 100;

	/**
	 * Private ctor, class should not be instantiated.
	 */
	private App() {
	}

	/**
	 * Return instance of current AppEntry class.
	 *
	 * @return
	 */
	public static AppEntry getApp() {
		return (AppEntry) UI.getCurrent();
	}

	/**
	 * Returns facade, which provides services for managing pipelines.
	 *
	 * @return pipeline facade
	 */
	public static PipelineFacade getPipelines() {
		return getApp().getPipelines();
	}

	/**
	 * Returns facade, which provides services for managing DPUs.
	 *
	 * @return dpu facade
	 */
	public static DPUFacade getDPUs() {
		return getApp().getDPUs();
	}

	/**
	 * Return facade, which provide services for manipulating with Schedules.
	 *
	 * @return schedules facade
	 */
	public static ScheduleFacade getSchedules() {
		return getApp().getSchedules();
	}

	/**
	 * Return facade, which provide services for manipulating with Logs.
	 *
	 * @return log facade
	 */
	public static LogFacade getLogs() {
		return getApp().getLogs();
	}

	/**
	 * Return application configuration.
	 *
	 * @return
	 */
	public static AppConfig getAppConfig() {
		return getApp().getAppConfiguration();
	}
}
