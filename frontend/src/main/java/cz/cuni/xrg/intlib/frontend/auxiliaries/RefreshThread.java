package cz.cuni.xrg.intlib.frontend.auxiliaries;

import cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.gui.components.DebuggingView;

/**
 * Thread used to automatically refresh data in DebuggingView.
 *
 * @author Bogo
 */
public class RefreshThread extends Thread {

    private int interval;
    private PipelineExecution execution;
    private DebuggingView debug;
    private boolean lastStatus = false;

    /**
     * Default constructor with refresh interval in milliseconds and pipeline
     * execution which data are refreshed.
     *
     * @param interval Interval in milliseconds between refreshes.
     * @param execution PipelineExecution which data are refreshed.
     * @param debugView DebuggingView in which data are refreshed.
     */
    public RefreshThread(int interval, PipelineExecution execution, DebuggingView debugView) {
        this.interval = interval;
        this.execution = execution;
        this.debug = debugView;
    }

    @Override
    public void run() {
        boolean isRunFinished = false;

        while (!isRunFinished) {

            execution = App.getPipelines().getExecution(execution.getId());
            isRunFinished = !(execution.getExecutionStatus() == ExecutionStatus.SCHEDULED || execution.getExecutionStatus() == ExecutionStatus.RUNNING);

            debug.getUI().access(new Runnable() {
                @Override
                public void run() {
                    if (debug.isRefreshingAutomatically()) {
                        lastStatus = true;
                        debug.refreshContent();
                    } else {
                        lastStatus = false;
                    }
                }
            });
            isRunFinished &= lastStatus;

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
