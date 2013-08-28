package cz.cuni.xrg.intlib.frontend.gui.components;

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

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibHelper;
import cz.cuni.xrg.intlib.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.xrg.intlib.frontend.auxiliaries.dpu.DPUInstanceWrap;
import cz.cuni.xrg.intlib.rdf.exceptions.SPARQLValidationException;
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
	/**
	 * DPU's configuration dialog.
	 */
	private AbstractConfigDialog<DPUConfigObject> confDialog;

	/**
	 * Basic constructor, takes DPUInstance which detail should be showed.
	 *
	 * @param dpu {@link DPUInstanceRecord} which detail will be showed.
	 */
	public DPUDetail(DPUInstanceRecord dpu) {

		this.setResizable(false);
		this.setModal(true);
		this.dpuInstance = new DPUInstanceWrap(dpu);
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
		dpuName.setWidth("200px");
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
		dpuGeneralSettingsLayout.addComponent(dpuName, 1, 0);

		Label descriptionLabel = new Label("Description");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(descriptionLabel, 0, 1);

		dpuDescription = new TextArea();
		dpuDescription.setImmediate(false);
		dpuDescription.setWidth("400px");
		dpuDescription.setHeight("60px");
		dpuDescription.setValue(dpu.getDescription().trim());
		dpuDescription.addValidator(new MaxLengthValidator(255));
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
			Notification.show("Failed to load DPU.", e.getMessage(),
					Type.ERROR_MESSAGE);
		}


		if (confDialog == null) {
		} else {
			// configure
			try {
				dpuInstance.configuredDialog();
			} catch (ConfigException e) {
				Notification.show(
						"Failed to load configuration. The dialog defaul configuration is used.",
						e.getMessage(), Type.WARNING_MESSAGE);
				LOG.error("Failed to load configuration for {}", dpuInstance
						.getDPUInstanceRecord().getId(), e);
			}
			// add to layout
			confDialog.setWidth("100%");
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
					close();
				}
			}
		});
		buttonBar.addComponent(saveAndCommitButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
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
			dpuInstance.getDPUInstanceRecord().setName(dpuName.getValue().trim());
			dpuInstance.getDPUInstanceRecord().setDescription(dpuDescription
					.getValue().trim());
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
				Throwable rootCause = IntlibHelper.findFinalCause(ce);
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
			DPUTemplateRecord newDPU = App.getDPUs().creatTemplateFromInstance(
					dpuInstance.getDPUInstanceRecord());
			dpuInstance.getDPUInstanceRecord().setTemplate(newDPU);
			App.getDPUs().save(newDPU);
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
}
