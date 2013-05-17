package cz.cuni.xrg.intlib.frontend.gui.views;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.rdf.RDFTriple;
import cz.cuni.xrg.intlib.frontend.gui.components.RecordsTable;

/**
 * @author Maria Kukhar
 */

public class ExecutionMonitor extends CustomComponent implements View,
		ClickListener {

	@AutoGenerated
	private VerticalLayout monitorTableLayout;
	private VerticalLayout logLayout;
	private HorizontalSplitPanel hsplit;
	private Panel mainLayout;
	@AutoGenerated
	private Label label;
	private Table monitorTable;
	private DateField dateFilter;
	private TextField nameFilter;
	private TextField userFilter;
	private ComboBox statusFilter;
	private ComboBox DebugFilter;
	private IndexedContainer tableData;
	static String filter;

	static String[] visibleCols = new String[] { "date", "name", "user",
			"status", "debug", "obsolete", "actions", "report" };
	static String[] headers = new String[] { "Date", "Name", "User", "Status",
			"Debug", "Obsolete", "Actions", "Report" };

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public ExecutionMonitor() {

	}

	private MonitorTableFilter tableDataFilter = null;

	@AutoGenerated
	private Panel buildMainLayout() {
		// common part: create layout

		mainLayout = new Panel("");

		hsplit = new HorizontalSplitPanel();
		mainLayout.setContent(hsplit);

		monitorTableLayout = new VerticalLayout();
		monitorTableLayout.setImmediate(true);
		monitorTableLayout.setMargin(true);
		monitorTableLayout.setSpacing(true);
		monitorTableLayout.setWidth("100%");
		monitorTableLayout.setHeight("100%");
		

		// top-level component properties

		setWidth("100%");
		setHeight("100%");

		// label

		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("<h1>ExecutionMonitor</h>");
		label.setContentMode(ContentMode.HTML);
		monitorTableLayout.addComponent(label);

		Label filtersLabel = new Label();
		filtersLabel.setCaption("Filters:");
		filtersLabel.setWidth("100px");
		monitorTableLayout.addComponent(filtersLabel);

		filter = new String();

		HorizontalLayout filtersLayout = new HorizontalLayout();
		filtersLayout.setWidth("100%");
		filtersLayout.setSpacing(true);

		dateFilter = new DateField();
		dateFilter.setDateFormat("yyyy.MM.dd");
		dateFilter.setWidth("90%");
		filtersLayout.addComponent(dateFilter);

		if (tableDataFilter == null) {
			tableDataFilter = new MonitorTableFilter();
		}
		nameFilter = new TextField();
		nameFilter.setInputPrompt("Pipeline Name Filter");
		nameFilter.setWidth("90%");
		nameFilter.setTextChangeEventMode(TextChangeEventMode.LAZY);
		nameFilter.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				tableDataFilter.setNameFilter(event.getText());

				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);

			}
		});

		filtersLayout.addComponent(nameFilter);

		userFilter = new TextField();
		userFilter.setInputPrompt("User Filter");
		userFilter.setWidth("90%");
		userFilter.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {

				tableDataFilter.setUserFilter(event.getText());
				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);

			}
		});

		filtersLayout.addComponent(userFilter);

		statusFilter = new ComboBox();
		statusFilter.setInputPrompt("Status Filter");
		statusFilter.setWidth("90%");
		statusFilter.addItem("Running");
		statusFilter.addItem("Finished no errors");
		statusFilter.addItem("Finished with errors");
		filtersLayout.addComponent(statusFilter);

		DebugFilter = new ComboBox();
		DebugFilter.setInputPrompt("Debug Filter");
		DebugFilter.setWidth("90%");
		DebugFilter.addItem("Debug Yes");
		filtersLayout.addComponent(DebugFilter);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Delete Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("100%");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						dateFilter.setValue(null);
						nameFilter.setValue("");
						userFilter.setValue("");
						statusFilter.setValue(null);
						DebugFilter.setValue(null);
						tableData.removeAllContainerFilters();

					}
				});
		filtersLayout.addComponent(buttonDeleteFilters);

		monitorTableLayout.addComponent(filtersLayout);

		tableData = getTableData(App.getApp().getPipelines().getAllExecutions());
		
		
		monitorTable = new Table("");
		monitorTable.setSelectable(true);
		monitorTable.setContainerDataSource(tableData);
		monitorTable.setWidth("100%");
		monitorTable.setHeight("100%");
		monitorTable.setImmediate(true);
		monitorTable.setVisibleColumns(visibleCols); // Set visible columns
		monitorTable.setColumnHeaders(headers);
		// monitorTable.setPageLength(10);

		monitorTable.addGeneratedColumn("actions",
				new GenerateActionColumnMonitor(this));

		monitorTableLayout.addComponent(monitorTable);
		// mainLayout.addComponent(monitorTable.createControls());

		logLayout = new VerticalLayout();
		logLayout.setImmediate(true);
		logLayout.setMargin(true);
		logLayout.setSpacing(true);
		logLayout.setWidth("100%");
		logLayout.setHeight("100%");

		GridLayout infoBar = new GridLayout();
		infoBar.setWidth("100%");
		infoBar.setSpacing(true);
		infoBar.setRows(2);
		infoBar.setColumns(5);
		
		infoBar.addComponent(new Label("Messages overview for: "), 0, 0);
		infoBar.addComponent(new Label("Pipeline: "), 1, 0);
		infoBar.addComponent(new Label("User: "), 1, 1);
		infoBar.addComponent(new Label("Start: "), 3, 0);
		infoBar.addComponent(new Label("End: "), 3, 1);
		
/*		TextField pipeline = new TextField();
		pipeline.setEnabled(true);
		infoBar.addComponent(pipeline, 2, 0);
		
		TextField user = new TextField();
		user.setEnabled(true);
		infoBar.addComponent(user, 2, 1);
		
		DateField start = new DateField();
		infoBar.addComponent(start, 4, 0);
		
		DateField end = new DateField();
		infoBar.addComponent(end, 4, 1); */
		
		logLayout.addComponent(infoBar);
		
		
	//	RecordsTable executionRecordsTable = new RecordsTable(
	//			buildStubMessageData());
	//	executionRecordsTable.setWidth("100%");
	//	executionRecordsTable.setHeight("100px");

	//	logLayout.addComponent(executionRecordsTable);
		


		
		
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidth("100%");

		Button buttonClose = new Button();
		buttonClose.setCaption("Close");
		buttonClose.setHeight("25px");
		buttonClose.setWidth("100px");
		buttonClose
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						
						hsplit.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);
						hsplit.setLocked(true);
					}
				});
		buttonBar.addComponent(buttonClose);
		buttonBar.setComponentAlignment(buttonClose,Alignment.BOTTOM_LEFT);

		Button buttonExport = new Button();
		buttonExport.setCaption("Export");
		buttonExport.setHeight("25px");
		buttonExport.setWidth("100px");
		buttonExport
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

					}
				});
		buttonBar.addComponent(buttonExport);
		buttonBar.setComponentAlignment(buttonExport,Alignment.BOTTOM_RIGHT);
		
		logLayout.addComponent(buttonBar);
		

		hsplit.setFirstComponent(monitorTableLayout);
		hsplit.setSecondComponent(null);
		hsplit.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);
		hsplit.setLocked(true);
		

		return mainLayout;
	}

	public static IndexedContainer getTableData(List<PipelineExecution> data) {

		String[] date = { "2012.02.12", "2012.02.13", "2012.12.12",
				"2013.05.13", "2013.01.18", "2012.03.01", "2012.04.30" };
		String[] name = { "Ext", "Alf", "Ext", "Pipe1", "Ted", "Ted", "Ted" };
		String[] user = { "knapt", "knapt", "tomesj", "kukharm", "skpdap",
				"kukharm", "skpdap" };
		String[] status = { "progress", "error", "ok", "error", "ok", "ok",
				"ok" };

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
			result.addContainerProperty(p, String.class, "");
		}

		int max = getMinLength(date, name, user, status);

		/*for (int i = 0; i < max; i++) {
			Object num = result.addItem();
			result.getContainerProperty(num, "date").setValue(date[i]);
			result.getContainerProperty(num, "user").setValue(user[i]);
			result.getContainerProperty(num, "name").setValue(name[i]);
			result.getContainerProperty(num, "status").setValue(status[i]);

		}*/
		
		for (PipelineExecution item : data)
		{
			//static String[] visibleCols = new String[] { "date", "name", "user",
			// "status", "debug", "obsolete", "actions", "report" }; 
			
			Object num = result.addItem();
			result.getContainerProperty(num, "date").setValue(" ");
			result.getContainerProperty(num, "user").setValue(" ");
			result.getContainerProperty(num, "name").setValue(item.getPipeline().getName());
			//String statusStr = item.getExecutionStatus().toString();
			result.getContainerProperty(num, "status").setValue(item.getExecutionStatus().toString());
			result.getContainerProperty(num, "debug").setValue((item.isDebugging())?"true":"false");
						
			
				
		}
 
		return result;
	}

	private final static int UNDEFINED_LENGTH = -1;

	public static int getMinLength(String[]... arraysLength) {
		int min = UNDEFINED_LENGTH;
		for (int i = 0; i < arraysLength.length; i++) {
			if (min == UNDEFINED_LENGTH) {
				min = arraysLength[i].length;
			} else {
				min = Math.min(min, arraysLength[i].length);
			}
		}
		return min;

	}

	private List<Record> buildStubMessageData() {
		List<Record> stubList = new ArrayList<>();
/*		Record m = new Record(new Date(), RecordType.DPUINFO, null,
				"Test message", "Long test message");
		m.setId(1);
		stubList.add(m);
		Record m2 = new Record(new Date(), RecordType.DPUWARNING, null,
				"Test warning", "Long test warning message");
		m2.setId(2);
		stubList.add(m2);*/

		return stubList;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Button senderButton = event.getButton();
		if (senderButton != null) {
			String caption = (String)senderButton.getData();
			if (caption.equals("stop")) {
			} else if (caption.equals("showlog")) {
				hsplit.setSplitPosition(55, Sizeable.UNITS_PERCENTAGE);
				hsplit.setSecondComponent(logLayout);
				hsplit.setLocked(false);

				
			} else if (caption.equals("debug")) {
			}

		}
	}

}

class MonitorTableFilter implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private String needle;
	private String userFilter;
	private String nameFilter;

	public MonitorTableFilter() {
		// this.needle = needle.toLowerCase();
	}

	public void setNameFilter(String value) {
		this.nameFilter = value.toLowerCase();

	}

	public void setUserFilter(String value) {
		this.userFilter = value.toLowerCase();

	}

	private boolean stringIsSet(String value) {
		if (value != null && value.length() > 0)
			return true;
		return false;
	}

	public boolean passesFilter(Object itemId, Item item) {

		if (stringIsSet(this.userFilter)) {
			String objectUser = ((String) item.getItemProperty("user")
					.getValue()).toLowerCase();
			if (objectUser.contains(this.userFilter) == false)
				return false;

		}
		if (stringIsSet(this.nameFilter)) {
			String objectUser = ((String) item.getItemProperty("name")
					.getValue()).toLowerCase();
			if (objectUser.contains(this.nameFilter) == false)
				return false;
		}

		return true;
	}

	public boolean appliesToProperty(Object id) {
		return true;
	}
}
