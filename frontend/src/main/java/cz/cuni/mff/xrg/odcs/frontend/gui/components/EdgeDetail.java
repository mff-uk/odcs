package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.cuni.mff.xrg.odcs.commons.app.data.EdgeCompiler;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Window showing Edge detail. Currently only corresponding DataUnit can be named.
 *
 * @author Bogo
 */
public class EdgeDetail extends Window {

	private final static Logger LOG = LoggerFactory.getLogger(EdgeDetail.class);

	private final Edge edge;
	
	private List<String> outputUnits;
	private List<String> inputUnits;
	private List<MutablePair<List<Integer>, Integer>> mappings;

	private ListSelect outputSelect;
	private ListSelect inputSelect;
	private ListSelect mappingsSelect;
	
	private HashMap<String, MutablePair<List<Integer>, Integer>> map;
	
	/**
	 * Class for working with edge's script.
	 */
	private EdgeCompiler edgeCompiler = new EdgeCompiler(App.getApp().getModules());

	/**
	 * Basic constructor, takes {@link Edge} which detail should be showed.
	 *
	 * @param edge {@link Edge} which detail will be showed.
	 */
	public EdgeDetail(Edge e) {
		this.map = new HashMap<>();
		this.setResizable(false);
		this.setModal(true);
		this.edge = e;
		this.setCaption("Edge detail");

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);

		GridLayout edgeSettingsLayout = new GridLayout(3, 10);
		edgeSettingsLayout.setSpacing(true);
		
		outputSelect = new ListSelect("Output data units of the source DPU:");
		outputSelect.setMultiSelect(true);
		outputSelect.setNewItemsAllowed(false);
		outputSelect.setWidth(250, Unit.PIXELS);
		outputSelect.setImmediate(true);
		outputSelect.setRows(10);
		outputUnits = edgeCompiler.getOutputNames(edge.getFrom().getDpuInstance()); 
				
		for(String unit : outputUnits) {
			outputSelect.addItem(unit);
		}
		edgeSettingsLayout.addComponent(outputSelect, 0, 0, 0, 4);
		
		
		inputSelect = new ListSelect("Input data units of the target DPU:");
		inputSelect.setMultiSelect(false);
		inputSelect.setWidth(250, Unit.PIXELS);
		inputSelect.setNewItemsAllowed(false);
		inputSelect.setNullSelectionAllowed(false);
		inputSelect.setImmediate(true);
		inputSelect.setRows(10);
		inputUnits = edgeCompiler.getInputNames(edge.getTo().getDpuInstance()); 

		for(String unit : inputUnits) {
			inputSelect.addItem(unit);
		}
		edgeSettingsLayout.addComponent(inputSelect, 1, 0, 1, 4);
		Button mapButton = new Button("Map", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				//Iterate whole collection to preserve order and identify same mapping with different order of selecting.
				Set<String> outputs = new HashSet<>(); //Set<String>)outputSelect.getValue();
				Collection<String> outputItems = (Collection<String>)outputSelect.getItemIds();
				for(String outputItem : outputItems) {
					if(outputSelect.isSelected(outputItem)) {
						outputs.add(outputItem);
					}
				}
				String input = (String)inputSelect.getValue();
				if(outputs.isEmpty() || input == null) {
					Notification.show("At least one output and exactly one input must be selected!", Notification.Type.ERROR_MESSAGE);
					return;
				}
				
				List<Integer> left = new ArrayList<>(outputs.size());
				for(String output : outputs) {
					left.add(outputUnits.indexOf(output));
				}
				MutablePair<List<Integer>, Integer> mapping = new MutablePair<>(left, inputUnits.indexOf(input));
				if(addMappingToList(mapping)) {
					mappings.add(mapping);
				}
			}
		});
		mapButton.setWidth(130, Unit.PIXELS);
		edgeSettingsLayout.addComponent(mapButton, 2, 1);
		
		Button clearButton = new Button("Clear selection", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				outputSelect.setValue(null);
				inputSelect.setValue(null);
			}
		});
		clearButton.setWidth(130, Unit.PIXELS);
		edgeSettingsLayout.addComponent(clearButton, 2, 2);
		
		mappingsSelect = new ListSelect("Available mappings:");
		mappingsSelect.setStyleName("select-hide-tb");
		mappingsSelect.setWidth(500, Unit.PIXELS);
		mappingsSelect.setMultiSelect(true);
		mappingsSelect.setNewItemsAllowed(false);
		mappingsSelect.setImmediate(true);
		// inputUnits and outputUnits are already set !
		mappings = edgeCompiler.decompileMapping(edge.getScript(), outputUnits, inputUnits); 
				
		for(MutablePair<List<Integer>, Integer> mapping : mappings) {
			addMappingToList(mapping);
		}
		edgeSettingsLayout.addComponent(mappingsSelect, 0, 5, 1, 9);
		
		Button deleteButton = new Button("Delete mapping", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				Set<String> selectedMappings = (Set<String>)mappingsSelect.getValue();
				for(String strMapping : selectedMappings) {
					MutablePair<List<Integer>, Integer> mapping = map.get(strMapping);
					map.remove(strMapping);
					mappingsSelect.removeItem(strMapping);
					mappings.remove(mapping);
				}
			}
		});
		deleteButton.setWidth(130, Unit.PIXELS);
		edgeSettingsLayout.addComponent(deleteButton, 2, 6);
		mainLayout.addComponent(edgeSettingsLayout);

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setSpacing(true);
		buttonBar.setWidth(100, Unit.PERCENTAGE);
		buttonBar.setMargin(new MarginInfo(true, false, false, false));
		
		Button cancelButton = new Button("Cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		cancelButton.setWidth(100, Unit.PIXELS);
		buttonBar.addComponent(cancelButton);

		Button saveAndCommitButton = new Button("Save",
				new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (save()) {
					close();
				}
			}
		});
		saveAndCommitButton.setWidth(100, Unit.PIXELS);
		buttonBar.addComponent(saveAndCommitButton);

		Label placeFiller = new Label(" ");
		buttonBar.addComponentAsFirst(placeFiller);
		buttonBar.setExpandRatio(placeFiller, 1.0f);

//		Button removeNamingButton = new Button("Remove Named Data Unit",
//				new Button.ClickListener() {
//			@Override
//			public void buttonClick(Button.ClickEvent event) {
//				ConfirmDialog.show(UI.getCurrent(),
//						"Really remove named data unit?",
//						new ConfirmDialog.Listener() {
//					@Override
//					public void onClose(ConfirmDialog cd) {
//						if (cd.isConfirmed()) {
//							edge.setDataUnitName(null);
//							close();
//						}
//					}
//				});
//			}
//		});
//		buttonBar.addComponent(removeNamingButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

	/**
	 * Saves configuration of Edge which was set in detail dialog.
	 *
	 * @return True if save was successful, false otherwise.
	 */
	protected boolean save() {
			if (!validate()) {
				return false;
			}
			String script = 
					edgeCompiler.compileScript(mappings, outputUnits, inputUnits);			
			edge.setScript(script);			
			return true;
	}

	
	private boolean validate() {
//		try {
//			edgeName.validate();
//		} catch (Validator.InvalidValueException e) {
//			Notification.show("Error saving Edge configuration. Reason:", e.getMessage(), Notification.Type.ERROR_MESSAGE);
//			return false;
//		}
		return true;
	}

	private boolean addMappingToList(MutablePair<List<Integer>, Integer> mapping) throws UnsupportedOperationException {
		Iterator<Integer> iter = mapping.left.iterator();
		String leftSide = outputUnits.get(iter.next());
		while(iter.hasNext()) {
			leftSide += ", " + outputUnits.get(iter.next());
		}
		String strMapping = String.format("%s -> %s", leftSide, inputUnits.get(mapping.right));
		map.put(strMapping, mapping);
		Item result = mappingsSelect.addItem(strMapping);
		return result != null;
	}
}
