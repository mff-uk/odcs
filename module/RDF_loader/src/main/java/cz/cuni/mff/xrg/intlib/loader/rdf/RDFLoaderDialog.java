package cz.cuni.mff.xrg.intlib.loader.rdf;


import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import cz.cuni.xrg.intlib.commons.data.rdf.WriteGraphType;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;

/**
 * Config dialog.
 *
 * @author Maria
 *
 */
public class RDFLoaderDialog extends AbstractConfigDialog<RDFLoaderConfig> {

    private static final long serialVersionUID = 1L;
    private GridLayout mainLayout;
    private TabSheet tabSheet;
    private VerticalLayout verticalLayoutDetails;
    private OptionGroup optionGroupDetail;
    private VerticalLayout verticalLayoutCore;
    private GridLayout gridLayoutAdm;
    private Label labelGraph;
    private PasswordField passwordFieldPass;
    private Label labelPass;
    private TextField textFieldNameAdm;
    private Label labelNameAdm;
    private ComboBox comboBoxSparql;
    private Label labelSparql;
    private GridLayout gridLayoutGraph;
    private TextField textFieldGraph;
    private Button buttonGraphRem;
    private Button buttonGraphAdd;
    int n = 1;
    private List<GraphItem> graphItems = new ArrayList<>();

    public RDFLoaderDialog() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
        mapData();
    }

    private String getGraphDescription(WriteGraphType type) {
        if (graphItems.isEmpty()) {
            mapData();
        }
        
        for (GraphItem item : graphItems) {
            if (item.getType().equals(type)) {
                return item.getDescription();
            }
        }
        return "";
    }

    private WriteGraphType getGraphType(String desc) {
        if (graphItems.isEmpty()) {
            mapData();
        }

        for (GraphItem item : graphItems) {
            if (item.getDescription().equals(desc)) {
                return item.getType();
            }
        }

        return WriteGraphType.OVERRIDE;
    }

    private void mapData() {

        if (graphItems.isEmpty()) {
            GraphItem override = new GraphItem(WriteGraphType.OVERRIDE, "Overwrite target graph");
            GraphItem merge = new GraphItem(WriteGraphType.MERGE, "Merge with target graph's content");
            GraphItem fail = new GraphItem(WriteGraphType.FAIL, "Fail if the target graph exists");

            graphItems.add(override);
            graphItems.add(merge);
            graphItems.add(fail);

            optionGroupDetail.addItem(override.getDescription());
            optionGroupDetail.addItem(merge.getDescription());
            optionGroupDetail.addItem(fail.getDescription());
            optionGroupDetail.setValue(override.getDescription());

        }
    }

    public static IndexedContainer getFridContainer() {


        String[] visibleCols = new String[]{"endpoint"};

        IndexedContainer result = new IndexedContainer();

        for (String p : visibleCols) {
            result.addContainerProperty(p, String.class, "");
        }


        /*	for (int i = 0; i < endpoint.length ; i++) {
         Object num = result.addItem();
         result.getContainerProperty(num, "endpoint").setValue(endpoint[i]);

         } */

        return result;
    }

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
        tabSheet = buildTabSheet();
        mainLayout.addComponent(tabSheet, 0, 0);
        mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);


        return mainLayout;
    }

    private TabSheet buildTabSheet() {
        // common part: create layout
        tabSheet = new TabSheet();
        tabSheet.setImmediate(true);
        tabSheet.setWidth("100%");
        tabSheet.setHeight("100%");

        // verticalLayoutCore
        verticalLayoutCore = buildVerticalLayoutCore();
        tabSheet.addTab(verticalLayoutCore, "Core", null);

        // verticalLayoutDetails
        verticalLayoutDetails = buildVerticalLayoutDetails();
        tabSheet.addTab(verticalLayoutDetails, "Details", null);

        return tabSheet;
    }

    private VerticalLayout buildVerticalLayoutCore() {
        // common part: create layout
        verticalLayoutCore = new VerticalLayout();
        verticalLayoutCore.setImmediate(false);
        verticalLayoutCore.setWidth("100.0%");
        verticalLayoutCore.setHeight("100%");
        verticalLayoutCore.setMargin(true);


        // gridLayoutAdm
        gridLayoutAdm = buildGridLayoutAdm();
        verticalLayoutCore.addComponent(gridLayoutAdm);

        return verticalLayoutCore;
    }

    private GridLayout buildGridLayoutAdm() {
        // common part: create layout
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

        // comboBoxSparql
        Container cont = getFridContainer();
        comboBoxSparql = new ComboBox();
        comboBoxSparql.setContainerDataSource(cont);
        comboBoxSparql.setImmediate(false);
        comboBoxSparql.setWidth("100%");
        comboBoxSparql.setHeight("-1px");
        comboBoxSparql.setNewItemsAllowed(true);
        comboBoxSparql.setTextInputAllowed(true);
        comboBoxSparql.setItemCaptionPropertyId("endpoint");
        comboBoxSparql.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        comboBoxSparql.setInputPrompt("http://example:8894/sparql");

        comboBoxSparql.setFilteringMode(FilteringMode.CONTAINS);
        comboBoxSparql.setImmediate(true);


        // Disallow null selections
        comboBoxSparql.setNullSelectionAllowed(false);

        // Check if the caption for new item already exists in the list of item
        // captions before approving it as a new item.

        comboBoxSparql.setNewItemHandler(new AbstractSelect.NewItemHandler() {
            @Override
            public void addNewItem(final String newItemCaption) {
                boolean newItem = true;
                for (final Object itemId : comboBoxSparql.getItemIds()) {
                    if (newItemCaption.equalsIgnoreCase(comboBoxSparql
                            .getItemCaption(itemId))) {
                        newItem = false;
                        break;
                    }
                }
                if (newItem) {
                    // Adds new option
                    if (comboBoxSparql.addItem(newItemCaption) != null) {
                        final Item item = comboBoxSparql.getItem(newItemCaption);
                        item.getItemProperty("endpoint")
                                .setValue(newItemCaption);
                        comboBoxSparql.setValue(newItemCaption);
                    }
                }
            }
        });
        comboBoxSparql.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value!=null) {
					return;
				}
				throw new InvalidValueException("SPARQL endpoint must be filled!");
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

        // textFieldNameAdm
        textFieldNameAdm = new TextField();
        textFieldNameAdm.setNullRepresentation("");
        textFieldNameAdm.setImmediate(false);
        textFieldNameAdm.setWidth("100%");
        textFieldNameAdm.setHeight("-1px");
        textFieldNameAdm.setInputPrompt("username to connect to SPARQL endpoints");
        gridLayoutAdm.addComponent(textFieldNameAdm, 1, 1);

        // labelPass
        labelPass = new Label();
        labelPass.setImmediate(false);
        labelPass.setWidth("-1px");
        labelPass.setHeight("-1px");
        labelPass.setValue("Password:");
        gridLayoutAdm.addComponent(labelPass, 0, 2);

        // passwordFieldPass
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


        initializeNamedGraphList();
        gridLayoutAdm.addComponent(gridLayoutGraph, 1, 3);


        return gridLayoutAdm;
    }
    private List<String> griddata = initializeGridData();

    private static List<String> initializeGridData() {
        List<String> result = new LinkedList<>();
        result.add("");

        return result;

    }

    private void addDataToGridData(String newData) {
        griddata.add(newData);
    }

    private void removeDataFromGridData(Integer row) {
        int index = row;
        if (griddata.size() > 1) {
            griddata.remove(index);
        }
    }
    private List<TextField> listedEditText = null;

    private void replaceText(int index, String newText) {
        griddata.remove(index);
        griddata.add(index, newText);

    }

    private void saveEditedTexts() {
        griddata = new LinkedList<>();
        for (TextField editText : listedEditText) {
            griddata.add(editText.getValue());
        }
    }

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
            textFieldGraph.setWidth("100%");
            textFieldGraph.setData(row);
            textFieldGraph.setValue(item);
            textFieldGraph.setInputPrompt("http://ld.opendata.cz/kb");

            buttonGraphRem = new Button();
            buttonGraphRem.setWidth("55px");
            buttonGraphRem.setCaption("-");
            buttonGraphRem.setData(row);
            buttonGraphRem.addClickListener(new Button.ClickListener() {
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
            gridLayoutGraph.setComponentAlignment(buttonGraphRem, Alignment.TOP_RIGHT);
            row++;
        }

        buttonGraphAdd = new Button();
        buttonGraphAdd.setCaption("+");
        buttonGraphAdd.setImmediate(true);
        buttonGraphAdd.setWidth("55px");
        buttonGraphAdd.setHeight("-1px");
        buttonGraphAdd.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveEditedTexts();
                addDataToGridData(" ");
                refreshNamedGraphData();
            }
        });
        gridLayoutGraph.addComponent(buttonGraphAdd, 0, row);
    }

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

    private VerticalLayout buildVerticalLayoutDetails() {
        // common part: create layout
        verticalLayoutDetails = new VerticalLayout();
        verticalLayoutDetails.setImmediate(false);
        verticalLayoutDetails.setWidth("100.0%");
        verticalLayoutDetails.setHeight("100.0%");
        verticalLayoutDetails.setMargin(true);
        verticalLayoutDetails.setSpacing(true);

        // optionGroup_1
        optionGroupDetail = new OptionGroup();
        optionGroupDetail.setCaption("Options:");
        optionGroupDetail.setImmediate(false);
        optionGroupDetail.setWidth("-1px");
        optionGroupDetail.setHeight("-1px");
        optionGroupDetail.setMultiSelect(false);
        verticalLayoutDetails.addComponent(optionGroupDetail);

        return verticalLayoutDetails;
    }

	@Override
	public RDFLoaderConfig getConfiguration() throws ConfigException {
		if (!comboBoxSparql.isValid()) {
			throw new ConfigException();
		} else {
		saveEditedTexts();
		RDFLoaderConfig config = new RDFLoaderConfig();
        String graphDescription = (String) optionGroupDetail.getValue();
        WriteGraphType graphType = getGraphType(graphDescription);		
		config.Options = graphType;
		config.SPARQL_endpoint = (String) comboBoxSparql.getValue();
		config.Host_name = textFieldNameAdm.getValue();
		config.Password = passwordFieldPass.getValue();
		config.GraphsUri = griddata;
		
		return config;
		}
	}
    
    /**
     * Load values from configuration into dialog.
     *
     * @throws ConfigException
     * @param conf
     */
    public void setConfiguration(RDFLoaderConfig conf) {
        try {
            String endp = conf.SPARQL_endpoint;

            if ((endp!=null)&& (comboBoxSparql.addItem(endp) != null)) {
                final Item item = comboBoxSparql.getItem(endp);
                item.getItemProperty("endpoint").setValue(endp);
                comboBoxSparql.setValue(endp);
            }
            textFieldNameAdm.setValue(conf.Host_name);
            passwordFieldPass.setValue(conf.Password);

            WriteGraphType graphType = conf.Options;
            String description = getGraphDescription(graphType);

            optionGroupDetail.setValue(description);

            try {
                griddata = conf.GraphsUri;
                if (griddata == null) {
                    griddata = new LinkedList<>();
                }
            } catch (Exception e) {
                griddata = new LinkedList<>();
            }
            refreshNamedGraphData();

        } catch (UnsupportedOperationException | Property.ReadOnlyException ex) {
            // throw setting exception
            throw new ConfigException();
        }
    }
}

class GraphItem {

    WriteGraphType type;
    String description;

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
