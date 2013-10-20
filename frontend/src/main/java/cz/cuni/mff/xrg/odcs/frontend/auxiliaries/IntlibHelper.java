package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.communication.Client;
import cz.cuni.mff.xrg.odcs.commons.app.communication.CommunicationException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.CANCELLING;

import java.util.HashSet;
import java.util.Set;
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
	public static ThemeResource getIconForExecutionStatus(PipelineExecutionStatus status) {
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
			case CANCELLING:
				img = new ThemeResource("icons/cancelling.png");
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

		final PipelineExecution pipelineExec = App.getPipelines().createExecution(pipeline); 
				
		pipelineExec.setDebugging(inDebugMode);
		if (inDebugMode && debugNode != null) {
			pipelineExec.setDebugNode(debugNode);
		}
		Client client = App.getApp().getBackendClient();

		// send message to backend
		try {
			if (client.connect()) {
				// store into DB
				App.getPipelines().save(pipelineExec);
			}
			client.checkDatabase();
		} catch (CommunicationException e) {
			ConfirmDialog.show(UI.getCurrent(), "Pipeline execution",
					"Backend is offline. Should the pipeline be scheduled to be launched when backend is online or do you want to cancel the execution?",
					"Schedule", "Cancel", new ConfirmDialog.Listener() {
				@Override
				public void onClose(ConfirmDialog cd) {
					if (cd.isConfirmed()) {
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
		Notification.show("Pipeline execution started ..",
				Notification.Type.HUMANIZED_MESSAGE);

		return pipelineExec;
	}

	/**
	 * Sets up parameters of pipeline execution and runs the pipeline.
	 *
	 * @param pipeline {@link Pipeline} to run.
	 * @param inDebugMode Run in debug/normal mode.
	 *
	 * @return {@link PipelineExecution} of given {@link Pipeline}.
	 */
	public static PipelineExecution runPipeline(Pipeline pipeline, boolean inDebugMode) {
		return runPipeline(pipeline, inDebugMode, null);
	}

	/**
	 * Finds the final cause for given {@link Throwable}.
	 *
	 * @param t Throwable which cause should be found.
	 * @return Final cause of Throwable.
	 */
	public static Throwable findFinalCause(Throwable t) {
		for (; t != null; t = t.getCause()) {
			if (t.getCause() == null) // We're at final cause
			{
				return t;
			}
		}
		return t;
	}

	/**
	 * Formats duration in miliseconds to hh:mm:ss string. Returns empty string
	 * for duration lesser than zero.
	 *
	 */
	public static String formatDuration(long duration) {
		if (duration < 0) {
			return "";
		}
		//to seconds
		duration /= 1000;
		short seconds = (short) (duration % 60);
		duration -= seconds;
		//to minutes
		duration /= 60;
		short minutes = (short) (duration % 60);
		duration -= minutes;
		short hours = (short) (duration / 60);

		return String.format("%d:%02d:%02d", hours, minutes, seconds);
	}

	/**
	 * Gets duration of given {@link PipelineExecution}.
	 *
	 * @param exec Pipeline execution.
	 * @return Duration of {@link PipelineExecution} or -1 if execution wasn't
	 * finished.
	 */
	public static String getDuration(PipelineExecution exec) {
		long duration = -1;
		if (exec != null) {
			duration = exec.getDuration();
		}
		return formatDuration(duration);
	}
}
