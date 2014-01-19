package cz.cuni.mff.xrg.odcs.extractor.rdf;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.*;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.SPARQLValidationException;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLQueryValidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter;

/**
 * Configuration dialog for DPU SPARQL Extractor.
 *
 * @author Maria Kukhar
 * @author Jiri Tomes
 *
 */
public class RDFExtractorDialog extends BaseConfigDialog<RDFExtractorConfig> {

	private static final long serialVersionUID = 1L;

	private GridLayout mainLayout;

	/**
	 * TabSheet of Configuration dialog. Contains two tabs: Core and Details
	 */
	private TabSheet tabSheet;

	private VerticalLayout verticalLayoutDetails;

	private VerticalLayout verticalLayoutProtocol;

	private VerticalLayout params;

	/**
	 * CheckBox to specify whether the extraction fails if there is no triple.
	 */
	private CheckBox extractFail;

	private Label labelOpt;

	private TextField last;

	/**
	 * TextArea to set SPARQL construct query.
	 */
	private TextArea textAreaConstr;

	private Label labelConstr;

	private GridLayout gridLayoutCore;

	/**
	 * PasswordField to set password to connect to SPARQL endpoints which
	 * require authorization.
	 */
	private PasswordField passwordFieldPass;

	private Label labelPass;

	/**
	 * TextField to set username to connect to SPARQL endpoints which require
	 * authorization.
	 */
	private TextField textFieldNameAdm;

	private Label labelNameAdm;

	/**
	 * For declaration what of request method used for extracting data from
	 * SPARQL endpoint.
	 */
	private OptionGroup requestTypeOption;

	/**
	 * For setting query parameter need for HTTP request need for extract from
	 * SPARQL endpoint.
	 */
	private TextField queryParamField;

	/**
	 * For setting default graph parameter need for HTTP request for extract
	 * from SPARQL endpoint.
	 */
	private TextField defaultGraphParamField;

	/**
	 * For setting named graph parameter need for HTTP request for extract from
	 * SPARQL endpoint.
	 */
	private TextField namedGraphParamField;

	/**
	 * ComboBox to set SPARQL endpoint.
	 */
	private TextField textFieldSparql;

	private Label labelSparql;

	private GridLayout namedGraphLayout;

	private GridLayout defaultGraphLayout;

	private InvalidValueException ex;

	/**
	 * Right SPARQL VALIDATOR - default true.
	 */
	private boolean isQueryValid = true;

	private String errorMessage = "no errors";

	private List<RequestItem> requestItems = new ArrayList<>();

	/**
	 * CheckBox to setting use statistical handler
	 */
	private CheckBox useHandler;

	private OptionGroup failsWhenErrors; // How to solve errors for Statistical handler

	private static final String STOP = "Stop pipeline execution if extractor "
			+ "extracted some triples with an error.";

	private static final String CONTINUE = "Extract only triples with no errors. "
			+ "\nIf fatal error is discovered, pipeline is stopped.";

	/**
	 * Set Count of attempts to reconnect if the connection fails. For infinite
	 * loop use zero or negative integer
	 */
	private TextField retrySizeField;

	/**
	 * For setting retry connection time before trying to reconnect.
	 */
	private TextField retryTimeField;

	int n = 1;

	//private Map<TextField, TextField> map = new HashMap<>();
	/**
	 * Basic constructor.
	 */
	public RDFExtractorDialog() {
		super(RDFExtractorConfig.class);
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}

	private void mapData() {
		if (requestItems.isEmpty()) {
			mapRequestItems();
		}
	}

	private void mapRequestItems() {

		RequestItem first = new RequestItem(ExtractorRequestType.GET_URL_ENCODER,
				"Use GET request with URL-encoded parameters");

		RequestItem second = new RequestItem(
				ExtractorRequestType.POST_URL_ENCODER,
				"Use POST request with URL-encoded parameters");

		RequestItem third = new RequestItem(
				ExtractorRequestType.POST_UNENCODED_QUERY,
				"Use POST with unencoded query in the content");

		requestItems.add(first);
		requestItems.add(second);
		requestItems.add(third);

		requestTypeOption.addItem(first.getDescription());
		requestTypeOption.addItem(second.getDescription());
		requestTypeOption.addItem(third.getDescription());

		requestTypeOption.setValue(second.getDescription());
	}

	private void inicialize() {
		ex = new InvalidValueException("Valid");
	}

	/**
	 * Get description of chosed way, how to extract RDF data from SPARQL
	 * endpoint: Uses in {@link #setConfiguration} for setiing
	 * {@link #requestTypeOption}
	 *
	 * @param type Type of insert data parts:
	 *             {@link ExtractorRequestType#POST_UNENCODED_QUERY} or
	 *             {@link ExtractorRequestType#POST_URL_ENCODER} or
	 *             {@link ExtractorRequestType#GET_URL_ENCODER}.
	 *
	 * @return description that corresponds to specific type or ""
	 */
	private String getPostDescription(ExtractorRequestType requestType) {
		if (requestItems.isEmpty()) {
			mapRequestItems();
		}

		for (RequestItem item : requestItems) {
			if (item.getRequestType().equals(requestType)) {
				return item.getDescription();
			}
		}

		return "";
	}

	/**
	 * Get type of REQUEST variant for extractomg RDF data from SPARQL endpoint:
	 * GET_URL_ENCODER,	POST_URL_ENCODER, POST_UNENCODED_QUERY. Uses in
	 * {@link #getConfiguration} for determine the type by description that
	 * located in {@link #requestTypeOption}
	 *
	 * @param desc String with description of chosed way, how to extract RDF
	 *             data from SPARQL endpoint. One value from
	 *             {@link #requestTypeOption}
	 * @return type that corresponds to desc or
	 *         {@link ExtractorRequestType#POST_URL_ENCODER} in case of absence.
	 */
	private ExtractorRequestType getRequestType(String desc) {
		if (requestItems.isEmpty()) {
			mapRequestItems();
		}

		for (RequestItem item : requestItems) {
			if (item.getDescription().equals(desc)) {
				return item.getRequestType();
			}
		}

		return ExtractorRequestType.POST_URL_ENCODER;
	}

	/**
	 * Builds main layout contains {@link #tabSheet} with all dialog components.
	 *
	 * @return mainLayout GridLayout with all components of configuration
	 *         dialog.
	 */
	private GridLayout buildMainLayout() {
		// common part: create layout

		mainLayout = new GridLayout(1, 1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		// tabSheet
		tabSheet = new TabSheet();
		tabSheet.setImmediate(true);
		tabSheet.setWidth("100%");
		tabSheet.setHeight("100%");

		// Core tab
		gridLayoutCore = buildGridLayoutCore();
		tabSheet.addTab(gridLayoutCore, "Core", null);

		//SPARQL protocol tab
		verticalLayoutProtocol = buildVerticalLayoutProtokol();
		tabSheet.addTab(verticalLayoutProtocol, "SPARQL protocol", null);

		// Details tab
		verticalLayoutDetails = buildVerticalLayoutDetails();
		tabSheet.addTab(verticalLayoutDetails, "Details", null);


		mainLayout.addComponent(tabSheet, 0, 0);
		mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);

		return mainLayout;
	}

	/**
	 * Builds layout contains Core tab components of {@link #tabSheet}. Calls
	 * from {@link #buildMainLayout}
	 *
	 * @return gridLayoutCore. GridLayout with components located at the Core
	 *         tab.
	 */
	private GridLayout buildGridLayoutCore() {

		// common part: create layout
		gridLayoutCore = new GridLayout();
		gridLayoutCore.setImmediate(false);
		gridLayoutCore.setWidth("100%");
		gridLayoutCore.setHeight("100%");
		gridLayoutCore.setMargin(true);
		gridLayoutCore.setColumns(2);
		gridLayoutCore.setRows(6);
		gridLayoutCore.setColumnExpandRatio(0, 0.10f);
		gridLayoutCore.setColumnExpandRatio(1, 0.90f);

		// labelSparql
		labelSparql = new Label();
		labelSparql.setImmediate(false);
		labelSparql.setWidth("-1px");
		labelSparql.setHeight("-1px");
		labelSparql.setValue("SPARQL endpoint:");
		gridLayoutCore.addComponent(labelSparql, 0, 0);
		gridLayoutCore.setComponentAlignment(labelSparql, Alignment.TOP_LEFT);

		// SPARQL endpoint Text Field
		textFieldSparql = new TextField();
		textFieldSparql.setImmediate(true);
		textFieldSparql.setWidth("100%");
		textFieldSparql.setHeight("-1px");
		textFieldSparql.setInputPrompt("http://localhost:8890/sparql");



		// Check if the caption for new item already exists in the list of item
		// captions before approving it as a new item.


		//textFieldSparql is mandatory fields
		textFieldSparql.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value == null) {

					ex = new InvalidValueException(
							"SPARQL endpoint must be filled!");
					throw ex;
				} else {
					String myValue = value.toString().toLowerCase().trim();
					if (!myValue.startsWith("http://")) {
						ex = new InvalidValueException(
								"Endpoint URL must start with \"http://\"");
						throw ex;
					} else if (myValue.contains(" ")) {
						ex = new InvalidValueException(
								"Endpoint name must contain no white spaces");
						throw ex;
					}

				}
			}
		});
		gridLayoutCore.addComponent(textFieldSparql, 1, 0);

		// labelNameAdm
		labelNameAdm = new Label();
		labelNameAdm.setImmediate(false);
		labelNameAdm.setWidth("-1px");
		labelNameAdm.setHeight("-1px");
		labelNameAdm.setValue("Name:");
		gridLayoutCore.addComponent(labelNameAdm, 0, 1);

		// Name textField 
		textFieldNameAdm = new TextField();
		textFieldNameAdm.setNullRepresentation("");
		textFieldNameAdm.setImmediate(false);
		textFieldNameAdm.setWidth("100%");
		textFieldNameAdm.setHeight("-1px");
		textFieldNameAdm.setInputPrompt(
				"username to connect to SPARQL endpoints");
		gridLayoutCore.addComponent(textFieldNameAdm, 1, 1);

		// labelPass
		labelPass = new Label();
		labelPass.setImmediate(false);
		labelPass.setWidth("-1px");
		labelPass.setHeight("-1px");
		labelPass.setValue("Password:");
		gridLayoutCore.addComponent(labelPass, 0, 2);

		//  Password field
		passwordFieldPass = new PasswordField();
		passwordFieldPass.setNullRepresentation("");
		passwordFieldPass.setImmediate(false);
		passwordFieldPass.setWidth("100%");
		passwordFieldPass.setHeight("-1px");
		passwordFieldPass.setInputPrompt("password");
		gridLayoutCore.addComponent(passwordFieldPass, 1, 2);

		// default graph label
		Label defaultGraphLabel = new Label();
		defaultGraphLabel.setImmediate(false);
		defaultGraphLabel.setWidth("-1px");
		defaultGraphLabel.setHeight("-1px");
		defaultGraphLabel.setValue("Default Graph:");
		gridLayoutCore.addComponent(defaultGraphLabel, 0, 3);

		//Default Graphs component
		initializeDefaultGraph();
		gridLayoutCore.addComponent(defaultGraphLayout, 1, 3);

		// named graph label
		Label namedGraphLabel = new Label();
		namedGraphLabel.setImmediate(false);
		namedGraphLabel.setWidth("-1px");
		namedGraphLabel.setHeight("-1px");
		namedGraphLabel.setValue("Named Graph:");
		gridLayoutCore.addComponent(namedGraphLabel, 0, 4);

		//Named Graphs component
		initializeNamedGraph();
		gridLayoutCore.addComponent(namedGraphLayout, 1, 4);

		// labelConstr
		labelConstr = new Label();
		labelConstr.setImmediate(false);
		labelConstr.setWidth("100%");
		labelConstr.setHeight("-1px");
		labelConstr.setValue("SPARQL  Construct:");
		gridLayoutCore.addComponent(labelConstr, 0, 5);

		// textAreaConstr
		textAreaConstr = new TextArea();
		textAreaConstr.setNullRepresentation("");
		textAreaConstr.setImmediate(true);
		textAreaConstr.setWidth("100%");
		textAreaConstr.setHeight("100px");
		textAreaConstr.setInputPrompt(
				"CONSTRUCT {<http://dbpedia.org/resource/Prague> ?p ?o} where {<http://dbpedia.org/resource/Prague> ?p ?o } LIMIT 100");

		textAreaConstr.addValueChangeListener(
				new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(Property.ValueChangeEvent event) {

				final String query = textAreaConstr.getValue().trim();
				if (query.isEmpty()) {
					isQueryValid = true;
					return;
				}

				cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator validator = new SPARQLQueryValidator(
						query, SPARQLQueryType.CONSTRUCT);

				if (!validator.isQueryValid()) {

					isQueryValid = false;
					errorMessage = validator.getErrorMessage();

				} else {
					isQueryValid = true;
				}
			}
		});

		gridLayoutCore.addComponent(textAreaConstr, 1, 5);


		return gridLayoutCore;
	}

	private TextField createParamQueryField() {

		queryParamField = new TextField("Query param:");
		queryParamField.setValue("query");
		queryParamField.setNullRepresentation("");
		queryParamField.setImmediate(true);
		queryParamField.setWidth("200px");
		queryParamField.setHeight("-1px");
		queryParamField.setInputPrompt("Query param:");


		return queryParamField;
	}

	private TextField createParamDefaultGraph() {

		defaultGraphParamField = new TextField("Default graph param:");
		defaultGraphParamField.setValue("default-graph-uri");
		defaultGraphParamField.setNullRepresentation("");
		defaultGraphParamField.setImmediate(true);
		defaultGraphParamField.setWidth("200px");
		defaultGraphParamField.setHeight("-1px");
		defaultGraphParamField.setInputPrompt("Default graph param:");

		return defaultGraphParamField;
	}

	private TextField createParamNamedGraph() {

		namedGraphParamField = new TextField("Named graph param:");
		namedGraphParamField.setValue("named-graph-uri");
		namedGraphParamField.setNullRepresentation("");
		namedGraphParamField.setImmediate(true);
		namedGraphParamField.setWidth("200px");
		namedGraphParamField.setHeight("-1px");
		namedGraphParamField.setInputPrompt("Named graph param:");

		return namedGraphParamField;

	}

	private VerticalLayout buildVerticalLayoutProtokol() {

		verticalLayoutProtocol = new VerticalLayout();
		verticalLayoutProtocol.setImmediate(true);
		verticalLayoutProtocol.setWidth("100.0%");
		verticalLayoutProtocol.setHeight("100.0%");
		verticalLayoutProtocol.setMargin(true);
		verticalLayoutProtocol.setSpacing(true);


		requestTypeOption = new OptionGroup("HTTP REQUEST Variant:");
		requestTypeOption.setImmediate(true);
		requestTypeOption.setWidth("-1px");
		requestTypeOption.setHeight("-1px");
		requestTypeOption.setMultiSelect(false);

		verticalLayoutProtocol.addComponent(requestTypeOption);

		params = new VerticalLayout();
		params.setSpacing(true);

		params.addComponent(createParamQueryField());
		params.addComponent(createParamDefaultGraph());
		params.addComponent(createParamNamedGraph());

		verticalLayoutProtocol.addComponent(params);

		return verticalLayoutProtocol;
	}

	/**
	 * List<String> that contains Defaults Graphs.
	 */
	private List<String> defaultGraphs = new LinkedList<>();

	/**
	 * List<String> that contains Named Graphs.
	 */
	private List<String> namedGraphs = new LinkedList<>();

	private List<TextField> namedGraphTexts = new LinkedList<>();

	private List<TextField> defaultGraphTexts = new LinkedList<>();

	/**
	 * Add new data to Named Graph component.
	 *
	 * @param newData. String that will be added
	 */
	private void addNamedGraph(String namedGraph) {
		namedGraphs.add(namedGraph.trim());

	}

	private void addDefaultGraph(String defaultGraph) {
		defaultGraphs.add(defaultGraph.trim());
	}

	/**
	 * Remove data from Graph component. Only if component contain more then 1
	 * row.
	 *
	 * @param row Data that will be removed.
	 */
	private void removeNamedGraph(Integer row) {
		if (namedGraphs.size() > 1) {
			namedGraphs.remove((int) row);

		}
	}

	private void removeDefaultGraph(Integer row) {
		if (defaultGraphs.size() > 1) {
			defaultGraphs.remove((int) row);

		}
	}

	/**
	 * Save edited texts in the Named Graph component
	 */
	private void saveGraphEditedTexts(boolean isNamedGraph) {
		if (isNamedGraph) {
			namedGraphs.clear();

			for (TextField editText : namedGraphTexts) {
				namedGraphs.add(editText.getValue().trim());
			}
		} else {
			defaultGraphs.clear();

			for (TextField editText : defaultGraphTexts) {
				defaultGraphs.add(editText.getValue().trim());
			}
		}
	}

	private List<String> getNamedGraphs() {
		List<String> result = new LinkedList<>();

		for (String nextGraph : namedGraphs) {
			String graphURI = nextGraph.trim();
			if (!graphURI.isEmpty()) {
				result.add(graphURI);
			}
		}
		return result;
	}

	private List<String> getDefaultGraphs() {
		List<String> result = new LinkedList<>();

		for (String nextGraph : defaultGraphs) {
			String graphURI = nextGraph.trim();
			if (!graphURI.isEmpty()) {
				result.add(graphURI);
			}
		}
		return result;
	}

	/**
	 *
	 * @return if all URI name graphs are valid or not.
	 */
	private boolean areGraphsNameValid() {
		for (TextField next : namedGraphTexts) {
			if (!next.isValid()) {
				return false;
			}
		}

		for (TextField next : defaultGraphTexts) {
			if (!next.isValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Builds Named Graph component which consists of textfields for graph name
	 * and buttons for add and remove this textfields. Used in
	 * {@link #initializeNamedGraph} and also in adding and removing fields for
	 * component refresh
	 */
	private void refreshNamedGraphData() {
		namedGraphLayout.removeAllComponents();
		namedGraphTexts.clear();

		int row = 0;

		if (namedGraphs.isEmpty()) {
			namedGraphs.add("");
		}
		namedGraphLayout.setRows(namedGraphs.size() + 1);
		for (String item : namedGraphs) {
			TextField textFieldGraph = new TextField();
			namedGraphTexts.add(textFieldGraph);
			//text field for the graph
			textFieldGraph.setWidth("100%");
			textFieldGraph.setData(row);
			textFieldGraph.setValue(item.trim());
			textFieldGraph.setInputPrompt("http://ld.opendata.cz/source1");
			textFieldGraph.addValidator(new Validator() {
				@Override
				public void validate(Object value) throws InvalidValueException {
					if (value != null) {

						String namedGraph = value.toString().toLowerCase()
								.trim();

						if (namedGraph.isEmpty()) {
							return;
						}

						if (namedGraph.contains(" ")) {
							ex = new InvalidValueException(
									"Graph name(s) must contain no white spaces");
							throw ex;
						} else if (!namedGraph.startsWith("http://")) {
							ex = new InvalidValueException(
									"Graph name must start with prefix \"http://\"");
							throw ex;
						}

					}

				}
			});

			//remove button
			Button removeButton = new Button();
			removeButton.setWidth("55px");
			removeButton.setCaption("-");
			removeButton.setData(textFieldGraph);

			removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveGraphEditedTexts(true);
					Button senderButton = event.getButton();

					TextField textField = (TextField) senderButton.getData();
					Integer row = (Integer) textField.getData();

					removeNamedGraph(row);

					refreshNamedGraphData();
				}
			});
			namedGraphLayout.addComponent(textFieldGraph, 0, row);
			namedGraphLayout.addComponent(removeButton, 1, row);
			namedGraphLayout.setComponentAlignment(removeButton,
					Alignment.TOP_RIGHT);
			row++;
		}
		//add button
		Button addButton = new Button();
		addButton.setCaption("+");
		addButton.setImmediate(true);
		addButton.setWidth("55px");
		addButton.setHeight("-1px");
		addButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveGraphEditedTexts(true);
				addNamedGraph("");

				refreshNamedGraphData();
			}
		});
		namedGraphLayout.addComponent(addButton, 0, row);

	}

	/**
	 * Initializes Named Graph component. Calls from
	 * {@link #buildGridLayoutCore}
	 */
	private void initializeNamedGraph() {

		namedGraphLayout = new GridLayout();
		namedGraphLayout.setImmediate(false);
		namedGraphLayout.setWidth("100%");
		namedGraphLayout.setHeight("100%");
		namedGraphLayout.setMargin(false);
		namedGraphLayout.setColumns(2);
		namedGraphLayout.setColumnExpandRatio(0, 0.95f);
		namedGraphLayout.setColumnExpandRatio(1, 0.05f);

		refreshNamedGraphData();

	}

	private void initializeDefaultGraph() {
		defaultGraphLayout = new GridLayout();
		defaultGraphLayout.setImmediate(false);
		defaultGraphLayout.setWidth("100%");
		defaultGraphLayout.setHeight("100%");
		defaultGraphLayout.setMargin(false);
		defaultGraphLayout.setColumns(2);
		defaultGraphLayout.setColumnExpandRatio(0, 0.95f);
		defaultGraphLayout.setColumnExpandRatio(1, 0.05f);

		refreshDefaultGraphData();
	}

	private void refreshDefaultGraphData() {
		defaultGraphLayout.removeAllComponents();
		defaultGraphTexts.clear();

		int row = 0;

		if (defaultGraphs.isEmpty()) {
			defaultGraphs.add("");
		}
		defaultGraphLayout.setRows(defaultGraphs.size() + 1);
		for (String item : defaultGraphs) {
			TextField textFieldGraph = new TextField();
			defaultGraphTexts.add(textFieldGraph);

			//text field for the graph
			textFieldGraph.setWidth("100%");
			textFieldGraph.setData(row);
			textFieldGraph.setValue(item.trim());
			textFieldGraph.setInputPrompt("http://ld.opendata.cz/source");
			textFieldGraph.addValidator(new Validator() {
				@Override
				public void validate(Object value) throws InvalidValueException {
					if (value != null) {

						String namedGraph = value.toString().toLowerCase()
								.trim();

						if (namedGraph.isEmpty()) {
							return;
						}

						if (namedGraph.contains(" ")) {
							ex = new InvalidValueException(
									"Graph name(s) must contain no white spaces");
							throw ex;
						} else if (!namedGraph.startsWith("http://")) {
							ex = new InvalidValueException(
									"Graph name must start with prefix \"http://\"");
							throw ex;
						}

					}

				}
			});

			//remove button
			Button removeButton = new Button();
			removeButton.setWidth("55px");
			removeButton.setCaption("-");
			removeButton.setData(textFieldGraph);
			removeButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveGraphEditedTexts(false);
					Button senderButton = event.getButton();
					TextField textField = (TextField) senderButton.getData();
					Integer row = (Integer) textField.getData();
					removeDefaultGraph(row);

					refreshDefaultGraphData();
				}
			});
			defaultGraphLayout.addComponent(textFieldGraph, 0, row);
			defaultGraphLayout.addComponent(removeButton, 1, row);
			defaultGraphLayout.setComponentAlignment(removeButton,
					Alignment.TOP_RIGHT);
			row++;
		}
		//add button
		Button addButton = new Button();
		addButton.setCaption("+");
		addButton.setImmediate(true);
		addButton.setWidth("55px");
		addButton.setHeight("-1px");
		addButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveGraphEditedTexts(false);
				addDefaultGraph("");

				refreshDefaultGraphData();
			}
		});
		defaultGraphLayout.addComponent(addButton, 0, row);

	}

	/**
	 * Builds layout contains Details tab components of {@link #tabSheet}. Calls
	 * from {@link #buildMainLayout}
	 *
	 * @return verticalLayoutDetails. VerticalLayout with components located at
	 *         the Details tab.
	 */
	private VerticalLayout buildVerticalLayoutDetails() {
		// common part: create layout
		verticalLayoutDetails = new VerticalLayout();
		verticalLayoutDetails.setImmediate(false);
		verticalLayoutDetails.setWidth("100%");
		verticalLayoutDetails.setHeight("-1px");
		verticalLayoutDetails.setMargin(true);
		verticalLayoutDetails.setSpacing(true);


		// labelOpt
		labelOpt = new Label();
		labelOpt.setImmediate(false);
		labelOpt.setWidth("-1px");
		labelOpt.setHeight("-1px");
		labelOpt.setValue("Options:");
		verticalLayoutDetails.addComponent(labelOpt);

		// CheckBox Extraction fails if there is no triple extracted.
		extractFail = new CheckBox();
		extractFail
				.setCaption("Extraction fails if there is no triple extracted.");
		extractFail.setImmediate(false);
		extractFail.setWidth("-1px");
		extractFail.setHeight("-1px");
		verticalLayoutDetails.addComponent(extractFail);

		//Statistical handler
		useHandler = new CheckBox("Use statistical and error handler");
		useHandler.setValue(true);
		useHandler.setWidth("-1px");
		useHandler.setHeight("-1px");
		useHandler.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (failsWhenErrors != null) {
					failsWhenErrors.setEnabled(useHandler.getValue());
				}
			}
		});

		//How to solve errors for Statistical handler
		failsWhenErrors = new OptionGroup();
		failsWhenErrors.setImmediate(false);
		failsWhenErrors.setWidth("-1px");
		failsWhenErrors.setHeight("-1px");
		failsWhenErrors.setMultiSelect(false);

		//extract only triples with no errors.
		failsWhenErrors.addItem(CONTINUE);
		//stop pipeline execution if extractor extracted some triples with an error.
		failsWhenErrors.addItem(STOP);

		failsWhenErrors.setValue(CONTINUE);
		failsWhenErrors.setEnabled(useHandler.getValue());

		verticalLayoutDetails.addComponent(useHandler);
		verticalLayoutDetails.addComponent(failsWhenErrors);

		VerticalLayout attempts = new VerticalLayout();
		attempts.setSpacing(true);
		attempts.setStyleName("graypanel");

		retrySizeField = new TextField(
				"Count of attempts to reconnect if the connection to SPARQL fails");
		retrySizeField.setDescription(
				"(Use 0 for no reperat, negative integer for infinity)");
		retrySizeField.setValue("-1");
		retrySizeField.setNullRepresentation("");
		retrySizeField.setImmediate(true);
		retrySizeField.setWidth("100px");
		retrySizeField.setHeight("-1px");
		retrySizeField.setInputPrompt(
				"Count of attempts to reconnect if the connection to SPARQL fails\n "
				+ "(Use 0 or negative integer for infinity)");

		retrySizeField.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws Validator.InvalidValueException {

				if (value != null) {
					String size = value.toString().trim();

					try {
						Integer.parseInt(size);

					} catch (NumberFormatException e) {
						ex = new Validator.InvalidValueException(
								"Count of attempts must be a number");
						throw ex;
					}

				} else {
					throw new Validator.EmptyValueException(
							"Count of attempts is a null");
				}
			}
		});

		attempts.addComponent(retrySizeField);


		retryTimeField = new TextField(
				"Time in miliseconds how long to wait before trying to reconnect");
		retryTimeField.setValue("1000");
		retryTimeField.setNullRepresentation("");
		retryTimeField.setImmediate(true);
		retryTimeField.setWidth("100px");
		retryTimeField.setHeight("-1px");
		retryTimeField.setInputPrompt(
				"Time in miliseconds how long to wait before trying to reconnect");

		retryTimeField.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws Validator.InvalidValueException {
				if (value != null) {
					String sTime = value.toString().trim();

					try {
						long time = Long.parseLong(sTime);

						if (time < 0) {
							ex = new Validator.InvalidValueException(
									"Time for reconnect must be >= 0");
							throw ex;
						}

					} catch (NumberFormatException e) {
						ex = new Validator.InvalidValueException(
								"Time for reconnect must be a number");
						throw ex;
					}

				} else {
					throw new Validator.EmptyValueException(
							"Time for reconnect is a null");
				}
			}
		});

		attempts.addComponent(retryTimeField);
		verticalLayoutDetails.addComponent(attempts);

		return verticalLayoutDetails;
	}

	private boolean allComponentAreValid() {

		boolean areValid = textFieldSparql.isValid()
				&& retrySizeField.isValid()
				&& retryTimeField.isValid()
				&& areGraphsNameValid();

		return areValid;
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU
	 *
	 * @throws ConfigException Exception which might be thrown when field
	 *                         {@link #textFieldSparql} contains null value.
	 * @return config Object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public RDFExtractorConfig getConfiguration() throws ConfigException {
		if (!allComponentAreValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else if (!isQueryValid) {
			throw new SPARQLValidationException(errorMessage);
		} else {

			String SPARQLEndpoint = (String) textFieldSparql.getValue();
			String hostName = textFieldNameAdm.getValue().trim();
			String password = passwordFieldPass.getValue();
			String SPARQLQuery = textAreaConstr.getValue().trim();

			boolean extractFailed = extractFail.getValue();
			boolean useStatisticalHandler = useHandler.getValue();

			String selectedValue = (String) failsWhenErrors.getValue();
			boolean failWhenErrors;
			if (selectedValue.equals(STOP)) {
				failWhenErrors = true;
			} else if (selectedValue.endsWith(CONTINUE)) {
				failWhenErrors = false;
			} else {
				throw new ConfigException(
						"No value for case using statistical and error handler");
			}

			int retrySize = Integer.parseInt(retrySizeField.getValue());
			long retryTime = Long.parseLong(retryTimeField.getValue());

			String queryParam = queryParamField.getValue().trim();

			String defaultGraphParam = defaultGraphParamField.getValue().trim();
			String namedGraphParam = namedGraphParamField.getValue().trim();

			String requestDescription = (String) requestTypeOption.getValue();
			ExtractorRequestType requestType = getRequestType(requestDescription);

			ExtractorEndpointParams endpointParams = new ExtractorEndpointParams(
					queryParam, defaultGraphParam, namedGraphParam,
					getNamedGraphs(), getDefaultGraphs(), requestType);

			RDFExtractorConfig config = new RDFExtractorConfig(SPARQLEndpoint,
					hostName, password, SPARQLQuery,
					extractFailed, useStatisticalHandler, failWhenErrors,
					retrySize, retryTime, endpointParams);

			return config;
		}
	}

	/**
	 * Load values from configuration object implementing
	 * {@link DPUConfigObject} interface and configuring DPU into the dialog
	 * where the configuration object may be edited.
	 *
	 * @throws ConfigException Exception which might be thrown when components {@link #textFieldSparql}, {@link #textFieldNameAdm}, {@link #passwordFieldPass},
	 * {@link #textAreaConstr}, {@link #extractFail}, {@link #useHandler}, {@link #namedGraphs},
	 *                         in read-only mode or when values loading to this
	 *                         fields could not be converted. Also when
	 *                         requested operation is not supported.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(RDFExtractorConfig conf) throws ConfigException {
		try {
			String endp = conf.getSPARQLEndpoint().trim();

			if (endp != null) {
				textFieldSparql.setValue(endp);
			}

			textFieldNameAdm.setValue(conf.getHostName().trim());
			passwordFieldPass.setValue(conf.getPassword());
			textAreaConstr.setValue(conf.getSPARQLQuery().trim());
			extractFail.setValue(conf.isExtractFail());
			useHandler.setValue(conf.isUsedStatisticalHandler());

			String retrySize = String.valueOf(conf.getRetrySize());
			retrySizeField.setValue(retrySize);

			String retryTime = String.valueOf(conf.getRetryTime());
			retryTimeField.setValue(retryTime);

			ExtractorEndpointParams endpointParams = conf.getEndpointParams();

			if (endpointParams != null) {
				ExtractorRequestType requestType = endpointParams
						.getRequestType();
				String requestDescription = getPostDescription(requestType);
				requestTypeOption.setValue(requestDescription);

				queryParamField.setValue(endpointParams.getQueryParam());
				defaultGraphParamField.setValue(endpointParams
						.getDefaultGraphParam());
				namedGraphParamField.setValue(endpointParams
						.getNamedGraphParam());

				namedGraphs = endpointParams.getNamedGraphURI();
				defaultGraphs = endpointParams.getDefaultGraphURI();

				refreshNamedGraphData();
				refreshDefaultGraphData();
			}

			if (conf.isFailWhenErrors()) {
				failsWhenErrors.setValue(STOP);
			} else {
				failsWhenErrors.setValue(CONTINUE);
			}


		} catch (UnsupportedOperationException | Property.ReadOnlyException | Converter.ConversionException e) {
			// throw setting exception
			throw new ConfigException(e.getMessage(), e);
		}
	}

	@Override
	public String getDescription() {
		StringBuilder description = new StringBuilder();
		description.append("Extract from SPARQL: ");
		description.append((String) textFieldSparql.getValue());
		return description.toString();
	}
}

class RequestItem {

	private ExtractorRequestType requestType;

	private String description;

	public RequestItem(ExtractorRequestType requestType, String description) {
		this.requestType = requestType;
		this.description = description;
	}

	public ExtractorRequestType getRequestType() {
		return requestType;
	}

	public String getDescription() {
		return description;
	}
}
