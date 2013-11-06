package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import cz.cuni.mff.xrg.odcs.frontend.ViewNavigator;

/**
 * Interface for PipelineList.
 *
 * @author Bogo
 */
public interface PipelineListView {

    /**
     * Sets data source for the view
     * @param c Container holding the data
     */
    public void setDataSource(Container c);

	public void refresh();

    /**
     * Interface the presenter implements so that it can process the events
     * emitted by the view
     */
    interface PipelineListViewListener extends ViewNavigator.Navigatable {

        void pipelineEvent(long id, String event);

        void event(String name);
	}

    /**
     * Registers the listener for the events of the view
     * @param listener Listener receiving events of the view (typically a presenter)
     */
    public void setListener(PipelineListViewListener listener);
}
