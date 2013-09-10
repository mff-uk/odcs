package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibHelper;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.frontend.gui.components.SchedulePipeline;
import java.util.List;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;
import org.vaadin.dialogs.ConfirmDialog;

class PipelineList extends ViewComponent {

	private VerticalLayout mainLayout;

	private IntlibPagedTable tablePipelines;

	private Button btnCreatePipeline;
	
	private PipelineFacade pipelineFacade = App.getApp().getPipelines();

	@Override
	public boolean isModified() {
		//There are no editable fields.
		return false;
	}

	/**
	 * Generate column in table with buttons.
	 * @author Petyr
	 *
	 */
	class actionColumnGenerator implements CustomTable.ColumnGenerator {

		@Override
		public Object generateCell(final CustomTable source, final Object itemId,
				Object columnId) {
			HorizontalLayout layout = new HorizontalLayout();

			Button updateButton = new Button();
			updateButton.setCaption("edit");
			updateButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// navigate to PIPELINE_EDIT/New
							App.getApp()
									.getNavigator()
									.navigateTo(
											ViewNames.PIPELINE_EDIT.getUrl()
													+ "/" + itemId.toString());
						}
					});
			layout.addComponent(updateButton);
			

			
			// get item
			CompositeItem  item = (CompositeItem) source.getItem(itemId);
			Long pipelineId = (Long)item.getItemProperty("id").getValue();
                        final Pipeline pipeline = App.getPipelines().getPipeline(pipelineId);
			Button copyButton = new Button();
			copyButton.setCaption("copy");
			copyButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							PipelineFacade pplFacade = App.getApp().getPipelines();
							Pipeline nPipeline = pplFacade.copyPipeline(pipeline);
							nPipeline.setName("Copy of " + pipeline.getName());
							pplFacade.save(nPipeline);
							refreshData();
							tablePipelines.setVisibleColumns("id", "name", "description","");
						}
					});
			layout.addComponent(copyButton); 


			Button deleteButton = new Button();
			deleteButton.setCaption("delete");
			deleteButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							String message = "Would you like to really delete the pipeline and all associated records (DPU instances e.g.)?";
							if(isExecInSystem(pipeline, PipelineExecutionStatus.RUNNING)) {
								message += "\nPipeline is running currently, the current run will be cancelled!";
							}
							if(isExecInSystem(pipeline, PipelineExecutionStatus.SCHEDULED)) {
								message += "\nPipeline is scheduled currently, the scheduled execution will be deleted!";
							}
							if(!App.getSchedules().getSchedulesFor(pipeline).isEmpty()) {
								message += "\nThere is/are scheduler rules with the pipeline, it/they will be deleted!";
							}
							
							ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting pipeline", message, "Delete pipeline", "Cancel", new ConfirmDialog.Listener() {

								@Override
								public void onClose(ConfirmDialog cd) {
									if(cd.isConfirmed()) {
										App.getApp().getPipelines().delete(pipeline);
										// now we have to remove pipeline from table
										source.removeItem(itemId);
										refreshData();
										tablePipelines.setVisibleColumns("id", "name", "description","");
									}
								}
							});
							
							
							
						}
					});
			layout.addComponent(deleteButton);

			Button runButton = new Button();
			runButton.setCaption("run");
			runButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							IntlibHelper.runPipeline(pipeline, false);
						}
					});
			layout.addComponent(runButton);

			Button runDebugButton = new Button();
			runDebugButton.setCaption("debug");
			runDebugButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							IntlibHelper.runPipeline(pipeline, true);
						}
					});
			layout.addComponent(runDebugButton);
			
			
			Button schedulerButton = new Button();
			schedulerButton.setCaption("scheduler");
			schedulerButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							// open scheduler dialog
							SchedulePipeline  sch = new SchedulePipeline();
							sch.setSelectePipeline(pipeline);
							//sch.selectedPipeline=pipeline;
							//openScheduler(sch);
							App.getApp().addWindow(sch);
							

						}
					});
			layout.addComponent(schedulerButton);

			return layout;
		}

	}
	
	private boolean isExecInSystem(Pipeline pipeline, PipelineExecutionStatus status) {
		List<PipelineExecution> execs = App.getPipelines().getExecutions(pipeline, status);
		if(execs.isEmpty()) {
			return false;
		} else {
			//TODO: Differentiate by user maybe ?!
			return true;
		}
	}
	
	/**
	 * Refresh data on the pipeline list table
	 */
	private void refreshData() {
		int page = tablePipelines.getCurrentPage();
		Container container = ContainerFactory.createPipelines();
		tablePipelines.setContainerDataSource(container);
		tablePipelines.setFilterFieldVisible("", false);
		tablePipelines.setCurrentPage(page);

	}


	public PipelineList() {

	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);
		//topLine.setWidth(100, Unit.PERCENTAGE);
		
		btnCreatePipeline = new Button();
		btnCreatePipeline.setCaption("Create pipeline");
		btnCreatePipeline.setHeight("25px");
		btnCreatePipeline.setWidth("150px");
		btnCreatePipeline
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						// navigate to PIPELINE_EDIT/New
						App.getApp()
								.getNavigator()
								.navigateTo(ViewNames.PIPELINE_EDIT_NEW.getUrl());
					}
				});
		topLine.addComponent(btnCreatePipeline);
		//topLine.setComponentAlignment(btnCreatePipeline, Alignment.MIDDLE_RIGHT);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("110px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				tablePipelines.resetFilters();
				tablePipelines.setFilterFieldVisible("", false);
			}
		});
		topLine.addComponent(buttonDeleteFilters);
		//topLine.setComponentAlignment(buttonDeleteFilters, Alignment.MIDDLE_RIGHT);
		
//		Label topLineFiller = new Label();
//		topLine.addComponentAsFirst(topLineFiller);
//		topLine.setExpandRatio(topLineFiller, 1.0f);
		mainLayout.addComponent(topLine);

		tablePipelines = new IntlibPagedTable();
		tablePipelines.setWidth("99%");
		tablePipelines.setPageLength(10);
		// assign data source
		Container container = ContainerFactory.createPipelines();
		tablePipelines.setContainerDataSource(container);

		// set columns
		tablePipelines.setVisibleColumns("id", "name", "description");
		mainLayout.addComponent(tablePipelines);
		mainLayout.addComponent(tablePipelines.createControls());
		tablePipelines.setPageLength(10);
		// add column
		tablePipelines.addGeneratedColumn("", new actionColumnGenerator());
		tablePipelines.setImmediate(true);
		tablePipelines.addGeneratedColumn("description", new CustomTable.ColumnGenerator() {

			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				String description = (String) source.getItem(itemId).getItemProperty(columnId).getValue();
				if(description.length() > App.MAX_TABLE_COLUMN_LENGTH) {
					Label descriptionLabel = new Label(description.substring(0, App.MAX_TABLE_COLUMN_LENGTH - 3) + "...");
					descriptionLabel.setDescription(description);
					return descriptionLabel;
				} else {
					return description;
				}
			}
		});
		tablePipelines.setFilterBarVisible(true);
                tablePipelines.setFilterLayout();
		tablePipelines.setSelectable(true);
		tablePipelines.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
				if (!tablePipelines.isSelected(event.getItemId())) {
					CompositeItem item = (CompositeItem) event.getItem();
					long pipelineId = (long) item.getItemProperty("id")
							.getValue();
					App.getApp().getNavigator().navigateTo(ViewNames.PIPELINE_EDIT.getUrl()+ "/" + pipelineId);
				}
			}
		});
		
		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

}
