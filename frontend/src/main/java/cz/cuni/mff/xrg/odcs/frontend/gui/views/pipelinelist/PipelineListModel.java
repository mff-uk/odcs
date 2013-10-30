package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Bogo
 */
public class PipelineListModel {

	/**
	 * Cache for last pipeline execution, so we do not load from DB every time
	 * table cell with duration, start, ..., is needed.
	 */
	private Map<Pipeline, PipelineExecution> execCache = new HashMap<>();
	private PipelineFacade pipelineFacade = App.getPipelines();
	private ContainerFactory containerFactory = App.getApp().getBean(ContainerFactory.class);

	private boolean isExecInSystem(Pipeline pipeline, PipelineExecutionStatus status) {
		List<PipelineExecution> execs = pipelineFacade.getExecutions(pipeline, status);
		if (execs.isEmpty()) {
			return false;
		} else {
			//TODO: Differentiate by user maybe ?!
			return true;
		}
	}

	/**
	 * Clears the pipeline cache.
	 */
	private void clearExecCache() {
		execCache = new HashMap<>();
	}

	/**
	 * Get last pipeline execution from cache. If execution is not found in
	 * cache, it is loaded from DB and cached.
	 *
	 * @param ppl pipeline
	 * @return last execution for given pipeline
	 */
	private PipelineExecution getLastExecution(Pipeline ppl) {
		PipelineExecution exec = execCache.get(ppl);
		if (exec == null) {
			execCache.put(ppl, pipelineFacade.getLastExec(ppl));
		}
		return exec;
	}

	void refresh() {
		clearExecCache();
	}

	String getLastExecutionDuration(Pipeline ppl) {
		PipelineExecution latestExec = pipelineFacade.getLastExec(ppl, PipelineExecutionStatus.FINISHED);
		return IntlibHelper.getDuration(latestExec);
	}

	Object getLastExecutionTime(Pipeline ppl) {
		PipelineExecution latestExec = getLastExecution(ppl);
		if (latestExec != null) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
			return df.format(latestExec.getStart());
		} else {
			return null;
		}
	}

	Object getLastExecutionStatus(Pipeline ppl) {
		PipelineExecution latestExec = getLastExecution(ppl);
		if (latestExec != null) {
			PipelineExecutionStatus type = latestExec.getStatus();
			return type;
		} else {
			return null;
		}
	}

	void copyPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		Pipeline nPipeline = pipelineFacade.copyPipeline(pipeline);
		String copiedPipelineName = "Copy of " + pipeline.getName();
		if (copiedPipelineName.length() > MaxLengthValidator.NAME_LENGTH) {
			Notification.show(String.format("Name of copied pipeline would exceed limit of %d characters, new pipeline has same name as original.", MaxLengthValidator.NAME_LENGTH), Notification.Type.WARNING_MESSAGE);
		} else {
			nPipeline.setName(copiedPipelineName);
		}
		pipelineFacade.save(nPipeline);
	}

	void deletePipeline(long id) {
		final Pipeline pipeline = pipelineFacade.getPipeline(id);
		String message = "Would you really like to delete the " + pipeline.getName() + " pipeline and all associated records (DPU instances e.g.)?";
		if (isExecInSystem(pipeline, PipelineExecutionStatus.RUNNING)) {
			message += "\nPipeline is running currently, the current run will be cancelled!";
		}
		if (isExecInSystem(pipeline, PipelineExecutionStatus.SCHEDULED)) {
			message += "\nPipeline is scheduled currently, the scheduled execution will be deleted!";
		}
		if (!App.getSchedules().getSchedulesFor(pipeline).isEmpty()) {
			message += "\nThere is/are scheduler rules with the pipeline, it/they will be deleted!";
		}

		ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting pipeline", message, "Delete pipeline", "Cancel", new ConfirmDialog.Listener() {
			@Override
			public void onClose(ConfirmDialog cd) {
				if (cd.isConfirmed()) {
					pipelineFacade.delete(pipeline);
					// now we have to remove pipeline from table
					//source.removeItem(itemId);

					//tablePipelines.setVisibleColumns("id", "name", "duration", "description","");
				}
			}
		});
	}

	void runPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		IntlibHelper.runPipeline(pipeline, false);
	}

	void debugPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		PipelineExecution exec = IntlibHelper.runPipeline(pipeline, true);
		if (exec != null) {
			App.getApp().getNavigator().navigateTo(ViewNames.EXECUTION_MONITOR.getUrl() + "/" + exec.getId());
		}
	}

	void schedulePipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		// open scheduler dialog
		SchedulePipeline sch = new SchedulePipeline();
		sch.setSelectePipeline(pipeline);
		App.getApp().addWindow(sch);
	}

	Container getDataSource(int pageLength) {
		return containerFactory.createPipelines(pageLength);
	}
}
