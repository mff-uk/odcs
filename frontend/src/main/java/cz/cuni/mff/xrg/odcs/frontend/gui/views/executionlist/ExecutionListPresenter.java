package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Presenter;
import org.tepi.filtertable.numberfilter.NumberInterval;

/**
 * Interface for presenter that take care about presenting information about
 * executions.
 *
 * @author Petyr
 */
public interface ExecutionListPresenter extends Presenter {

	/**
	 * Refresh data from data sources.
	 */
	public void refreshEventHandler();

	/**
	 * Stop given execution.
	 *
	 * @param executionId
	 */
	public void stopEventHandler(long executionId);

	/**
	 * Show debug data for given execution.
	 *
	 * @param executionId
	 */
	public void showDebugEventHandler(long executionId);

	/**
	 * Re-run given execution.
	 *
	 * @param executionId
	 */
	public void runEventHandler(long executionId);

	/**
	 * Re-run given execution in debug mode.
	 *
	 * @param executionId
	 */
	public void debugEventHandler(long executionId);

	public void stopRefreshEventHandler();

	public void startDebugRefreshEventHandler(DebuggingView debugView, PipelineExecution execution);

	public void pageChangedHandler(Integer newPageNumber);

	public void filterParameterEventHander(String string, Object filterValue);
	
	public void navigateToEventHandler(Class where, Object param);

	/**
	 * View that can be used with the presenter.
	 */
	public interface ExecutionListView {

		/**
		 * Generate view, that interact with given presenter.
		 *
		 * @param presenter
		 * @return
		 */
		public Object enter(final ExecutionListPresenter presenter);

		/**
		 * Set data for view.
		 *
		 * @param dataObject
		 */
		public void setDisplay(ExecutionListData dataObject);

		/**
		 * Show detail for given execution.
		 *
		 * @param execution
		 * @param detailDataObject
		 */
		public void showExecutionDetail(PipelineExecution execution, ExecutionDetailData detailDataObject);

		public void refresh(boolean modified);

		public void setSelectedRow(Long execId);

		public void setFilter(String name, Object value);

		public void setPage(int pageNumber);
	}

	/**
	 * Data object for handling informations between view and presenter.
	 */
	public final class ExecutionListData {

		private final ReadOnlyContainer<PipelineExecution> container;

		public ReadOnlyContainer<PipelineExecution> getContainer() {
			return container;
		}

		public ExecutionListData(ReadOnlyContainer<PipelineExecution> container) {
			this.container = container;
		}
	}

	@Deprecated
	public class ExecutionDetailData {
	
		private final ReadOnlyContainer<MessageRecord> messageContainer;

		public ReadOnlyContainer<MessageRecord> getMessageContainer() {
			return messageContainer;
		}

		public ExecutionDetailData(ReadOnlyContainer<MessageRecord> messageContainer) {
			this.messageContainer = messageContainer;
		}

	}
}
