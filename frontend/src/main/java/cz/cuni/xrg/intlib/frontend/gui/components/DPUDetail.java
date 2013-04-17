/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.auxiliaries.ModuleDialogGetter;
import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;

/**
 *
 * @author Bogo
 */
public class DPUDetail extends Window {

	private DPUInstance dpu;
	private DPUExecutive dpuExec;
	private TextField dpuName;
	private TextArea dpuDescription;

	public DPUDetail(DPUInstance dpu) {

		this.setModal(true);
		this.dpu = dpu;
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

		mainLayout.addComponent(dpuGeneralSettingsLayout);

		String jarPath = dpu.getDpu().getJarPath();
		//String jarPath = "file:///C:/Users/Bogo/intlib/intlib/module/target/module-0.0.1.jar";

		try {
			dpuExec = App.getApp().getModules().getInstance(jarPath);
			if (dpuExec != null) {
				CustomComponent dpuConfigurationDialog = ModuleDialogGetter.getDialog(dpuExec);
				dpuConfigurationDialog.setWidth("100%");
				mainLayout.addComponent(dpuConfigurationDialog);
			}
			Configuration conf = dpu.getInstanceConfig();
			if (conf != null) {
				dpuExec.setSettings(conf);
			}
		} catch (ModuleException me) {
			//TODO: Show info about failed load of custom part of dialog
		} catch (ConfigurationException ce) {
			//TODO: Show info about invalid saved config(should not happen -> validity check on save)
		}

		HorizontalLayout buttonBar = new HorizontalLayout();

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

    protected void saveDPUInstance() {
        dpu.setName(dpuName.getValue());
        dpu.setDescription(dpuDescription.getValue());




		this.setContent(mainLayout);
	}

	protected boolean saveDPUInstance() {

		try {
			Configuration conf = dpuExec.getSettings();
			dpu.setName(dpuName.getValue());
			dpu.setDescription(dpuDescription.getValue());
			dpu.setInstanceConfig(conf);
		} catch (ConfigurationException ce) {
			//TODO: Inform about invalid settings and do not close detail dialog
			return false;
		}
		return true;
	}
}
