package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;
import org.vaadin.dialogs.ConfirmDialog;
import ru.xpoft.vaadin.VaadinView;

@Component
@Scope("prototype")
@VaadinView(PipelineList.NAME)
class PipelineList extends ViewComponent {

	/**
	 * View name.
	 */
	public static final String NAME = "PipelineList";
	private static final int PAGE_LENGTH = 20;
	private VerticalLayout mainLayout;
	private IntlibPagedTable tablePipelines;
	private Button btnCreatePipeline;
	@Autowired
	private PipelineFacade pipelineFacade;

	@Override
	public boolean isModified() {
		//There are no editable fields.
		return false;
	}

	@Override
	@Transactional
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@Transactional
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
		btnCreatePipeline.setWidth("120px");
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
		buttonDeleteFilters.setWidth("120px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				tablePipelines.resetFilters();
				tablePipelines.setFilterFieldVisible("", false);
				tablePipelines.setFilterFieldVisible("duration", false);
			}
		});
		topLine.addComponent(buttonDeleteFilters);
		//topLine.setComponentAlignment(buttonDeleteFilters, Alignment.MIDDLE_RIGHT);

//		Label topLineFiller = new Label();
//		topLine.addComponentAsFirst(topLineFiller);
//		topLine.setExpandRatio(topLineFiller, 1.0f);
		mainLayout.addComponent(topLine);

		tablePipelines = new IntlibPagedTable() {

			@Override
			public Collection<?> getSortableContainerPropertyIds() {
				ArrayList<String> sortableIds = new ArrayList<>(2);
				sortableIds.add("id");
				sortableIds.add("name");
				return sortableIds;
			}
			
		};
		tablePipelines.setWidth("99%");
		
		mainLayout.addComponent(tablePipelines);
		mainLayout.addComponent(tablePipelines.createControls());
		tablePipelines.setPageLength(PAGE_LENGTH);
		//tablePipelines.setSortContainerPropertyId("id");
		//tablePipelines.setSortAscending(false);
		// assign data source
		Container container = App.getApp().getBean(ContainerFactory.class).createPipelines(PAGE_LENGTH);
		tablePipelines.setContainerDataSource(container);
		//tablePipelines.sort();
		// add column
		tablePipelines.addGeneratedColumn("", new actionColumnGenerator());
		tablePipelines.setImmediate(true);
		tablePipelines.addGeneratedColumn("description", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				String description = (String) source.getItem(itemId).getItemProperty(columnId).getValue();
				if (description.length() > App.MAX_TABLE_COLUMN_LENGTH) {
					Label descriptionLabel = new Label(description.substring(0, App.MAX_TABLE_COLUMN_LENGTH - 3) + "...");
					descriptionLabel.setDescription(description);
					return descriptionLabel;
				} else {
					return description;
				}
			}
		});
		tablePipelines.addGeneratedColumn("duration", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Long pipelineId = (Long) source.getItem(itemId).getItemProperty("id").getValue();
				PipelineExecution latestExec = pipelineFacade.getLastExec(pipelineFacade.getPipeline(pipelineId), IntlibHelper.getFinishedStatuses());
				return IntlibHelper.getDuration(latestExec);
			}
		});
		tablePipelines.addGeneratedColumn("lastExecTime", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				IntlibLazyQueryContainer container = (IntlibLazyQueryContainer) ((IntlibPagedTable) source).getContainerDataSource().getContainer();
				Pipeline ppl = (Pipeline) container.getEntity(itemId);
				PipelineExecution latestExec = pipelineFacade.getLastExec(ppl);
				if (latestExec != null) {
					DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());

					return df.format(latestExec.getStart());
				} else {
					return null;
				}
			}
		});
		tablePipelines.addGeneratedColumn("lastExecStatus", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				IntlibLazyQueryContainer container = (IntlibLazyQueryContainer) ((IntlibPagedTable) source).getContainerDataSource().getContainer();
				Pipeline ppl = (Pipeline) container.getEntity(itemId);
				PipelineExecution latestExec = pipelineFacade.getLastExec(ppl);
				if (latestExec != null) {
					PipelineExecutionStatus type = latestExec.getStatus();
					ThemeResource img = IntlibHelper.getIconForExecutionStatus(type);
					Embedded emb = new Embedded(type.name(), img);
					emb.setDescription(type.name());
					return emb;
				} else {
					return null;
				}
			}
		});

		// set columns
		tablePipelines.setVisibleColumns("id", "name", "description", "", "duration", "lastExecTime", "lastExecStatus");
		tablePipelines.setColumnHeader("duration", "Last run time");
		tablePipelines.setColumnHeader("lastExecTime", "Last execution time");
		tablePipelines.setColumnHeader("lastExecStatus", "Last status");
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
					App.getApp().getNavigator().navigateTo(ViewNames.PIPELINE_EDIT.getUrl() + "/" + pipelineId);
				}
			}
		});

		return mainLayout;
	}

	/**
	 * Refresh data on the pipeline list table
	 */
	@Transactional
	private void refreshData() {
		int page = tablePipelines.getCurrentPage();
		IntlibLazyQueryContainer c = (IntlibLazyQueryContainer) tablePipelines.getContainerDataSource().getContainer();
		c.refresh();
		tablePipelines.setCurrentPage(page);
	}

	private boolean isExecInSystem(Pipeline pipeline, PipelineExecutionStatus status) {
		List<PipelineExecution> execs = pipelineFacade.getExecutions(pipeline, status);
		if (execs.isEmpty()) {
			return false;
		} else {
			//TODO: Differentiate by user maybe ?!
			return true;
		}
	}

	/**
	 * Generate column in table with buttons.
	 *
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
			updateButton.setWidth("80px");
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
			CompositeItem item = (CompositeItem) source.getItem(itemId);
			Long pipelineId = (Long) item.getItemProperty("id").getValue();
			final Pipeline pipeline = pipelineFacade.getPipeline(pipelineId);
			Button copyButton = new Button();
			copyButton.setCaption("copy");
			copyButton.setWidth("80px");
			copyButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Pipeline nPipeline = pipelineFacade.copyPipeline(pipeline);
					String copiedPipelineName = "Copy of " + pipeline.getName();
					if (copiedPipelineName.length() > MaxLengthValidator.NAME_LENGTH) {
						Notification.show(String.format("Name of copied pipeline would exceed limit of %d characters, new pipeline has same name as original.", MaxLengthValidator.NAME_LENGTH), Notification.Type.WARNING_MESSAGE);
					} else {
						nPipeline.setName(copiedPipelineName);
					}
					pipelineFacade.save(nPipeline);
					refreshData();
					//tablePipelines.setVisibleColumns("id", "name", "duration", "description","");
				}
			});
			layout.addComponent(copyButton);


			Button deleteButton = new Button();
			deleteButton.setCaption("delete");
			deleteButton.setWidth("80px");
			deleteButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					String message = "Would you really like to delete the " + pipeline.getName() + " pipeline and all associated records (DPU instances e.g.)?";
					if (isExecInSystem(pipeline, PipelineExecutionStatus.RUNNING)) {
						message += "\nPipeline is running currently, the current run will be cancelled!";
					}
					if (isExecInSystem(pipeline, PipelineExecutionStatus.SCHEDULED)) {
						message += "\nPipeline is scheduled currently, the scheduled execution will be deleted!";
					}
					if (!App.getSchedules().getSchedulesFor(pipeline).isEmpty()) {
						message += "\nThere is/are scheduler rules with the pipeline, it/they will be deleted!";
					}

					ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting pipeline", message, "Delete pipeline", "Cancel", new ConfirmDialog.Listener() {
						@Override
						public void onClose(ConfirmDialog cd) {
							if (cd.isConfirmed()) {
								pipelineFacade.delete(pipeline);
								// now we have to remove pipeline from table
								source.removeItem(itemId);
								refreshData();
								//tablePipelines.setVisibleColumns("id", "name", "duration", "description","");
							}
						}
					});



				}
			});
			layout.addComponent(deleteButton);

			Button runButton = new Button();
			runButton.setCaption("run");
			runButton.setWidth("80px");
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
			runDebugButton.setWidth("80px");
			runDebugButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					PipelineExecution exec = IntlibHelper.runPipeline(pipeline, true);
					if (exec != null) {
						App.getApp().getNavigator().navigateTo(ViewNames.EXECUTION_MONITOR.getUrl() + "/" + exec.getId());
					}
				}
			});
			layout.addComponent(runDebugButton);


			Button schedulerButton = new Button();
			schedulerButton.setCaption("schedule");
			schedulerButton.setWidth("80px");
			schedulerButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// open scheduler dialog
					SchedulePipeline sch = new SchedulePipeline();
					sch.setSelectePipeline(pipeline);
					App.getApp().addWindow(sch);
				}
			});
			layout.addComponent(schedulerButton);

			return layout;
		}
	}
}
