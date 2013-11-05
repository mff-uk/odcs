package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor.ActionColumnGenerator.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ExecutionView}.
 *
 * @author Petyr
 */
@Component
@Scope("prototype")
public class ExecutionViewImpl extends CustomComponent implements ExecutionView {

    // TODO: get from user settings
    private static final int PAGE_LENGTH = 20;

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionViewImpl.class);

    private IntlibPagedTable monitorTable;

    /**
     * Used to separate table from execution detail view.
     */
    private HorizontalSplitPanel hsplit;

    private VerticalLayout logLayout;

    private Panel mainLayout;

    private DebuggingView debugView;
    
    @Override
    public CustomComponent enter(final ExecutionPresenter presenter) {
        // build page
        buildPage(presenter);
        
        // TODO if an execution is selectedwe then should show the details 
        
        return this;
    }

    @Override
    public void setDisplay(ReadOnlyContainer<PipelineExecution> executions) {
        monitorTable.setContainerDataSource(executions);
    }

    @Override
    public void showExecutionDetail(PipelineExecution execution) {
        App.getApp().getRefreshManager().removeListener(RefreshManager.DEBUGGINGVIEW);
        // secure existance of detail layout
        if (logLayout == null) {
            buildExecutionDetail(execution);
            hsplit.setSecondComponent(logLayout);
        } else {
            // will just set the debug view conten
            buildDebugView(execution);
        }
        // adjust hsplit
        if (hsplit.isLocked()) {
            hsplit.setSplitPosition(55, Unit.PERCENTAGE);
            hsplit.setHeight("-1px");
            hsplit.setLocked(false);
        }
    }

    /**
     * Build page layout.
     *
     * @param presenter
     */
    private void buildPage(final ExecutionPresenter presenter) {
        mainLayout = new Panel("");
        // split page into two parts
        hsplit = new HorizontalSplitPanel();
        mainLayout.setContent(hsplit);
        // set top level element properties
        setWidth("100%");
        setHeight("100%");

        VerticalLayout monitorTableLayout = new VerticalLayout();
        monitorTableLayout.setImmediate(true);
        monitorTableLayout.setMargin(true);
        monitorTableLayout.setSpacing(true);
        monitorTableLayout.setWidth("100%");
        monitorTableLayout.setHeight("100%");

        // Layout for buttons Refresh and Clear Filters on the top.
        HorizontalLayout topLine = new HorizontalLayout();
        topLine.setSpacing(true);
        monitorTableLayout.addComponent(topLine);

        // Refresh button. Refreshing the table
        Button btnRefresh = new Button("Refresh", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.refresh();
            }
        });
        btnRefresh.setWidth("120px");
        topLine.addComponent(btnRefresh);

        //Clear Filters button. Clearing filters on the table with executions.
        Button btnClearFilters = new Button();
        btnClearFilters.setCaption("Clear Filters");
        btnClearFilters.setHeight("25px");
        btnClearFilters.setWidth("120px");
        btnClearFilters.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO move this to the monitorTable
                monitorTable.resetFilters();
                monitorTable.setFilterFieldVisible("actions", false);
                monitorTable.setFilterFieldVisible("duration", false);
                monitorTable.setFilterFieldVisible("schedule", false);
            }
        });
        topLine.addComponent(btnClearFilters);

        // Table with pipeline execution records
        monitorTable = new IntlibPagedTable();
        monitorTable.setSelectable(true);
        monitorTable.setWidth("100%");
        monitorTable.setHeight("100%");
        monitorTable.setImmediate(true);
        // TODO move this to monitorTable
        monitorTable.setColumnWidth("schedule", 65);
        monitorTable.setColumnWidth("status", 50);
        monitorTable.setColumnWidth("isDebugging", 50);
        monitorTable.setColumnWidth("duration", 60);
        monitorTable.setColumnWidth("actions", 200);
        monitorTable.setColumnWidth("id", 50);
        monitorTable.setSortEnabled(true);
        monitorTable.setPageLength(PAGE_LENGTH);

        monitorTableLayout.addComponent(monitorTable);
        monitorTableLayout.addComponent(monitorTable.createControls());

        //monitorTable.setFilterGenerator(createFilterGenerator());
        //monitorTable.setFilterDecorator(new filterDecorator());
        monitorTable.setFilterBarVisible(true);

        monitorTable.addItemClickListener(
            new ItemClickEvent.ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    Long executionId = (long) event.getItem().getItemProperty("id").getValue();
                    // TODO show debug data
                }
            });
        // add generated columns to the table
        monitorTable.addGeneratedColumn("actions", createColumnGenerator(presenter));

        hsplit.setFirstComponent(monitorTableLayout);
        hsplit.setSecondComponent(null);
        hsplit.setSplitPosition(100, Unit.PERCENTAGE);
        hsplit.setLocked(true);

        App.getApp().getRefreshManager().addListener(RefreshManager.EXECUTION_MONITOR, new Refresher.RefreshListener() {
            @Override
            public void refresh(Refresher source) {
                // TODO refresh execution monitor
            }
        });

        // at the end set page root
        setCompositionRoot(mainLayout);
    }

    /**
     * Create and return {@link ActionColumnGenerator}.
     *
     * @param presenter
     * @return
     */
    private ActionColumnGenerator createColumnGenerator(final ExecutionPresenter presenter) {
        ActionColumnGenerator generator = new ActionColumnGenerator();
        // add action buttons

        generator.addButton("Cancel", "90px", new Action() {
            @Override
            protected void action(long id) {
                presenter.stop(id);
            }
        }, new ActionColumnGenerator.ButtonShowCondition() {
            @Override
            public boolean show(CustomTable source, long id) {
                Property propStatus = source.getItem(id).getItemProperty("status");
                PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
                // ...
                return status == PipelineExecutionStatus.SCHEDULED
                    || status == PipelineExecutionStatus.RUNNING;
            }
        });

        generator.addButton("Show log", "110px", new Action() {
            @Override
            protected void action(long id) {
                presenter.showLog(id);
            }
        }, new ActionColumnGenerator.ButtonShowCondition() {
            @Override
            public boolean show(CustomTable source, long id) {
                Property propStatus = source.getItem(id).getItemProperty("status");
                PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
                boolean isDebug = (boolean) source.getItem(id).getItemProperty("isDebugging").getValue();
                // ...
                return !isDebug && status != PipelineExecutionStatus.SCHEDULED;
            }
        });

        generator.addButton("Debug data", "120px", new Action() {
            @Override
            protected void action(long id) {
                presenter.showLog(id);
            }
        }, new ActionColumnGenerator.ButtonShowCondition() {
            @Override
            public boolean show(CustomTable source, long id) {
                Property propStatus = source.getItem(id).getItemProperty("status");
                PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
                boolean isDebug = (boolean) source.getItem(id).getItemProperty("isDebugging").getValue();
                // ...
                return isDebug && status != PipelineExecutionStatus.SCHEDULED;
            }
        });

        generator.addButton("Run pipeline", "90px", new Action() {
            @Override
            protected void action(long id) {
                presenter.run(id);
            }
        }, new ActionColumnGenerator.ButtonShowCondition() {
            @Override
            public boolean show(CustomTable source, long id) {
                Property propStatus = source.getItem(id).getItemProperty("status");
                PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
                // ...
                return status != PipelineExecutionStatus.RUNNING
                    && status != PipelineExecutionStatus.CANCELLING;
            }
        });

        generator.addButton("Debug pipeline", "90px", new Action() {
            @Override
            protected void action(long id) {
                presenter.debug(id);
            }
        }, new ActionColumnGenerator.ButtonShowCondition() {
            @Override
            public boolean show(CustomTable source, long id) {
                Property propStatus = source.getItem(id).getItemProperty("status");
                PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
                // ...
                return status != PipelineExecutionStatus.RUNNING
                    && status != PipelineExecutionStatus.CANCELLING;
            }
        });

        return generator;
    }

    private void buildExecutionDetail(PipelineExecution execution) {
        logLayout = new VerticalLayout();
        logLayout.setImmediate(true);
        logLayout.setMargin(true);
        logLayout.setSpacing(true);
        logLayout.setWidth("100%");
        logLayout.setHeight("100%");

        // build the debug view
        buildDebugView(execution);

        logLayout.addComponent(debugView);
        logLayout.setExpandRatio(debugView, 1.0f);

        // Layout for buttons  Close and  Export on the bottom
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setWidth("100%");

        Button buttonClose = new Button();
        buttonClose.setCaption("Close");
        buttonClose.setHeight("25px");
        buttonClose.setWidth("100px");
        buttonClose.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO move into presenter
                App.getApp().getRefreshManager().removeListener(RefreshManager.DEBUGGINGVIEW);
                hsplit.setSplitPosition(100, Unit.PERCENTAGE);
                hsplit.setHeight("100%");
                hsplit.setLocked(true);
            }
        });
        buttonBar.addComponent(buttonClose);
        buttonBar.setComponentAlignment(buttonClose, Alignment.BOTTOM_RIGHT);

        logLayout.addComponent(buttonBar);
        logLayout.setExpandRatio(buttonBar, 0);
        
        if (execution.getStatus() == PipelineExecutionStatus.RUNNING || 
            execution.getStatus() == PipelineExecutionStatus.SCHEDULED) {
			App.getApp().getRefreshManager().addListener(RefreshManager.DEBUGGINGVIEW, 
                RefreshManager.getDebugRefresher(debugView, execution));
		}
    }

    /**
	 * Builds debugging view upon first call, sets the pipeline execution to
	 * show inside it.
	 *
	 * @param exec pipeline execution to show in debugging view
	 */
	private void buildDebugView(PipelineExecution execution) {
		if (debugView == null) {
			debugView = new DebuggingView(execution, null, execution.isDebugging(), false);
		} else {
			debugView.setExecution(execution, null);
		}
	}
    
}
