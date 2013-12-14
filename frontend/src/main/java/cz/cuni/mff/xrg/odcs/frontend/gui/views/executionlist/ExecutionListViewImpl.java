package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.CANCELLED;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.CANCELLING;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.FAILED;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.FINISHED_SUCCESS;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.FINISHED_WARNING;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.QUEUED;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.RUNNING;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.DecorationHelper;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.ActionColumnGenerator;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.ActionColumnGenerator.Action;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibFilterDecorator;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.paged.PagedFilterTable;
import org.tepi.filtertable.paged.PagedTableChangeEvent;

/**
 * Implementation of view for {@link ExecutionListPresenter}.
 *
 * @author Petyr
 */
@Component
@Scope("prototype")
public class ExecutionListViewImpl extends CustomComponent implements ExecutionListPresenter.ExecutionListView {

	private static final Logger LOG = LoggerFactory.getLogger(ExecutionListViewImpl.class);
	private IntlibPagedTable monitorTable;
	/**
	 * Used to separate table from execution detail view.
	 */
	private HorizontalSplitPanel hsplit;
	private VerticalLayout logLayout;
	private Panel mainLayout;
	@Autowired
	private DebuggingView debugView;
	private HashMap<Date, Label> runTimeLabels = new HashMap<>();
	private ExecutionListPresenter presenter;
	@Autowired
	private Utils utils;

	@Override
	public Object enter(final ExecutionListPresenter presenter) {
		// build page
		buildPage(presenter);
		this.presenter = presenter;
		return this;
	}

	@Override
	public void setDisplay(ExecutionListPresenter.ExecutionListData dataObject) {
		monitorTable.setContainerDataSource(dataObject.getContainer());
		//monitorTable.setVisibleColumns(dataObject.getContainer().getContainerPropertyIds());
	}

	@Override
	public void showExecutionDetail(PipelineExecution execution, ExecutionListPresenter.ExecutionDetailData detailDataObject) {
		presenter.stopRefreshEventHandler();
		// secure existance of detail layout
		if (logLayout == null) {
			buildExecutionDetail(execution);
		} else {
			// will just set the debug view content
			buildDebugView(execution);
		}
		// no DPU specified
		debugView.setExecution(execution, null);
		//debugView.setDisplay(detailDataObject);

		hsplit.setSecondComponent(logLayout);
		// adjust hsplit
		if (hsplit.isLocked()) {
			debugView.setActiveTab("Events");
			hsplit.setSplitPosition(55, Unit.PERCENTAGE);
			//hsplit.setHeight("-1px");
			hsplit.setLocked(false);
		}
	}

	@Override
	public void refresh(boolean modified) {
		if (modified) {
			runTimeLabels.clear();
		}
		for (Map.Entry<Date, Label> entry : runTimeLabels.entrySet()) {
			long duration = (new Date()).getTime() - entry.getKey().getTime();
			entry.getValue().setValue(DecorationHelper.formatDuration(duration));
		}
	}

	/**
	 * Build page layout.
	 *
	 * @param presenter
	 */
	private void buildPage(final ExecutionListPresenter presenter) {
		mainLayout = new Panel("");
		// split page into two parts
		hsplit = new HorizontalSplitPanel();
		mainLayout.setContent(hsplit);
		// set top level element properties
		setWidth("100%");
		//setHeight("100%");

		VerticalLayout monitorTableLayout = new VerticalLayout();
		monitorTableLayout.setImmediate(true);
		monitorTableLayout.setMargin(true);
		monitorTableLayout.setSpacing(true);
		monitorTableLayout.setWidth("100%");
		//monitorTableLayout.setHeight("100%");

		// Layout for buttons Refresh and Clear Filters on the top.
		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);
		monitorTableLayout.addComponent(topLine);

		// Refresh button. Refreshing the table
		Button btnRefresh = new Button("Refresh", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				presenter.refreshEventHandler();
			}
		});
		btnRefresh.setWidth("120px");
		topLine.addComponent(btnRefresh);

		//Clear Filters button. Clearing filters on the table with executions.
		Button btnClearFilters = new Button();
		btnClearFilters.setCaption("Clear Filters");
		btnClearFilters.setHeight("25px");
		btnClearFilters.setWidth("120px");
		btnClearFilters.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				// TODO move this to the monitorTable
				monitorTable.resetFilters();
			}
		});
		topLine.addComponent(btnClearFilters);
		// Table with pipeline execution records
		monitorTable = initializeExecutionTable(presenter);
		monitorTableLayout.addComponent(monitorTable);
		monitorTableLayout.addComponent(monitorTable.createControls());

		hsplit.setFirstComponent(monitorTableLayout);
		hsplit.setSecondComponent(null);
		if (146 + (utils.getPageLength() * 32) <= 850) {
			hsplit.setHeight(850, Unit.PIXELS);
		} else {
			hsplit.setHeight(-1, Unit.PIXELS);
		}


		hsplit.setSplitPosition(100, Unit.PERCENTAGE);
		hsplit.setLocked(true);

		// at the end set page root
		setCompositionRoot(mainLayout);
	}

	/**
	 * Create and return {@link ActionColumnGenerator}.
	 *
	 * @param presenter
	 * @return
	 */
	private ActionColumnGenerator createColumnGenerator(final ExecutionListPresenter presenter) {
		ActionColumnGenerator generator = new ActionColumnGenerator();
		// add action buttons

		generator.addButton("Cancel", "90px", new Action() {
			@Override
			protected void action(long id) {
				presenter.stopEventHandler(id);
			}
		}, new ActionColumnGenerator.ButtonShowCondition() {
			@Override
			public boolean show(CustomTable source, long id) {
				Property propStatus = source.getItem(id).getItemProperty("status");
				PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
				// ...
				return status == PipelineExecutionStatus.QUEUED
						|| status == PipelineExecutionStatus.RUNNING;
			}
		});

		generator.addButton("Show log", "90px", new Action() {
			@Override
			protected void action(long id) {
				presenter.showDebugEventHandler(id);
			}
		}, new ActionColumnGenerator.ButtonShowCondition() {
			@Override
			public boolean show(CustomTable source, long id) {
				Property propStatus = source.getItem(id).getItemProperty("status");
				PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
				boolean isDebug = (boolean) source.getItem(id).getItemProperty("isDebugging").getValue();
				// ...
				return !isDebug && status != PipelineExecutionStatus.QUEUED;
			}
		});

		generator.addButton("Debug data", "90px", new Action() {
			@Override
			protected void action(long id) {
				presenter.showDebugEventHandler(id);
			}
		}, new ActionColumnGenerator.ButtonShowCondition() {
			@Override
			public boolean show(CustomTable source, long id) {
				Property propStatus = source.getItem(id).getItemProperty("status");
				PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
				boolean isDebug = (boolean) source.getItem(id).getItemProperty("isDebugging").getValue();
				// ...
				return isDebug && status != PipelineExecutionStatus.QUEUED;
			}
		});

		generator.addButton("Run pipeline", null, new Action() {
			@Override
			protected void action(long id) {
				presenter.runEventHandler(id);
			}
		}, new ActionColumnGenerator.ButtonShowCondition() {
			@Override
			public boolean show(CustomTable source, long id) {
				Property propStatus = source.getItem(id).getItemProperty("status");
				PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
				// ...
				return status != PipelineExecutionStatus.RUNNING
						&& status != PipelineExecutionStatus.CANCELLING;
			}
		}, new ThemeResource("icons/running.png"));

		generator.addButton("Debug pipeline", null, new Action() {
			@Override
			protected void action(long id) {
				presenter.debugEventHandler(id);
			}
		}, new ActionColumnGenerator.ButtonShowCondition() {
			@Override
			public boolean show(CustomTable source, long id) {
				Property propStatus = source.getItem(id).getItemProperty("status");
				PipelineExecutionStatus status = (PipelineExecutionStatus) propStatus.getValue();
				// ...
				return status != PipelineExecutionStatus.RUNNING
						&& status != PipelineExecutionStatus.CANCELLING;
			}
		}, new ThemeResource("icons/debug.png"));

		return generator;
	}

	private void buildExecutionDetail(PipelineExecution execution) {
		logLayout = new VerticalLayout();
		logLayout.setImmediate(true);
		logLayout.setMargin(true);
		logLayout.setSpacing(true);
		logLayout.setWidth("100%");
		//logLayout.setHeight("100%");
		//debugView = new DebuggingView();
		// build the debug view
		buildDebugView(execution);

		logLayout.addComponent(debugView);
		logLayout.setExpandRatio(debugView, 1.0f);

		// Layout for buttons  Close and  Export on the bottom
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidth("100%");

		Button buttonClose = new Button();
		buttonClose.setCaption("Close");
		buttonClose.setHeight("25px");
		buttonClose.setWidth("100px");
		buttonClose.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				presenter.stopRefreshEventHandler();
				hsplit.setSplitPosition(100, Unit.PERCENTAGE);
				//hsplit.setHeight("100%");
				hsplit.setLocked(true);
			}
		});
		buttonBar.addComponent(buttonClose);
		buttonBar.setComponentAlignment(buttonClose, Alignment.BOTTOM_RIGHT);

		logLayout.addComponent(buttonBar);
		logLayout.setExpandRatio(buttonBar, 0);

		if (execution.getStatus() == PipelineExecutionStatus.RUNNING
				|| execution.getStatus() == PipelineExecutionStatus.QUEUED) {
			presenter.startDebugRefreshEventHandler(debugView, execution);
		}
	}

	/**
	 * Builds debugging view upon first call, sets the pipeline execution to
	 * show inside it.
	 *
	 * @param exec pipeline execution to show in debugging view
	 */
	private void buildDebugView(PipelineExecution execution) {
		if (logLayout == null) {
			// secure that the debug view exist
			buildExecutionDetail(execution);
		}

		if (!debugView.isInitialized()) {
			debugView.initialize(execution, null, execution.isDebugging(), false);
		} else {
			debugView.setExecution(execution, null);
		}
	}

	private FilterGenerator createFilterGenerator() {
		return new FilterGenerator() {
			@Override
			public Container.Filter generateFilter(Object propertyId, Object value) {
				if ("schedule".equals(propertyId)) {
					boolean val = (boolean) value;

					if (!val) {
						return new IsNull(propertyId);
					} else {
						return new Not(new IsNull(propertyId));
					}
				}
				return null;
			}

			@Override
			public Container.Filter generateFilter(Object propertyId, Field<?> originatingField) {
				return null;
			}

			@Override
			public AbstractField<?> getCustomFilterComponent(Object propertyId) {
				if ("schedule".equals(propertyId)) {
					ComboBox comboScheduled = new ComboBox();
					comboScheduled.addItem(true);
					ThemeResource iconScheduled = new ThemeResource("icons/scheduled.png");
					comboScheduled.setItemIcon(true, iconScheduled);
					comboScheduled.setItemCaption(true, "Scheduled");
					comboScheduled.addItem(false);
					ThemeResource iconNotScheduled = new ThemeResource("icons/not_scheduled.png");
					comboScheduled.setItemIcon(false, iconNotScheduled);
					comboScheduled.setItemCaption(false, "Manual");
					return comboScheduled;
				}
				return null;
			}

			@Override
			public void filterRemoved(Object propertyId) {
			}

			@Override
			public void filterAdded(Object propertyId, Class<? extends Container.Filter> filterType, Object value) {
			}

			@Override
			public Container.Filter filterGeneratorFailed(Exception reason, Object propertyId, Object value) {
				return null;
			}
		};
	}

	private IntlibPagedTable initializeExecutionTable(final ExecutionListPresenter presenter) {

		final IntlibPagedTable executionTable = new IntlibPagedTable();
		executionTable.setSelectable(true);
		executionTable.setWidth("100%");
		executionTable.setHeight("100%");
		executionTable.setImmediate(true);

		executionTable.setColumnWidth("schedule", 65);
		executionTable.setColumnWidth("status", 50);
		executionTable.setColumnWidth("isDebugging", 50);
		executionTable.setColumnWidth("duration", 60);
		executionTable.setColumnWidth("actions", 200);
		executionTable.setColumnWidth("id", 50);
		executionTable.setSortEnabled(true);
		executionTable.setPageLength(utils.getPageLength());

		executionTable.setFilterGenerator(createFilterGenerator());
		executionTable.setFilterDecorator(new filterDecorator());
		executionTable.setFilterBarVisible(true);

		executionTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				ValueItem item = (ValueItem) event.getItem();
				final long executionId = item.getId();

				presenter.showDebugEventHandler(executionId);
			}
		});

		//Status column. Contains status icons.
		executionTable.addGeneratedColumn("status", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId,
					Object columnId) {
				PipelineExecutionStatus type = (PipelineExecutionStatus) source.getItem(itemId)
						.getItemProperty(columnId).getValue();
				if (type != null) {
					ThemeResource img = DecorationHelper.getIconForExecutionStatus(type);
					Embedded emb = new Embedded(type.name(), img);
					emb.setDescription(type.name());
					return emb;
				} else {
					return null;
				}
			}
		});

		//Debug column. Contains debug icons.
		executionTable.addGeneratedColumn("isDebugging", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId,
					Object columnId) {
				boolean inDebug = (boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
				Embedded emb;
				if (inDebug) {
					emb = new Embedded("True", new ThemeResource("icons/debug.png"));
					emb.setDescription("TRUE");
				} else {
					emb = new Embedded("False", new ThemeResource("icons/no_debug.png"));
					emb.setDescription("FALSE");
				}
				return emb;
			}
		});

		executionTable.addGeneratedColumn("duration", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				long duration = (long) source.getItem(itemId).getItemProperty(columnId).getValue();
				//It is refreshed only upon change in db, so for running pipeline it is not refreshed
				PipelineExecutionStatus status = (PipelineExecutionStatus) source.getItem(itemId).getItemProperty("status").getValue();
				if (duration == -1 && (status == RUNNING || status == PipelineExecutionStatus.CANCELLING)) {
					Date start = (Date) source.getItem(itemId).getItemProperty("start").getValue();
					if (start != null) {
						duration = (new Date()).getTime() - start.getTime();
						Label durationLabel = new Label(DecorationHelper.formatDuration(duration));
						durationLabel.setImmediate(true);
						runTimeLabels.put(start, durationLabel);
						return durationLabel;
					}
				}
				return DecorationHelper.formatDuration(duration);
			}
		});
		executionTable.addGeneratedColumn("schedule", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				boolean isScheduled = (boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
				Embedded emb = DecorationHelper.getIconForScheduled(isScheduled);
				return emb;
			}
		});

		// add generated columns to the executionTable
		executionTable.addGeneratedColumn("", createColumnGenerator(presenter));
		executionTable.setVisibleColumns();
		executionTable.addListener(new PagedFilterTable.PageChangeListener() {

			@Override
			public void pageChanged(PagedTableChangeEvent event) {
				int newPageNumber = event.getCurrentPage();
				presenter.pageChangedHandler(newPageNumber);
			}
		});
		executionTable.addItemSetChangeListener(new Container.ItemSetChangeListener() {

			@Override
			public void containerItemSetChange(Container.ItemSetChangeEvent event) {
				for(Object id : event.getContainer().getContainerPropertyIds()) {
					Object filterValue = executionTable.getFilterFieldValue(id);
					presenter.filterParameterEventHander((String)id, filterValue);
				}
			}
		});

		return executionTable;
	}

	@Override
	public void setSelectedRow(Long execId) {
		monitorTable.select(execId);
	}

	@Override
	public void setFilter(String name, Object value) {
		monitorTable.setFilterFieldValue(name, value);
	}

	@Override
	public void setPage(int pageNumber) {
		monitorTable.setCurrentPage(pageNumber);
	}

	/**
	 * Settings icons to the table filters "status" and "debug"
	 *
	 * @author Bogo
	 *
	 */
	class filterDecorator extends IntlibFilterDecorator {

		@Override
		public String getEnumFilterDisplayName(Object propertyId, Object value) {
			if (propertyId.equals("status")) {
				return ((PipelineExecutionStatus) value).name();
			}
			return super.getEnumFilterDisplayName(propertyId, value);
		}

		@Override
		public Resource getEnumFilterIcon(Object propertyId, Object value) {
			if (propertyId.equals("status")) {
				PipelineExecutionStatus type = (PipelineExecutionStatus) value;
				ThemeResource img = null;
				switch (type) {
					case FINISHED_SUCCESS:
						img = new ThemeResource("icons/ok.png");
						break;
					case FINISHED_WARNING:
						img = new ThemeResource("icons/warning.png");
						break;
					case FAILED:
						img = new ThemeResource("icons/error.png");
						break;
					case RUNNING:
						img = new ThemeResource("icons/running.png");
						break;
					case QUEUED:
						img = new ThemeResource("icons/queued.png");
						break;
					case CANCELLED:
						img = new ThemeResource("icons/cancelled.png");
						break;
					case CANCELLING:
						img = new ThemeResource("icons/cancelling.png");
						break;
					default:
						//no icon
						break;
				}
				return img;
			}
			return super.getEnumFilterIcon(propertyId, value);
		}

		@Override
		public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
			if (propertyId.equals("isDebugging")) {
				if (value) {
					return "Debug";
				} else {
					return "Run";
				}
			}
			return super.getBooleanFilterDisplayName(propertyId, value);
		}

		@Override
		public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
			if (propertyId.equals("isDebugging")) {
				if (value) {
					return new ThemeResource("icons/debug.png");
				} else {
					return new ThemeResource("icons/no_debug.png");
				}
			}
			return super.getBooleanFilterIcon(propertyId, value);
		}
	};
}
