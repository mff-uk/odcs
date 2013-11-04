package cz.cuni.mff.xrg.odcs.extractor.rdf;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.*;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.SPARQLValidationException;
import cz.cuni.mff.xrg.odcs.rdf.impl.SPARQLQueryValidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.*;
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

	/**
	 * CheckBox to specify whether the extraction fails if there is no triple.
	 */
	private CheckBox extractFail;

	private Label labelOpt;


	/**
	 * TextArea to set SPARQL construct query.
	 */
	private TextArea textAreaConstr;

	private Label labelConstr;

	private GridLayout gridLayoutCore;

	private Label labelGraph;

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
	 * ComboBox to set SPARQL endpoint.
	 */
	private TextField textFieldSparql;

	private Label labelSparql;

	private GridLayout gridLayoutGraph;

	private TextField textFieldGraph;

	private Button buttonGraphRem;

	private Button buttonGraphAdd;

	private InvalidValueException ex;

	/**
	 * Right SPARQL VALIDATOR - default true.
	 */
	private boolean isQueryValid = true;

	private String errorMessage = "no errors";

	/**
	 * CheckBox to setting use statistical handler
	 */
	private CheckBox useHandler;

	int n = 1;

	/**
	 * Basic constructor.
	 */
	public RDFExtractorDialog() {
		super(RDFExtractorConfig.class);
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void inicialize() {
		ex = new InvalidValueException("Valid");
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
		gridLayoutCore.setRows(5);
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
		textFieldSparql.setInputPrompt("http://example:8894/sparql");



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

		// labelGraph
		labelGraph = new Label();
		labelGraph.setImmediate(false);
		labelGraph.setWidth("-1px");
		labelGraph.setHeight("-1px");
		labelGraph.setValue("Named Graph:");
		gridLayoutCore.addComponent(labelGraph, 0, 3);

		//Named Graph component
		initializeNamedGraphList();
		gridLayoutCore.addComponent(gridLayoutGraph, 1, 3);
		
		
		// labelConstr
		labelConstr = new Label();
		labelConstr.setImmediate(false);
		labelConstr.setWidth("100%");
		labelConstr.setHeight("-1px");
		labelConstr.setValue("SPARQL  Construct:");
		gridLayoutCore.addComponent(labelConstr, 0, 4);

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

				cz.cuni.mff.xrg.odcs.rdf.interfaces.Validator validator = new SPARQLQueryValidator(
						query, SPARQLQueryType.CONSTRUCT);

				if (!validator.isQueryValid()) {

					isQueryValid = false;
					errorMessage = validator.getErrorMessage();

				} else {
					isQueryValid = true;
				}
			}
		});

		gridLayoutCore.addComponent(textAreaConstr, 1, 4);



		return gridLayoutCore;
	}

	/**
	 * List<String> that contains Named Graphs.
	 */
	private List<String> griddata = initializeGridData();

	/**
	 * Initializes data of the Named Graph component
	 */
	private static List<String> initializeGridData() {
		List<String> result = new LinkedList<>();
		result.add("");

		return result;

	}

	/**
	 * Add new data to Named Graph component.
	 *
	 * @param newData. String that will be added
	 */
	private void addDataToGridData(String newData) {
		griddata.add(newData.trim());
	}

	/**
	 * Remove data from Named Graph component. Only if component contain more
	 * then 1 row.
	 *
	 * @param row Data that will be removed.
	 */
	private void removeDataFromGridData(Integer row) {
		int index = row;
		if (griddata.size() > 1) {
			griddata.remove(index);
		}
	}

	private List<TextField> listedEditText = null;

	/**
	 * Save edited texts in the Named Graph component
	 */
	private void saveEditedTexts() {
		griddata = new LinkedList<>();
		for (TextField editText : listedEditText) {
			griddata.add(editText.getValue().trim());
		}
	}

	/**
	 *
	 * @return if all URI name graphs are valid or not.
	 */
	private boolean areGraphsNameValid() {
		for (TextField next : listedEditText) {
			if (!next.isValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Builds Named Graph component which consists of textfields for graph name
	 * and buttons for add and remove this textfields. Used in
	 * {@link #initializeNamedGraphList} and also in adding and removing fields
	 * for component refresh
	 */
	private void refreshNamedGraphData() {
		gridLayoutGraph.removeAllComponents();
		int row = 0;
		listedEditText = new ArrayList<>();
		if (griddata.size() < 1) {
			griddata.add("");
		}
		gridLayoutGraph.setRows(griddata.size() + 1);
		for (String item : griddata) {
			textFieldGraph = new TextField();
			listedEditText.add(textFieldGraph);

			//text field for the graph
			textFieldGraph.setWidth("100%");
			textFieldGraph.setData(row);
			textFieldGraph.setValue(item.trim());
			textFieldGraph.setInputPrompt("http://ld.opendata.cz/source1");
			textFieldGraph.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

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
			buttonGraphRem = new Button();
			buttonGraphRem.setWidth("55px");
			buttonGraphRem.setCaption("-");
			buttonGraphRem.setData(row);
			buttonGraphRem.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveEditedTexts();
					Button senderButton = event.getButton();
					Integer row = (Integer) senderButton.getData();
					removeDataFromGridData(row);
					refreshNamedGraphData();
				}
			});
			gridLayoutGraph.addComponent(textFieldGraph, 0, row);
			gridLayoutGraph.addComponent(buttonGraphRem, 1, row);
			gridLayoutGraph.setComponentAlignment(buttonGraphRem,
					Alignment.TOP_RIGHT);
			row++;
		}
		//add button
		buttonGraphAdd = new Button();
		buttonGraphAdd.setCaption("+");
		buttonGraphAdd.setImmediate(true);
		buttonGraphAdd.setWidth("55px");
		buttonGraphAdd.setHeight("-1px");
		buttonGraphAdd.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveEditedTexts();
				addDataToGridData(" ");
				refreshNamedGraphData();
			}
		});
		gridLayoutGraph.addComponent(buttonGraphAdd, 0, row);

	}

	/**
	 * Initializes Named Graph component. Calls from
	 * {@link #buildGridLayoutCore}
	 */
	private void initializeNamedGraphList() {

		gridLayoutGraph = new GridLayout();
		gridLayoutGraph.setImmediate(false);
		gridLayoutGraph.setWidth("100%");
		gridLayoutGraph.setHeight("100%");
		gridLayoutGraph.setMargin(false);
		gridLayoutGraph.setColumns(2);
		gridLayoutGraph.setColumnExpandRatio(0, 0.95f);
		gridLayoutGraph.setColumnExpandRatio(1, 0.05f);

		refreshNamedGraphData();

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
		//TODO MARIA - set parameters and placement for this component
		useHandler = new CheckBox("Use statistical handler");
		useHandler.setValue(false);
		useHandler.setWidth("-1px");
		useHandler.setHeight("-1px");
		verticalLayoutDetails.addComponent(useHandler);

		return verticalLayoutDetails;
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
		if (!textFieldSparql.isValid() | !areGraphsNameValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else if (!isQueryValid) {
			throw new SPARQLValidationException(errorMessage);
		} else {
			saveEditedTexts();
			RDFExtractorConfig config = new RDFExtractorConfig();
			config.SPARQL_endpoint = (String) textFieldSparql.getValue();
			config.Host_name = textFieldNameAdm.getValue().trim();
			config.Password = passwordFieldPass.getValue();
			config.SPARQL_query = textAreaConstr.getValue().trim();
			config.GraphsUri = griddata;
			config.ExtractFail = extractFail.getValue();
			config.UseStatisticalHandler = useHandler.getValue();
			config.ExtractFail = extractFail.getValue();

			return config;
		}
	}

	/**
	 * Load values from configuration object implementing
	 * {@link DPUConfigObject} interface and configuring DPU into the dialog
	 * where the configuration object may be edited.
	 *
	 * @throws ConfigException Exception which might be thrown when components
	 *                         null	null	null	null	null	null	null	null	null	null
	 *                         null	null	null	null	null	null	null	null	null	null
	 *                         null	null	 {@link #textFieldSparql}, {@link #textFieldNameAdm}, {@link #passwordFieldPass}, 
    * {@link #textAreaConstr}, {@link #extractFail}, {@link #useHandler}, {@link #griddata},
	 *                         in read-only mode or when values loading to this
	 *                         fields could not be converted. Also when
	 *                         requested operation is not supported.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(RDFExtractorConfig conf) throws ConfigException {
		try {
			String endp = conf.SPARQL_endpoint.trim();

			if (endp != null) {
				textFieldSparql.setValue(endp);
			}

			textFieldNameAdm.setValue(conf.Host_name.trim());
			passwordFieldPass.setValue(conf.Password);
			textAreaConstr.setValue(conf.SPARQL_query.trim());
			extractFail.setValue(conf.ExtractFail);
			useHandler.setValue(conf.UseStatisticalHandler);
			extractFail.setValue(conf.ExtractFail);

			griddata = conf.GraphsUri;
			if (griddata == null) {
				griddata = new LinkedList<>();
			}
			refreshNamedGraphData();
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
