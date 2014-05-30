package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.communication.CheckDatabaseService;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * @author Bogo
 */
public class PipelineHelper {

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private CheckDatabaseService checkDatabaseService;

    /**
     * Sets up parameters of pipeline execution and runs the pipeline.
     * 
     * @param pipeline
     *            {@link Pipeline} to run.
     * @param inDebugMode
     *            Run in debug/normal mode.
     * @return {@link PipelineExecution} of given {@link Pipeline}.
     */
    public PipelineExecution runPipeline(Pipeline pipeline, boolean inDebugMode) {
        return runPipeline(pipeline, inDebugMode, null);
    }

    /**
     * Sets up parameters of pipeline execution and runs the pipeline.
     * 
     * @param pipeline
     *            {@link Pipeline} to run.
     * @param inDebugMode
     *            Run in debug/normal mode.
     * @param debugNode
     *            {@link Node} where debug execution should stop. Valid
     *            only for debug mode.
     * @return {@link PipelineExecution} of given {@link Pipeline}.
     */
    public PipelineExecution runPipeline(Pipeline pipeline, boolean inDebugMode, Node debugNode) {
        final PipelineExecution pipelineExec = pipelineFacade.createExecution(pipeline);
        pipelineExec.setDebugging(inDebugMode);
        if (inDebugMode && debugNode != null) {
            pipelineExec.setDebugNode(debugNode);
        }

        try {
            pipelineFacade.save(pipelineExec);
            checkDatabaseService.checkDatabase();
        } catch (RemoteAccessException e) {
            ConfirmDialog.show(UI.getCurrent(), "Pipeline execution", "Backend is offline. Should the pipeline be scheduled to be launched when backend is online or do you want to cancel the execution?", "Schedule", "Cancel", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog cd) {
                    if (cd.isConfirmed()) {
                        pipelineFacade.save(pipelineExec);
                    } else {
                        pipelineFacade.delete(pipelineExec);
                    }
                }
            });
            return null;
        }
        Notification.show("Pipeline execution started ..", Notification.Type.HUMANIZED_MESSAGE);
        return pipelineExec;
    }

}
