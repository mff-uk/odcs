package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.execution.context.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibHelper;
import cz.cuni.xrg.intlib.frontend.auxiliaries.RefreshThread;
import cz.cuni.xrg.intlib.frontend.browser.BrowserInitFailedException;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowser;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowserFactory;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitNotFoundException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private ExecutionContextInfo ctxReader;
    private DPUInstanceRecord debugDpu;
    private boolean isInDebugMode;
    private RecordsTable executionRecordsTable;
    private Tab browseTab;
    private Tab logTab;
    private Tab queryTab;
    private Tab infoTab;
    private TabSheet tabs;
    private QueryView queryView;
    //private HorizontalLayout refreshComponent;
    private LogMessagesTable logMessagesTable;
    private ComboBox dpuSelector;
    private boolean isFromCanvas;
    private Embedded iconStatus;
    private CheckBox refreshAutomatically = null;
    private RefreshThread refreshThread = null;

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
        return !(pipelineExec.getExecutionStatus() == PipelineExecutionStatus.SCHEDULED || pipelineExec.getExecutionStatus() == PipelineExecutionStatus.RUNNING);
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

        executionRecordsTable = new RecordsTable();
        executionRecordsTable.setWidth("100%");

        mainLayout.addComponent(executionRecordsTable);

        HorizontalLayout optionLine = new HorizontalLayout();
        optionLine.setWidth(100, Unit.PERCENTAGE);
        // DPU selector is shown in debug mode only
        if (isInDebugMode) {
            optionLine.addComponent(buildDpuSelector());
        }
        if(!isRunFinished()) {
            refreshAutomatically = new CheckBox("Refresh automatically", true);
            refreshAutomatically.setImmediate(true);
            optionLine.addComponent(refreshAutomatically);
            optionLine.setComponentAlignment(refreshAutomatically, Alignment.MIDDLE_RIGHT);
            
            refreshThread = new RefreshThread(2000, this.pipelineExec, this);
            refreshThread.start();
        }
        mainLayout.addComponent(optionLine);

        tabs = new TabSheet();
        tabs.setSizeFull();

        browseTab = tabs.addTab(new Label("Browser"), "Browse");

        //refreshComponent = buildRefreshComponent();

        //logTextArea = new TextArea();
        //logTextArea.setValue("Log file content");
        VerticalLayout logLayout = new VerticalLayout();
        //logLayout.addComponent(refreshComponent);

        logMessagesTable = new LogMessagesTable();
        logLayout.addComponent(logMessagesTable);
        //logLayout.addComponent(logTextArea);
        logLayout.setSizeFull();
        //logTextArea.setSizeFull();
        //logTextArea.setReadOnly(true);
        //logTextArea.setHeight(460, Unit.PIXELS);
        logTab = tabs.addTab(logLayout, "Log");

        queryView = new QueryView(this);
        if (debugDpu != null) {
            queryView.setGraphs(debugDpu.getType());
        }
        queryTab = tabs.addTab(queryView, "Query");

        mainLayout.setSizeFull();
        mainLayout.addComponent(tabs);

        fillContent();
    }

    /**
     * Fills DebuggingView with data, obtained from objects passed in
     * constructor.
     */
    public void fillContent() {

        if (isFromCanvas) {
            ThemeResource icon = IntlibHelper.getIconForExecutionStatus(pipelineExec.getExecutionStatus());
            iconStatus.setSource(icon);
            iconStatus.setDescription(pipelineExec.getExecutionStatus().name());
        }

        boolean loadSuccessful = loadExecutionContextReader();

        List<MessageRecord> records = App.getDPUs().getAllDPURecords(pipelineExec);
        executionRecordsTable.setDataSource(records);

        if (loadSuccessful && isInDebugMode) {
            refreshDpuSelector();
        }

        //Table with data
        if (loadSuccessful && isInDebugMode && debugDpu != null && isRunFinished()) {
            DataUnitBrowser browser = loadBrowser(false);
            if (browser != null) {
                tabs.removeTab(browseTab);
                browseTab = tabs.addTab(browser, "Browse");
                browseTab.setEnabled(true);
                //tabs.setSelectedTab(browseTab);
            } else {
                browseTab.setEnabled(false);
                loadSuccessful = false;
            }
        } else {
            browseTab.setEnabled(false);
        }

        //Content of text log file
        logMessagesTable.setDpu(pipelineExec, isInDebugMode ? (DPUInstanceRecord) dpuSelector.getValue() : null);

        //Query View
        if (loadSuccessful && isInDebugMode && debugDpu != null && isRunFinished()) {
            queryTab.setEnabled(true);
        } else {
            queryTab.setEnabled(false);
        }

        //Create tab with information about running pipeline and refresh button
        if (infoTab != null) {
            tabs.removeTab(infoTab);

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
        fillContent();
        fireRefreshRequest();
        if (debugDpu != null) {
            queryView.setGraphs(debugDpu.getType());
        }
        setCompositionRoot(mainLayout);
    }

    /**
     * Tries to load context for given pipeline execution.
     *
     * @return Load was successful.
     */
    private boolean loadExecutionContextReader() {
        ctxReader = pipelineExec.getContextReadOnly();
        return ctxReader != null;
    }

    /**
     * Loads Browser tab content.
     *
     * @param showInput Input/Output graph should be showed.
     * @return {@link DataUnitBrowser} for actual {@link DPUInstanceRecord}.
     */
    private DataUnitBrowser loadBrowser(boolean showInput) {
        if (debugDpu == null) {
            return null;
        }
        List<DataUnitInfo> indexes = ctxReader.getDPUInfo(debugDpu).getDataUnits();
        
        
        if (indexes == null) {
            return null;
        }

        Iterator<DataUnitInfo> iter = indexes.iterator();
        while (iter.hasNext()) {

            DataUnitInfo dataUnitInfo = iter.next();
            //If I Have only one data unit, I use it, otherwise select the right input/output data unit.
            if (indexes.size() == 1 || showInput) {
                DataUnitBrowser duBrowser;
                try {
                    //File dumpDir = ctxReader.getDataUnitStorage(debugDpu, dataUnitInfo.getIndex());
                    duBrowser =
                            DataUnitBrowserFactory.getBrowser(ctxReader, pipelineExec, debugDpu, dataUnitInfo);


                } catch (DataUnitNotFoundException | BrowserInitFailedException ex) {
                    Logger.getLogger(DebuggingView.class
                            .getName()).log(Level.SEVERE, null, ex);

                    return null;
                }
                if (duBrowser != null) {
                    duBrowser.enter();
                }
                return duBrowser;
            }
        }
        return null;
    }

    /**
     * Gets repository path from context.
     *
     * @return {@link ExecutionContextInfo} containing current execution
     * information.
     */
    LocalRDFRepo getRepository(boolean onInputGraph) {
        if (debugDpu == null) {
            return null;
        }
        List<DataUnitInfo> infos = 
        		ctxReader.getDPUInfo(debugDpu).getDataUnits();

        if (infos == null) {
            return null;
        }

        Iterator<DataUnitInfo> iter = infos.iterator();
        while (iter.hasNext()) {
            DataUnitInfo duInfo = iter.next();

            if (debugDpu.getType() != DPUType.TRANSFORMER || duInfo.isInput() == onInputGraph) {
                return DataUnitBrowserFactory.getRepository(ctxReader, pipelineExec, debugDpu, duInfo);
            }
        }
        return null;
    }

    /**
     * Gets repository directory from context.
     *
     * @param onInputGraph Repository path of Input/Output graph.
     * @return {@link File} representing directory of repository.
     */
    /*File getRepositoryDirectory(boolean onInputGraph) {
     * 
     * if (debugDpu == null) {
     * return null;
     * }
     * List<DataUnitInfo> infos = ctxReader.getDataUnitsInfo(debugDpu);
     * 
     * if (infos == null) {
     * return null;
     * }
     * 
     * Iterator<DataUnitInfo> iter = infos.iterator();
     * while (iter.hasNext()) {
     * DataUnitInfo duInfo = iter.next();
     * 
     * if (debugDpu.getType() != DPUType.TRANSFORMER || duInfo.isInput() == onInputGraph) {
     * duInfo.getDirectory();
     * }
     * }
     * return null;
     * }*/
    /**
     * Refresh component factory. Is to be displayed while pipeline is still
     * running. Contains refresh button, which updates the content of debugging
     * view and shows the most current data of given pipeline run.
     *
     * @return Layout with label and refresh button.
     */
    private HorizontalLayout buildRefreshComponent() {

        Button refreshButton = new Button("Refresh",
                new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                refreshContent();
            }
        });

        Label label = new Label("Pipeline is still running. Please click refresh to update status.");
        label.setStyleName("warning");
        label.setWidth(450, Unit.PIXELS);

        HorizontalLayout refreshLayout = new HorizontalLayout();
        refreshLayout.setWidth(100, Unit.PERCENTAGE);
        refreshLayout.addComponent(label);
        refreshLayout.addComponent(refreshButton);
        refreshLayout.setComponentAlignment(refreshButton, Alignment.MIDDLE_RIGHT);

        refreshLayout.setVisible(false);

        return refreshLayout;
    }

    /**
     * DPU select box factory.
     */
    private ComboBox buildDpuSelector() {
        dpuSelector = new ComboBox("Select DPU:");
        dpuSelector.setImmediate(true);
        if (ctxReader != null) {
            refreshDpuSelector();
        }
        dpuSelector.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();


                if (value != null && value.getClass() == DPUInstanceRecord.class) {
                    debugDpu = (DPUInstanceRecord) value;
                } else {
                    debugDpu = null;
                }
                refreshContent();
            }
        });
        return dpuSelector;
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
     * Fills dpu selector with dpus for which there are debug information
     * available.
     */
    private void refreshDpuSelector() {

//		 Collection<?> o = dpuSelector.getVisibleItemIds();
//		if(o != null && !o.isEmpty()) {
//			return;
//		}
        //dpuSelector.removeAllItems();
        Set<DPUInstanceRecord> contextDpuIndexes = ctxReader.getDPUIndexes();
        for (DPUInstanceRecord dpu : contextDpuIndexes) {
            if (!dpuSelector.containsId(dpu)) {
                dpuSelector.addItem(dpu);
                if (dpu.equals(debugDpu)) {
                    dpuSelector.select(debugDpu);


                }
            }
        }
//		if (debugDpu != null) {
//			dpuSelector.select(debugDpu);
//		}
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
     * Sets execution and debug node about which debug ingo should be shown.
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
            refreshThread = new RefreshThread(2000, this.pipelineExec, this);
            refreshThread.start();
        }
    }
}
