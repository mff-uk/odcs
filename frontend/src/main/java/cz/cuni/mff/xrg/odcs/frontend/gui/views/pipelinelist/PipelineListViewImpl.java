package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PipelineEdit;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.ActionColumnGenerator;

import org.springframework.transaction.annotation.Transactional;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Vaadin implementation for PipelineListView.
 *
 * @author Bogo
 */
public class PipelineListViewImpl extends CustomComponent implements PipelineListView {

	private VerticalLayout mainLayout;
	private IntlibPagedTable tablePipelines;
	private Button btnCreatePipeline;
	/* Only the presenter registers one listener... */
	private PipelineListViewListener listener;

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
				listener.navigation(PipelineEdit.NAME, "New");
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
		tablePipelines.setWidth("100%");

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
		tablePipelines.addGeneratedColumn("actions", createColumnGenerator());

		tablePipelines.setFilterBarVisible(true);
		tablePipelines.setFilterLayout();
		tablePipelines.setSelectable(true);
		tablePipelines.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (!tablePipelines.isSelected(event.getItemId())) {
					ValueItem item = (ValueItem) event.getItem();
					long pipelineId = (long) item.getItemProperty("id").getValue();
					listener.navigation(PipelineEdit.NAME, pipelineId);
				}
			}
		});

		return mainLayout;
	}

	/**
	 * Refresh data on the pipeline list table.
	 */
	@Override
	public void refresh() {
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
	 * @author Petyr
	 *
	 */
	private ActionColumnGenerator createColumnGenerator() {

		ActionColumnGenerator generator = new ActionColumnGenerator();
		// add action buttons

		generator.addButton("Edit", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				listener.navigation(PipelineEdit.NAME, id);
			}
		});

		generator.addButton("Copy", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				listener.pipelineEvent(id, "copy");
			}
		});

		generator.addButton("Delete", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(final long id) {
				//String message = "Would you really like to delete the " + item.getItemProperty("name").getValue() + " pipeline and all associated records (DPU instances e.g.)?";
				String message = "Would you really like to delete this pipeline and all associated records (DPU instances e.g.)?";
				ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting pipeline", message, "Delete pipeline", "Cancel", new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog cd) {
						if (cd.isConfirmed()) {
							listener.pipelineEvent(id, "delete");
						}
					}
				});
			}
		});
		
		generator.addButton("Run", "80px", new ActionColumnGenerator.Action() {

			@Override
			protected void action(long id) {
				listener.pipelineEvent(id, "run");
			}
		});
		
		generator.addButton("Debug", "80px", new ActionColumnGenerator.Action() {

			@Override
			protected void action(long id) {
				listener.pipelineEvent(id, "debug");
			}
		});
		
		generator.addButton("Schedule", "80px", new ActionColumnGenerator.Action() {

			@Override
			protected void action(long id) {
				listener.pipelineEvent(id, "schedule");
			}
		});
		
		return generator;
	}
}
