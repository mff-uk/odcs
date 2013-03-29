package cz.cuni.intlib.frontend.gui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.intlib.auxiliaries.App;
import cz.cuni.intlib.commons.app.data.pipeline.Pipeline;
import cz.cuni.intlib.frontend.gui.ViewNames;

public class PipelineEdit extends CustomComponent implements View {

	private VerticalLayout mainLayout;

	private Label label;
	
	/**
	 * Current pipeline entity.
	 */
	private com.vaadin.addon.jpacontainer.EntityItem<Pipeline> entity = null;
	
	private Pipeline pipeline = null;
	
	public PipelineEdit() {
		// put init code into enter method
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		
		// top-level component properties
		setWidth("600px");
		setHeight("800px");
		
		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label);
		
		com.vaadin.ui.Button button = new com.vaadin.ui.Button();
		button.setCaption("save");
		button.setHeight("25px");
		button.setWidth("150px");
		button.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// save current pipeline
				savePipeline();
			}
		});
		mainLayout.addComponent(button);		
				
		return mainLayout;
	}

	/**
	 * Return true if given string is positive number.
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if ( Character.isDigit(str.charAt(i)) ) {
				
			} else {
				return false;
			}
		}
		return true;
	}	
	
	/**
	 * Load pipeline with given id from database. 
	 * @param id
	 * @return
	 */
	protected Pipeline loadPipeline(String id) {
		// get data from DB ..
		this.entity = App.getDataAccess().pipelines().getPipeline(id);
		if (this.entity == null) {
			return null;
		} else {
			return this.entity.getEntity();
		}
	}
	
	/**
	 * Load pipeline to edit/create. Pipeline entity is loaded into
	 * this.entity. If /New parameter is passed in url, create just representation
	 * for pipeline.
	 * @param event
	 * @return Loaded pipeline class instance or null.
	 */
	protected Pipeline loadPipeline(ViewChangeEvent event) {
		// some information text ...
		String pipeIdstr = event.getParameters();
		if (pipeIdstr.compareTo( ViewNames.PipelineEdit_New.getParametr() ) == 0) {
			// create empty, for new record
			this.pipeline = new Pipeline("new pipeline", "description");
			this.entity = null;
		} else if (isInteger(pipeIdstr)) {
			// use pipeIdstr as id
			this.pipeline = loadPipeline(pipeIdstr);
		} else {
			// wring pipeIdstr
			this.pipeline = null;
			this.entity = null;
		}
		return this.pipeline;
	}
	
	/**
	 * Save loaded pipeline ie. this.entity.
	 */
	protected void savePipeline() {
		this.entity = 
				App.getDataAccess().pipelines().set(this.pipeline, this.entity);
	}
	
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// ..
		this.loadPipeline(event);
		// or use this.entity.getEntity();
		
		if (this.pipeline == null) {
			label.setValue("<h1>Pipeline '" + event.getParameters() + "' doesn't exist.</h1>");
		} else {
			label.setValue("<h1>Editing pipeline : " + this.pipeline.getName() + "</h1>");
		}
		
		// work with pipeline here ...
		
	}
	
	
}
