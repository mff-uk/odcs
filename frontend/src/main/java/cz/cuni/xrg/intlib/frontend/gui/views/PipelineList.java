package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;

public class PipelineList extends CustomComponent implements View {

	private VerticalLayout mainLayout;

	private Label label;

	private Table tablePipelines;

	private Button btnCreatePipeline;

	/**
	 * Class used to run pipelines.
	 * @author Petyr
	 *
	 */
	public class RunPipeline implements Runnable {

		/**
		 * Pipeline to run.
		 */
	    private Pipeline pipeline;

	    public RunPipeline(Pipeline pipeline) {
	        this.pipeline = pipeline;
	    }

	    public void run() {
	    	PipelineExecution pipelineExec = new PipelineExecution(pipeline);
			pipelineExec.run();
	    }
	}
	
	/**
	 * Generate column in table with buttons.
	 * @author Petyr
	 *
	 */
	class actionColumnGenerator implements com.vaadin.ui.Table.ColumnGenerator {

		@Override
		public Object generateCell(final Table source, final Object itemId,
				Object columnId) {
			HorizontalLayout layout = new HorizontalLayout();

			Button updateButton = new Button();
			updateButton.setCaption("edit");
			updateButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// navigate to PipelineEdit/New
							App.getApp()
									.getNavigator()
									.navigateTo(
											ViewNames.PipelineEdit.getUrl()
													+ "/" + itemId.toString());
						}
					});
			layout.addComponent(updateButton);
			// get item
			final BeanItem<Pipeline> item = (BeanItem<Pipeline>) source.getItem(itemId);

			Button deleteButton = new Button();
			deleteButton.setCaption("delete");
			deleteButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// navigate to PipelineEdit/New
							App.getApp().getPipelines().delete(item.getBean());
							// now we have to remove pipeline from table
							source.removeItem(itemId);
						}
					});
			layout.addComponent(deleteButton);

			Button runButton = new Button();
			runButton.setCaption("run");
			runButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// navigate to PipelineEdit/New
							Pipeline pipeline = item.getBean();

							// start pipeline in othre thread
							RunPipeline pipelineRun = new RunPipeline(pipeline);
					        Thread t = new Thread(pipelineRun);
					        t.start();
														
							// show message about action
							Notification.show("pipeline execution started ..",
									Type.HUMANIZED_MESSAGE);
						}
					});
			layout.addComponent(runButton);

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
		Container container = ContainerFactory.CreatePipelines(App.getApp()
				.getPipelines().getAllPipelines());
		tablePipelines.setContainerDataSource(container);

		// set columns
		tablePipelines.setVisibleColumns(new String[] { "id", "name",
				"description" });
		mainLayout.addComponent(tablePipelines);
		// add column
		tablePipelines.addGeneratedColumn("", new actionColumnGenerator());

		btnCreatePipeline = new Button();
		btnCreatePipeline.setCaption("create pipeline");
		btnCreatePipeline.setHeight("25px");
		btnCreatePipeline.setWidth("150px");
		btnCreatePipeline
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						// navigate to PipelineEdit/New
						App.getApp()
								.getNavigator()
								.navigateTo(ViewNames.PipelineEdit_New.getUrl());
					}
				});
		mainLayout.addComponent(btnCreatePipeline);

		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

}
