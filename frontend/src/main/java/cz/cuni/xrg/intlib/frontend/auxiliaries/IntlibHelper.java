package cz.cuni.xrg.intlib.frontend.auxiliaries;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import cz.cuni.xrg.intlib.commons.app.communication.Client;
import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus;
import static cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus.CANCELLED;
import static cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus.FAILED;
import static cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus.FINISHED_SUCCESS;
import static cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus.FINISHED_WARNING;
import static cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus.RUNNING;
import static cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus.SCHEDULED;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Class with helper methods used in frontend.
 *
 * @author Bogo
 */
public class IntlibHelper {

    /**
     * Gets corresponding icon for given {@link ExecutionStatus}.
     *
     * @param status Status to get icon for.
     * @return Icon for given status.
     */
    public static ThemeResource getIconForExecutionStatus(ExecutionStatus status) {
        ThemeResource img = null;
        switch (status) {
            case FINISHED_SUCCESS:
                img = new ThemeResource("icons/ok.png");
                break;
            case FINISHED_WARNING:
                img = new ThemeResource("icons/warning.png");
                break;
            case FAILED:
                img = new ThemeResource("icons/error.png");
                break;
            case RUNNING:
                img = new ThemeResource("icons/running.png");
                break;
            case SCHEDULED:
                img = new ThemeResource("icons/scheduled.png");
                break;
            case CANCELLED:
                img = new ThemeResource("icons/cancelled.png");
                break;
            default:
                //no icon
                break;
        }
        return img;
    }

    /**
     * Sets up parameters of pipeline execution and runs the pipeline.
     *
     * @param pipeline {@link Pipeline} to run.
     * @param inDebugMode Run in debug/normal mode.
     * @param debugNode {@link Node} where debug execution should stop. Valid
     * only for debug mode.
     * @return {@link PipelineExecution} of given {@link Pipeline}.
     */
    public static PipelineExecution runPipeline(Pipeline pipeline, boolean inDebugMode, Node debugNode) {

        final PipelineExecution pipelineExec = new PipelineExecution(pipeline);
        pipelineExec.setDebugging(inDebugMode);
        if (inDebugMode && debugNode != null) {
            pipelineExec.setDebugNode(debugNode);
        }
        // do some settings here
        AppConfig config = App.getApp().getAppConfiguration();
        Client client = new Client(
                config.getString(ConfigProperty.BACKEND_HOST),
                config.getInteger(ConfigProperty.BACKEND_PORT));

        // send message to backend
        try {
            if(client.connect()) {
                // store into DB
                App.getPipelines().save(pipelineExec);
            }
            client.checkDatabase();
        } catch (CommunicationException e) {
            ConfirmDialog.show(UI.getCurrent(), "Pipeline execution", 
                    "Backend is offline. Should the pipeline be launched when possible or do you want to cancel the execution?", 
                    "Launch when possible", "Cancel the execution", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog cd) {
                    if(cd.isConfirmed()) {
                        // store into DB for later launch
                        App.getPipelines().save(pipelineExec);
                    } else {
                        App.getPipelines().delete(pipelineExec);
                    }
                }
            });
//            Notification.show("Error", "Can't connect to backend. Exception: " + e.getCause().getMessage(),
//                    Notification.Type.ERROR_MESSAGE);
            return null;
        }

        // show message about action
        Notification.show("pipeline execution started ..",
                Notification.Type.HUMANIZED_MESSAGE);

        return pipelineExec;
    }

    /**
     * Sets up parameters of pipeline execution and runs the pipeline.
     *
     * @param pipeline {@link Pipeline} to run.
     * @param inDebugMode Run in debug/normal mode.
     */
    public static void runPipeline(Pipeline pipeline, boolean inDebugMode) {
        runPipeline(pipeline, inDebugMode, null);
    }
}
