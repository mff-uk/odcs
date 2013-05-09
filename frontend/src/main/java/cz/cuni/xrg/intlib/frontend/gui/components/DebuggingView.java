package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.commons.ExecutionMessage;
import cz.cuni.xrg.intlib.commons.MessageType;
import cz.cuni.xrg.intlib.commons.RDFTriple;
import java.util.AbstractList;
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
		executionRecordsTable.setHeight("150px");

		mainLayout.addComponent(executionRecordsTable);

		TabSheet tabs = new TabSheet();
		tabs.setHeight("300px");

		//Table with data
		BrowserTable browserTable = new BrowserTable(buildStubRDFData());
		tabs.addTab(browserTable, "Browse");


		//RecordsTable with different data source
		RecordsTable fullRecordsTable = new RecordsTable(buildStubFullData());
		tabs.addTab(fullRecordsTable, "Log");

		//Query View
		QueryView queryView = new QueryView();
		tabs.addTab(queryView, "Query");

		mainLayout.setWidth("600px");
		mainLayout.addComponent(tabs);


		//return mainLayout;
	}



	private List<ExecutionMessage> buildStubMessageData() {
		List<ExecutionMessage> stubList = new ArrayList<ExecutionMessage>();
		ExecutionMessage m = new ExecutionMessage(1, new Date(), MessageType.OK, null, "Test message", "Long test message");
		stubList.add(m);
		ExecutionMessage m2 = new ExecutionMessage(2, new Date(), MessageType.Warning, null, "Test warning", "Long test warning message");
		stubList.add(m2);

		return stubList;
	}

	private List<ExecutionMessage> buildStubFullData() {
		List<ExecutionMessage> fullList = buildStubMessageData();

		ExecutionMessage m = new ExecutionMessage(3, new Date(), MessageType.Log, null, "Test log message", "Long test log message");
		fullList.add(1, m);
		ExecutionMessage m2 = new ExecutionMessage(4, new Date(), MessageType.Log, null, "Another test log message", "Bla bla Long test warning message");
		fullList.add(m2);

		return fullList;
	}

	private List<RDFTriple> buildStubRDFData() {
		List<RDFTriple> rdfTripleList = new ArrayList<RDFTriple>();

		rdfTripleList.add(new RDFTriple(1, "rdf:Description", "rdf:about", "http://www.recshop.fake/cd/Empire Burlesque"));
		rdfTripleList.add(new RDFTriple(2, "rdf:Description", "cd:artist", "Bob Dylan"));
		rdfTripleList.add(new RDFTriple(3, "rdf:Description", "cd:country", "USA"));
		rdfTripleList.add(new RDFTriple(4, "rdf:Description", "cd:company", "Columbia"));
		rdfTripleList.add(new RDFTriple(5, "rdf:Description", "cd:price", "10.90"));
		rdfTripleList.add(new RDFTriple(6, "rdf:Description", "cd:year", "1985"));

		return rdfTripleList;
	}

}
