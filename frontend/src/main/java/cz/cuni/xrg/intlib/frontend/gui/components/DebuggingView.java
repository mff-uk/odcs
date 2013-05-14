package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.app.data.rdf.RDFTriple;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Bogo
 */
public class DebuggingView extends Window {

	private VerticalLayout mainLayout;

	public DebuggingView() {
		 setCaption("Debug window");
		 buildMainLayout();
		 this.setContent(mainLayout);
	}

	public final void buildMainLayout() {
		mainLayout = new VerticalLayout();

		RecordsTable executionRecordsTable = new RecordsTable(buildStubMessageData());
		executionRecordsTable.setWidth("100%");
		executionRecordsTable.setHeight("100px");

		mainLayout.addComponent(executionRecordsTable);

		TabSheet tabs = new TabSheet();
		tabs.setHeight("500px");

		//Table with data
		BrowserTable browserTable = new BrowserTable(buildStubRDFData());
		tabs.addTab(browserTable, "Browse");


		//RecordsTable with different data source
		RecordsTable fullRecordsTable = new RecordsTable(buildStubFullData());
		fullRecordsTable.setWidth("100%");
		fullRecordsTable.setHeight("100%");
		tabs.addTab(fullRecordsTable, "Log");

		//Query View
		QueryView queryView = new QueryView();
		tabs.addTab(queryView, "Query");

		mainLayout.setSizeUndefined();
		mainLayout.setWidth("600px");
		mainLayout.addComponent(tabs);


		//return mainLayout;
	}



	private List<Record> buildStubMessageData() {
		List<Record> stubList = new ArrayList<>();
		Record m = new Record(new Date(), RecordType.DPUINFO, null, "Test message", "Long test message");
		m.setId(1);
		stubList.add(m);
		Record m2 = new Record(new Date(), RecordType.DPUWARNING, null, "Test warning", "Long test warning message");
		m2.setId(2);
		stubList.add(m2);

		return stubList;
	}

	private List<Record> buildStubFullData() {
		List<Record> fullList = buildStubMessageData();

		Record m = new Record(new Date(), RecordType.DPULOG, null, "Test log message", "Long test log message");
		m.setId(3);
		fullList.add(1, m);
		Record m2 = new Record(new Date(), RecordType.DPULOG, null, "Another test log message", "Bla bla Long test warning message");
		m2.setId(4);
		fullList.add(m2);

		return fullList;
	}

	private List<RDFTriple> buildStubRDFData() {
		List<RDFTriple> rdfTripleList = new ArrayList<>();

		rdfTripleList.add(new RDFTriple(1, "rdf:Description", "rdf:about", "http://www.recshop.fake/cd/Empire Burlesque"));
		rdfTripleList.add(new RDFTriple(2, "rdf:Description", "cd:artist", "Bob Dylan"));
		rdfTripleList.add(new RDFTriple(3, "rdf:Description", "cd:country", "USA"));
		rdfTripleList.add(new RDFTriple(4, "rdf:Description", "cd:company", "Columbia"));
		rdfTripleList.add(new RDFTriple(5, "rdf:Description", "cd:price", "10.90"));
		rdfTripleList.add(new RDFTriple(6, "rdf:Description", "cd:year", "1985"));

		return rdfTripleList;
	}

}
