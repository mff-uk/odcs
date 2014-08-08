package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DataUnitSelector.SelectionChangedEvent;

/**
 * Component for browsing results of {@link PipelineExecution}.
 * 
 * @author Bogo
 */
public class Browse extends CustomComponent {

    private final VerticalLayout mainLayout;

    private final DataUnitSelector selector;

    private final PipelineExecution execution;

    private QueryView queryView;

    /**
     * Constructor.
     * 
     * @param execution
     */
    public Browse(PipelineExecution execution) {
        // set local execution
        this.execution = execution;

        mainLayout = new VerticalLayout();

        selector = new DataUnitSelector(execution);
        selector.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event.getClass() == DataUnitSelector.BrowseRequestedEvent.class) {
                    queryView.browseDataUnit();
                } else if (event.getClass() == DataUnitSelector.EnableEvent.class) {
                    queryView.setQueryingEnabled(true);
                } else if (event.getClass() == DataUnitSelector.DisableEvent.class) {
                    queryView.setQueryingEnabled(false);
                } else if (event.getClass() == SelectionChangedEvent.class) {
                    SelectionChangedEvent changedEvent = (SelectionChangedEvent) event;
                    checkQueryView(changedEvent.getInfo());
                    queryView.setDataUnitInfo(changedEvent.getInfo());
                    queryView.setSelectedDpu(changedEvent.getDpu());
                }
            }
        });

        mainLayout.addComponent(selector);

        queryView = new RDFQueryView();
        queryView.setQueryingEnabled(false);
        queryView.setExecutionInfo(getExecutionInfo(execution));
        mainLayout.addComponent(queryView);

        setCompositionRoot(mainLayout);
    }

    void setDpu(DPUInstanceRecord debugDpu) {
        selector.setSelectedDPU(debugDpu);
    }

    void refreshDPUs(PipelineExecution pipelineExec) {
        selector.refresh(pipelineExec);
        queryView.setExecutionInfo(getExecutionInfo(pipelineExec));
        queryView.reset();
    }

    private void checkQueryView(DataUnitInfo info) {
        if (info == null) {
            return;
        }
        if (info.getType() == ManagableDataUnit.Type.FILE ||
        		info.getType() == ManagableDataUnit.Type.FILES) {
            if (queryView.getClass() == RDFQueryView.class) {
                mainLayout.removeComponent(queryView);
                queryView = new FileQueryView();
                mainLayout.addComponent(queryView);
            }
        } else {
            if (queryView.getClass() == FileQueryView.class) {
                mainLayout.removeComponent(queryView);
                queryView = new RDFQueryView();
                queryView.setExecutionInfo(getExecutionInfo(execution));
                mainLayout.addComponent(queryView);
            }

        }
    }

    private ExecutionInfo getExecutionInfo(PipelineExecution exec) {
        if (exec == null) {
            return null;
        }
        ExecutionContextInfo context = exec.getContextReadOnly();
        if (context != null) {
            return new ExecutionInfo(context);
        }
        return null;
    }
}
