package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.DecorationHelper;
import cz.cuni.mff.xrg.odcs.frontend.dpu.wrap.DPUInstanceWrap;
import cz.cuni.mff.xrg.odcs.frontend.dpu.wrap.DPUWrapException;
import cz.cuni.mff.xrg.odcs.frontend.gui.dialog.ComponentDialogWrap;
import cz.cuni.mff.xrg.odcs.frontend.gui.dialog.DialogReady;
import cz.cuni.mff.xrg.odcs.frontend.gui.validator.ValidatorFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.SPARQLValidationException;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Detail of selected DPU. Consists of common properties, name and description
 * and configuration dialog specific for DPU, which is loaded from DPU's jar
 * file.
 *
 * @author Škoda Petr
 * @author Bogo
 */
public class DPUDetail extends Window {

	private final static Logger LOG = LoggerFactory.getLogger(DPUDetail.class);

	/**
	 * Current DPU instance.
	 */
	private DPUInstanceWrap dpuInstance;

	private final DPUFacade dpuFacade;

	private TextField dpuName;

	private TextArea dpuDescription;

	private Button btnSaveAsNew;

	private Button btnSaveAndCommit;

	private Button btnCancel;

	/**
	 * Panel for DPU configuration dialog.
	 */
	private Panel dpuDetailPanel;

	private boolean result;
	
	/**
	 * Basic constructor, takes DPUFacade. In order to generate the layout call
	 * {@link #build()}. The build function has to be called before any other
	 * function.
	 *
	 * @param dpuFacade
	 */
	public DPUDetail(DPUFacade dpuFacade) {
		this.dpuFacade = dpuFacade;
	}

	/**
	 * Construct page layout.
	 */
	public void build() {

		// <editor-fold defaultstate="collapsed" desc="General detail">
		GridLayout generalSettings = new GridLayout(2, 2);
		generalSettings.setWidth("100%");
		generalSettings.setSpacing(true);
		generalSettings.setMargin(new MarginInfo(false, false, true, false));

		{
			// we have to set the width, so the expansion works corectely
			Label lbl = new Label("Name");
			lbl.setWidth("80px");
			generalSettings.addComponent(lbl, 0, 0);
		}

		dpuName = new TextField();
		//dpuName.setImmediate(false);
		dpuName.setWidth("100%");
		dpuName.setHeight(null);
		dpuName.setRequired(true);
		dpuName.setRequiredError("DPU name must be filled!");
		dpuName.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				setCaption(dpuName.getValue().trim());
			}
		});
		dpuName.addValidator(ValidatorFactory.CreateMaxLength("name",
				LenghtLimits.DPU_NAME.limit()));

		generalSettings.addComponent(dpuName, 1, 0);

		generalSettings.addComponent(new Label("Description"), 0, 1);
		dpuDescription = new TextArea();
		dpuDescription.setWidth("100%");
		dpuDescription.setHeight("60px");
		generalSettings.addComponent(dpuDescription, 1, 1);

		generalSettings.setColumnExpandRatio(1, 1.0f);
		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="DPU detail part">
		// panel for DPU detail dilog
		Panel dpuDetail = new Panel();
		dpuDetail.setHeight("100%");
		dpuDetail.setWidth("100%");
		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Botton button line">
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));
		buttonBar.setSpacing(true);
		buttonBar.setWidth("100%");

		btnSaveAndCommit = new Button("Save");
		btnSaveAndCommit.setWidth("90px");
		buttonBar.addComponent(btnSaveAndCommit);

		btnCancel = new Button("Cancel");
		btnCancel.setWidth("90px");
		buttonBar.addComponent(btnCancel);

		btnSaveAsNew = new Button("Save as DPU template");
		btnSaveAsNew.setWidth("160px");
		buttonBar.addComponent(btnSaveAsNew);
		buttonBar.setExpandRatio(btnSaveAsNew, 1.0f);
		buttonBar.setComponentAlignment(btnSaveAsNew, Alignment.MIDDLE_RIGHT);
		// </editor-fold>

		// <editor-fold desc="Assign actions">
		btnSaveAndCommit.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (saveDPUInstance()) {
					result = true;
					close();
				}
			}
		});

		btnCancel.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				result = false;
				close();
			}
		});

		btnSaveAsNew.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(),
						"Save as new DPU template?",
						new ConfirmDialog.Listener() {
							@Override
							public void onClose(ConfirmDialog cd) {
								if (cd.isConfirmed() && saveDpuAsNew()) {
									result = true;
									close();
								}
							}
						});
			}
		});

		// </editor-fold>
		
		// <editor-fold defaultstate="collapes" desc="Set the main layout">
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");

		mainLayout.addComponent(generalSettings);
		mainLayout.setExpandRatio(generalSettings, 0.0f);

		mainLayout.addComponent(dpuDetail);
		mainLayout.setExpandRatio(dpuDetail, 1.0f);

		mainLayout.addComponent(buttonBar);
		mainLayout.setExpandRatio(buttonBar, 0.0f);

		setContent(mainLayout);
		// </editor-fold>
	}

	/**
	 * Show DPU detail.
	 *
	 * @param dpu
	 * @param readOnly
	 */
	public void showDpuDetail(DPUInstanceRecord dpu, boolean readOnly) {
		this.dpuInstance = new DPUInstanceWrap(dpu, dpuFacade);
		this.setCaption(String.format("%s detail%s", dpu.getName().trim(),
				readOnly ? " - Read only mode" : ""));
		dpuName.setValue(dpu.getName());
		if (dpu.useDPUDescription()) {
			// leave dpuDescription blank
			dpuDescription.setValue("");
		} else {
			dpuDescription.setValue(dpu.getDescription().trim());
		}
		btnSaveAndCommit.setEnabled(!readOnly);
		btnSaveAsNew.setEnabled(!readOnly);

//		// load instance
//		if (confDialog != null) {
////#			mainLayout.removeComponent(confDialog);
//		}
		Component confDialog = null;
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
			Notification.show("Failed to load DPU,", ex.getMessage(),
					Type.ERROR_MESSAGE);
		}
		// TODO We may want to show some default label with information
		//	instead of the config dialog

		if (confDialog == null) {
			// DPU does not support configuration
		} else {
			// configure the DPU
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
				LOG.error("Unexpected error while loading dialog for {}",
						dpuInstance.getDPUInstanceRecord().getId(), e);
			}
			// add to the component
			confDialog.setWidth("100%");
			confDialog.setHeight("100%");

			dpuDetailPanel.setContent(confDialog);
		}
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
			//Can cause Exception, so should be before any actual saving.
			dpuInstance.saveConfig();

			String userDescription = dpuDescription.getValue().trim();
			if (userDescription.isEmpty()) {
				String dialogDescription = dpuInstance.getDescription();
				if (dialogDescription == null) {
					// dialog description is not supported .. we have no 
					// description at all
					dpuInstance.getDPUInstanceRecord().setDescription("");
					dpuInstance.getDPUInstanceRecord().setUseDPUDescription(
							false);
				} else {
					// use dialogDescription
					dpuInstance.getDPUInstanceRecord().setDescription(
							dialogDescription);
					dpuInstance.getDPUInstanceRecord()
							.setUseDPUDescription(true);
				}
			} else {
				// use user provided description
				dpuInstance.getDPUInstanceRecord().setDescription(dpuDescription
						.getValue().trim());
				dpuInstance.getDPUInstanceRecord().setUseDPUDescription(false);
			}

			dpuInstance.getDPUInstanceRecord()
					.setName(dpuName.getValue().trim());

		} catch (Exception ce) {
			if (ce instanceof SPARQLValidationException) {

				SPARQLValidationException validationEx = (SPARQLValidationException) ce;

				Notification.show("Query Validator",
						"Validation of " + validationEx.getQueryNumber() + ". query failed: "
						+ validationEx.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			} else if (ce instanceof ConfigException) {
				Notification.show("Failed to save configuration. Reason:", ce
						.getMessage(), Type.ERROR_MESSAGE);
			} else {
				Throwable rootCause = DecorationHelper.findFinalCause(ce);
				String text = String.format("Exception: %s, Message: %s",
						rootCause.getClass().getName(), rootCause.getMessage());
				Notification.show(
						"Method for storing configuration threw exception:",
						text, Type.ERROR_MESSAGE);
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
			Notification.show("Error saving DPU configuration. Reason:", e
					.getMessage(), Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * True in case that the dialog save some changes.
	 * 
	 * @return 
	 */
	public boolean isResult() {
		return result;
	}
	
}
