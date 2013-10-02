package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
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

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.browser.RDFDataUnitHelper;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.impl.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.impl.RDFTriple;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.AUTO;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.N3;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.RDFXML;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.TRIG;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.TTL;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyTupleQueryResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple query view for browsing and querying debug data. User selects DPU and
 * then specifies DataUnit. User can simply browse the data, or query them. Both
 * SELECT and CONSTRUCT query can be used to show data in table and also to
 * download them. User can select format of data.
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
	private final static Logger LOG = LoggerFactory
			.getLogger(RDFQueryView.class);
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
		for (SelectFormatType t : SelectFormatType.values()) {
			formatSelect.addItem(t);
		}
		formatSelect.setImmediate(true);
		formatSelect.setNullSelectionAllowed(false);
		queryControls.addComponent(formatSelect);

		queryDownloadButton = new Button("Run Query and Download");
		OnDemandFileDownloader fileDownloader = new OnDemandFileDownloader(
				new OnDemandStreamResource() {
			private Object format;
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
					Notification.show("Format not selected!",
							"Format must be selected for download!",
							Notification.Type.ERROR_MESSAGE);
					fileName = "";
					return;
				}
				String filename = null;
				format = o;
				if (format.getClass() == RDFFormatType.class) { 
					switch ((RDFFormatType)format) {
						case TTL:
							filename = "data.ttl";
							break;
						case AUTO:
						case RDFXML:
							filename = "data.rdf";
							break;
						case N3:
							filename = "data.n3";
							break;
						case TRIG:
							filename = "data.trig";
							break;
					}
				} else if(format.getClass() == SelectFormatType.class) {
					switch ((SelectFormatType)format) {
						case CSV:
							filename = "data.csv";
							break;
						case XML:
							filename = "data.xml";
							break;
						case TSV:
							filename = "data.tsv";
							break;
						case JSON:
							filename = "data.json";
							break;
					}
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
					RDFDataUnit repository = getRepository(selector
							.getSelectedDataUnit());
					if (repository == null) {
						return null;
					}
					String query = queryText.getValue();
					boolean isSelectQuery = isSelectQuery(query);

					File constructData;
					String fn = File.createTempFile("data", "dt")
							.getAbsolutePath();

					if(isSelectQuery != (format.getClass() == SelectFormatType.class)) {
						Notification.show("Bad format selected!",
							"Proper format for query type must be selected!",
							Notification.Type.ERROR_MESSAGE);
						return null;
					}

					if (isSelectQuery) {
						SelectFormatType selectType = (SelectFormatType)format;
						constructData = repository.executeSelectQuery(query, fn,
								selectType);
					} else {
						RDFFormatType rdfType = (RDFFormatType)format;
						constructData = repository.executeConstructQuery(query,
								rdfType, fn);
					}

					FileInputStream fis = new FileInputStream(constructData);
					return fis;
				} catch (IOException ex) {
					LOG.error("File with result not found.", ex);
					Notification.show(
							"There was error in creating donwload file!",
							Notification.Type.ERROR_MESSAGE);
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

	/**
	 * Gets repository for selected DataUnit from context.
	 *
	 * @return {@link RDFDataUnit} of selected DataUnitInfo.
	 *
	 */
	RDFDataUnit getRepository(DataUnitInfo dataUnit) {
		return RDFDataUnitHelper.getRepository(selector.getContext(), selector
				.getSelectedDPU(), dataUnit);
	}

	void setDpu(DPUInstanceRecord dpu) {
		selector.setSelectedDPU(dpu);
	}

	void refreshDPUs(PipelineExecution exec) {
		selector.refresh(exec);
	}

	private boolean isSelectQuery(String query) throws InvalidQueryException {
		if (query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException(
					"Invalid query."));
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
			RDFDataUnit repository = getRepository(selector
					.getSelectedDataUnit());
			if (repository == null) {
				//TODO is this an error?	
				return;
			}

			if (isSelectQuery) {
				data = repository.executeSelectQueryAsTuples(query);
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
			container = buildDataSource((MyTupleQueryResult) data);
		} else {
			container = App.getApp().getBean(ContainerFactory.class)
					.createRDFData((List<RDFTriple>) data);
		}
		resultTable.setContainerDataSource(container);
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
	 * Initializes table with data from SELECT query.
	 *
	 * @param data Data with result of SELECT query.
	 * @return {@link IndexedContainer} to serve as data source for
	 * {@link IntlibPagedTable}.
	 */
	private IndexedContainer buildDataSource(MyTupleQueryResult data) {
		IndexedContainer result = new IndexedContainer();
		try {
			if (!data.hasNext()) {
				return result;
			}

			List<String> columns = data.getBindingNames();

			result.addContainerProperty("id", Integer.class, "");
			for (String column : columns) {
				result.addContainerProperty(column, String.class, "");
			}
			int i = 1;
			while (data.hasNext()) {
				BindingSet item = data.next();
				Object num = result.addItem();
				result.getContainerProperty(num, "id").setValue(i);
				for (String column : columns) {
					String value = item.getValue(column).stringValue();
					result.getContainerProperty(num, column).setValue(value);
				}
			}
			return result;
		} catch (QueryEvaluationException e) {
			LOG.error("Error geting data from query", e);
			return result;
		}
	}

	private void setQueryingEnabled(boolean value) {
		if (isEnabled != value) {
			queryText.setEnabled(value);
			formatSelect.setEnabled(value);
			queryButton.setEnabled(value);
			queryDownloadButton.setEnabled(value);
			isEnabled = value;
		}
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

		public OnDemandFileDownloader(
				OnDemandStreamResource onDemandStreamResource) {
			super(new StreamResource(onDemandStreamResource, ""));
			this.onDemandStreamResource = onDemandStreamResource;
		}

		@Override
		public boolean handleConnectorRequest(VaadinRequest request,
				VaadinResponse response, String path)
				throws IOException {
			getResource().setFilename(onDemandStreamResource.getFilename());
			return super.handleConnectorRequest(request, response, path);
		}

		private StreamResource getResource() {
			return (StreamResource) this.getResource("dl");
		}
	}
}
