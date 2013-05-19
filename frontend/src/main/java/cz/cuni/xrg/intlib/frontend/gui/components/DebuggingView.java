package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextFactory;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextReader;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.rdf.RDFTriple;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.browser.BrowserInitFailedException;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowser;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowserFactory;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bogo
 */
public class DebuggingView extends CustomComponent {

	private VerticalLayout mainLayout;
	private PipelineExecution pipelineExec;
	private ExecutionContextReader ctxReader;
	private DPUInstance debugDpu;

	public DebuggingView(PipelineExecution pipelineExec, DPUInstance debugDpu) {
		//setCaption("Debug window");
		this.pipelineExec = pipelineExec;
		this.debugDpu = debugDpu;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		//this.setContent(mainLayout);
	}

	public final void buildMainLayout() {
		mainLayout = new VerticalLayout();

		boolean loadSuccessful = loadExecutionContextReader();
		if (!loadSuccessful) {
			//Notification.show("Failed to load execution context!", Notification.Type.ERROR_MESSAGE);
		}

		//List<Record> records = debugDpu == null ? App.getDPUs().getAllDPURecords() : App.getDPUs().getAllDPURecords(debugDpu);
		List<Record> records = App.getDPUs().getAllDPURecords();
		records = filterRecords(records, pipelineExec);
		RecordsTable executionRecordsTable = new RecordsTable(records);
		executionRecordsTable.setWidth("100%");
		executionRecordsTable.setHeight("100px");

		mainLayout.addComponent(executionRecordsTable);

		TabSheet tabs = new TabSheet();
		tabs.setHeight("500px");

		//Table with data
		//VirtuosoRDFRepo rdfRepo = VirtuosoRDFRepo.createVirtuosoRDFRepo();
		//rdfRepo.getRDFTriplesInRepository();
		if (loadSuccessful) {


			DataUnitBrowser browser = loadBrowser(false);
			tabs.addTab(browser, "Browse");
		}


		//RecordsTable with different data source
		if (loadSuccessful) {
			File logFile = ctxReader.getLog4jFile();
			String logText = "";
			if (logFile.exists()) {
				try {
					Scanner scanner = new Scanner(logFile).useDelimiter("\\A");
					if (scanner.hasNext()) {
						logText = scanner.next();
					}
				} catch (FileNotFoundException ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			TextArea logTextArea = new TextArea("Log from log4j", logText);
			logTextArea.setSizeFull();

			//List<Record> fullRecords = App.getDPUs().getAllDPURecords();
			//RecordsTable fullRecordsTable = new RecordsTable(fullRecords);
			//fullRecordsTable.setWidth("100%");
			//fullRecordsTable.setHeight("100%");
			tabs.addTab(logTextArea, "Log");
		}

		//Query View
		QueryView queryView = new QueryView();
		tabs.addTab(queryView, "Query");

		mainLayout.setSizeUndefined();
		mainLayout.setWidth("600px");
		mainLayout.addComponent(tabs);


		//return mainLayout;
	}

//	private List<Record> buildStubMessageData() {
//		List<Record> stubList = new ArrayList<>();
///*		Record m = new Record(new Date(), RecordType.DPUINFO, null, "Test message", "Long test message");
//		m.setId(1);
//		stubList.add(m);
//		Record m2 = new Record(new Date(), RecordType.DPUWARNING, null, "Test warning", "Long test warning message");
//		m2.setId(2);
//		stubList.add(m2);*/
//
//		return stubList;
//	}
//	private List<Record> buildStubFullData() {
//		List<Record> fullList = buildStubMessageData();
//
///*		Record m = new Record(new Date(), RecordType.DPULOG, null, "Test log message", "Long test log message");
//		m.setId(3);
//		fullList.add(1, m);
//		Record m2 = new Record(new Date(), RecordType.DPULOG, null, "Another test log message", "Bla bla Long test warning message");
//		m2.setId(4);
//		fullList.add(m2);*/
//
//		return fullList;
//	}
	private boolean loadExecutionContextReader() {
		File workingDir = new File(pipelineExec.getWorkingDirectory());
		try {
			ctxReader = ExecutionContextFactory.restoreAsRead(workingDir);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}

	private DataUnitBrowser loadBrowser(boolean showInput) {
		Set<Integer> indexes = ctxReader.getIndexesForDataUnits(debugDpu);
		while (indexes.iterator().hasNext()) {
			Integer index = indexes.iterator().next();
			DataUnitInfo duInfo = ctxReader.getDataUnitInfo(debugDpu, index);
			if (duInfo.isInput() == showInput) {
				DataUnitBrowser duBrowser;
				try {
					duBrowser = DataUnitBrowserFactory.getBrowser(ctxReader, debugDpu, index);
				} catch (DataUnitNotFoundException | BrowserInitFailedException ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
					return null;
				}
				try {
					duBrowser.loadDataUnit(duInfo.getDirectory());
				} catch (Exception ex) {
					Logger.getLogger(DebuggingView.class.getName()).log(Level.SEVERE, null, ex);
					return null;
				}
				duBrowser.enter();
				return duBrowser;
			}

		}
		return null;
	}

	private List<Record> filterRecords(List<Record> records, PipelineExecution pipelineExec) {
		List<Record> filteredRecords = new ArrayList<>();
		for (Record record : records) {
			if (record.getExecution().equals(pipelineExec)) {
				filteredRecords.add(record);
			}
		}
		return filteredRecords;
	}
}
