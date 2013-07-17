package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.ControlsLayout;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.DownloadStreamResource;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple query view for querying debug data. User can query data for given DPU.
 * If DPU is Transformer, user can select if input or output graph should be
 * queried. If SELECT query is used, data are shown in table. If CONSTRUCT query
 * is used, data are provided as file for download. User can select format of
 * data.
 *
 * @author Bogo
 */
public class QueryView extends CustomComponent {

	private DebuggingView parent;
	private TextArea queryText;
	private IntlibPagedTable resultTable;
	private HorizontalLayout resultTableControls;
	private NativeSelect graphSelect;
	private NativeSelect formatSelect;
	private Link export;
	private final static String IN_GRAPH = "Input Graph";
	private final static String OUT_GRAPH = "Output Graph";
	private final static Logger LOG = LoggerFactory.getLogger(QueryView.class);

	/**
	 * Constructor with parent view.
	 *
	 * @param parent {@link DebuggingView} which is parent to this
	 * {@link QueryView}.
	 */
	public QueryView(DebuggingView parent) {
		this.parent = parent;
		VerticalLayout mainLayout = new VerticalLayout();

		HorizontalLayout topLine = new HorizontalLayout();

		graphSelect = new NativeSelect("Graph:");
		graphSelect.setImmediate(true);
		graphSelect.setNullSelectionAllowed(false);
		topLine.addComponent(graphSelect);


		Button queryButton = new Button("Query");
		queryButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					doQuery();
				} catch (InvalidQueryException e) {
					Notification.show("Query Validator",
							"Query is not valid: "
							+ e.getCause().getMessage(),
							Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		topLine.addComponent(queryButton);
		topLine.setComponentAlignment(queryButton, Alignment.BOTTOM_RIGHT);
		topLine.setSpacing(true);

		//Export options
		formatSelect = new NativeSelect("Format:");
		for (RDFFormatType type : RDFFormatType.values()) {
			if (type != RDFFormatType.AUTO) {
				formatSelect.addItem(type);
			}
		}
		formatSelect.setImmediate(true);
		formatSelect.setNullSelectionAllowed(false);
		topLine.addComponent(formatSelect);
		//topLine.addComponent(export);
		mainLayout.addComponent(topLine);

		queryText = new TextArea("SPARQL Query:");
		queryText.setWidth("100%");
		queryText.setHeight("30%");
		mainLayout.addComponent(queryText);

		resultTable = new IntlibPagedTable();
		resultTable.setWidth("100%");
		resultTable.setHeight("60%");
		resultTable.setImmediate(true);
		mainLayout.addComponent(resultTable);
		resultTableControls = resultTable.createControls();
		resultTableControls.setImmediate(true);
		mainLayout.addComponent(resultTableControls);
		export = new Link("Download data", null);
		export.setVisible(false);
		export.setImmediate(true);
		mainLayout.addComponent(export);

		mainLayout.setSizeFull();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Prepare data file for download after CONSTRUCT query.
	 *
	 * @param repository {@link LocalRDFRepo} of selected graph.
	 * @param constructQuery {@link String} containing query to execute on
	 * repository.
	 * @throws InvalidQueryException If the query is badly formatted.
	 */
	private boolean prepareDownloadData(LocalRDFRepo repository, String constructQuery) throws InvalidQueryException {

		Object o = formatSelect.getValue();
		if (o == null) {
			Notification.show("Format not selected!", "Format must be selected for CONSTRUCT query!", Notification.Type.ERROR_MESSAGE);
			return false;
		}
		RDFFormatType format = (RDFFormatType) o;

		String mimeType = null;
		String filename = null;
		switch (format) {
			case TTL:
				mimeType = DownloadStreamResource.MIME_TYPE_TTL;
				filename = "data.ttl";
				break;
			case AUTO:
			case RDFXML:
				mimeType = DownloadStreamResource.MIME_TYPE_RDFXML;
				filename = "data.rdf";
				break;
			case N3:
				mimeType = "text/n3";
				filename = "data.n3";
				break;
			case TRIG:
				mimeType = "application/trig";
				filename = "data.trig";
				break;
		}

		//final DownloadStreamResource streamResource =
		//		new DownloadStreamResource(data, filename,
		//			mimeType);

		//streamResource.setCacheTime(5 * 1000);

		File constructData = repository.makeConstructQueryOverRepository(constructQuery, format, filename);
		FileResource resource = new FileResource(constructData);
		resource.setCacheTime(5000);

		export.setResource(resource);
		return true;
	}

	/**
	 * Execute query on selected graph.
	 *
	 * @throws InvalidQueryException If the query is badly formatted.
	 */
	private void doQuery() throws InvalidQueryException {

		boolean onInputGraph = graphSelect.getValue().equals("Input Graph");
		String query = queryText.getValue();
		File repoDir = parent.getRepositoryDirectory(onInputGraph);

		if(query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException("Invalid query."));
		}
		String queryStart = query.trim().substring(0, 9).toLowerCase();
		boolean isSelectQuery = queryStart.startsWith("select");
		boolean isQuerySuccessful = true;
		
		Map<String, List<String>> data = null;
		if (repoDir == null) {
			data = new HashMap<>();
		} else {
			// FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
			LOG.debug("Create LocalRDFRepo in directory={} ", repoDir.toString());
			try {
				LocalRDFRepo repository = LocalRDFRepo.createLocalRepo("");
				// load data from stora
				repository.load(repoDir);
				
				if (isSelectQuery) {
					data = repository.makeSelectQueryOverRepository(query);
				} else {
					isQuerySuccessful = prepareDownloadData(repository, query);
				}
				// close reporistory
				repository.shutDown();
			} catch(RuntimeException e) {
				throw new RuntimeException(e);
			}
		}
		if (isSelectQuery) {
			IndexedContainer container = buildDataSource(data);
			resultTable.setContainerDataSource(container);
		}
		boolean visibility = isSelectQuery || !isQuerySuccessful;
		export.setVisible(!visibility);
		resultTable.setVisible(visibility);
		resultTableControls.setVisible(visibility);

	}

	/**
	 * Initializes table with data from SELECT query.
	 *
	 * @param data Data with result of SELECT query.
	 * @return {@link IndexedContainer} to serve as data source for
	 * {@link IntlibPagedTable}.
	 */
	private IndexedContainer buildDataSource(Map<String, List<String>> data) {
		IndexedContainer result = new IndexedContainer();
		if (data.isEmpty()) {
			return result;
		}

		Set<String> columns = data.keySet();

		result.addContainerProperty("#", Integer.class, "");
		for (String column : columns) {
			//		if (p.equals("exeid")==false)
			result.addContainerProperty(column, String.class, "");
		}
		int count = data.get(columns.iterator().next()).size();
		for (int i = 0;
				i < count;
				i++) {
			Object num = result.addItem();
			result.getContainerProperty(num, "#").setValue(i);
			for (String column : columns) {
				String value = data.get(column).get(i);
				result.getContainerProperty(num, column).setValue(value);
			}
		}
		return result;
	}

	/**
	 * Populates select box for RDF graph to query.
	 *
	 * @param type DPU type to query
	 */
	public void setGraphs(DPUType type) {
		graphSelect.removeAllItems();
		if (DPUType.Extractor.equals(type)) {
			graphSelect.addItem(IN_GRAPH);
			graphSelect.select(IN_GRAPH);
		} else if (DPUType.Transformer.equals(type)) {
			graphSelect.addItem(IN_GRAPH);
			graphSelect.addItem(OUT_GRAPH);
			graphSelect.select(OUT_GRAPH);
		} else if (DPUType.Loader.equals(type)) {
			graphSelect.addItem(OUT_GRAPH);
			graphSelect.select(OUT_GRAPH);
		}
	}
}
