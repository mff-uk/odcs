package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.browser.BrowserInitFailedException;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowser;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowserFactory;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitNotFoundException;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shows complex debug information about current pipeline execution. Shows information about whole run or if specific DPU is selected only information related to this DPU.
 * Top table shows events which occurred during pipeline execution. DPU selection is available if the pipeline is in debug mode. Bottom part consists of tabs. 
 * Log tab shows log messages, which can be filtered by level. Browse tab shows triples from graph which selected DPU created. 
 * Query tab allows to query data from graphs which were created during pipeline execution.
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
	private TextArea logTextArea;
	private QueryView queryView;
	private HorizontalLayout refreshComponent;
	private LogMessagesTable logMessagesTable;

	/**
	 * Default constructor.
	 * 
	 * @param pipelineExec Pipeline execution.
	 * @param debugDpu Preselected DPU or null.
	 * @param debug Is execution in debug mode?
	 */
	public DebuggingView(PipelineExecution pipelineExec, DPUInstanceRecord debugDpu, boolean debug) {
		this.pipelineExec = pipelineExec;
		this.debugDpu = debugDpu;
		this.isInDebugMode = debug;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Builds main layout.
	 */
	public final void buildMainLayout() {
		mainLayout = new VerticalLayout();

		executionRecordsTable = new RecordsTable();
		executionRecordsTable.setWidth("100%");

		mainLayout.addComponent(executionRecordsTable);
		
		// DPU selector is shown in debug mode only
		if (isInDebugMode) {
			buildDpuSelector();
		}

		tabs = new TabSheet();
		tabs.setSizeFull();

		browseTab = tabs.addTab(new Label("Browser"), "Browse");
		
		refreshComponent = buildRefreshComponent();

		//logTextArea = new TextArea();
		//logTextArea.setValue("Log file content");
		VerticalLayout logLayout = new VerticalLayout();
		logLayout.addComponent(refreshComponent);
		
		logMessagesTable = new LogMessagesTable();
		logLayout.addComponent(logMessagesTable);
		//logLayout.addComponent(logTextArea);
		logLayout.setSizeFull();
		//logTextArea.setSizeFull();
		//logTextArea.setReadOnly(true);
		//logTextArea.setHeight(460, Unit.PIXELS);
		logTab = tabs.addTab(logLayout, "Log");

		queryView = new QueryView(this);
		if(debugDpu != null) {
			queryView.setGraphs(debugDpu.getType());
		}
		queryTab = tabs.addTab(queryView, "Query");

		mainLayout.setSizeFull();
		mainLayout.addComponent(tabs);

		fillContent();		
	}

	/**
	 * Fills DebuggingView with data.
	 * 
	 */
	public void fillContent() {
		boolean loadSuccessful = loadExecutionContextReader();

		List<Record> records = App.getDPUs().getAllDPURecords(pipelineExec);
		executionRecordsTable.setDataSource(records);

		boolean isRunFinished = !(pipelineExec.getExecutionStatus() == ExecutionStatus.SCHEDULED || pipelineExec.getExecutionStatus() == ExecutionStatus.RUNNING);

		//Table with data
		if (loadSuccessful && isInDebugMode && debugDpu != null && isRunFinished) {
			DataUnitBrowser browser = loadBrowser(false);
			if (browser != null) {
				tabs.removeTab(browseTab);
				browseTab = tabs.addTab(browser, "Browse");
				browseTab.setEnabled(true);
				tabs.setSelectedTab(browseTab);
			} else {
				browseTab.setEnabled(false);
				loadSuccessful = false;
			}
		} else {
			browseTab.setEnabled(false);
		}

		//Content of text log file
		logMessagesTable.setDpu(debugDpu);
		if (loadSuccessful) {
// TODO !! List log			
			/*
			File logFile = ctxReader.getLogFile();
			String logText = "Log file is empty!";
			if (logFile.exists()) {
				try {
					Scanner scanner = new Scanner(logFile).useDelimiter("\\A");
					if (scanner.hasNext()) {
						logText = scanner.next();
					}
				} catch (FileNotFoundException ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
					logText = "Failed to load log file!";
				}
			} else {
				logText = "Log file doesn't exist!";
			}
			logTextArea.setValue(logText);
			logTab.setEnabled(true);
			*/
		} else {
			//logTab.setEnabled(false);
		}

		//Query View
		if (loadSuccessful && isInDebugMode && debugDpu != null && isRunFinished) {
			queryTab.setEnabled(true);
		} else {
			queryTab.setEnabled(false);
		}

		//Create tab with information about running pipeline and refresh button
		if(infoTab != null) {
			tabs.removeTab(infoTab);

		}
		
		boolean showRefresh = !loadSuccessful && isInDebugMode || !isRunFinished;
		refreshComponent.setVisible(showRefresh);
	}

	/**
	 * Reloads content.
	 */
	private void refreshContent() {
		pipelineExec = App.getPipelines().getExecution(pipelineExec.getId());
		fillContent();
		if(debugDpu != null) {
			queryView.setGraphs(debugDpu.getType());
		}
		setCompositionRoot(mainLayout);
	}

	/**
	 * Tries to load context for given pipeline execution.
	 * @return Load was successful.
	 */
	private boolean loadExecutionContextReader() {	
		ctxReader = pipelineExec.getContextReadOnly();
		return ctxReader != null;
	}

	/**
	 * Loads Browser tab content.
	 * @param showInput Input/Output graph should be showed.
	 * @return Browser
	 */
	private DataUnitBrowser loadBrowser(boolean showInput) {
		if (debugDpu == null) {
			return null;
		}
		List<DataUnitInfo> indexes = ctxReader.getDataUnitsInfo(debugDpu);
		
		if (indexes == null) {
			return null;
		}

		Iterator<DataUnitInfo> iter = indexes.iterator();
		while (iter.hasNext()) {
		
			DataUnitInfo dataUnitInfo = iter.next();
			
			
			if (indexes.size() == 1 || showInput) {
				DataUnitBrowser duBrowser;
				try {
					//File dumpDir = ctxReader.getDataUnitStorage(debugDpu, dataUnitInfo.getIndex());
					duBrowser = 
							DataUnitBrowserFactory.getBrowser(ctxReader, debugDpu, showInput, dataUnitInfo);
				} catch (DataUnitNotFoundException | BrowserInitFailedException ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
					return null;
				}
				
				duBrowser.enter();
				return duBrowser;
			}
		}
		return null;
	}

	/**
	 * Gets repository path from context.
	 * @param onInputGraph
	 * @return 
	 */
	String getRepositoryPath(boolean onInputGraph) {
		
		if (debugDpu == null) {
			return null;
		}
		List<DataUnitInfo> infos = ctxReader.getDataUnitsInfo(debugDpu);
				
		if (infos == null) {
			return null;
		}

		Iterator<DataUnitInfo> iter = infos.iterator();
		while (iter.hasNext()) {
			DataUnitInfo duInfo = iter.next();
			
			if (debugDpu.getType() != DPUType.Transformer || duInfo.isInput() == onInputGraph) {
				ctxReader.getDataUnitStorage(debugDpu, duInfo.getIndex());
			}
		}
		return null;
	}

	/**
	 * Gets repository directory from context.
	 * @param onInputGraph
	 * @return 
	 */
	File getRepositoryDirectory(boolean onInputGraph) {
		
		if (debugDpu == null) {
			return null;
		}
		List<DataUnitInfo> infos = ctxReader.getDataUnitsInfo(debugDpu);
				
		if (infos == null) {
			return null;
		}

		Iterator<DataUnitInfo> iter = infos.iterator();
		while (iter.hasNext()) {
			DataUnitInfo duInfo = iter.next();
			
			if (debugDpu.getType() != DPUType.Transformer || duInfo.isInput() == onInputGraph) {
				duInfo.getDirectory();
			}
		}
		return null;
	}
	
	/**
	 * Refresh component factory. Is to be displayed while pipeline is still
	 * running. Contains refresh button, which updates the content of
	 * debugging view and shows the most current data of given pipeline run.
	 * 
	 * @return layout with label and refresh button
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
	private void buildDpuSelector() {
		ComboBox dpuSelector = new ComboBox("Select DPU:");
		dpuSelector.setImmediate(true);
		for (Node node : pipelineExec.getPipeline().getGraph().getNodes()) {
			dpuSelector.addItem(node.getDpuInstance());
		}
		if(debugDpu != null) {
			dpuSelector.select(debugDpu);
		}
		dpuSelector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				if (value != null && value.getClass() == DPUInstanceRecord.class) {
					debugDpu = (DPUInstanceRecord) value;
					refreshContent();
				}
			}
		});
		mainLayout.addComponent(dpuSelector);
	}

	/**
	 * Resizes log area after window with DebuggingView was resized.
	 * 
	 * @param height 
	 */
	public void resize(float height) {
		float newLogHeight = height - 325;
		if(newLogHeight < 400) {
			newLogHeight = 400;
		}
		//logTextArea.setHeight(newLogHeight, Unit.PIXELS);
	}
}
