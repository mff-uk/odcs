package cz.cuni.mff.xrg.odcs.loader.rdf;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.*;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;

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

	private VerticalLayout verticalLayoutProtocol;

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
	 * For declaration what of POST method used for loading data to SPARQL
	 * endpoint.
	 */
	private OptionGroup postTypeOption;

	/**
	 * For setting query parameter need for HTTP POST to SPARQL endpoint.
	 */
	private TextField queryParamField;

	/**
	 * For setting default graph parameter need for HTTP POST to SPARQL
	 * endpoint.
	 */
	private TextField defaultGraphParamField;

	/**
	 * Button for set default chunk size.
	 */
	private Button chunkDefault;

	private Button buttonGraphRem;

	private Button buttonGraphAdd;

	int n = 1;

	private List<PostItem> postItems = new ArrayList<>();

	private List<GraphItem> graphItems = new ArrayList<>();

	private List<InsertItem> insertItems = new ArrayList<>();

	private CheckBox validateDataBefore;

	private PostItem last;

	/**
	 * Basic constructor.
	 */
	public RDFLoaderDialog() {
		super(RDFLoaderConfig.class);
		initialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}

	private void initialize() {
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
	 * Get description of chosed way, how to insert RDF data to SPARQL endpoint:
	 * Uses in {@link #setConfiguration} for setiing {@link #postTypeOption}
	 *
	 * @param type Type of insert data parts:
	 *             {@link LoaderPostType#POST_UNENCODED_QUERY} or
	 *             {@link LoaderPostType#POST_URL_ENCODER} or
	 *             {@link LoaderPostType#POST_VIRTUOSO_SPEFIC}.
	 *
	 * @return description that corresponds to specific type or ""
	 */
	private String getPostDescription(LoaderPostType postType) {
		if (postItems.isEmpty()) {
			mapPostItems();
		}

		for (PostItem item : postItems) {
			if (item.getPostType().equals(postType)) {
				return item.getDescription();
			}
		}

		return "";
	}

	private PostItem getPostItem(String desc) {
		if (postItems.isEmpty()) {
			mapPostItems();
		}

		for (PostItem item : postItems) {
			if (item.getDescription().equals(desc)) {
				return item;
			}
		}

		return last;
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
	 * Get type of POST variant for loading RDF data to SPARQL endpoint:
	 * POST_URL_ENCODER, POST_UNENCODED_QUERY or POST_VIRTUOSO_SPEFIC. Uses in
	 * {@link #getConfiguration} for determine the type by description that
	 * located in {@link #postTypeOption}
	 *
	 * @param desc String with description of chosed way, how to loading RDF
	 *             data to SPARQL endpoint. One value from
	 *             {@link #postTypeOption}
	 * @return type that corresponds to desc or
	 *         {@link LoaderPostType#POST_URL_ENCODER} in case of absence.
	 */
	private LoaderPostType getPostType(String desc) {
		if (postItems.isEmpty()) {
			mapPostItems();
		}

		for (PostItem item : postItems) {
			if (item.getDescription().equals(desc)) {
				return item.getPostType();
			}
		}

		return LoaderPostType.POST_URL_ENCODER;
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
		if (postItems.isEmpty()) {
			mapPostItems();
		}

		if (graphItems.isEmpty()) {
			mapGraphItems();
		}

		if (insertItems.isEmpty()) {
			mapInsertItems();
		}

	}

	private void mapPostItems() {
		PostItem first = new PostItem(LoaderPostType.POST_URL_ENCODER,
				"Use POST request with URL-encoded parameters (SPARQL 1.1)");
		PostItem second = new PostItem(LoaderPostType.POST_UNENCODED_QUERY,
				"Use POST with unencoded query in the content (SPARQL 1.1)");

		postItems.add(first);
		postItems.add(second);

		postTypeOption.addItem(first.getDescription());
		postTypeOption.addItem(second.getDescription());

		postTypeOption.setValue(first.getDescription());
		last = first;
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
		mainLayout.setImmediate(true);
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
	 * @return verticalLayoutCore. VerticalLayout with components located at the
	 *         Core tab.
	 */
	private VerticalLayout buildVerticalLayoutCore() {
		// common part: create layout
		verticalLayoutCore = new VerticalLayout();
		verticalLayoutCore.setImmediate(true);
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("100%");
		verticalLayoutCore.setMargin(true);


		// Admin layout
		gridLayoutAdm = new GridLayout();
		gridLayoutAdm.setImmediate(true);
		gridLayoutAdm.setWidth("100%");
		gridLayoutAdm.setHeight("100%");
		gridLayoutAdm.setMargin(false);
		gridLayoutAdm.setColumns(2);
		gridLayoutAdm.setRows(4);
		gridLayoutAdm.setColumnExpandRatio(0, 0.10f);
		gridLayoutAdm.setColumnExpandRatio(1, 0.90f);

		// labelSparql
		labelSparql = new Label();
		labelSparql.setImmediate(true);
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
		comboBoxSparql.setInputPrompt("http://localhost:8890/sparql");


		//comboBoxSparql is mandatory fields
		comboBoxSparql.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value == null) {

					ex = new InvalidValueException(
							"SPARQL endpoint must be filled");
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

		labelNameAdm.setImmediate(true);
		labelNameAdm.setWidth("-1px");
		labelNameAdm.setHeight("-1px");
		labelNameAdm.setValue("Name:");
		gridLayoutAdm.addComponent(labelNameAdm, 0, 1);

		// Name textField 
		textFieldNameAdm = new TextField();

		textFieldNameAdm.setNullRepresentation("");
		textFieldNameAdm.setImmediate(true);
		textFieldNameAdm.setWidth("100%");
		textFieldNameAdm.setHeight("-1px");
		textFieldNameAdm.setInputPrompt(
				"username to connect to SPARQL endpoints");
		gridLayoutAdm.addComponent(textFieldNameAdm, 1, 1);

		// labelPass
		labelPass = new Label();

		labelPass.setImmediate(true);
		labelPass.setWidth("-1px");
		labelPass.setHeight("-1px");
		labelPass.setValue("Password:");
		gridLayoutAdm.addComponent(labelPass, 0, 2);

		//  Password field
		passwordFieldPass = new PasswordField();

		passwordFieldPass.setNullRepresentation("");
		passwordFieldPass.setImmediate(true);
		passwordFieldPass.setWidth("100%");
		passwordFieldPass.setHeight("-1px");
		passwordFieldPass.setInputPrompt("password");
		gridLayoutAdm.addComponent(passwordFieldPass, 1, 2);

		// labelGraph
		labelGraph = new Label();

		labelGraph.setImmediate(true);
		labelGraph.setWidth("-1px");
		labelGraph.setHeight("-1px");
		labelGraph.setValue("Default Graph:");
		gridLayoutAdm.addComponent(labelGraph, 0, 3);

		//Named Graph component
		initializeNamedGraphList();

		gridLayoutAdm.addComponent(gridLayoutGraph, 1, 3);

		verticalLayoutCore.addComponent(gridLayoutAdm);

		return verticalLayoutCore;
	}

	/**
	 * List<String> that contains Default Graphs.
	 */
	private List<String> defaultGraphs = initializeGridData();

	/**
	 * Initializes data of the Named Graph component
	 */
	private static List<String> initializeGridData() {
		List<String> result = new LinkedList<>();
		result.add("");

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
	 * Add new data to Named Graph component
	 *
	 * @param newData. String that will be added
	 */
	private void addDataToGridData(String newData) {
		defaultGraphs.add(newData.trim());
	}

	/**
	 * Remove data from Named Graph component. Only if component contain more
	 * then 1 row.
	 *
	 * @param row Data that will be removed.
	 */
	private void removeDataFromGridData(Integer row) {
		int index = row;
		if (defaultGraphs.size() > 1) {
			defaultGraphs.remove(index);
		}
	}

	private List<TextField> listedEditText = null;

	/**
	 * Save edited texts in the Named Graph component
	 */
	private void saveEditedTexts() {
		defaultGraphs.clear();
		for (TextField editText : listedEditText) {
			defaultGraphs.add(editText.getValue().trim());
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
		if (defaultGraphs.size() < 1) {
			defaultGraphs.add("");
		}
		gridLayoutGraph.setRows(defaultGraphs.size() + 1);
		for (String item : defaultGraphs) {
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
		gridLayoutGraph.setImmediate(true);
		gridLayoutGraph.setWidth("100%");
		gridLayoutGraph.setHeight("100%");
		gridLayoutGraph.setMargin(false);
		gridLayoutGraph.setColumns(2);
		gridLayoutGraph.setColumnExpandRatio(0, 0.95f);
		gridLayoutGraph.setColumnExpandRatio(1, 0.05f);

		refreshNamedGraphData();
	}

	private VerticalLayout buildVerticalLayoutProtokol() {

		verticalLayoutProtocol = new VerticalLayout();
		verticalLayoutProtocol.setImmediate(true);
		verticalLayoutProtocol.setWidth("100.0%");
		verticalLayoutProtocol.setHeight("100.0%");
		verticalLayoutProtocol.setMargin(true);
		verticalLayoutProtocol.setSpacing(true);


		postTypeOption = new OptionGroup("HTTP POST Variant:");
		postTypeOption.setImmediate(true);
		postTypeOption.setWidth("-1px");
		postTypeOption.setHeight("-1px");
		postTypeOption.setMultiSelect(false);
		postTypeOption.addValueChangeListener(
				new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				if (last != null) {
					last.setQueryParam(queryParamField.getValue());
					last.setDefaultGraphParam(defaultGraphParamField.getValue());
				}

				String desriptionItem = (String) postTypeOption.getValue();
				PostItem actualItem = getPostItem(desriptionItem);

				if (last != actualItem) {
					queryParamField.setValue(actualItem.getQueryParam());
					defaultGraphParamField.setValue(actualItem
							.getDefaultGraphParam());
					last = actualItem;
				}
			}
		});

		verticalLayoutProtocol.addComponent(postTypeOption);

		VerticalLayout params = new VerticalLayout();
		params.setSpacing(true);

		queryParamField = new TextField("Query param:");
		queryParamField.setValue("update");
		queryParamField.setNullRepresentation("");
		queryParamField.setImmediate(true);
		queryParamField.setWidth("200px");
		queryParamField.setHeight("-1px");
		queryParamField.setInputPrompt("Query param:");

		params.addComponent(queryParamField);

		defaultGraphParamField = new TextField("Default graph param:");
		defaultGraphParamField.setValue("using-graph-uri");
		defaultGraphParamField.setNullRepresentation("");
		defaultGraphParamField.setImmediate(true);
		defaultGraphParamField.setWidth("200px");
		defaultGraphParamField.setHeight("-1px");
		defaultGraphParamField.setInputPrompt("Default graph param:");

		params.addComponent(defaultGraphParamField);

		verticalLayoutProtocol.addComponent(params);

		return verticalLayoutProtocol;
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
		verticalLayoutDetails.setImmediate(true);
		verticalLayoutDetails.setWidth("100.0%");
		verticalLayoutDetails.setHeight("100.0%");
		verticalLayoutDetails.setMargin(true);
		verticalLayoutDetails.setSpacing(true);

		// OptionGroup graphOption
		optionGroupDetail = new OptionGroup("Graph options:");
		optionGroupDetail.setImmediate(true);
		optionGroupDetail.setWidth("-1px");
		optionGroupDetail.setHeight("-1px");
		optionGroupDetail.setMultiSelect(false);
		verticalLayoutDetails.addComponent(optionGroupDetail);

		dataPartsOption = new OptionGroup("In case of data errors:");
		dataPartsOption.setImmediate(true);
		dataPartsOption.setWidth("-1px");
		dataPartsOption.setHeight("-1px");
		dataPartsOption.setMultiSelect(false);
		verticalLayoutDetails.addComponent(dataPartsOption);


		VerticalLayout chunkSizeV = new VerticalLayout();
		chunkSizeV.setSpacing(true);
		chunkSizeV.setStyleName("graypanel");

		chunkSizeV.addComponent(new Label(
				"Chunk size of triples which inserted at once"));

		HorizontalLayout chunkSizeH = new HorizontalLayout();
		chunkSizeH.setSpacing(true);
		// Create chunkparts
		chunkParts = new TextField();
		chunkParts.setValue("100");
		chunkParts.setNullRepresentation("");
		chunkParts.setImmediate(true);
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

		chunkSizeH.addComponent(chunkParts);

		//add button
		chunkDefault = new Button("Default size");
		chunkDefault.setImmediate(true);
		chunkDefault.setWidth("90px");
		chunkDefault.setHeight("-1px");
		chunkDefault.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				String value = String.valueOf(SPARQLoader.getDefaultChunkSize());
				chunkParts.setValue(value);
			}
		});

		chunkSizeH.addComponent(chunkDefault);
		chunkSizeV.addComponent(chunkSizeH);

		verticalLayoutDetails.addComponent(chunkSizeV);

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

		attempts.addComponent(retryTimeField);
		verticalLayoutDetails.addComponent(attempts);

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

	private String validationMessage() {

		String errors = "";
		try {
			comboBoxSparql.validate();

		} catch (Validator.InvalidValueException e) {
			errors = errors + e.getMessage();
		}

		if (!areGraphsNameValid()) {
			if (!errors.equals("")) {
				errors = errors + "; Graph name must start with prefix \"http://\" and contain no white spaces";
			} else {
				errors = errors + "Graph name must start with prefix \"http://\" and contain no white spaces";
			}
		}

		try {
			chunkParts.validate();

		} catch (Validator.InvalidValueException e) {
			if (!errors.equals("")) {
				errors = errors + "; " + e.getMessage();
			} else {
				errors = errors + e.getMessage();
			}
		}

		try {
			retrySizeField.validate();

		} catch (Validator.InvalidValueException e) {
			if (!errors.equals("")) {
				errors = errors + "; " + e.getMessage();
			} else {
				errors = errors + e.getMessage();
			}
		}

		try {
			retryTimeField.validate();

		} catch (Validator.InvalidValueException e) {
			if (!errors.equals("")) {
				errors = errors + "; " + e.getMessage();
			} else {
				errors = errors + e.getMessage();
			}
		}


		if (!errors.equals("")) {
			errors = errors + ".";
		}


		return errors;
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface.
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
			String message = validationMessage();

			throw new ConfigException(message);
		} else {
			saveEditedTexts();


			String graphDescription = (String) optionGroupDetail.getValue();
			String insertDescription = (String) dataPartsOption.getValue();

			WriteGraphType graphType = getGraphType(graphDescription);
			InsertType insertType = getInsertType(insertDescription);

			long chunkSize = Long.parseLong(chunkParts.getValue());

			int retrySize = Integer.parseInt(retrySizeField.getValue());
			long retryTime = Long.parseLong(retryTimeField.getValue());

			String SPARQLEndpoint = (String) comboBoxSparql.getValue();
			String hostName = textFieldNameAdm.getValue().trim();
			String password = passwordFieldPass.getValue();
			boolean validDataBefore = validateDataBefore.getValue();

			String queryParam = queryParamField.getValue().trim();
			String defaultGraphParam = defaultGraphParamField.getValue().trim();

			String postDescription = (String) postTypeOption.getValue();
			LoaderPostType postType = getPostType(postDescription);

			LoaderEndpointParams endpointParams = new LoaderEndpointParams(
					queryParam, defaultGraphParam, postType);

			RDFLoaderConfig config = new RDFLoaderConfig(SPARQLEndpoint,
					hostName, password, getDefaultGraphs(), graphType,
					insertType,
					chunkSize, validDataBefore, retryTime, retrySize,
					endpointParams);

			return config;
		}
	}

	/**
	 * Load values from configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU into the dialog where the configuration object may be
	 * edited.
	 *
	 * @throws ConfigException Exception which might be thrown when components
	 *
	 * {@link #comboBoxSparql}, {@link #textFieldNameAdm},
	 * {@link #passwordFieldPass}, {@link #optionGroupDetail},
	 * {@link #defaultGraphs}, in read-only mode or when requested operation is
	 * not supported.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(RDFLoaderConfig conf) throws ConfigException {
		try {
			String endp = conf.getSPARQLEndpoint().trim();

			if (endp != null) {

				comboBoxSparql.setValue(endp);
			}
			textFieldNameAdm.setValue(conf.getHostName().trim());
			passwordFieldPass.setValue(conf.getPassword());

			WriteGraphType graphType = conf.getGraphOption();
			InsertType insertType = conf.getInsertOption();

			String graphDescription = getGraphDescription(graphType);
			String insertDescription = getInsertDescription(insertType);

			optionGroupDetail.setValue(graphDescription);
			dataPartsOption.setValue(insertDescription);

			String chunkSize = String.valueOf(conf.getChunkSize());
			chunkParts.setValue(chunkSize);

			String retrySize = String.valueOf(conf.getRetrySize());
			retrySizeField.setValue(retrySize);

			String retryTime = String.valueOf(conf.getRetryTime());
			retryTimeField.setValue(retryTime);

			validateDataBefore.setValue(conf.isValidDataBefore());

			LoaderEndpointParams endpointParams = conf.getEndpointParams();

			LoaderPostType postType = endpointParams.getPostType();
			String postDescription = getPostDescription(postType);
			postTypeOption.setValue(postDescription);

			queryParamField.setValue(endpointParams.getQueryParam());
			defaultGraphParamField.setValue(endpointParams
					.getDefaultGraphParam());

			try {
				defaultGraphs = conf.getGraphsUri();
				if (defaultGraphs == null) {
					defaultGraphs = new LinkedList<>();
				}
			} catch (Exception e) {
				defaultGraphs = new LinkedList<>();
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

class PostItem {

	private LoaderPostType postType;

	private String description;

	private String queryParam;

	private String defaultGraphParam;

	public PostItem(LoaderPostType postType, String description) {
		this.postType = postType;
		this.description = description;
		this.queryParam = LoaderEndpointParams.DEFAULT_QUERY_PARAM;
		this.defaultGraphParam = LoaderEndpointParams.DEFAULT_GRAPH_PARAM;
	}

	public LoaderPostType getPostType() {
		return postType;
	}

	public String getDescription() {
		return description;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public void setDefaultGraphParam(String defaultGraphParam) {
		this.defaultGraphParam = defaultGraphParam;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public String getDefaultGraphParam() {
		return defaultGraphParam;
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
