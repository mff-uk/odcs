package cz.cuni.xrg.intlib.frontend.gui.views;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.auxiliaries.ModuleDialogGetter;
import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.Type;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.web.*;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class DPUDialog extends CustomComponent implements View {

	private VerticalLayout mainLayout;
	
	private DPUExecutive dpu;
	
	@Override	
	public void enter(ViewChangeEvent event) {
		String pathToBundle = event.getParameters();

		buildMainLayout();
		setCompositionRoot(mainLayout);
		// load module, and show dialog ..
		
//		App.getApp().getModules().start();
		
		// load bundle
		try {
			dpu = App.getApp().getModules().getInstance(pathToBundle);
		} catch (ModuleException ex) {
			// in case of exception show the exception and end
			TextArea txtError = new TextArea();			
			txtError.setValue( ex.getMessage() + " >> " + ex.getOriginal().getMessage() );
						
			txtError.setWidth("300px");
			txtError.setHeight("200px");			
			mainLayout.addComponent(txtError);
			return;
		}
		// resolve type and load configuration dialog
		CustomComponent configComponenet = ModuleDialogGetter.getDialog(dpu);
		
		if (configComponenet == null) {
			mainLayout.addComponent(new Label("can't get DPU dialog"));
		} else {
			mainLayout.addComponent( configComponenet );
		}
		
//		App.getApp().getModules().stop();
		
		// add button
		Button btnSave = new Button();
		btnSave.setWidth("100px");
		btnSave.setCaption("save");
		mainLayout.addComponent(btnSave);
		
		final TextArea txtOutput = new TextArea();
		txtOutput.setWidth("250px");
		txtOutput.setHeight("250px");
		mainLayout.addComponent(txtOutput);
		
		btnSave.addClickListener(new ClickListener(){
			@Override
			public void buttonClick(ClickEvent event) {
				// get configuration and show it in textbox ..
				String valueStr = dpu.getSettings().toString();				
				txtOutput.setValue(valueStr);
			}});
		
		// use methods: to get, set configuration
		// methods can throw or retun null
		// dpu.getSettings(), dpu.setSettings(configuration)
	}
	
	private void buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		
		// top-level component properties
		setWidth("600px");
		setHeight("800px");
		
		mainLayout.addComponent(new Label("Dialog:"));
	}

}
