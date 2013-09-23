package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.execution.context.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.DownloadStreamResource;
import cz.cuni.xrg.intlib.frontend.browser.DataUnitBrowserFactory;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple query view for browsing and querying debug data. User can query data for given DPU.
 * If DPU is TRANSFORMER, user can select if input or output graph should be
 * queried. If SELECT query is used, data are shown in table. If CONSTRUCT query
 * is used, data are provided as file for download. User can select format of
 * data.
 *
 * TODO: Refresh description
 * @author Bogo
 */
public class RDFQueryView extends CustomComponent {
	private PipelineExecution execution;
	
	private DebuggingView parent;
	private DataUnitSelector selector;
	private TextArea queryText;
	private IntlibPagedTable resultTable;
	private HorizontalLayout resultTableControls;
	private NativeSelect formatSelect;
	private NativeSelect downloadFormatSelect;
	private Link export;
	private final static String IN_GRAPH = "Input Graph";
	private final static String OUT_GRAPH = "Output Graph";
	private final static Logger LOG = LoggerFactory.getLogger(RDFQueryView.class);

	/**
	 * Constructor with parent view.
	 *
	 * @param parent {@link DebuggingView} which is parent to this
	 * {@link RDFQueryView}.
	 */
	public RDFQueryView(DebuggingView parent, PipelineExecution execution) {
		this.parent = parent;
		this.execution = execution;
		VerticalLayout mainLayout = new VerticalLayout();
		
		selector = new DataUnitSelector(execution);

		HorizontalLayout queryLine = new HorizontalLayout();
		queryLine.setWidth(100, Unit.PERCENTAGE);
		
		queryText = new TextArea("SPARQL Query:");
		queryText.setWidth("100%");
		queryText.setHeight("30%");
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

		Button queryDownloadButton = new Button("Run Query and Download");
		queryDownloadButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					//TODO: Download.
					doQuery();
				} catch (InvalidQueryException e) {
					Notification.show("Query Validator",
							"Query is not valid: "
							+ e.getCause().getMessage(),
							Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		queryControls.addComponent(queryDownloadButton);

		Button queryButton = new Button("Run Query");
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
		queryControls.addComponent(queryButton);
		queryControls.setSpacing(true);
		
		queryLine.addComponent(queryControls);

		mainLayout.addComponent(queryLine);

		

		resultTable = new IntlibPagedTable();
		resultTable.setWidth("100%");
		resultTable.setHeight("60%");
		resultTable.setImmediate(true);
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
	private boolean prepareDownloadData(RDFDataUnit repository, String constructQuery) throws InvalidQueryException {

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
		Date now = new Date();
		export.setCaption("Download data from query on " + now.toString());
		Notification.show("Query is being executed!", Notification.Type.HUMANIZED_MESSAGE);
		return true;
	}

	/**
	 * Execute query on selected graph.
	 *
	 * @throws InvalidQueryException If the query is badly formatted.
	 */
	private void doQuery() throws InvalidQueryException {

		String query = queryText.getValue();
		
		if(query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException("Invalid query."));
		}
		String queryStart = query.trim().substring(0, 9).toLowerCase();
		boolean isSelectQuery = queryStart.startsWith("select");
		boolean isQuerySuccessful = true;
		
		Map<String, List<String>> data = null;
	
		
			try {
				
				RDFDataUnit repository = getRepository(selector.getSelectedDataUnit());
				if(repository == null) {
					return;
				}
				
				if (isSelectQuery) {
					data = repository.executeSelectQuery(query);
				} else {
					isQuerySuccessful = prepareDownloadData(repository, query);
				}
				// close reporistory
				repository.shutDown();
			} catch(RuntimeException e) {
				throw new RuntimeException(e);
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
}
