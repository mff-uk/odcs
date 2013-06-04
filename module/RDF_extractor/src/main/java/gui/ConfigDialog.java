package gui;

import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.*;
import com.vaadin.data.util.*;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.*;
import com.vaadin.shared.ui.combobox.FilteringMode;
import java.io.Serializable;

/**
 * Config dialog.
 *
 * @author Maria
 *
 */
public class ConfigDialog extends CustomComponent {

    private static final long serialVersionUID = 1L;
    private GridLayout mainLayout;
    private TabSheet tabSheet;
    private VerticalLayout verticalLayoutDetails;
    private CheckBox checkBoxFail;
    private Label labelOpt;
    private GridLayout gridLayoutConstr;
    private TextArea textAreaConstr;
    private Label labelConstr;
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

    public ConfigDialog() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    /**
     * Return current configuration from dialog. Can return null, if current
     * configuration is invalid.
     *
     */
    public void getConfiguration(Config config) {
        saveEditedTexts();
        config.setValue(Config.SPARQL_endpoint.name(), (String) comboBoxSparql.getValue());
        config.setValue(Config.Host_name.name(), textFieldNameAdm.getValue());
        config.setValue(Config.Password.name(), passwordFieldPass.getValue());
        config.setValue(Config.SPARQL_query.name(), textAreaConstr.getValue());
        config.setValue(Config.GraphsUri.name(), (Serializable) griddata);
        config.setValue(Config.ExtractFail.name(), checkBoxFail.getValue());
    }

    /**
     * Load values from configuration into dialog.
     *
     * @throws ConfigException
     * @param conf
     */
    public void setConfiguration(Config conf) {
        try {
            String endp = (String) conf.getValue(Config.SPARQL_endpoint.name());

            if ((endp!=null)&& (comboBoxSparql.addItem(endp) != null)) {
                final Item item = comboBoxSparql.getItem(endp);
                item.getItemProperty("endpoint").setValue(endp);
                comboBoxSparql.setValue(endp);
            }
            textFieldNameAdm.setValue((String) conf.getValue(Config.Host_name
                    .name()));
            passwordFieldPass.setValue((String) conf.getValue(Config.Password
                    .name()));

            textAreaConstr.setValue((String) conf.getValue(Config.SPARQL_query
                    .name()));
            checkBoxFail.setValue((Boolean) conf.getValue(Config.ExtractFail
                    .name()));

            try {
                griddata = (List<String>) conf.getValue(Config.GraphsUri.name());
                if (griddata == null) {
                    griddata = new LinkedList<>();
                }
            } catch (Exception e) {
                griddata = new LinkedList<>();
            }
            refreshNamedGraphData();
        } catch (UnsupportedOperationException | Property.ReadOnlyException | Converter.ConversionException ex) {
            // throw setting exception
            throw new ConfigException();
        }
    }

    public static IndexedContainer getFridContainer() {

        String[] visibleCols = new String[]{"endpoint"};
        IndexedContainer result = new IndexedContainer();

        for (String p : visibleCols) {
            result.addContainerProperty(p, String.class, "");
        }

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
        comboBoxSparql.setInputPrompt("http://example:8894/sparql");
        comboBoxSparql
                .setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);

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
                        final Item item = comboBoxSparql
                                .getItem(newItemCaption);
                        item.getItemProperty("endpoint").setValue(
                                newItemCaption);
                        comboBoxSparql.setValue(newItemCaption);
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
        // griddata.insertElementAt(newText, index);
    }

    private void saveEditedTexts() {
        griddata = new LinkedList<>();
        for (TextField editText : listedEditText) {
            griddata.add(editText.getValue());
        }
    }

    // @SuppressWarnings("serial")
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
            textFieldGraph.setInputPrompt("http://ld.opendata.cz/source1");

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
            gridLayoutGraph.setComponentAlignment(buttonGraphRem,
                    Alignment.TOP_RIGHT);
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

    }

    private VerticalLayout buildVerticalLayoutDetails() {
        // common part: create layout
        verticalLayoutDetails = new VerticalLayout();
        verticalLayoutDetails.setImmediate(false);
        verticalLayoutDetails.setWidth("100%");
        verticalLayoutDetails.setHeight("-1px");
        verticalLayoutDetails.setMargin(true);
        verticalLayoutDetails.setSpacing(true);

        // gridLayoutConstr
        gridLayoutConstr = buildGridLayoutConstr();
        verticalLayoutDetails.addComponent(gridLayoutConstr);

        // labelOpt
        labelOpt = new Label();
        labelOpt.setImmediate(false);
        labelOpt.setWidth("-1px");
        labelOpt.setHeight("-1px");
        labelOpt.setValue("Options:");
        verticalLayoutDetails.addComponent(labelOpt);

        // checkBoxFail
        checkBoxFail = new CheckBox();
        checkBoxFail
                .setCaption("Extraction fails if there is no triple extracted.");
        checkBoxFail.setImmediate(false);
        checkBoxFail.setWidth("-1px");
        checkBoxFail.setHeight("-1px");
        verticalLayoutDetails.addComponent(checkBoxFail);

        return verticalLayoutDetails;
    }

    private GridLayout buildGridLayoutConstr() {
        // common part: create layout
        gridLayoutConstr = new GridLayout();
        gridLayoutConstr.setImmediate(false);
        gridLayoutConstr.setWidth("100%");
        gridLayoutConstr.setHeight("-1px");
        gridLayoutConstr.setMargin(false);
        gridLayoutConstr.setSpacing(true);
        gridLayoutConstr.setColumns(2);
        gridLayoutConstr.setColumnExpandRatio(0, 0.20f);
        gridLayoutConstr.setColumnExpandRatio(1, 0.80f);

        // labelConstr
        labelConstr = new Label();
        labelConstr.setImmediate(false);
        labelConstr.setWidth("100%");
        labelConstr.setHeight("-1px");
        labelConstr.setValue("SPARQL  Construct:");
        gridLayoutConstr.addComponent(labelConstr, 0, 0);

        // textAreaConstr
        textAreaConstr = new TextArea();
        textAreaConstr.setNullRepresentation("");
        textAreaConstr.setImmediate(false);
        textAreaConstr.setWidth("100%");
        textAreaConstr.setHeight("190px");
        textAreaConstr.setInputPrompt("CONSTRUCT {<http://dbpedia.org/resource/Prague> ?p ?o} where {<http://dbpedia.org/resource/Prague> ?p ?o } LIMIT 100");
        gridLayoutConstr.addComponent(textAreaConstr, 1, 0);

        return gridLayoutConstr;
    }
}
