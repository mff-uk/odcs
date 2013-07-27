package cz.cuni.xrg.intlib.frontend.auxiliaries;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
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
            isRunFinished = !(execution.getExecutionStatus() == PipelineExecutionStatus.SCHEDULED || execution.getExecutionStatus() == PipelineExecutionStatus.RUNNING);

            debug.getUI().access(new Runnable() {
                @Override
                public void run() {
                    debug.refreshContent();
                }
            });

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
