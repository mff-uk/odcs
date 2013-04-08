package cz.cuni.xrg.intlib.frontend.gui.views;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.module.*;
import cz.cuni.xrg.intlib.frontend.OSGi.*;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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
		Framework frame = App.getApp().getFrameWork();
		
		// load bundle
		try {
			dpu = frame.loadDPU(pathToBundle);
		} catch (OSGiException ex) {
			// in case of exception show the exception and end
			TextArea txtError = new TextArea();			
			txtError.setValue( ex.getMessage() );
						
			txtError.setWidth("300px");
			txtError.setHeight("200px");			
			mainLayout.addComponent(txtError);
			return;
		}
		// resolve type and load configuration dialog
		switch(dpu.getType()) {
		case EXTRACTOR:
			GraphicalExtractor graphExtract = (GraphicalExtractor)dpu;
			mainLayout.addComponent( graphExtract.getConfigurationComponent() );
			break;
		case LOADER:
			GraphicalLoader graphLoader = (GraphicalLoader)dpu;
			mainLayout.addComponent( graphLoader.getConfigurationComponent() );
			break;
		case TRANSFORMER:
			GraphicalTransformer graphTrans = (GraphicalTransformer)dpu;
			mainLayout.addComponent( graphTrans.getConfigurationComponent() );
			break;
		default:
			mainLayout.addComponent(new Label("unknown dpu type"));
			break;
		}
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
