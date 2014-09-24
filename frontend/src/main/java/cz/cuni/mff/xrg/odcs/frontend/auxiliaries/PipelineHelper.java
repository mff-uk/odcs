package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import cz.cuni.mff.xrg.odcs.commons.app.JobsTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.communication.CheckDatabaseService;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.RuntimePropertiesFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.properties.RuntimeProperty;

/**
 * @author Bogo
 */
public class PipelineHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(PipelineHelper.class);

    private static final Long DEFAULT_ORDER_POSITION = JobsTypes.UNSCHEDULED;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private CheckDatabaseService checkDatabaseService;
    
    @Autowired
    private RuntimePropertiesFacade runtimePropertyFacade;

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
            Long orderPosition = getOrderPosition(pipelineExec.getCreated());
            // run immediately - set higher priority
            pipelineExec.setOrderPosition(orderPosition);
            pipelineFacade.save(pipelineExec);
            checkDatabaseService.checkDatabase();
        } catch (RemoteAccessException e) {
            ConfirmDialog.show(UI.getCurrent(), "Pipeline execution", "Backend is offline. Should the pipeline be scheduled to be launched when backend is online or do you want to cancel the execution?", "Schedule", "Cancel", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog cd) {
                    PipelineExecution pplExec = pipelineFacade.getExecution(pipelineExec.getId());
                    if (pplExec != null && pplExec.getStatus() != PipelineExecutionStatus.QUEUED) {
                        Notification.show("Execution not sheduled / canceled", "Execution state changed in the meantime. Check and try again.", Type.WARNING_MESSAGE);
                        return; // already running
                    }
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

    /**
     * Computes order position using runtime property
     * 
     * @param epoch
     * @return
     */
    private Long getOrderPosition(Long epoch) {
        Long priority = DEFAULT_ORDER_POSITION;
        
        // checking format of value
        RuntimeProperty property = runtimePropertyFacade.getByName(ConfigProperty.FRONTEND_RUN_NOW_PIPELINE_PRIORITY.toString());
        if (property != null) {
            try {
                priority = Long.parseLong(property.getValue());
            } catch (NumberFormatException e) {
                LOG.error("Value not a number of RuntimeProperty: " + ConfigProperty.FRONTEND_RUN_NOW_PIPELINE_PRIORITY.toString()
                        + ", error: " + e.getMessage());
                LOG.warn("Using default value: " + DEFAULT_ORDER_POSITION);
            }
        }
        
        // schould be in range 1 - 10
        if (priority < 1L) {
            priority = 1L;
        } else if (priority > 10L) {
            priority = 10L;
        }
        
        // Should be almost identical to ScheduleFacadeImpl.execute
        Long orderPosition;
        if (priority == JobsTypes.UNSCHEDULED) {
            orderPosition = JobsTypes.UNSCHEDULED;
        } else {
            // because we divide by zero
            if (priority == JobsTypes.MAX_PRIORITY) {
                orderPosition = epoch;
            } else {
                orderPosition = (epoch / priority);
            }
        }
        return orderPosition;
    }

}
