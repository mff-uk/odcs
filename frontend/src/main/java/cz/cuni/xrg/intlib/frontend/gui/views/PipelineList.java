package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.intlib.xrg.commons.app.data.pipeline.Pipeline;
import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.AppEntry;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;

public class PipelineList extends CustomComponent implements View {

	private VerticalLayout mainLayout;

	private Label label;

	private Table tablePipelines;

	private Button btnCreatePipeline;
	
	public PipelineList() {

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
		label.setValue("<h1>Pipelines</h>");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label);
		
		tablePipelines = new Table();
		tablePipelines.setWidth("640px");
		tablePipelines.setHeight("480px");
		// assign data source
		JPAContainer<Pipeline> pipes = App.getDataAccess().pipelines().getPipelines();		
		tablePipelines.setContainerDataSource(pipes);
		// set columns
		tablePipelines.setVisibleColumns(new String[] {"id", "name", "description"});		
		mainLayout.addComponent(tablePipelines);
		
		btnCreatePipeline = new Button();
		btnCreatePipeline.setCaption("create pipeline");
		btnCreatePipeline.setHeight("25px");
		btnCreatePipeline.setWidth("150px");
		btnCreatePipeline.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// navigate to PipelineEdit/New
				((AppEntry)UI.getCurrent()).getNavigator().navigateTo( ViewNames.PipelineEdit_New.getUrl() );
			}
		});
		mainLayout.addComponent(btnCreatePipeline);
		
		return mainLayout;
	}

	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);		
	}

}
