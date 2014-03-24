package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.frontend.gui.validator.ValidatorFactory;

/**
 * Component for setting general information about DPU like name and
 * description.
 *
 * @author Škoda Petr
 */
public class DPUGeneralDetail extends CustomComponent {

	private final TextField dpuName;

	private final TextArea dpuDescription;

	/**
	 * True if the dialog content is read only.
	 */
	private boolean isReadOnly = false;	
	
	/**
	 * Used to report change of insight property.
	 */
	private ValueChangeListener valueChangeListener = null;
	
	public DPUGeneralDetail() {
		setWidth("100%");
		setHeight("-1px");
		// create subcomponents
		GridLayout mainLayout = new GridLayout(2, 2);
		mainLayout.setWidth("100%");
		mainLayout.setSpacing(true);
		mainLayout.setMargin(new MarginInfo(false, false, true, false));

		{
			// we have to set the width, so the expansion works corectely
			Label lbl = new Label("Name");
			lbl.setWidth("80px");
			mainLayout.addComponent(lbl, 0, 0);
		}

		dpuName = new TextField();
		//dpuName.setImmediate(false);
		dpuName.setWidth("100%");
		dpuName.setHeight(null);
		dpuName.setRequired(true);
		dpuName.setRequiredError("DPU name must be filled!");
		dpuName.addValidator(ValidatorFactory.CreateMaxLength("name",
				LenghtLimits.DPU_NAME.limit()));
		
		mainLayout.addComponent(dpuName, 1, 0);

		mainLayout.addComponent(new Label("Description"), 0, 1);
		dpuDescription = new TextArea();
		dpuDescription.setWidth("100%");
		dpuDescription.setHeight("60px");
		mainLayout.addComponent(dpuDescription, 1, 1);

		// expand column with the text boxes
		mainLayout.setColumnExpandRatio(1, 1.0f);

		// set root
		setCompositionRoot(mainLayout);
		
		// set on change listener
		ValueChangeListener changeListener = new ValueChangeListener() {

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				// just recall if set
				if (valueChangeListener != null && !isReadOnly) {
					valueChangeListener.valueChange(event);
				}
			}
		};
		
		dpuName.addValueChangeListener(changeListener);
		dpuDescription.addValueChangeListener(changeListener);
	}

	/**
	 * Set listener that is called in case of change of any property.
	 * 
	 * @param valueChangeListener 
	 */
	public void setValueChangeListener(ValueChangeListener valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}	
	
	/**
	 * Set values in component from the given {@link DPURecord}.
	 *
	 * @param dpu
	 * @param readOnly True if the component should be read only.
	 */
	public void loadFromDPU(DPURecord dpu, boolean readOnly) {
		this.isReadOnly = readOnly;
		
		dpuName.setValue(dpu.getName());
		if (dpu.useDPUDescription()) {
			// leave dpuDescription blank
			dpuDescription.setValue("");
		} else {
			dpuDescription.setValue(dpu.getDescription().trim());
		}
		dpuName.setEnabled(!readOnly);
		dpuDescription.setEnabled(!readOnly);
	}

	/**
	 * Save the values from component into the given {@link DPURecord}.
	 *
	 * @param dpu
	 */
	public void saveToDPU(DPURecord dpu) {
		String userDescription = dpuDescription.getValue().trim();
		if (userDescription.isEmpty()) {
			String dialogDescription = dpu.getDescription();
			if (dialogDescription == null) {
				// dialog description is not supported .. we have no 
				// description at all
				dpu.setDescription("");
				dpu.setUseDPUDescription(false);
			} else {
				// use dialogDescription
				dpu.setDescription(dialogDescription);
				dpu.setUseDPUDescription(true);
			}
		} else {
			// use user provided description
			dpu.setDescription(dpuDescription.getValue().trim());
			dpu.setUseDPUDescription(false);
		}
		dpu.setName(dpuName.getValue().trim());
	}

	/**
	 *
	 * @param dpu
	 * @return True if the data in component differ from those in given
	 *         {@link DPURecord}.
	 */
	public boolean isChanged(DPURecord dpu) {
		return !dpuName.getValue().equals(dpu.getName())
				|| !dpuDescription.getValue().equals(dpu.getDescription());
	}

	/**
	 * Validate the data in the component.
	 *
	 * @return False is the data are invalid.
	 */
	public boolean validate() {
		try {
			dpuName.validate();
			dpuDescription.validate();
		} catch (Validator.InvalidValueException e) {
			Notification.show("Error saving DPU configuration. Reason:", e
					.getMessage(), Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
