package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.dpu.wrap.DPURecordWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component for setting general information about DPU like name and
 * description.
 *
 * @author Škoda Petr
 */
public class DPUGeneralDetail extends CustomComponent {

	private static final Logger LOG = LoggerFactory.getLogger(
			DPUGeneralDetail.class);
	
	private final TextField dpuName;
	
	private final TextField dpuTemplateName;
	
	private final Label dpuTemplateNameLabel;

	private final TextArea dpuDescription;

	private final CheckBox useUserDescription;
	
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
		GridLayout mainLayout = new GridLayout(2, 4);
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
		dpuName.addValidator(new MaxLengthValidator(LenghtLimits.DPU_NAME));
		mainLayout.addComponent(dpuName, 1, 0);

		dpuTemplateNameLabel = new Label("Parent");
		mainLayout.addComponent(dpuTemplateNameLabel, 0, 1);
		dpuTemplateName = new TextField();
		dpuTemplateName.setWidth("100%");
		dpuTemplateName.setHeight(null);
		dpuTemplateName.setEnabled(false);
		mainLayout.addComponent(dpuTemplateName, 1, 1);
		
		mainLayout.addComponent(new Label("Description"), 0, 2);
		dpuDescription = new TextArea();
		dpuDescription.setWidth("100%");
		dpuDescription.setHeight("60px");
		mainLayout.addComponent(dpuDescription, 1, 2);

		mainLayout.addComponent(new Label("Use custom description"), 0, 3);
		useUserDescription = new CheckBox();
		useUserDescription.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				final boolean useUserDesc = useUserDescription.getValue();
				dpuDescription.setEnabled(useUserDesc);
			}
		});
		mainLayout.addComponent(useUserDescription, 1, 3);
		
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
		useUserDescription.addValueChangeListener(changeListener);
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
		
		if (dpu instanceof DPUInstanceRecord) {
			DPUInstanceRecord instance = (DPUInstanceRecord)dpu;
			// use template name
			if (instance.getTemplate() == null) {
				dpuTemplateName.setValue("no parent is set!");
				LOG.error("No parent for dpu instance id: {}", dpu.getId());
			} else {
				dpuTemplateName.setValue(instance.getTemplate().getName());
			}

			dpuTemplateName.setVisible(true);
			dpuTemplateNameLabel.setVisible(true);

		} else {
			dpuTemplateName.setVisible(false);
			dpuTemplateNameLabel.setVisible(false);
		}
				
		if (dpu.useDPUDescription()) {
			// generated description used
			dpuDescription.setValue("");
			dpuDescription.setEnabled(false);
		} else {
			dpuDescription.setValue(dpu.getDescription().trim());
			// we should be enabled, but we also have to respec readOnly
			dpuDescription.setEnabled(!readOnly);
		}
		useUserDescription.setValue(!dpu.useDPUDescription());
				
		dpuName.setEnabled(!readOnly);		
		useUserDescription.setEnabled(!readOnly);
	}

	/**
	 * Save the values from component into the given {@link DPURecord}.
	 *
	 * @param dpu
	 * @param wrap
	 */
	public void saveToDPU(DPURecord dpu, DPURecordWrap wrap) {
		if (useUserDescription.getValue()) {
			// use user description
			final String userDescription = dpuDescription.getValue().trim();
			dpu.setDescription(userDescription);
			dpu.setUseDPUDescription(false);
		} else {
			final String generateDescription = wrap.getDescription();
			// if null then use empty, else use the value
			if (generateDescription == null) {
				dpu.setDescription("");
			} else {
				dpu.setDescription(generateDescription);
			}
			dpu.setUseDPUDescription(true);
		}
		
//		if (userDescription.isEmpty()) {
//			// get configuration from dialog
//			String dialogDescription = wrap.getDescription();
//			if (dialogDescription == null) {
//				// dialog description is not supported .. we have no 
//				// description at all
//				// so we use uset empty
//				dpu.setDescription("");
//				dpu.setUseDPUDescription(false);
//			} else {
//				// use dialogDescription - generate by configuration
//				// dialog
//				dpu.setDescription(dialogDescription);
//				dpu.setUseDPUDescription(true);
//			}
//		} else {
//			// use user provided description
//			dpu.setDescription(dpuDescription.getValue().trim());
//			dpu.setUseDPUDescription(true);
//		}
		
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
