package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;

import org.springframework.transaction.annotation.Transactional;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Vaadin implementation for PipelineListView. 
 *
 * @author Bogo
 */
public class PipelineListViewImpl extends CustomComponent implements PipelineListView {

	/**
	 * View name.
	 */
        //TODO do we need the NAME? Not used in the app
	public static final String NAME = "PipelineList";
	//private static final int PAGE_LENGTH = 20;
	private VerticalLayout mainLayout;
	private IntlibPagedTable tablePipelines;
	private Button btnCreatePipeline;
	/* Only the presenter registers one listener... */
	//TODO why not private?
        PipelineListViewListener listener;
	//TODO why not private?
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

		btnCreatePipeline = new Button();
		btnCreatePipeline.setCaption("Create pipeline");
		btnCreatePipeline.setHeight("25px");
		btnCreatePipeline.setWidth("120px");
		btnCreatePipeline
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// navigate to PIPELINE_EDIT/New
                                // TODO this should be solved by the presenter, he should navigate further
				App.getApp()
						.getNavigator()
						.navigateTo(ViewNames.PIPELINE_EDIT_NEW.getUrl());
			}
		});
		topLine.addComponent(btnCreatePipeline);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("120px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				tablePipelines.resetFilters();
			}
		});
		topLine.addComponent(buttonDeleteFilters);

		mainLayout.addComponent(topLine);

		tablePipelines = new IntlibPagedTable();
		tablePipelines.setWidth("99%"); //TODO why 99%?

		mainLayout.addComponent(tablePipelines);
		mainLayout.addComponent(tablePipelines.createControls());
		tablePipelines.setPageLength(Utils.PAGE_LENGTH);

		// add column
		tablePipelines.setImmediate(true);
//		tablePipelines.addGeneratedColumn("description", new CustomTable.ColumnGenerator() {
//			@Override
//			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
//				String description = (String) source.getItem(itemId).getItemProperty(columnId).getValue();
//				if (description.length() > App.MAX_TABLE_COLUMN_LENGTH) {
//					Label descriptionLabel = new Label(description.substring(0, App.MAX_TABLE_COLUMN_LENGTH - 3) + "...");
//					descriptionLabel.setDescription(description);
//					return descriptionLabel;
//				} else {
//					return description;
//				}
//			}
//		});
		tablePipelines.addGeneratedColumn("", new ActionColumnGenerator());

		tablePipelines.setFilterBarVisible(true);
		tablePipelines.setFilterLayout();
		tablePipelines.setSelectable(true);
		tablePipelines.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
                                //TODO navigation should be done by presenter
				if (!tablePipelines.isSelected(event.getItemId())) {
					ValueItem item = (ValueItem) event.getItem();
					long pipelineId = (long) item.getItemProperty("id")
							.getValue();
                                        //TODO navigation must be done by the presenter
					App.getApp().getNavigator().navigateTo(ViewNames.PIPELINE_EDIT.getUrl() + "/" + pipelineId);
				}
			}
		});

		return mainLayout;
	}

	/**
	 * Refresh data on the pipeline list table
         * //TODO this should work opposite - presenter pushes new container to the view (thus presenter also calls refreshData)
         * 
	 */
	@Transactional
	private void refreshData() {
		listener.event("refresh");
		int page = tablePipelines.getCurrentPage();
		ReadOnlyContainer c = (ReadOnlyContainer) tablePipelines.getContainerDataSource().getContainer();
		//c.refresh();
		tablePipelines.setCurrentPage(page);
	}

	@Override
	public void setDataSource(Container c) {
		// assign data source
		tablePipelines.setContainerDataSource(c);
	}

	@Override
	public void setListener(PipelineListViewListener listener) {
		this.listener = listener;
	}

	/**
	 * Generate column in table with buttons.
         * 
         * TODO Should be separated to extra component "field of action buttons", which is displayed in every row
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
			final ValueItem item = (ValueItem) source.getItem(itemId);
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
					//TODO presenter should initiate refresh of data
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
					String message = "Would you really like to delete the " + item.getItemProperty("name").getValue() + " pipeline and all associated records (DPU instances e.g.)?";

					ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting pipeline", message, "Delete pipeline", "Cancel", new ConfirmDialog.Listener() {
						@Override
						public void onClose(ConfirmDialog cd) {
							if (cd.isConfirmed()) {
								listener.pipelineEvent(pipelineId, "delete");
								// now we have to remove pipeline from table
                                                                //TODO this should be done by adjusting the container and setting a new container to the table - is that possible?
								source.removeItem(itemId);
                                                                //TODO refresh should be called by the presenter, view should not refresh itself
								refreshData();
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
