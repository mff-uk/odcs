package cz.cuni.xrg.intlib.frontend.gui.views;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.frontend.gui.components.SchedulePipeline;

class Scheduler extends ViewComponent {

	private VerticalLayout mainLayout;
	private Label label;
	private IntlibPagedTable schedulerTable;
	private IndexedContainer tableData;
	static String[] visibleCols = new String[]{"pipeline", "rule", "user",
		"last", "next", "status", "commands"};
	static String[] headers = new String[]{"pipeline", "Rule", "User",
		"Last", "Next", "Status", "Commands"};
	private DateFormat localDateFormat = null;
	int style = DateFormat.MEDIUM;
	private Long schId;
	static String filter;

	public Scheduler() {
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		// mainLayout.setWidth("600px");
		// mainLayout.setHeight("800px");

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);
		topLine.setWidth(100, Unit.PERCENTAGE);

		Button addRuleButton = new Button();
		addRuleButton.setCaption("Add new scheduling rule");
		addRuleButton
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// open scheduler dialog
				SchedulePipeline sch = new SchedulePipeline();
				App.getApp().addWindow(sch);
				sch.addCloseListener(new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						refreshData();
					}
				});
			}
		});
		topLine.addComponent(addRuleButton);
		topLine.setComponentAlignment(addRuleButton, Alignment.MIDDLE_RIGHT);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("110px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				schedulerTable.resetFilters();
			}
		});
		topLine.addComponent(buttonDeleteFilters);
		topLine.setComponentAlignment(buttonDeleteFilters, Alignment.MIDDLE_RIGHT);
		
		Label topLineFiller = new Label();
		topLine.addComponentAsFirst(topLineFiller);
		topLine.setExpandRatio(topLineFiller, 1.0f);
		mainLayout.addComponent(topLine);

		tableData = getTableData(App.getApp().getSchedules().getAllSchedules());

		schedulerTable = new IntlibPagedTable();
		schedulerTable.setSelectable(true);
		schedulerTable.setContainerDataSource(tableData);
		schedulerTable.setWidth("100%");
		schedulerTable.setHeight("100%");
		schedulerTable.setImmediate(true);
		schedulerTable.setVisibleColumns(visibleCols); // Set visible columns
		schedulerTable.setColumnHeaders(headers);

		schedulerTable.addGeneratedColumn("commands",
				new actionColumnGenerator());

		mainLayout.addComponent(schedulerTable);
		mainLayout.addComponent(schedulerTable.createControls());
		schedulerTable.setPageLength(20);
		schedulerTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
				if (!schedulerTable.isSelected(event.getItemId())) {
					schId = (Long) event.getItem().getItemProperty("schid").getValue();
					showSchedulePipeline(schId);
				}
			}
		});

		return mainLayout;
	}

	private void refreshData() {
		int page = schedulerTable.getCurrentPage();
		tableData = getTableData(App.getApp().getSchedules().getAllSchedules());
		schedulerTable.setContainerDataSource(tableData);
		schedulerTable.setCurrentPage(page);
		schedulerTable.setVisibleColumns(visibleCols);

	}

	public static IndexedContainer getTableData(List<Schedule> data) {

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {

			if ((p.equals("last")) || (p.equals("next"))) {
				result.addContainerProperty(p, Date.class, null);
			} else {
				result.addContainerProperty(p, String.class, "");
			}

		}
		result.addContainerProperty("schid", Long.class, "");

		for (Schedule item : data) {

			Object num = result.addItem();

			if (item.getFirstExecution() == null) {
				result.getContainerProperty(num, "next").setValue(null);
			} else {
				result.getContainerProperty(num, "next").setValue(
						item.getNextExecutionTimeInfo());
			}

			if (item.getLastExecution() == null) {
				result.getContainerProperty(num, "last").setValue(null);
			} else {

				result.getContainerProperty(num, "last").setValue(
						item.getLastExecution());
			}

			if (item.isEnabled()) {
				result.getContainerProperty(num, "status").setValue("Enabled");
			} else {
				result.getContainerProperty(num, "status").setValue("Disabled");
			}

			if (item.getType().equals(ScheduleType.PERIODICALLY)) {
				if (item.isJustOnce()) {
					result.getContainerProperty(num, "rule").setValue(
							"Run on " + item.getFirstExecution().toLocaleString());
				} else {
					if (item.getPeriod().equals((long) 1)) {
						result.getContainerProperty(num, "rule").setValue(
								"Run on "
								+ item.getFirstExecution().toLocaleString()
								+ " and then repeat every "
								+ item.getPeriodUnit().toString()
								.toLowerCase());
					} else {
						result.getContainerProperty(num, "rule").setValue(
								"Run on "
								+ item.getFirstExecution().toLocaleString()
								+ " and then repeat every "
								+ item.getPeriod().toString()
								+ " "
								+ item.getPeriodUnit().toString()
								.toLowerCase() + "s");
					}
				}
			} else {

				Set<Pipeline> after = item.getAfterPipelines();
				String afterPipelines = "";
				after.size();
				int i = 0;
				for (Pipeline afteritem : after) {
					i++;
					if (i < after.size()) {
						afterPipelines = afterPipelines + afteritem.getName() + ", ";
					} else {
						afterPipelines = afterPipelines + afteritem.getName() + ". ";
					}
				}
				if (after.size() > 1) {
					result.getContainerProperty(num,
							"rule").setValue("Run after pipelines: " + afterPipelines);
				} else {
					result.getContainerProperty(num,
							"rule").setValue("Run after pipeline: " + afterPipelines);
				}
			}

			result.getContainerProperty(num, "schid").setValue(item.getId());
			result.getContainerProperty(num, "user").setValue(" ");
			result.getContainerProperty(num, "pipeline").setValue(
					item.getPipeline().getName());


		}


		return result;

	}

	/*	private void openScheduler(final SchedulePipeline schedule) {
	 Window scheduleWindow = new Window("Schedule a pipeline", schedule);
	 scheduleWindow.setImmediate(true);
	 scheduleWindow.setWidth("820px");
	 scheduleWindow.setHeight("550px");
	 scheduleWindow.addCloseListener(new Window.CloseListener() {
	 @Override
	 public void windowClose(Window.CloseEvent e) {
	 // closeDebug();
	 }
	 });
	 scheduleWindow.addResizeListener(new Window.ResizeListener() {

	 @Override
	 public void windowResized(Window.ResizeEvent e) {
	 schedule.resize(e.getWindow().getHeight());
	 }
	 });
	 App.getApp().addWindow(scheduleWindow);
	 }*/
	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	class actionColumnGenerator implements CustomTable.ColumnGenerator {

		private ClickListener clickListener = null;

		@Override
		public Object generateCell(final CustomTable source, final Object itemId,
				Object columnId) {
			Property propStatus = source.getItem(itemId).getItemProperty(
					"status");
			String testStatus = "---";

			HorizontalLayout layout = new HorizontalLayout();

			if (propStatus.getType().equals(String.class)) {

				testStatus = (String) propStatus.getValue().toString();

				if (testStatus == "Disabled") {
					Button enableButton = new Button("Enable");
					enableButton.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {

							schId = (Long) tableData.getContainerProperty(itemId, "schid")
									.getValue();
							List<Schedule> schedulers = App.getApp().getSchedules().getAllSchedules();
							for (Schedule item : schedulers) {
								if (item.getId().equals(schId)) {
									item.setEnabled(true);
									App.getApp().getSchedules().save(item);
								}
							}
							refreshData();

						}
					});
					layout.addComponent(enableButton);

				} else {
					Button disableButton = new Button();
					disableButton.setCaption("Disable");
					disableButton.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {

							schId = (Long) tableData.getContainerProperty(itemId, "schid")
									.getValue();
							List<Schedule> schedulers = App.getApp().getSchedules().getAllSchedules();
							for (Schedule item : schedulers) {
								if (item.getId().equals(schId)) {
									item.setEnabled(false);
									App.getApp().getSchedules().save(item);
								}
							}
							refreshData();

						}
					});
					layout.addComponent(disableButton);
				}

			}

			Button editButton = new Button();
			editButton.setCaption("Edit");
			editButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					schId = (Long) tableData.getContainerProperty(itemId, "schid")
							.getValue();
					showSchedulePipeline(schId);
				}
			});
			layout.addComponent(editButton);

			Button deleteButton = new Button();
			deleteButton.setCaption("Delete");
			deleteButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					schId = (Long) tableData.getContainerProperty(itemId, "schid")
							.getValue();
					Schedule schedule = App.getApp().getSchedules().getSchedule(schId);
					App.getApp().getSchedules().delete(schedule);
					refreshData();
				}
			});
			layout.addComponent(deleteButton);

			return layout;
		}
	}

	/**
	 * Shows dialog for scheduling pipeline with given scheduling rule.
	 *
	 * @param id Id of schedule to show.
	 */
	private void showSchedulePipeline(Long id) {

		// open scheduler dialog
		SchedulePipeline sch = new SchedulePipeline();

		//openScheduler(sch);
		Schedule schedule = App.getApp().getSchedules().getSchedule(id);
		sch.setSelectedSchedule(schedule);

		App.getApp().addWindow(sch);
		sch.addCloseListener(new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				refreshData();
			}
		});
	}
}
