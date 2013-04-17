package gui;

import module.Config;

import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.commons.configuration.*;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/**
 * Configuration dialog.
 * @author Petyr
 *
 */
public class ConfigDialog extends CustomComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * TODO Implement your own configuration component. You can use vaadin visual editor if you like.
	 * Just remember don't use vaddin classes the ere not located directly in package com.vaadi.ui;
	 */

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
	int n=1;

	public ConfigDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}
	private void mapData() {

		optionGroupDetail.addItem("Overwrite target graph");
		optionGroupDetail.addItem("Merge with target graph's content");
		optionGroupDetail.addItem("Fail if the target graph exists");
		optionGroupDetail.setValue("Overwrite target graph");
	}
	/**
	 * Return current configuration from dialog. Can return null, if
	 * current configuration is invalid.
	 * @return current configuration or null
	 */
	public Configuration getConfiguration() {
		Configuration config = new Configuration();
		/**
		 * TODO Gather data from you dialog and store them into configuration. You can use
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 * 	Also remember that you can return null in case of invalid configuration in dialog.
		 */

		//config.setValue(Config.DPU_name.name(), textFieldName.getValue());
		//config.setValue(Config.Description.name(), textAreaDesc.getValue());
		config.setValue(Config.SPARQL_endpoint.name(), comboBoxSparql.getValue());
		config.setValue(Config.Host_name.name(), textFieldNameAdm.getValue());
		config.setValue(Config.Password.name(), passwordFieldPass.getValue());
		config.setValue(Config.GraphsUri.name(), griddata.getValue());

		return config;
	}

	/**
	 * Load values from configuration into dialog.
	 * @throws ConfigurationException
	 * @param conf
	 */
	public void setConfiguration(Configuration conf) {
		/**
		 * TODO Load configuration from conf into dialog components. You can use
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 *  The ConfigurationException can be thrown in case of invalid configuration.
		 */

		try
		{
		//	textFieldName.setValue( (String) conf.getValue(Config.DPU_name.name()));
		//	textAreaDesc.setValue( (String) conf.getValue(Config.Description.name()));
			comboBoxSparql.setValue( (String) conf.getValue(Config.SPARQL_endpoint.name()));
			textFieldNameAdm.setValue( (String) conf.getValue(Config.Host_name.name()));
			passwordFieldPass.setValue( (String) conf.getValue(Config.Password.name()));
		//	griddata.setValue( (List<String>) conf.getValue(Config.GraphsUri.name()));
			List<String> griddata = (List<String>)conf.getValue(Config.GraphsUri.name());

		}
		catch(Exception ex) {
			// throw setting exception
			throw new ConfigurationException();
		}
	}

	public static IndexedContainer getFridContainer() {

		String[] endpoint = { "http://example:8894/sparql" };
		String[] visibleCols = new String[] {  "endpoint" };

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
			result.addContainerProperty(p, String.class, "");
		}


		for (int i = 0; i < endpoint.length ; i++) {
			Object num = result.addItem();
			result.getContainerProperty(num, "endpoint").setValue(endpoint[i]);

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
			mainLayout.addComponent(tabSheet,  0, 0);
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
			comboBoxSparql.setNullSelectionAllowed(false);
			comboBoxSparql.setTextInputAllowed(true);

			comboBoxSparql.setItemCaptionPropertyId("endpoint");
			comboBoxSparql.setItemCaptionMode(ItemCaptionMode.PROPERTY);

			comboBoxSparql.setFilteringMode(FilteringMode.CONTAINS);
			comboBoxSparql.setImmediate(true);


		        // Disallow null selections
			comboBoxSparql.setNullSelectionAllowed(false);

		        // Check if the caption for new item already exists in the list of item
		        // captions before approving it as a new item.

			comboBoxSparql.setNewItemHandler(new NewItemHandler() {
	           
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

			comboBoxSparql.addValueChangeListener(new Property.ValueChangeListener() {

				
				public void valueChange(Property.ValueChangeEvent event) {
					// TODO Auto-generated method stub
					 final String valueString = String.valueOf(event.getProperty()
		                        .getValue());
		                Notification.show("Value changed:", valueString,
		                        Type.TRAY_NOTIFICATION);
				}
			});
			//comboBoxSparql.setInputPrompt(inputPrompt);
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
			textFieldNameAdm.setImmediate(false);
			textFieldNameAdm.setWidth("100%");
			textFieldNameAdm.setHeight("-1px");
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
			passwordFieldPass.setImmediate(false);
			passwordFieldPass.setWidth("100%");
			passwordFieldPass.setHeight("-1px");
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


		private static List<String> griddata = initializeGridData();
		private static List<String> initializeGridData()
		{
			List<String> result = new LinkedList<String>();
			result.add("Some Item 1");
		//	result.add("Some Item 2");
			return result;

		}
		private static void addDataToGridData(String newData)
		{
			griddata.add(newData);
		}

		private static void removeDataFromGridData(Integer row)
		{
			int index=  row;
			if(griddata.size()>1){
				griddata.remove(index);
			}
		}
		private static void replaceText(int index, String newText)
		{
			griddata.remove(index);
			griddata.add(index, newText);
			//griddata.insertElementAt(newText, index);
		}

		private void refreshNamedGraphData()
		{
			gridLayoutGraph.removeAllComponents();
			gridLayoutGraph.setRows(griddata.size()+1);
			int row = 0;

			for (String item : griddata) {
				textFieldGraph = new TextField();
				textFieldGraph.setWidth("100%");
				textFieldGraph.setData(row);
				textFieldGraph.setValue(item);
				textFieldGraph.addListener(new TextChangeListener() {

					
					public void textChange(TextChangeEvent event) {
						TextField tf = (TextField)event.getComponent();
						Integer index = (Integer)tf.getData();
						replaceText(index,event.getText());
					}
				});
				buttonGraphRem = new Button();
				buttonGraphRem.setWidth("55px");
				buttonGraphRem.setCaption("-");
				buttonGraphRem.setData(row);
				buttonGraphRem.addClickListener(new ClickListener() {

					
					public void buttonClick(ClickEvent event) {
						Button senderButton = event.getButton();
						Integer row =  (Integer)senderButton.getData();
						removeDataFromGridData(row);
						refreshNamedGraphData();
					}
				});
				gridLayoutGraph.addComponent(textFieldGraph,0,row);
				gridLayoutGraph.addComponent(buttonGraphRem,1,row);
				gridLayoutGraph.setComponentAlignment(buttonGraphRem, Alignment.TOP_RIGHT);
				row++;
			}
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

			// buttonGraphAdd
			buttonGraphAdd = new Button();
			buttonGraphAdd.setCaption("+");
			buttonGraphAdd.setImmediate(true);
			buttonGraphAdd.setWidth("55px");
			buttonGraphAdd.setHeight("-1px");
			buttonGraphAdd.addListener(new ClickListener() {

				
				public void buttonClick(ClickEvent event) {
					addDataToGridData("New data");
					refreshNamedGraphData();
				}
			});

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
			verticalLayoutDetails.addComponent(optionGroupDetail);

			return verticalLayoutDetails;
		}
}
