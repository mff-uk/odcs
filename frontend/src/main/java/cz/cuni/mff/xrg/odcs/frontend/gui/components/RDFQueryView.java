package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.data.Container;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandFileDownloader;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandStreamResource;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RDFDataUnitHelper;
import cz.cuni.mff.xrg.odcs.frontend.container.RDFLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.RDFQueryDefinition;
import cz.cuni.mff.xrg.odcs.frontend.container.RDFQueryFactory;
import cz.cuni.mff.xrg.odcs.frontend.container.RDFRegexFilter;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.AUTO;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.N3;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryPart;

import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.RDFXML;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.TRIG;
import static cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType.TTL;
import static cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType.CSV;
import static cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType.JSON;
import static cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType.TSV;
import static cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType.XML;

import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLQueryValidator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterGenerator;

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

	private Button tableDownload;

	private final static Logger LOG = LoggerFactory
			.getLogger(RDFQueryView.class);

	private boolean isEnabled = true;

	Button queryDownloadButton;

	Button queryButton;

	private HorizontalLayout resultDownloadControls;

	private String tableQuery = null;

	private DPUInstanceRecord tableDpu = null;

	private DataUnitInfo tableDataUnit = null;

	/**
	 * Constructor with parent view.
	 *
	 * @param parent {@link DebuggingView} which is parent to this
	 *               {@link RDFQueryView}.
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
		queryText.setHeight("210px");
		queryText.setImmediate(true);
		queryLine.addComponent(queryText);

		VerticalLayout queryControls = new VerticalLayout();

		//Export options
		formatSelect = new NativeSelect();
		for (RDFFormatType type : RDFFormatType.values()) {
			if (type != RDFFormatType.AUTO) {
				formatSelect.addItem(RDFFormatType.getStringValue(type));
			}
		}
		for (SelectFormatType t : SelectFormatType.values()) {
			formatSelect.addItem(t);
		}
		formatSelect.setImmediate(true);
		formatSelect.setNullSelectionAllowed(false);
		formatSelect.select(RDFFormatType.getStringValue(RDFFormatType.RDFXML));
		queryControls.addComponent(formatSelect);

		queryDownloadButton = new Button("Run Query and Download");
		OnDemandFileDownloader fileDownloader = new OnDemandFileDownloader(
				new OnDemandStreamResource() {
			@Override
			public String getFilename() {
				return getFileName(formatSelect.getValue());
			}

			@Override
			public InputStream getStream() {
				RDFDataUnit repository = getRepository(selector.getSelectedDPU(),
						selector.getSelectedDataUnit());
				if (repository == null) {
					return null;
				}
				String query = queryText.getValue();
				return getDownloadData(repository, query, formatSelect
						.getValue());
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
		resultTable.setPageLength(15);
		resultTable.setSortEnabled(false);
		//resultTable.setFilterBarVisible(true);
		resultTable.setFilterGenerator(new FilterGenerator() {
			@Override
			public Container.Filter generateFilter(Object propertyId,
					Object value) {
				return new RDFRegexFilter((String) propertyId, (String) value);
			}

			@Override
			public Container.Filter generateFilter(Object propertyId,
					Field<?> originatingField) {
				return null;
			}

			@Override
			public AbstractField<?> getCustomFilterComponent(Object propertyId) {
				return null;
			}

			@Override
			public void filterRemoved(Object propertyId) {
			}

			@Override
			public void filterAdded(Object propertyId,
					Class<? extends Container.Filter> filterType, Object value) {
			}

			@Override
			public Container.Filter filterGeneratorFailed(Exception reason,
					Object propertyId, Object value) {
				return null;
			}
		});
		mainLayout.addComponent(resultTable);
		resultTableControls = resultTable.createControls();
		resultTableControls.setImmediate(true);
		mainLayout.addComponent(resultTableControls);

		resultDownloadControls = new HorizontalLayout();

		downloadFormatSelect = new NativeSelect();
		downloadFormatSelect.setImmediate(true);
		resultDownloadControls.addComponent(downloadFormatSelect);

		tableDownload = new Button("Download");
		OnDemandFileDownloader tableFileDownloader = new OnDemandFileDownloader(
				new OnDemandStreamResource() {
			@Override
			public String getFilename() {
				return getFileName(downloadFormatSelect.getValue());
			}

			@Override
			public InputStream getStream() {
				if (tableDpu == null) {
					return null;
				}
				RDFDataUnit tableRepo = getRepository(tableDpu, tableDataUnit);
				return getDownloadData(tableRepo, tableQuery,
						downloadFormatSelect.getValue());
			}
		});
		tableFileDownloader.extend(tableDownload);
		resultDownloadControls.addComponent(tableDownload);
		resultDownloadControls.setVisible(false);
		mainLayout.addComponent(resultDownloadControls);


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
	RDFDataUnit getRepository(DPUInstanceRecord dpu, DataUnitInfo dataUnit) {
		return RDFDataUnitHelper.getRepository(selector.getContext(), dpu,
				dataUnit);
	}

	void setDpu(DPUInstanceRecord dpu) {
		selector.setSelectedDPU(dpu);
	}

	void refreshDPUs(PipelineExecution exec) {
		selector.refresh(exec);
		queryText.setValue("");
		setResultVisible(false);
	}

	private boolean isSelectQuery(String query) throws InvalidQueryException {

		if (query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException(
					"Invalid query: " + query));
		}
		QueryPart queryPart = new QueryPart(query);
		SPARQLQueryType type = queryPart.getSPARQLQueryType();

		if (type == SPARQLQueryType.SELECT) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Execute query on selected graph.
	 *
	 * @param isBrowse Is it browse query?
	 * @throws InvalidQueryException If the query is badly formatted.
	 */
	private void runQuery() throws InvalidQueryException {
		String query = queryText.getValue();

		SPARQLQueryValidator validator = new SPARQLQueryValidator(query);
		if (!validator.isQueryValid()) {
			Notification.show("Query Validator",
					"Query is not valid: "
					+ validator.getErrorMessage(),
					Notification.Type.ERROR_MESSAGE);
			return;
		}

		DPUInstanceRecord selectedDpu = selector.getSelectedDPU();
		DataUnitInfo selectedDataUnit = selector.getSelectedDataUnit();
		setUpTableDownload(selectedDpu, selectedDataUnit, query);

		//New RDFLazyQueryContainer
		RDFLazyQueryContainer container = new RDFLazyQueryContainer(
				new RDFQueryDefinition(15, "id", query, selector.getContext(),
				selectedDpu, selectedDataUnit), new RDFQueryFactory());
		if (container.size() > 0) {
			if (isSelectQuery(query)) {
				for (Object propertyId : container.getItem(container
						.getIdByIndex(0)).getItemPropertyIds()) {
					container.addContainerProperty(propertyId, String.class,
							null, true, true);
				}
				resultTable.setFilterBarVisible(true);
			} else {
				container.addContainerProperty("subject", String.class, null,
						true, true);
				container.addContainerProperty("predicate", String.class, null,
						true, true);
				container.addContainerProperty("object", String.class, null,
						true, true);
				resultTable.setFilterBarVisible(false);
			}
		}
		setResultVisible(true);
		resultTable.setContainerDataSource(container);
	}

	private void browseDataUnit() {
		queryText.setValue("CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}");
		try {
			runQuery();
		} catch (InvalidQueryException ex) {
			//Should not happen
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
	 * Prepare data file for download after SELECT or CONSTRUCT query.
	 *
	 * @param repository {@link LocalRDFRepo} of selected graph.
	 * @param query      {@link String} containing query to execute on
	 *                   repository.
	 * @throws InvalidQueryException If the query is badly formatted.
	 */
	private InputStream getDownloadData(RDFDataUnit repository, String query,
			Object format) {
		try {
			boolean isSelectQuery = isSelectQuery(query);

			File constructData;
			String fn = File.createTempFile("data", "dt")
					.getAbsolutePath();

			if (isSelectQuery != (format.getClass() == SelectFormatType.class)) {

				if (isSelectQuery) {
					Notification.show("Not suported format for SELECT query !",
							"This format can be used only for CONSTRUCT queries !",
							Notification.Type.ERROR_MESSAGE);
				} else {
					Notification.show(
							"Not suported format for CONSTRUCT query !",
							"This format can be used only for SELECT queries !",
							Notification.Type.ERROR_MESSAGE);
				}

				return null;
			}

			if (isSelectQuery) {
				SelectFormatType selectType = (SelectFormatType) format;
				constructData = repository.executeSelectQuery(query, fn,
						selectType);
			} else {

				RDFFormatType rdfType = RDFFormatType.getTypeByString(format
						.toString());
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

	private String getFileName(Object oFormat) {
		if (oFormat == null) {
			Notification.show("Format not selected!",
					"Format must be selected for download!",
					Notification.Type.ERROR_MESSAGE);
			return "";
		}
		String filename = null;
		if (oFormat.getClass() == String.class) {

			RDFFormatType type = RDFFormatType.getTypeByString(oFormat
					.toString());

			switch (type) {
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
				case TRIX:
					filename = "data.trix";
					break;
				case NT:
					filename = "data.nt";
					break;
			}
		} else if (oFormat.getClass() == SelectFormatType.class) {
			switch ((SelectFormatType) oFormat) {
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
		return filename;
	}

	private void setUpTableDownload(DPUInstanceRecord selectedDpu,
			DataUnitInfo selectedDataUnit, String query) {
		resultDownloadControls.setVisible(true);
		tableDpu = selectedDpu;
		tableDataUnit = selectedDataUnit;
		tableQuery = query;
		try {
			Object first = null;
			if (isSelectQuery(query)) {
				downloadFormatSelect.removeAllItems();
				for (SelectFormatType t : SelectFormatType.values()) {
					if (first == null) {
						first = t;
					}
					downloadFormatSelect.addItem(t);
				}
			} else {
				downloadFormatSelect.removeAllItems();
				for (RDFFormatType type : RDFFormatType.values()) {
					if (type != RDFFormatType.AUTO) {
						String value = RDFFormatType.getStringValue(type);
						if (first == null) {
							first = value;
						}
						downloadFormatSelect.addItem(value);
					}
				}
			}
			downloadFormatSelect.select(first);
		} catch (InvalidQueryException ex) {
			//Should not happen, only correct queries are shown in table.
		}
	}

	private void setResultVisible(boolean visible) {
		resultTable.setVisible(visible);
		resultTableControls.setVisible(visible);
		resultDownloadControls.setVisible(visible);
	}
}
