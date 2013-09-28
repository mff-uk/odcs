package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;

import java.util.*;

/**
 * Shows complex debug information about current pipeline execution. Shows
 * information about whole run or if specific DPU is selected only information
 * related to this DPU. Top table shows events which occurred during pipeline
 * execution. DPU selection is available if the pipeline is in debug mode.
 * Bottom part consists of tabs. Log tab shows log messages, which can be
 * filtered by level. Browse tab shows triples from graph which selected DPU
 * created. Query tab allows to query data from graphs which were created during
 * pipeline execution.
 *
 * @author Bogo
 */
public class DebuggingView extends CustomComponent {

    private VerticalLayout mainLayout;
    private PipelineExecution pipelineExec;
    private DPUInstanceRecord debugDpu;
    private boolean isInDebugMode;
    private RecordsTable executionRecordsTable;
    private Tab eventsTab;
	private Tab logTab;
    private Tab queryTab;
    private TabSheet tabs;
    private RDFQueryView queryView;
    //private HorizontalLayout refreshComponent;
    private LogMessagesTable logMessagesTable;
    private boolean isFromCanvas;
    private Embedded iconStatus;
    private CheckBox refreshAutomatically = null;

    /**
     * Default constructor.
     *
     * @param pipelineExec Pipeline execution.
     * @param debugDpu Preselected DPU or null.
     * @param debug Is execution in debug mode?
     */
    public DebuggingView(PipelineExecution pipelineExec, DPUInstanceRecord debugDpu, boolean debug, boolean isFromCanvas) {
        this.pipelineExec = pipelineExec;
        this.debugDpu = debugDpu;
        this.isInDebugMode = debug;
        this.isFromCanvas = isFromCanvas;
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    /**
     * Returns whether given execution is finished.
     *
     * @return Execution is finished.
     */
    public boolean isRunFinished() {
        return !(pipelineExec.getStatus() == PipelineExecutionStatus.SCHEDULED || pipelineExec.getStatus() == PipelineExecutionStatus.RUNNING);
    }
    
    public boolean isRefreshingAutomatically() {
        return refreshAutomatically.getValue();
    }

    /**
     * Builds main layout.
     */
    public final void buildMainLayout() {

        mainLayout = new VerticalLayout();

        if (isFromCanvas) {
            HorizontalLayout topLine = new HorizontalLayout();
            Label labelPipelineStatus = new Label("Pipeline status:");
            topLine.addComponent(labelPipelineStatus);
            iconStatus = new Embedded();
            iconStatus.setImmediate(true);
            topLine.addComponent(iconStatus);
            mainLayout.addComponent(topLine);
        }
		
		tabs = new TabSheet();
        tabs.setSizeFull();

        executionRecordsTable = new RecordsTable();
        executionRecordsTable.setWidth("100%");

		eventsTab = tabs.addTab(executionRecordsTable, "Events");

        HorizontalLayout optionLine = new HorizontalLayout();
        optionLine.setWidth(100, Unit.PERCENTAGE);
		
        if(!isRunFinished()) {
            refreshAutomatically = new CheckBox("Refresh automatically", true);
            refreshAutomatically.setImmediate(true);
            optionLine.addComponent(refreshAutomatically);
            optionLine.setComponentAlignment(refreshAutomatically, Alignment.MIDDLE_RIGHT);
        }
        mainLayout.addComponent(optionLine);

        VerticalLayout logLayout = new VerticalLayout();

        logMessagesTable = new LogMessagesTable();
        logLayout.addComponent(logMessagesTable);
        logLayout.setSizeFull();
        logTab = tabs.addTab(logLayout, "Log");

        queryView = new RDFQueryView(pipelineExec);
        if (debugDpu != null) {
            queryView.setDpu(debugDpu);
        }
        queryTab = tabs.addTab(queryView, "Browse/Query");

        mainLayout.setSizeFull();
        mainLayout.addComponent(tabs);

        fillContent(false);
    }

    /**
     * Fills DebuggingView with data, obtained from objects passed in
     * constructor.
     */
    public void fillContent(boolean isRefresh) {

        if (isFromCanvas) {
            ThemeResource icon = IntlibHelper.getIconForExecutionStatus(pipelineExec.getStatus());
            iconStatus.setSource(icon);
            iconStatus.setDescription(pipelineExec.getStatus().name());
        }

        executionRecordsTable.setPipelineExecution(pipelineExec);

        //Content of text log file
        logMessagesTable.setDpu(pipelineExec, null, isRefresh);

        //Query View
        if (isInDebugMode && isRunFinished()) {
            queryTab.setEnabled(true);
        } else {
            queryTab.setEnabled(false);
        }
        
        if(isRunFinished() && refreshAutomatically != null) {
            refreshAutomatically.setVisible(false);
        }
    }

    /**
     * Reloads content. Data are obtained from objects passed in constructor.
     */
    public void refreshContent() {
        pipelineExec = App.getPipelines().getExecution(pipelineExec.getId());
        fillContent(true);
        fireRefreshRequest();
//        if (debugDpu != null) {
//            queryView.setGraphs(debugDpu.getType());
//        }
        setCompositionRoot(mainLayout);
    }

    /**
     * Resizes log area after window with DebuggingView was resized.
     *
     * @param height New height of log text area.
     */
    public void resize(float height) {
        float newLogHeight = height - 325;
        if (newLogHeight < 400) {
            newLogHeight = 400;
        }
        //logTextArea.setHeight(newLogHeight, Unit.PIXELS);
    }

    

    /**
     * Fires refresh request event.
     */
    protected void fireRefreshRequest() {
        Collection<Listener> ls = (Collection<Listener>) this.getListeners(Component.Event.class);
        for (Listener l : ls) {
            l.componentEvent(new Event(this));
        }
    }

    /**
     * Sets execution and debug node about which debug info should be shown.
     * 
     * @param execution New execution.
     * @param instance New debug node.
     * 
     */
    public void setExecution(PipelineExecution execution, DPUInstanceRecord instance) {
        this.pipelineExec = execution;
        this.debugDpu = instance;
        refreshContent();
        if(refreshAutomatically.getValue()) {
			//App.getApp().getRefreshThread().refreshExecution(this.pipelineExec, this);
			App.getApp().getRefreshManager().addListener(RefreshManager.DEBUGGINGVIEW, RefreshManager.getDebugRefresher(this, execution));
		}
    }
}
