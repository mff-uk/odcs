package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.DecorationHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUInstanceWrap;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUWrapException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.SPARQLValidationException;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Detail of selected DPU. Consists of common properties, name and description
 * and configuration dialog specific for DPU, which is loaded from DPU's jar
 * file.
 *
 * @author Bogo
 */
public class DPUDetail extends Window {

	private final static Logger LOG = LoggerFactory.getLogger(DPUDetail.class);
	private DPUInstanceWrap dpuInstance;
	private TextField dpuName;
	private TextArea dpuDescription;
	private boolean result = false;
	/**
	 * DPU's configuration dialog.
	 */
	private AbstractConfigDialog<DPUConfigObject> confDialog;
	private DPUFacade dpuFacade;

	/**
	 * Basic constructor, takes DPUInstance which detail should be showed.
	 *
	 * @param dpu {@link DPUInstanceRecord} which detail will be showed.
	 */
	public DPUDetail(DPUInstanceRecord dpu, DPUFacade dpuFacade) {
		this.dpuFacade = dpuFacade;
		this.setResizable(false);
		this.setModal(true);
		this.dpuInstance = new DPUInstanceWrap(dpu, dpuFacade);
		this.setCaption(String.format("%s detail", dpu.getName().trim()));

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);

		GridLayout dpuGeneralSettingsLayout = new GridLayout(2, 2);
		dpuGeneralSettingsLayout.setSpacing(true);

		Label nameLabel = new Label("Name");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(nameLabel, 0, 0);

		dpuName = new TextField();
		dpuName.setImmediate(false);
		dpuName.setWidth("280px");
		dpuName.setHeight("-1px");
		dpuName.setValue(dpu.getName().trim());
		dpuName.setRequired(true);
		dpuName.setRequiredError("DPU name must be filled!");
		dpuName.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				setCaption(dpuName.getValue().trim());
			}
		});
		dpuName.addValidator(new MaxLengthValidator(MaxLengthValidator.DPU_NAME_LENGTH));
		dpuGeneralSettingsLayout.addComponent(dpuName, 1, 0);

		Label descriptionLabel = new Label("Description");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(descriptionLabel, 0, 1);

		dpuDescription = new TextArea();
		dpuDescription.setImmediate(false);
		dpuDescription.setWidth("500px");
		dpuDescription.setHeight("60px");
		if (dpu.useDPUDescription()) {
			// leave dpuDescription blank
		} else {
			dpuDescription.setValue(dpu.getDescription().trim());
		}
		dpuDescription.addValidator(new MaxLengthValidator(MaxLengthValidator.DESCRIPTION_LENGTH));
		dpuGeneralSettingsLayout.addComponent(dpuDescription, 1, 1);

		dpuGeneralSettingsLayout.setMargin(new MarginInfo(false, false, true,
				false));
		mainLayout.addComponent(dpuGeneralSettingsLayout);

		// load instance
		confDialog = null;
		try {
			confDialog = dpuInstance.getDialog();
		} catch (ModuleException e) {
			Notification.show("Failed to load configuration dialog.", e
					.getMessage(), Type.ERROR_MESSAGE);
			LOG.error("Failed to load dialog for {}", dpuInstance
					.getDPUInstanceRecord().getId(), e);
		} catch (FileNotFoundException e) {
			Notification.show("Missing DPU jar file.", e.getMessage(),
					Type.ERROR_MESSAGE);
		} catch (DPUWrapException ex) {
			Notification.show("Failed to load DPU,", ex.getMessage(), Type.ERROR_MESSAGE);
		}


		if (confDialog == null) {
		} else {
			// configure
			try {
				dpuInstance.configuredDialog();
			} catch (ConfigException e) {
				Notification.show(
						"Configuration problem",
						e.getMessage(), Type.WARNING_MESSAGE);
				LOG.error("Problem with configuration for {}", dpuInstance
						.getDPUInstanceRecord().getId(), e);
			} catch (DPUWrapException e) {
				Notification.show(
						"Unexpected error. The configuration dialog may not be loaded correctly.",
						e.getMessage(), Type.WARNING_MESSAGE);
				LOG.error("Unexpected error while loading dialog for {}", dpuInstance.getDPUInstanceRecord().getId(), e);
			}
			// add to layout
			confDialog.setWidth("100%");

			setResizable(true);
			mainLayout.addComponent(confDialog);
		}

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveAndCommitButton = new Button("Save",
				new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (saveDPUInstance()) {
					result = true;
					close();
				}
			}
		});
		saveAndCommitButton.setWidth("90px");
		buttonBar.addComponent(saveAndCommitButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				result = false;
				close();
			}
		});
		cancelButton.setWidth("90px");
		buttonBar.addComponent(cancelButton);

		Label placeFiller = new Label(" ");
		buttonBar.addComponent(placeFiller);

		Button saveAsNewButton = new Button("Save as DPU template",
				new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(),
						"Save as new DPU template?",
						new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog cd) {
						if (cd.isConfirmed()) {
							if (saveDpuAsNew()) {
								result = true;
								close();
							}
						}
					}
				});
			}
		});
		buttonBar.addComponent(saveAsNewButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

	/**
	 * Saves configuration of DPURecord Instance which was set in detail dialog.
	 *
	 * @return True if save was successful, false otherwise.
	 */
	protected boolean saveDPUInstance() {
		try {
			if (!validate()) {
				return false;
			}

			String userDescription = dpuDescription.getValue().trim();
			if (userDescription.isEmpty()) {
				String dialogDescription = confDialog.getDescription();
				if (dialogDescription == null) {
					// dialog description is not supported .. we have no 
					// description at all
					dpuInstance.getDPUInstanceRecord().setDescription("");
					dpuInstance.getDPUInstanceRecord().setUseDPUDescription(false);
				} else {
					// use dialogDescription
					dpuInstance.getDPUInstanceRecord().setDescription(dialogDescription);
					dpuInstance.getDPUInstanceRecord().setUseDPUDescription(true);
				}
			} else {
				// use user provided description
				dpuInstance.getDPUInstanceRecord().setDescription(dpuDescription
						.getValue().trim());
				dpuInstance.getDPUInstanceRecord().setUseDPUDescription(false);
			}

			dpuInstance.getDPUInstanceRecord().setName(dpuName.getValue().trim());
			dpuInstance.saveConfig();
		} catch (Exception ce) {
			if (ce instanceof SPARQLValidationException) {

				Notification.show("Query Validator",
						"Query is not valid: "
						+ ce.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			} else if (ce instanceof ConfigException) {
				Notification.show("Failed to save configuration. Reason:", ce
						.getMessage(), Type.ERROR_MESSAGE);
			} else {
				Throwable rootCause = DecorationHelper.findFinalCause(ce);
				String text = String.format("Exception: %s, Message: %s", rootCause.getClass().getName(), rootCause.getMessage());
				Notification.show("Method for storing configuration threw exception:", text, Type.ERROR_MESSAGE);
			}
			return false;
		}
		return true;
	}

	/**
	 * Creates new DPU in tree with prefilled configuration taken from current
	 * configuration of this DPU.
	 *
	 * @return True if save was successful, false otherwise.
	 */
	protected boolean saveDpuAsNew() {
		if (saveDPUInstance()) {
			DPUTemplateRecord newDPU = dpuFacade.createTemplateFromInstance(
					dpuInstance.getDPUInstanceRecord());
			dpuInstance.getDPUInstanceRecord().setTemplate(newDPU);
			dpuFacade.save(newDPU);
			return true;
		}
		return false;
	}

	private boolean validate() {
		try {
			dpuName.validate();
			dpuDescription.validate();
		} catch (Validator.InvalidValueException e) {
			Notification.show("Error saving DPU configuration. Reason:", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	public boolean getResult() {
		return result;
	}
}
