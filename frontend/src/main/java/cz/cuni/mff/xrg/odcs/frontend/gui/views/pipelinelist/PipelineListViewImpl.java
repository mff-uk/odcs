package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;

/**
 *
 * @author Bogo
 */
public class PipelineListViewImpl extends CustomComponent implements PipelineListView {

	/**
	 * View name.
	 */
	public static final String NAME = "PipelineList";
	private static final int PAGE_LENGTH = 20;
	private VerticalLayout mainLayout;
	private IntlibPagedTable tablePipelines;
	private Button btnCreatePipeline;
	/* Only the presenter registers one listener... */
	PipelineListViewListener listener;
	Container container;

	public boolean isModified() {
		//There are no editable fields.
		return false;
	}

	public PipelineListViewImpl() {
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
				tablePipelines.setFilterFieldVisible("lastExecTime", false);
				tablePipelines.setFilterFieldVisible("lastExecStatus", false);
			}
		});
		topLine.addComponent(buttonDeleteFilters);

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

		// add column
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
		tablePipelines.addGeneratedColumn("", new ActionColumnGenerator());
		tablePipelines.addGeneratedColumn("duration", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				IntlibLazyQueryContainer container = (IntlibLazyQueryContainer) ((IntlibPagedTable) source).getContainerDataSource().getContainer();
				Pipeline ppl = (Pipeline) container.getEntity(itemId);
				return listener.getLastExecDetail(ppl, "duration");
			}
		});
		tablePipelines.addGeneratedColumn("lastExecTime", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				IntlibLazyQueryContainer container = (IntlibLazyQueryContainer) ((IntlibPagedTable) source).getContainerDataSource().getContainer();
				Pipeline ppl = (Pipeline) container.getEntity(itemId);
				return listener.getLastExecDetail(ppl, "time");
			}
		});
		tablePipelines.addGeneratedColumn("lastExecStatus", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				IntlibLazyQueryContainer container = (IntlibLazyQueryContainer) ((IntlibPagedTable) source).getContainerDataSource().getContainer();
				Pipeline ppl = (Pipeline) container.getEntity(itemId);
				PipelineExecutionStatus status = (PipelineExecutionStatus) listener.getLastExecDetail(ppl, "status");
				if (status != null) {
					ThemeResource img = IntlibHelper.getIconForExecutionStatus(status);
					Embedded emb = new Embedded(status.name(), img);
					emb.setDescription(status.name());
					return emb;
				} else {
					return null;
				}
			}
		});

		// set columns
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
		listener.event("refresh");
		int page = tablePipelines.getCurrentPage();
		IntlibLazyQueryContainer c = (IntlibLazyQueryContainer) tablePipelines.getContainerDataSource().getContainer();
		c.refresh();
		tablePipelines.setCurrentPage(page);
	}

	@Override
	public void setDataSource(Container c) {
		// assign data source
		tablePipelines.setContainerDataSource(c);
		tablePipelines.setFilterFieldVisible("", false);
		tablePipelines.setFilterFieldVisible("duration", false);
		tablePipelines.setFilterFieldVisible("lastExecTime", false);
		tablePipelines.setFilterFieldVisible("lastExecStatus", false);
	}

	@Override
	public void setListener(PipelineListViewListener listener) {
		this.listener = listener;
	}

	/**
	 * Generate column in table with buttons.
	 *
	 * @author Petyr
	 *
	 */
	private class ActionColumnGenerator implements CustomTable.ColumnGenerator {

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
			final Long pipelineId = (Long) item.getItemProperty("id").getValue();
			//final Pipeline pipeline = pipelineFacade.getPipeline(pipelineId);
			Button copyButton = new Button();
			copyButton.setCaption("copy");
			copyButton.setWidth("80px");
			copyButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					listener.pipelineEvent(pipelineId, "copy");
					refreshData();
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
					listener.pipelineEvent(pipelineId, "delete");
					refreshData();
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
					listener.pipelineEvent(pipelineId, "run");
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
					listener.pipelineEvent(pipelineId, "debug");
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
					listener.pipelineEvent(pipelineId, "schedule");
				}
			});
			layout.addComponent(schedulerButton);

			return layout;
		}
	}
}
