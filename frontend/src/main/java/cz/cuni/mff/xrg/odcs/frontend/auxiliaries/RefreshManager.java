package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;

import java.util.HashMap;
import org.slf4j.LoggerFactory;

/**
 * Manager for refresh events in frontend.
 *
 * @author Bogo
 */
public class RefreshManager {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RefreshManager.class);
	private Refresher refresher;
	private HashMap<String, RefreshListener> listeners;
	public static final String BACKEND_STATUS = "backend_status";
	public static final String EXECUTION_MONITOR = "execution_monitor";
	public static final String DEBUGGINGVIEW = "debugging_view";

	public RefreshManager(Refresher refresher) {
		this.refresher = refresher;
		this.listeners = new HashMap<>(3);

	}

	public void addListener(String name, RefreshListener listener) {
		if (listeners.containsKey(name)) {
			RefreshListener oldListener = listeners.remove(name);
			refresher.removeListener(oldListener);
		}
		refresher.addListener(listener);
		listeners.put(name, listener);
	}

	public void removeListener(String name) {
		RefreshListener removedListener = listeners.remove(name);
		if (removedListener != null) {
			refresher.removeListener(removedListener);
		}
	}

	public static RefreshListener getDebugRefresher(final DebuggingView debug, final PipelineExecution exec) {
		return new Refresher.RefreshListener() {
			boolean isWorking = true;
			PipelineExecution execution = exec;
			boolean lastExecutionStatus = false;

			@Override
			public void refresh(Refresher source) {
				if (!isWorking) {
					return;
				}
				execution = App.getPipelines().getExecution(execution.getId());
				boolean isRunFinished = !(execution.getStatus() == PipelineExecutionStatus.SCHEDULED || execution.getStatus() == PipelineExecutionStatus.RUNNING);

				if (debug.isRefreshingAutomatically()) {
					lastExecutionStatus = true;
					debug.refreshContent();
					//Notification.show("Refreshing", Notification.Type.HUMANIZED_MESSAGE);
				} else {
					lastExecutionStatus = false;
				}
				isRunFinished &= lastExecutionStatus;
				if (isRunFinished) {
					isWorking = false;
				}
				LOG.debug("DebuggingView refreshed.");
			}
		};
	}
}
