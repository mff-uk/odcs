package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.Pipeline;
import cz.cuni.xrg.intlib.frontend.AppEntry;
import cz.cuni.xrg.intlib.frontend.data.DataAccess;
import cz.cuni.xrg.intlib.frontend.data.Pipelines;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;

public class PipelineList extends CustomComponent implements View {

	private VerticalLayout mainLayout;

	private Label label;

	private Table tablePipelines;

	private Button btnCreatePipeline;
	
	class actionColumnGenerator implements com.vaadin.ui.Table.ColumnGenerator {

		public Object generateCell(Table source, final Object itemId, Object columnId) {
			HorizontalLayout layout = new HorizontalLayout();
			
			Button updateButton = new Button();
			updateButton.setCaption("edit");
			updateButton.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
						// navigate to PipelineEdit/New
						App.getApp().getNavigator().navigateTo( 
								ViewNames.PipelineEdit.getUrl() + "/" + itemId.toString() );
					}
				});
			layout.addComponent(updateButton);
			
			Button daleteButton = new Button();
			daleteButton.setCaption("delete");
			daleteButton.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
						// navigate to PipelineEdit/New
						App.getDataAccess().pipelines().remove(itemId);
					}
				});
			layout.addComponent(daleteButton);			
			
			return layout;
		}
		
	}
	
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
		// add column
		tablePipelines.addGeneratedColumn("", new actionColumnGenerator() );
		
		btnCreatePipeline = new Button();
		btnCreatePipeline.setCaption("create pipeline");
		btnCreatePipeline.setHeight("25px");
		btnCreatePipeline.setWidth("150px");
		btnCreatePipeline.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// navigate to PipelineEdit/New
				App.getApp().getNavigator().navigateTo( ViewNames.PipelineEdit_New.getUrl() );
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
