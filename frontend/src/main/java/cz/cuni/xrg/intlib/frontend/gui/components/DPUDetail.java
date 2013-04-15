/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.auxiliaries.ModuleDialogGetter;
import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;

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

        this.setCaption("DPU instance detail");
        this.setModal(true);
        this.dpu = dpu;

        VerticalLayout mainLayout = new VerticalLayout();

        GridLayout dpuGeneralSettingsLayout = new GridLayout(2,2);

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

        //TODO: Add custom configuration dialog from DPUInstance


		String jarPath = dpu.getJarPath();
		//String jarPath = "file:///C:/Users/Bogo/intlib/intlib/module/target/module-0.0.1.jar";

		dpuExec = App.getApp().getModules().getInstance(jarPath);
		if(dpuExec != null) {
			CustomComponent dpuConfigurationDialog = ModuleDialogGetter.getDialog(dpuExec);
			mainLayout.addComponent(dpuConfigurationDialog);
		}
		Configuration conf = dpu.getInstanceConfiguration();
		if(conf != null) {
			dpuExec.setSettings(conf);
		}

        Button saveButton = new Button("Save", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveDPUInstance();
                close();
            }


        });
        mainLayout.addComponent(saveButton);


        this.setContent(mainLayout);
    }

    protected void saveDPUInstance() {
        dpu.setName(dpuName.getValue());
        dpu.setDescription(dpuDescription.getValue());

		//TODO: Make this work - solve COnfiguration/Template/Instance ambiguity
		Configuration conf = dpuExec.getSettings();
		dpu.setInstanceConfiguration(conf);
    }

}

