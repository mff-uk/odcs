package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.commons.app.communication.Client;
import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfiguration;
import cz.cuni.xrg.intlib.commons.app.conf.ConfProperty;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;

class PipelineList extends ViewComponent {

	private VerticalLayout mainLayout;

	private Label label;

	private IntlibPagedTable tablePipelines;

	private Button btnCreatePipeline;

	public void runPipeline(Pipeline pipeline, boolean inDebugMode) {
		PipelineExecution pipelineExec =  new PipelineExecution(pipeline);
		pipelineExec.setDebugging(inDebugMode);
		// TODO Petyr: leave null value?
		pipelineExec.setWorkingDirectory("");
		// do some settings here

		// store into DB
		App.getPipelines().save(pipelineExec);
		AppConfiguration config = App.getApp().getAppConfiguration();
		Client client = new Client(
			config.getString(ConfProperty.BACKEND_HOST),
			config.getInteger(ConfProperty.BACKEND_PORT)
		);

		// send message to backend
		try {
			client.checkDatabase();
		} catch (CommunicationException e) {
			Notification.show("Error", "Can't connect to backend. Exception: " + e.getCause().getMessage(),
					Type.ERROR_MESSAGE);
			return;
		}

		// show message about action
		Notification.show("pipeline execution started ..",
				Type.HUMANIZED_MESSAGE);
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

			
			// TODO Petyr, Maria, Bohuslav, Honza: Pipeline delete
/*			Button deleteButton = new Button();
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
			layout.addComponent(deleteButton);*/

			Button runButton = new Button();
			runButton.setCaption("run");
			runButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// navigate to PipelineEdit/New
							Pipeline pipeline = item.getBean();
							runPipeline(pipeline, false);
						}
					});
			layout.addComponent(runButton);

			Button runDebugButton = new Button();
			runDebugButton.setCaption("debug");
			runDebugButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// navigate to PipelineEdit/New
							Pipeline pipeline = item.getBean();
							runPipeline(pipeline, true);
						}
					});
			layout.addComponent(runDebugButton);

			return layout;
		}

	}

	public PipelineList() {

	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setSpacing(true);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("<h1>Pipelines</h>");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label);

		tablePipelines = new IntlibPagedTable();
		tablePipelines.setWidth("99%");
		tablePipelines.setPageLength(10);
		// assign data source
		Container container = ContainerFactory.CreatePipelines(App.getApp()
				.getPipelines().getAllPipelines());
		tablePipelines.setContainerDataSource(container);

		// set columns
		tablePipelines.setVisibleColumns(new String[] { "id", "name",
				"description" });
		mainLayout.addComponent(tablePipelines);
		mainLayout.addComponent(tablePipelines.createControls());
		tablePipelines.setPageLength(10);
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
