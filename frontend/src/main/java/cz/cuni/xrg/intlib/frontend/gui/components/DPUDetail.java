/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.gui.components;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.dpu.DPUInstanceWrap;

/**
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
	private AbstractConfigDialog<Config> confDialog;

	/**
	 * Basic constructor, takes DPUInstance which detail should be showed.
	 * @param dpu
	 */
	public DPUDetail(DPUInstanceRecord dpu) {

		this.setResizable(false);
		this.setModal(true);
		this.dpuInstance = new DPUInstanceWrap(dpu);
		this.setCaption(String.format("%s detail", dpu.getName()));

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
		dpuName.setValue(dpu.getName());
		dpuName.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				setCaption(dpuName.getValue());
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
		dpuDescription.setValue(dpu.getDescription());
		dpuGeneralSettingsLayout.addComponent(dpuDescription, 1, 1);

		dpuGeneralSettingsLayout.setMargin(new MarginInfo(false, false, true, false));
		mainLayout.addComponent(dpuGeneralSettingsLayout);

		// load instance
		confDialog = null;
		try {
			confDialog = dpuInstance.getDialog();
		} catch (ModuleException e) {
			Notification.show("Failed to load configuration dialog.", e.getMessage(), Type.ERROR_MESSAGE);
			LOG.error("Failed to load dialog for {}", dpuInstance.getDPUInstanceRecord().getId(),  e);
		} catch(FileNotFoundException e) {
			Notification.show("Failed to load DPU.", e.getMessage(), Type.ERROR_MESSAGE);
		} catch(ConfigException e) {
			Notification.show("Failed to load configuration. The dialog defaul configuration is used.",	e.getMessage(), Type.WARNING_MESSAGE);
			LOG.error("Failed to load configuration for {}", dpuInstance.getDPUInstanceRecord().getId(),  e);
		}

		if (confDialog == null) {

		} else {
			// add to layout
			confDialog.setWidth("100%");
			mainLayout.addComponent(confDialog);
		}

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

//		Button saveButton = new Button("Develop", new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(Button.ClickEvent event) {
//				if(saveDPUInstance()) {
//					close();
//				}
//			}
//		});
//		buttonBar.addComponent(saveButton);

		Button saveAndCommitButton = new Button("Save", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if(saveDPUInstance()) {
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

		Button saveAsNewButton = new Button("Save to DPURecord tree", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if(saveDpuAsNew()) {
					close();
				}
			}
		});
		buttonBar.addComponent(saveAsNewButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

	/**
	 * Saves configuration of DPURecord Instance which was set in detail dialog.
	 * @return
	 */
	protected boolean saveDPUInstance() {
		dpuInstance.getDPUInstanceRecord().setName(dpuName.getValue());
		dpuInstance.getDPUInstanceRecord().setDescription(dpuDescription.getValue());
		try {
			dpuInstance.saveConfig();
		} catch (ConfigException ce) {
			Notification.show("ConfigException:", ce.getMessage(), Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	protected boolean saveDpuAsNew() {
		if(saveDPUInstance()) {
			DPUTemplateRecord newDPU = App.getDPUs().creatTemplateFromInstance(dpuInstance.getDPUInstanceRecord());
			App.getDPUs().save(newDPU);
			return true;
		}
		return false;
	}
}
