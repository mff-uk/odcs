package cz.cuni.mff.xrg.odcs.loader.rdf;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.*;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.repositories.BaseRDFRepo;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Configuration dialog for DPU SPARQL Loader.
 *
 * @author Maria Kukhar
 * @author Jiri Tomes
 *
 */
public class RDFLoaderDialog extends BaseConfigDialog<RDFLoaderConfig> {

	private static final long serialVersionUID = 1L;

	private GridLayout mainLayout;

	/**
	 * TabSheet of Configuration dialog. Contains two tabs: Core and Details
	 */
	private TabSheet tabSheet;

	private VerticalLayout verticalLayoutDetails;

	/**
	 * OptionGroup to specify what should happen if the graph that was set in
	 * Named Graph component already exists.
	 */
	private OptionGroup optionGroupDetail;

	/**
	 * For declaration what to do with invalid data insert part for loading data
	 * to SPARQL endpoint.
	 */
	private OptionGroup dataPartsOption;

	private VerticalLayout verticalLayoutCore;

	private GridLayout gridLayoutAdm;

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
	private TextField comboBoxSparql;

	private Validator.InvalidValueException ex;

	private Label labelSparql;

	private GridLayout gridLayoutGraph;

	private TextField textFieldGraph;

	/**
	 * For setting size of insert part to load.
	 */
	private TextField chunkParts;

	/**
	 * Set Count of attempts to reconnect if the connection fails. For infinite
	 * loop use zero or negative integer
	 */
	private TextField retrySizeField;

	/**
	 * For setting retry connection time before trying to reconnect.
	 */
	private TextField retryTimeField;

	/**
	 * Button for set default chunk size.
	 */
	private Button chunkDefault;

	private Button buttonGraphRem;

	private Button buttonGraphAdd;

	int n = 1;

	private List<GraphItem> graphItems = new ArrayList<>();

	private List<InsertItem> insertItems = new ArrayList<>();

	private CheckBox validateDataBefore;

	/**
	 * Basic constructor.
	 */
	public RDFLoaderDialog() {
		super(RDFLoaderConfig.class);
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}

	private void inicialize() {
		ex = new Validator.InvalidValueException("Valid");
	}

	/**
	 * Get description of chosed way, how to load RDF data to named graph to
	 * SPARQL endpoint. Uses in {@link #setConfiguration} for setiing
	 * {@link #optionGroupDetail}
	 *
	 * @param type Type of load RDF data to named graph: OVERRIDE, MERGE or FAIL
	 * @return description that corresponds to specific type or ""
	 */
	private String getGraphDescription(WriteGraphType type) {
		if (graphItems.isEmpty()) {
			mapGraphItems();
		}

		for (GraphItem item : graphItems) {
			if (item.getType().equals(type)) {
				return item.getDescription();
			}
		}
		return "";
	}

	/**
	 * Get description of chosed way, how to insert RDF data part to SPARQL
	 * endpoint: Uses in {@link #setConfiguration} for setiing
	 * {@link #dataPartsOption}
	 *
	 * @param type Type of insert data parts: {@link InsertType#SKIP_BAD_PARTS}
	 *             or {@link InsertType#STOP_WHEN_BAD_PART}
	 * @return description that corresponds to specific type or ""
	 */
	private String getInsertDescription(InsertType type) {
		if (insertItems.isEmpty()) {
			mapInsertItems();
		}
		for (InsertItem item : insertItems) {
			if (item.getType().equals(type)) {
				return item.getDescription();
			}
		}

		return "";
	}

	/**
	 * Get type of load RDF data to named graph: OVERRIDE, MERGE or FAIL Uses in
	 * {@link #getConfiguration} for determine the type by description that
	 * located in {@link #optionGroupDetail}
	 *
	 * @param desc String with description of chosed way, how to load RDF data
	 *             to named graph from {@link #optionGroupDetail}
	 * @return type that corresponds to desc or WriteGraphType.OVERRIDE in case
	 *         of absence.
	 */
	private WriteGraphType getGraphType(String desc) {
		if (graphItems.isEmpty()) {
			mapGraphItems();
		}

		for (GraphItem item : graphItems) {
			if (item.getDescription().equals(desc)) {
				return item.getType();
			}
		}

		return WriteGraphType.OVERRIDE;
	}

	/**
	 * Get type of insert RDF data part to SPARQL endpoint:
	 * {@link InsertType#SKIP_BAD_PARTS} or
	 * {@link InsertType#STOP_WHEN_BAD_PART}. Uses in {@link #getConfiguration}
	 * for determine the type by description that located in
	 * {@link #dataPartsOption}
	 *
	 * @param desc String with description of chosed way, how to load RDF data
	 *             to named graph from {@link #dataPartsOption}
	 * @return type that corresponds to desc or InsertType.STOP_WHEN_BAD_PART in
	 *         case of absence.
	 */
	private InsertType getInsertType(String desc) {
		if (insertItems.isEmpty()) {
			mapInsertItems();
		}

		for (InsertItem item : insertItems) {
			if (item.getDescription().equals(desc)) {
				return item.getType();
			}
		}

		return InsertType.STOP_WHEN_BAD_PART;
	}

	/**
	 * Set values of {@link #optionGroupDetail}
	 */
	private void mapData() {

		if (graphItems.isEmpty()) {
			mapGraphItems();
		}

		if (insertItems.isEmpty()) {
			mapInsertItems();
		}

	}

	private void mapGraphItems() {
		GraphItem override = new GraphItem(WriteGraphType.OVERRIDE,
				"Overwrite target graph");
		GraphItem merge = new GraphItem(WriteGraphType.MERGE,
				"Merge with target graph's content");
		GraphItem fail = new GraphItem(WriteGraphType.FAIL,
				"Fail if the target graph exists");

		graphItems.add(override);
		graphItems.add(merge);
		graphItems.add(fail);

		optionGroupDetail.addItem(override.getDescription());
		optionGroupDetail.addItem(merge.getDescription());
		optionGroupDetail.addItem(fail.getDescription());

		optionGroupDetail.setValue(override.getDescription());
	}

	private void mapInsertItems() {
		InsertItem skip = new InsertItem(InsertType.SKIP_BAD_PARTS,
				"Continue with the loading "
				+ "process, resulting data will be incomplete");
		InsertItem stop = new InsertItem(InsertType.STOP_WHEN_BAD_PART,
				"Stop the loading process and "
				+ "end with an error");

		InsertItem repeat = new InsertItem(InsertType.REPEAT_IF_BAD_PART,
				"Repeat the loading process once again");

		insertItems.add(stop);
		insertItems.add(skip);
		insertItems.add(repeat);


		dataPartsOption.addItem(stop.getDescription());
		dataPartsOption.addItem(skip.getDescription());
		dataPartsOption.addItem(repeat.getDescription());

		dataPartsOption.setValue(stop.getDescription());
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
		verticalLayoutCore = buildVerticalLayoutCore();
		tabSheet.addTab(verticalLayoutCore, "Core", null);

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
	 * @return verticalLayoutCore. VerticalLayout with components located at the
	 *         Core tab.
	 */
	private VerticalLayout buildVerticalLayoutCore() {
		// common part: create layout
		verticalLayoutCore = new VerticalLayout();
		verticalLayoutCore.setImmediate(false);
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("100%");
		verticalLayoutCore.setMargin(true);


		// Admin layout
		gridLayoutAdm = new GridLayout();
		gridLayoutAdm.setImmediate(false);
		gridLayoutAdm.setWidth("100%");
		gridLayoutAdm.setHeight("100%");
		gridLayoutAdm.setMargin(false);
		gridLayoutAdm.setColumns(2);
		gridLayoutAdm.setRows(4);
		gridLayoutAdm.setColumnExpandRatio(0, 0.10f);
		gridLayoutAdm.setColumnExpandRatio(1, 0.90f);

		// labelSparql
		labelSparql = new Label();
		labelSparql.setImmediate(false);
		labelSparql.setWidth("-1px");
		labelSparql.setHeight("-1px");
		labelSparql.setValue("SPARQL endpoint:");
		gridLayoutAdm.addComponent(labelSparql, 0, 0);
		gridLayoutAdm.setComponentAlignment(labelSparql, Alignment.TOP_LEFT);

		// SPARQL endpoint ComboBox
		comboBoxSparql = new TextField();
		comboBoxSparql.setImmediate(true);
		comboBoxSparql.setWidth("100%");
		comboBoxSparql.setHeight("-1px");
		comboBoxSparql.setInputPrompt("http://example:8894/sparql");


		//comboBoxSparql is mandatory fields
		comboBoxSparql.addValidator(new Validator() {
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

		gridLayoutAdm.addComponent(comboBoxSparql, 1, 0);

		// labelNameAdm
		labelNameAdm = new Label();

		labelNameAdm.setImmediate(false);
		labelNameAdm.setWidth("-1px");
		labelNameAdm.setHeight("-1px");
		labelNameAdm.setValue("Name:");
		gridLayoutAdm.addComponent(labelNameAdm, 0, 1);

		// Name textField 
		textFieldNameAdm = new TextField();

		textFieldNameAdm.setNullRepresentation("");
		textFieldNameAdm.setImmediate(false);
		textFieldNameAdm.setWidth("100%");
		textFieldNameAdm.setHeight("-1px");
		textFieldNameAdm.setInputPrompt(
				"username to connect to SPARQL endpoints");
		gridLayoutAdm.addComponent(textFieldNameAdm, 1, 1);

		// labelPass
		labelPass = new Label();

		labelPass.setImmediate(false);
		labelPass.setWidth("-1px");
		labelPass.setHeight("-1px");
		labelPass.setValue("Password:");
		gridLayoutAdm.addComponent(labelPass, 0, 2);

		//  Password field
		passwordFieldPass = new PasswordField();

		passwordFieldPass.setNullRepresentation("");
		passwordFieldPass.setImmediate(false);
		passwordFieldPass.setWidth("100%");
		passwordFieldPass.setHeight("-1px");
		passwordFieldPass.setInputPrompt("password");
		gridLayoutAdm.addComponent(passwordFieldPass, 1, 2);

		// labelGraph
		labelGraph = new Label();

		labelGraph.setImmediate(false);
		labelGraph.setWidth("-1px");
		labelGraph.setHeight("-1px");
		labelGraph.setValue("Named Graph:");
		gridLayoutAdm.addComponent(labelGraph, 0, 3);

		//Named Graph component
		initializeNamedGraphList();

		gridLayoutAdm.addComponent(gridLayoutGraph, 1, 3);

		verticalLayoutCore.addComponent(gridLayoutAdm);

		return verticalLayoutCore;
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
	 * Add new data to Named Graph component
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
			textFieldGraph.setInputPrompt("http://ld.opendata.cz/kb");
			textFieldGraph.addValidator(new Validator() {
				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws Validator.InvalidValueException {
					if (value != null) {
						String namedGraph = value.toString().trim();

						if (namedGraph.isEmpty()) {
							return;
						}

						if (namedGraph.contains(" ")) {
							ex = new Validator.InvalidValueException(
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
				addDataToGridData("");
				refreshNamedGraphData();
			}
		});
		gridLayoutGraph.addComponent(buttonGraphAdd, 0, row);
	}

	/**
	 * Initializes Named Graph component. Calls from
	 * {@link #buildVerticalLayoutCore()}
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
		verticalLayoutDetails.setWidth("100.0%");
		verticalLayoutDetails.setHeight("100.0%");
		verticalLayoutDetails.setMargin(true);
		verticalLayoutDetails.setSpacing(true);

		// OptionGroup graphOption
		optionGroupDetail = new OptionGroup("Graph options:");
		optionGroupDetail.setImmediate(false);
		optionGroupDetail.setWidth("-1px");
		optionGroupDetail.setHeight("-1px");
		optionGroupDetail.setMultiSelect(false);
		verticalLayoutDetails.addComponent(optionGroupDetail);

		dataPartsOption = new OptionGroup("In case of data errors:");
		dataPartsOption.setImmediate(false);
		dataPartsOption.setWidth("-1px");
		dataPartsOption.setHeight("-1px");
		dataPartsOption.setMultiSelect(false);
		verticalLayoutDetails.addComponent(dataPartsOption);

		// Create chunkparts
		chunkParts = new TextField(
				"Chunk size of triples which inserted at once");
		chunkParts.setValue("100");
		chunkParts.setNullRepresentation("");
		chunkParts.setImmediate(false);
		chunkParts.setWidth("100px");
		chunkParts.setHeight("-1px");
		chunkParts.setInputPrompt(
				"Chunk size of triples which inserted at once");
		chunkParts.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws Validator.InvalidValueException {
				if (value != null) {
					String size = value.toString().trim();

					try {
						long result = Long.parseLong(size);

						if (result <= 0) {
							ex = new Validator.InvalidValueException(
									"Chunk size must be number greater than 0");
							throw ex;
						}

					} catch (NumberFormatException e) {
						ex = new Validator.InvalidValueException(
								"Chunk size must be a number");
						throw ex;
					}

				} else {
					throw new Validator.EmptyValueException(
							"Chunk size is a null");
				}
			}
		});

		verticalLayoutDetails.addComponent(chunkParts);

		//add button
		chunkDefault = new Button("Default size");
		chunkDefault.setImmediate(true);
		chunkDefault.setWidth("90px");
		chunkDefault.setHeight("-1px");
		chunkDefault.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				String value = String.valueOf(BaseRDFRepo.getDefaultChunkSize());
				chunkParts.setValue(value);
			}
		});
		verticalLayoutDetails.addComponent(chunkDefault);

		verticalLayoutDetails.addComponent(new Label(
				"Count of attempts to reconnect if the connection to SPARQL fails"));

		retrySizeField = new TextField(
				"(Use 0 or negative integer for infinity)");
		retrySizeField.setValue("-1");
		retrySizeField.setNullRepresentation("");
		retrySizeField.setImmediate(false);
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

		verticalLayoutDetails.addComponent(retrySizeField);


		retryTimeField = new TextField(
				"Time in miliseconds how long to wait before trying to reconnect");
		retryTimeField.setValue("1000");
		retryTimeField.setNullRepresentation("");
		retryTimeField.setImmediate(false);
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

						if (time <= 0) {
							ex = new Validator.InvalidValueException(
									"Time for reconnect must be number greater than 0");
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

		verticalLayoutDetails.addComponent(retryTimeField);

		//add checkbox for data validation
		validateDataBefore = new CheckBox(
				"Validate data before loading - "
				+ "if validation fails, the loading fails immediately");
		validateDataBefore.setValue(false);
		validateDataBefore.setWidth("-1px");
		validateDataBefore.setHeight("-1px");

		verticalLayoutDetails.addComponent(validateDataBefore);

		return verticalLayoutDetails;
	}

	private boolean allComponentAreValid() {

		boolean areValid = comboBoxSparql.isValid()
				&& chunkParts.isValid()
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
	 *                         {@link #comboBoxSparql} contains null value.
	 * @return config Object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public RDFLoaderConfig getConfiguration() throws ConfigException {
		if (!allComponentAreValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else {
			saveEditedTexts();
			RDFLoaderConfig config = new RDFLoaderConfig();

			String graphDescription = (String) optionGroupDetail.getValue();
			String insertDescription = (String) dataPartsOption.getValue();

			WriteGraphType graphType = getGraphType(graphDescription);
			InsertType insertType = getInsertType(insertDescription);

			long chunkSize = Long.parseLong(chunkParts.getValue());

			int retrySize = Integer.parseInt(retrySizeField.getValue());
			long retryTime = Long.parseLong(retryTimeField.getValue());


			config.graphOption = graphType;
			config.insertOption = insertType;
			config.SPARQL_endpoint = (String) comboBoxSparql.getValue();
			config.Host_name = textFieldNameAdm.getValue().trim();
			config.Password = passwordFieldPass.getValue();
			config.GraphsUri = griddata;
			config.chunkSize = chunkSize;
			config.retrySize = retrySize;
			config.retryTime = retryTime;
			config.validDataBefore = validateDataBefore.getValue();

			return config;
		}
	}

	/**
	 * Load values from configuration object implementing {@link Config}
	 * interface and configuring DPU into the dialog where the configuration
	 * object may be edited.
	 *
	 * @throws ConfigException Exception which might be thrown when components
	 *
	 * {@link #comboBoxSparql}, {@link #textFieldNameAdm},
	 * {@link #passwordFieldPass}, {@link #optionGroupDetail},
	 * {@link #griddata}, in read-only mode or when requested operation is not
	 * supported.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(RDFLoaderConfig conf) throws ConfigException {
		try {
			String endp = conf.SPARQL_endpoint.trim();

			if (endp != null) {

				comboBoxSparql.setValue(endp);
			}
			textFieldNameAdm.setValue(conf.Host_name.trim());
			passwordFieldPass.setValue(conf.Password);

			WriteGraphType graphType = conf.graphOption;
			InsertType insertType = conf.insertOption;

			String graphDescription = getGraphDescription(graphType);
			String insertDescription = getInsertDescription(insertType);

			optionGroupDetail.setValue(graphDescription);
			dataPartsOption.setValue(insertDescription);

			String chunkSize = String.valueOf(conf.chunkSize);
			chunkParts.setValue(chunkSize);

			String retrySize = String.valueOf(conf.retrySize);
			retrySizeField.setValue(retrySize);

			String retryTime = String.valueOf(conf.retryTime);
			retryTimeField.setValue(retryTime);

			validateDataBefore.setValue(conf.validDataBefore);


			try {
				griddata = conf.GraphsUri;
				if (griddata == null) {
					griddata = new LinkedList<>();
				}
			} catch (Exception e) {
				griddata = new LinkedList<>();
			}
			refreshNamedGraphData();

		} catch (UnsupportedOperationException | Property.ReadOnlyException e) {
			// throw setting exception
			throw new ConfigException(e.getMessage(), e);
		}
	}

	@Override
	public String getDescription() {
		StringBuilder description = new StringBuilder();
		description.append("Load to SPARQL: ");
		description.append((String) comboBoxSparql.getValue());
		return description.toString();
	}
}

class GraphItem {

	private WriteGraphType type;

	private String description;

	public GraphItem(WriteGraphType type, String description) {
		this.type = type;
		this.description = description;

	}

	public WriteGraphType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}
}

class InsertItem {

	private InsertType type;

	private String description;

	public InsertItem(InsertType type, String description) {
		this.type = type;
		this.description = description;
	}

	public InsertType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}
}
