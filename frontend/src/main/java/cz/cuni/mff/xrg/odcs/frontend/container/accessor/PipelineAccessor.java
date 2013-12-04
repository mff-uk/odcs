package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.DataTimeCache;
import java.text.DateFormat;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;

public class PipelineAccessor implements ClassAccessor<Pipeline> {

	@Autowired
	PipelineFacade pipelineFacade;
	private List<String> all = Arrays.asList("id", "name", "description", "duration", "lastExecTime", "lastExecStatus");
	private List<String> sortable = Arrays.asList("id", "name");
	private List<String> filtrable = Arrays.asList("id", "name", "description");
	/**
	 * Cache for last pipeline execution, so we do not load from DB every time
	 * table cell with duration, start, ..., is needed.
	 */
	private DataTimeCache<PipelineExecution> execCache = new DataTimeCache<>();

	@Override
	public List<String> all() {
		return all;
	}

	@Override
	public List<String> sortable() {
		return sortable;
	}

	@Override
	public List<String> filtrable() {
		return filtrable;
	}

	@Override
	public List<String> visible() {
		return all;
	}

	@Override
	public List<String> toFetch() {
		return null;
	}
	
	@Override
	public Class<Pipeline> getEntityClass() {
		return Pipeline.class;
	}

	@Override
	public String getColumnName(String id) {
		switch (id) {
			case "id":
				return "id";
			case "name":
				return "name";
			case "description":
				return "description";
			case "duration":
				return "Last run time";
			case "lastExecTime":
				return "Last execution time";
			case "lastExecStatus":
				return "Last status";
			default:
				return id;
		}
	}

	@Override
	public Object getValue(Pipeline object, String id) {
		switch (id) {
			case "id":
				return object.getId();
			case "name":
				return object.getName();
			case "description":
				return object.getDescription();
			case "duration":
				PipelineExecution latestExec = pipelineFacade.getLastExec(object, PipelineExecutionStatus.FINISHED);
				return IntlibHelper.getDuration(latestExec);
			case "lastExecTime":
				return getLastExecutionTime(object);
			case "lastExecStatus":
				return getLastExecutionStatus(object);
			default:
				return null;
		}
	}

	@Override
	public Class<?> getType(String id) {
		switch (id) {
			case "id":
				return Integer.class;
			case "name":
			case "description":
			case "duration":
			case "lastExecTime":
				return String.class;
			case "lastExecStatus":
				return Embedded.class;
			default:
				return null;
		}
	}

	/**
	 * Clears the pipeline cache.
	 */
	public void clearExecCache() {
		execCache.invalidate();
	}

	/**
	 * Get last pipeline execution from cache. If execution is not found in
	 * cache, it is loaded from DB and cached.
	 *
	 * @param ppl pipeline
	 * @return last execution for given pipeline
	 */
	private PipelineExecution getLastExecution(Pipeline ppl) {
		PipelineExecution exec = execCache.get(ppl.getId());
		if (exec == null) {
			exec = pipelineFacade.getLastExec(ppl);
			execCache.set(ppl.getId(), exec);
		}
		return exec;
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
			if (type != null) {
				ThemeResource img = IntlibHelper.getIconForExecutionStatus(type);
				Embedded emb = new Embedded(type.name(), img);
				emb.setDescription(type.name());
				return emb;
			} else {
				return null;
			}
		} else {
			return null;
		}

	}
}
