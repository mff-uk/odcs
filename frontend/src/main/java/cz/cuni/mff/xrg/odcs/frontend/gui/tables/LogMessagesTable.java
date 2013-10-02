package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Field;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.container.InFilter;
import cz.cuni.mff.xrg.odcs.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.PropertiesFilter;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.LogMessageDetail;

import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.datefilter.DateInterval;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;

/**
 * Component for viewing and filtering of log messages.
 *
 * @author Bogo
 */
public class LogMessagesTable extends CustomComponent {

	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;
	private DPUInstanceRecord dpu;
	private PipelineExecution pipelineExecution;
	private ComboBox levelSelector;
	private ComboBox dpuSelector;
	private LogMessageDetail detail = null;
	private IntlibLazyQueryContainer container;
	private LogFacade logFacade = App.getLogs();
	private DPUFacade dpuFacade = App.getDPUs();

	/**
	 * Default constructor.
	 */
	public LogMessagesTable() {
		mainLayout = new VerticalLayout();
		messageTable = new IntlibPagedTable();
		messageTable.setSelectable(true);

		messageTable.setSizeFull();
		mainLayout.addComponent(messageTable);
		mainLayout.addComponent(messageTable.createControls());
		messageTable.setPageLength(28);
		messageTable.setSelectable(true);
		messageTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
				if (!messageTable.isSelected(event.getItemId())) {
					CompositeItem item = (CompositeItem) event.getItem();
					long logId = (long) item.getItemProperty("id")
							.getValue();
					LogMessage log = App.getLogs().getLog(logId);
					showLogMessageDetail(log);
				}
			}
		});

		levelSelector = new ComboBox();
		levelSelector.setImmediate(true);
		levelSelector.setNullSelectionAllowed(false);
		levelSelector.addItem(Level.ALL);
		for (Level level : App.getLogs().getAllLevels(false)) {
			levelSelector.addItem(level);
			levelSelector.setItemCaption(level, level.toString() + "+");
		}
		dpuSelector = new ComboBox();
		dpuSelector.setImmediate(true);
		loadMessageTable();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Show log messages related only to given DPU. If null is passed, data for
	 * whole pipeline are shown.
	 *
	 * @param exec {@link PipelineExecution} which log to show.
	 * @param dpu {@link DPUInstanceRecord} or null.
	 */
	public void setDpu(PipelineExecution exec, DPUInstanceRecord dpu, boolean isRefresh) {
		this.dpu = dpu;
		if (pipelineExecution != exec && !isRefresh) {
			levelSelector.setValue(exec.isDebugging() ? Level.ALL : Level.INFO);
		}
		IntlibLazyQueryContainer c = (IntlibLazyQueryContainer) messageTable.getContainerDataSource().getContainer();
		if (!isRefresh) {
			this.pipelineExecution = exec;
			c.removeDefaultFilters();
			c.addDefaultFilter(new PropertiesFilter(LogMessage.MDPU_EXECUTION_KEY_NAME, pipelineExecution.getId()));
//			if (dpu != null) {
//				c.addDefaultFilter(new PropertiesFilter(LogMessage.MDC_DPU_INSTANCE_KEY_NAME, dpu.getId()));
//			}
		}
		refreshDpuSelector();
		c.refresh();
		messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
	}

	public List<LogMessage> getData(PipelineExecution exec, DPUInstanceRecord dpu, Level level) {
		LogFacade facade = App.getLogs();

		Set<Level> levels = facade.getLevels(level);

		if (dpu == null) {
			return facade.getLogs(exec, levels);
		} else {
			return facade.getLogs(exec, dpu, levels);
		}

	}

	/**
	 * Initializes the table.
	 *
	 * @param data List of {@link LogMessages} to show in table.
	 */
	private void loadMessageTable() {

		container = App.getApp().getBean(ContainerFactory.class).createLogMessages();
		messageTable.addGeneratedColumn("dpuInstanceId", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Long dpuId = (Long) source.getItem(itemId).getItemProperty(columnId).getValue();
				if (dpuId == null) {
					return null;
				}
				DPUInstanceRecord dpu = dpuFacade.getDPUInstance(dpuId);
				return dpu.getName();
			}
		});
		messageTable.setFilterGenerator(new FilterGenerator() {
			@Override
			public Container.Filter generateFilter(Object propertyId, Object value) {
				if (propertyId.equals("level")) {
					return new InFilter(App.getLogs().getLevels((Level) value), "levelString");
				} else if (propertyId.equals("date")) {
					DateInterval interval = (DateInterval) value;
					if (interval.getFrom() == null) {
						return new Compare.LessOrEqual("timestamp", interval.getTo().getTime());
					} else if (interval.getTo() == null) {
						return new Compare.GreaterOrEqual("timestamp", interval.getFrom().getTime());
					} else {
						return new Between("timestamp", interval.getFrom().getTime(), interval.getTo().getTime());
					}
				} else if (propertyId.equals("dpuInstanceId")) {
					if (value != null) {
						Long id = ((DPUInstanceRecord) value).getId();
						return new PropertiesFilter(LogMessage.MDC_DPU_INSTANCE_KEY_NAME, id);
					}
				}
				return null;
			}

			@Override
			public AbstractField<?> getCustomFilterComponent(Object propertyId) {
				if (propertyId == null) {
					return null;
				}
				if (propertyId.equals("level")) {
					return levelSelector;
				} else if (propertyId.equals("dpuInstanceId")) {
					return dpuSelector;
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

			@Override
			public Container.Filter generateFilter(Object propertyId, Field<?> originatingField) {
				return null;
			}
		});
		messageTable.setContainerDataSource(container);
		messageTable.setVisibleColumns("date", "level", "dpuInstanceId", "message", "source");
		messageTable.setColumnHeader("dpuInstanceId", "DPU Instance");
		messageTable.setSortEnabled(false);
		messageTable.setFilterBarVisible(true);
		levelSelector.setValue(Level.INFO);

		//messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
	}

	/*
	 * Creates {@link LogMessageDetail} for given log message.
	 * 
	 * @param log Log message to show detail of.
	 */
	private void showLogMessageDetail(LogMessage log) {
		if (detail == null) {
			final LogMessageDetail detailWindow = new LogMessageDetail(log);
			detailWindow.setHeight(600, Unit.PIXELS);
			detailWindow.setWidth(500, Unit.PIXELS);
			detailWindow.setImmediate(true);
			detailWindow.setContentHeight(600, Unit.PIXELS);
			detailWindow.addResizeListener(new Window.ResizeListener() {
				@Override
				public void windowResized(Window.ResizeEvent e) {
					detailWindow.setContentHeight(e.getWindow().getHeight(), Unit.PIXELS);
				}
			});
			detailWindow.addCloseListener(new Window.CloseListener() {
				@Override
				public void windowClose(Window.CloseEvent e) {
					detail = null;
				}
			});
			detail = detailWindow;
			App.getApp().addWindow(detailWindow);
		} else {
			detail.loadMessage(log);
			detail.bringToFront();
		}
	}

	private void refreshDpuSelector() {
		ExecutionContextInfo ctx = pipelineExecution.getContextReadOnly();
		if (ctx != null) {
			for (DPUInstanceRecord dpuInstance : ctx.getDPUIndexes()) {
				if (!dpuSelector.containsId(dpuInstance)) {
					dpuSelector.addItem(dpuInstance);
				}
			}
			if (dpuSelector.containsId(dpu)) {
				dpuSelector.select(dpu);
			}
		}
	}
}
