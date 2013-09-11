package cz.cuni.xrg.intlib.frontend.auxiliaries;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.AppEntry;
import cz.cuni.xrg.intlib.frontend.gui.components.DebuggingView;
import cz.cuni.xrg.intlib.frontend.gui.views.ExecutionMonitor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread used to automatically refresh data in DebuggingView.
 *
 * @author Bogo
 */
public class RefreshThread extends Thread {
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RefreshThread.class);

	private int interval;
	private ExecutionMonitor executionMonitor;
	//field corresponding to DebuggingView refreshing
	private boolean isRefreshingDebugView = false;
	private PipelineExecution execution;
	private DebuggingView debug;
	private int debugLoadingCounter = 0;
	boolean lastExecutionStatus = false;
	//fields corresponding to Backend status refresh
	private boolean lastBackendStatus = false;
	private final Object lock = new Object();

	/**
	 * Default constructor with refresh interval in milliseconds.
	 *
	 * @param interval Interval in milliseconds between refreshes.
	 */
	public RefreshThread(int interval) {
		this.interval = interval;
	}

	/**
	 * Method for start of refreshing DebuggingView of given pipeline execution.
	 *
	 * @param execution PipelineExecution which data are refreshed.
	 * @param debugView DebuggingView in which data are refreshed.
	 */
	public void refreshExecution(PipelineExecution execution, DebuggingView debugView) {
		synchronized (lock) {
			this.isRefreshingDebugView = false;
			if(execution != null) {
				this.execution = execution;
				this.debug = debugView;
				this.debugLoadingCounter = 0;
				this.isRefreshingDebugView = true;
			}
		}

	}

	@Override
	public void run() {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException ex) {
			Logger.getLogger(RefreshThread.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}
		
		while (true) {
			LOG.debug("Iteration started.");
			try {
			if (isRefreshingDebugView) {
				if(debugLoadingCounter >= 2) {
					isRefreshingDebugView = refreshDebuggingView();
					LOG.debug("DebuggingView refreshed.");
				}
				++debugLoadingCounter;
			}
			if (executionMonitor != null) {
				refreshExecutionMonitor();
				LOG.debug("ExecutionMonitor refreshed.");
			}

			refreshBackendStatus();
			LOG.debug("Backend status refreshed.");
			
			Thread.sleep(interval);
			
			} catch (InterruptedException e) {
				return;
			} catch (Exception ex) {
				LOG.error("Uncaught exception", ex);
				isRefreshingDebugView = false;
			}

		}
	}

	private boolean refreshDebuggingView() {
		execution = App.getPipelines().getExecution(execution.getId());
		boolean isRunFinished = !(execution.getExecutionStatus() == PipelineExecutionStatus.SCHEDULED || execution.getExecutionStatus() == PipelineExecutionStatus.RUNNING);
		synchronized (lock) {
			if(debug == null) {
				return false;
			}
			debug.getUI().access(new Runnable() {
				@Override
				public void run() {
					if (debug.isRefreshingAutomatically()) {
						lastExecutionStatus = true;
						debug.refreshContent();
						//Notification.show("Refreshing", Notification.Type.HUMANIZED_MESSAGE);
					} else {
						lastExecutionStatus = false;
					}
				}
			});
		}
		isRunFinished &= lastExecutionStatus;
		if (isRunFinished) {
			execution = null;
			debug = null;
		}
		return !isRunFinished;
	}

	private void refreshExecutionMonitor() {
		executionMonitor.getUI().access(new Runnable() {
			@Override
			public void run() {
				executionMonitor.refresh();
			}
		});
	}

	private void refreshBackendStatus() {
		boolean isRunning = App.getApp().getBackendClient().checkStatus();
		if (lastBackendStatus != isRunning) {
			lastBackendStatus = isRunning;
			App.getApp().getMain().getUI().access(new Runnable() {
				@Override
				public void run() {
					App.getApp().getMain().refreshBackendStatus(lastBackendStatus);
				}
			});
		}
	}

	/**
	 * Sets active ExecutionMonitor for refreshing or null.
	 *
	 * @param executionMonitor ExecutionMonitor to refresh.
	 */
	public void setExecutionMonitor(ExecutionMonitor executionMonitor) {
		this.executionMonitor = executionMonitor;
	}
}
