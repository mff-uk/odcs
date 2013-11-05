package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;

/**
 * Interface for PipelineList. 
 *
 * @author Bogo
 */
public interface PipelineListView {
	
	public void setDataSource(Container c);
	
	interface PipelineListViewListener {
        void navigation(String where);
		void pipelineEvent(long id, String event);
		void event(String name);
    }
	
    public void setListener(PipelineListViewListener listener);
	
}
