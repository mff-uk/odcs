package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Presenter;

/**
 * Interface for presenter that take care about presenting information about
 * pipelines.
 *
 * @author Bogo
 */
public interface PipelineListPresenter extends Presenter {

	/**
	 * Refresh data from data sources.
	 */
	public void refreshEventHandler();

	/**
	 * Copy pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void copyEventHandler(long id);

	/**
	 * Delete pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void deleteEventHandler(long id);

	/**
	 * Schedule pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void scheduleEventHandler(long id);

	/**
	 * Run pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void runEventHandler(long id, boolean inDebugMode);

	/**
	 * Navigate to other view.
	 *
	 * @param where View class.
	 * @param param Parameter for new view or null.
	 */
	public void navigateToEventHandler(Class where, Object param);

	public void pageChangedHandler(Integer newPageNumber);

	public void filterParameterEventHander(String string, Object filterValue);

	public interface PipelineListView {

		/**
		 * Generate view, that interact with given presenter.
		 *
		 * @param presenter
		 * @return
		 */
		public Object enter(final PipelineListPresenter presenter);

		/**
		 * Set data for view.
		 *
		 * @param dataObject
		 */
		public void setDisplay(PipelineListData dataObject);

		public void setPage(int pageNumber);

		public void setFilter(String key, Object value);
	}

	/**
	 * Data object for handling informations between view and presenter.
	 */
	public final class PipelineListData {

		private final ReadOnlyContainer<Pipeline> container;

		public ReadOnlyContainer<Pipeline> getContainer() {
			return container;
		}

		public PipelineListData(ReadOnlyContainer<Pipeline> container) {
			this.container = container;
		}
	}
}
