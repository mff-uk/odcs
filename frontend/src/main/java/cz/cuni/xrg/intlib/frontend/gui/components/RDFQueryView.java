package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;

import cz.cuni.xrg.intlib.commons.app.execution.context.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.frontend.auxiliaries.DownloadStreamResource;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowserFactory;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import static cz.cuni.xrg.intlib.rdf.enums.RDFFormatType.AUTO;
import static cz.cuni.xrg.intlib.rdf.enums.RDFFormatType.N3;
import static cz.cuni.xrg.intlib.rdf.enums.RDFFormatType.RDFXML;
import static cz.cuni.xrg.intlib.rdf.enums.RDFFormatType.TRIG;
import static cz.cuni.xrg.intlib.rdf.enums.RDFFormatType.TTL;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.RDFTriple;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple query view for browsing and querying debug data. User can query data
 * for given DPU. If DPU is TRANSFORMER, user can select if input or output
 * graph should be queried. If SELECT query is used, data are shown in table. If
 * CONSTRUCT query is used, data are provided as file for download. User can
 * select format of data.
 *
 * TODO: Refresh description
 *
 * @author Bogo
 */
public class RDFQueryView extends CustomComponent {

	private DataUnitSelector selector;
	private TextArea queryText;
	private IntlibPagedTable resultTable;
	private HorizontalLayout resultTableControls;
	private NativeSelect formatSelect;
	private NativeSelect downloadFormatSelect;
	private Link export;
	private final static Logger LOG = LoggerFactory.getLogger(RDFQueryView.class);
	private boolean isEnabled = true;
	
	Button queryDownloadButton;
	Button queryButton;

	/**
	 * Constructor with parent view.
	 *
	 * @param parent {@link DebuggingView} which is parent to this
	 * {@link RDFQueryView}.
	 */
	public RDFQueryView(PipelineExecution execution) {
		VerticalLayout mainLayout = new VerticalLayout();

		selector = new DataUnitSelector(execution);
		selector.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				if (event.getClass() == DataUnitSelector.BrowseRequestedEvent.class) {
					browseDataUnit();
				} else if (event.getClass() == DataUnitSelector.EnableEvent.class) {
					setQueryingEnabled(true);
				} else if (event.getClass() == DataUnitSelector.DisableEvent.class) {
					setQueryingEnabled(false);
				}
			}
		});
		mainLayout.addComponent(selector);

		mainLayout.addComponent(new Label("SPARQL Query:"));

		HorizontalLayout queryLine = new HorizontalLayout();
		queryLine.setSpacing(true);
		queryLine.setWidth(100, Unit.PERCENTAGE);

		queryText = new TextArea();
		queryText.setWidth("100%");
		queryText.setHeight("100%");
		queryText.setImmediate(true);
		queryLine.addComponent(queryText);

		VerticalLayout queryControls = new VerticalLayout();

		//Export options
		formatSelect = new NativeSelect();
		for (RDFFormatType type : RDFFormatType.values()) {
			if (type != RDFFormatType.AUTO) {
				formatSelect.addItem(type);
			}
		}
		formatSelect.setImmediate(true);
		formatSelect.setNullSelectionAllowed(false);
		queryControls.addComponent(formatSelect);

		queryDownloadButton = new Button("Run Query and Download");
		OnDemandFileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamResource() {
			private RDFFormatType format;
			private String fileName;

			@Override
			public String getFilename() {
				getFormat();
				return fileName;
			}

			@Override
			public InputStream getStream() {
				getFormat();
				return getDownloadData();
			}

			private void getFormat() {
				Object o = formatSelect.getValue();
				if (o == null) {
					Notification.show("Format not selected!", "Format must be selected for download!", Notification.Type.ERROR_MESSAGE);
					fileName = "";
					return;
				}
				format = (RDFFormatType) o;


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
				fileName = filename;
			}

			/**
			 * Prepare data file for download after CONSTRUCT query.
			 *
			 * @param repository {@link LocalRDFRepo} of selected graph.
			 * @param constructQuery {@link String} containing query to execute
			 * on repository.
			 * @throws InvalidQueryException If the query is badly formatted.
			 */
			private InputStream getDownloadData() {
				try {
					RDFDataUnit repository = getRepository(selector.getSelectedDataUnit());
					if(repository == null) {
						return null;
					}
					String query = queryText.getValue();
					boolean isSelectQuery = isSelectQuery(query);

					File constructData;
					String fn = File.createTempFile("data", "dt").getAbsolutePath();
					if (isSelectQuery) {
						constructData = repository.makeSelectQueryOverRepository(query, fn);
					} else {
						constructData = repository.makeConstructQueryOverRepository(query, format, fn);
					}

					FileInputStream fis = new FileInputStream(constructData);
					return fis;
				} catch (IOException ex) {
					LOG.error("File with result not found.", ex);
					Notification.show("There was error in creating donwload file!", Notification.Type.ERROR_MESSAGE);
					return null;
				} catch (InvalidQueryException ex) {
					LOG.error("Invalid query!", ex);
					Notification.show("Query Validator",
							"Query is not valid: "
							+ ex.getCause().getMessage(),
							Notification.Type.ERROR_MESSAGE);
					return null;
				}
			}
		});
		fileDownloader.extend(queryDownloadButton);
		queryControls.addComponent(queryDownloadButton);

		queryButton = new Button("Run Query");
		queryButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					runQuery();
				} catch (InvalidQueryException e) {
					Notification.show("Query Validator",
							"Query is not valid: "
							+ e.getCause().getMessage(),
							Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		queryControls.addComponent(queryButton);
		queryControls.setSpacing(true);
		queryControls.setWidth(170, Unit.PIXELS);
		queryLine.setExpandRatio(queryText, 1.0f);
		queryLine.addComponent(queryControls);

		mainLayout.addComponent(queryLine);



		resultTable = new IntlibPagedTable();
		resultTable.setWidth("100%");
		resultTable.setImmediate(true);
		resultTable.setPageLength(20);
		mainLayout.addComponent(resultTable);
		resultTableControls = resultTable.createControls();
		resultTableControls.setImmediate(true);
		mainLayout.addComponent(resultTableControls);

		HorizontalLayout bottomLine = new HorizontalLayout();

		downloadFormatSelect = new NativeSelect();
		bottomLine.addComponent(downloadFormatSelect);

		export = new Link("Download", null);
		export.setIcon(new ThemeResource("icons/download.png"));
		export.setVisible(false);
		export.setImmediate(true);
		export.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				export.setEnabled(false);
			}
		});
		bottomLine.addComponent(export);


		mainLayout.setSizeFull();
		setQueryingEnabled(false);
		setCompositionRoot(mainLayout);
	}

	private boolean isSelectQuery(String query) throws InvalidQueryException {
		if (query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException("Invalid query."));
		}
		String queryStart = query.trim().substring(0, 9).toLowerCase();
		return queryStart.startsWith("select");
	}

	/**
	 * Execute query on selected graph.
	 *
	 * @param isBrowse Is it browse query?
	 * @throws InvalidQueryException If the query is badly formatted.
	 */
	private void runQuery() throws InvalidQueryException {
		String query = queryText.getValue();
		boolean isSelectQuery = isSelectQuery(query);

		Object data = null;
		try {
			RDFDataUnit repository = getRepository(selector.getSelectedDataUnit());
			if (repository == null) {
				//TODO is this an error?	
				return;
			}

			if (isSelectQuery) {
				data = repository.executeSelectQuery(query);
			} else {
				Graph queryResult = repository.executeConstructQuery(query);
				List<Statement> result = new ArrayList<>(queryResult.size());
				Iterator<Statement> it = queryResult.iterator();
				while (it.hasNext()) {
					result.add(it.next());
				}
				data = getRDFTriplesData(result);
			}
			// close reporistory
			repository.shutDown();
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		Container container;
		if (isSelectQuery) {
			container = buildDataSource((Map<String, List<String>>) data);
		} else {
			container = ContainerFactory.createRDFData((List<RDFTriple>) data);
		}
		resultTable.setContainerDataSource(container);
	}

	private void downloadQuery() throws InvalidQueryException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private List<RDFTriple> getRDFTriplesData(List<Statement> statements) {

		List<RDFTriple> triples = new ArrayList<>();

		int count = 0;

		for (Statement next : statements) {
			String subject = next.getSubject().stringValue();
			String predicate = next.getPredicate().stringValue();
			String object = next.getObject().stringValue();

			count++;

			RDFTriple triple = new RDFTriple(count, subject, predicate, object);
			triples.add(triple);
		}

		return triples;
	}

	private void browseDataUnit() {
		queryText.setValue("CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}");
		try {
			runQuery();
		} catch (InvalidQueryException ex) {
			//Should not happen
		}
	}

	/**
	 * Gets repository path from context.
	 *
	 * @return {@link ExecutionContextInfo} containing current execution
	 * information.
	 *
	 * //TODO: Change getRepository method and arguments
	 */
	RDFDataUnit getRepository(DataUnitInfo dataUnit) {
		return DataUnitBrowserFactory.getRepository(selector.getContext(), selector.getSelectedDPU(), dataUnit);
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

	void setDpu(DPUInstanceRecord dpu) {
		selector.setSelectedDPU(dpu);
	}

	/**
	 * Provide both the {@link StreamSource} and the filename in an on-demand
	 * way.
	 */
	public interface OnDemandStreamResource extends StreamSource {

		String getFilename();
	}

	/**
	 * This specializes {@link FileDownloader} in a way, such that both the file
	 * name and content can be determined on-demand, i.e. when the user has
	 * clicked the component.
	 */
	public class OnDemandFileDownloader extends FileDownloader {

		private static final long serialVersionUID = 1L;
		private final OnDemandStreamResource onDemandStreamResource;

		public OnDemandFileDownloader(OnDemandStreamResource onDemandStreamResource) {
			super(new StreamResource(onDemandStreamResource, ""));
			this.onDemandStreamResource = onDemandStreamResource;
		}

		@Override
		public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path)
				throws IOException {
			getResource().setFilename(onDemandStreamResource.getFilename());
			return super.handleConnectorRequest(request, response, path);
		}

		private StreamResource getResource() {
			return (StreamResource) this.getResource("dl");
		}
	}

	private void setQueryingEnabled(boolean value) {
		if(isEnabled != value) {
			queryText.setEnabled(value);
			formatSelect.setEnabled(value);
			queryButton.setEnabled(value);
			queryDownloadButton.setEnabled(value);
			isEnabled = value;
		}
	}
}
