package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandFileDownloader;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandStreamResource;
import cz.cuni.mff.xrg.odcs.frontend.container.InFilter;
import cz.cuni.mff.xrg.odcs.frontend.container.PropertiesFilter;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.LogMessageDetail;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.datefilter.DateInterval;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;

/**
 * Component for viewing and filtering of log messages.
 *
 * @author Bogo
 */
public class LogMessagesTable extends CustomComponent {

	@Autowired
	private LogFacade logFacade;
	private static final int PAGE_LENGTH = 28;
	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;
	private HorizontalLayout mtControls;
	private DPUInstanceRecord dpu;
	private PipelineExecution pipelineExecution;
	private LogMessageDetail detail = null;
	public IntlibPagedTable preparedRefreshedTable = null;
	private FilterGenerator filterGenerator;
	private Thread fetchData = null;
	private HashMap<Long, String> dpuNames;
	private static final Logger LOG = LoggerFactory.getLogger(LogMessagesTable.class);
	//Last filter values
	DPUInstanceRecord lastSelectedDpu = null;
	Level lastLevel = Level.ALL;
	String lastMessage = "";
	String lastSource = "";
	Object lastDate = null;

	/**
	 * Default constructor.
	 */
	public LogMessagesTable() {
	}

	@PostConstruct
	private void initialize() {
		mainLayout = new VerticalLayout();

		messageTable = new IntlibPagedTable();
		messageTable.setSelectable(true);
		messageTable.setSizeFull();
		messageTable.setPageLength(PAGE_LENGTH);
		messageTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
				if (!messageTable.isSelected(event.getItemId())) {
					CompositeItem item = (CompositeItem) event.getItem();
					long logId = (long) item.getItemProperty("id")
							.getValue();
					LogMessage log = logFacade.getLog(logId);
					showLogMessageDetail(log);
				}
			}
		});
		dpuNames = new HashMap<>();
		messageTable.addGeneratedColumn("dpuInstanceId", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Long dpuId = (Long) source.getItem(itemId).getItemProperty(columnId).getValue();
				if (dpuId == null) {
					return null;
				}

				return dpuNames.get(dpuId);
			}
		});
		ComboBox levelSelector = new ComboBox();
		levelSelector.setImmediate(true);
		levelSelector.setNullSelectionAllowed(false);
		levelSelector.addItem(Level.ALL);
		for (Level level : logFacade.getAllLevels(false)) {
			levelSelector.addItem(level);
			levelSelector.setItemCaption(level, level.toString() + "+");
		}
		ComboBox dpuSelector = new ComboBox();
		dpuSelector.setImmediate(true);
		filterGenerator = createFilterGenerator(dpuSelector, levelSelector);
		mtControls = messageTable.createControls();
		mainLayout.addComponent(messageTable);
		mainLayout.addComponent(mtControls);

		Button download = new Button("Download");
		FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamResource() {
			@Override
			public String getFilename() {
				return "log.txt";
			}

			@Override
			public InputStream getStream() {
				DPUInstanceRecord dpu = (DPUInstanceRecord) messageTable.getFilterFieldValue("dpuInstanceId");
				Level level = (Level) messageTable.getFilterFieldValue("level");

				String message = (String) messageTable.getFilterFieldValue("message");
				String source = (String) messageTable.getFilterFieldValue("source");
				Object date = messageTable.getFilterFieldValue("date");
				Date start = null;
				Date end = null;
				if (date != null) {
					DateInterval di = (DateInterval) date;
					start = di.getFrom();
					end = di.getTo();
				}
				return logFacade.getLogsAsStream(pipelineExecution, dpu, level, message, source, start, end);
			}
		});
		fileDownloader.extend(download);
		mainLayout.addComponent(download);
		setCompositionRoot(mainLayout);
	}

	/**
	 * Show log messages related only to given DPU. If null is passed, data for
	 * whole pipeline are shown.
	 *
	 * @param exec {@link PipelineExecution} which log to show.
	 * @param dpu {@link DPUInstanceRecord} or null.
	 */
	public void setDpu(PipelineExecution exec, DPUInstanceRecord dpu, ReadOnlyContainer container) {
		if (fetchData != null) {
			fetchData.interrupt();
		}
		preparedRefreshedTable = null;

		this.pipelineExecution = exec;
		if (dpuNames == null) {
			dpuNames = new HashMap<>();
		} else {
			dpuNames.clear();
		}
		for (Node node : pipelineExecution.getPipeline().getGraph().getNodes()) {
			DPUInstanceRecord nodeDpu = node.getDpuInstance();
			dpuNames.put(nodeDpu.getId(), nodeDpu.getName());
		}

		loadMessageTable(exec.getId(), container);

		ReadOnlyContainer c = (ReadOnlyContainer) messageTable.getContainerDataSource().getContainer();
		c.removeAllContainerFilters();
		c.addContainerFilter(new PropertiesFilter(LogMessage.MDPU_EXECUTION_KEY_NAME, pipelineExecution.getId()));

		LOG.debug("Start of reloading the log container");
		this.dpu = dpu;
		Level newValue = exec.isDebugging() ? Level.ALL : Level.INFO;
		if (newValue.equals(messageTable.getFilterFieldValue("level"))) {
			c.refresh();
		} else {
			messageTable.setFilterFieldValue("level", newValue);
		}
		messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
		refreshDpuSelector((ComboBox) messageTable.getFilterField("dpuInstanceId"));
		LOG.debug("End of reloading the log container");
	}

	public boolean refresh(boolean immediate, boolean getNewData) {
		pipelineExecution = App.getPipelines().getExecution(pipelineExecution.getId());
		//if (immediate) {
		ReadOnlyContainer c = (ReadOnlyContainer) messageTable.getContainerDataSource().getContainer();
		c.refresh();
		return true;
		/*} else {

		 DPUInstanceRecord selectedDpu = (DPUInstanceRecord) messageTable.getFilterFieldValue("dpuInstanceId");
		 Level level = (Level) messageTable.getFilterFieldValue("level");
		 String message = (String) messageTable.getFilterFieldValue("message");
		 String source = (String) messageTable.getFilterFieldValue("source");
		 Object date = messageTable.getFilterFieldValue("date");

		 boolean result = false;

		 if (preparedRefreshedTable != null) {
		 if (filterValuesStayedSame(selectedDpu, level, message, source, date)) {
		 refreshDpuSelector((ComboBox) preparedRefreshedTable.getFilterField("dpuInstanceId"));
		 LOG.debug("Data refresh updated to client.");
		 mainLayout.removeComponent(messageTable);
		 mainLayout.removeComponent(mtControls);
		 messageTable = preparedRefreshedTable;
		 mtControls = messageTable.createControls();
		 mainLayout.addComponent(messageTable, 0);
		 mainLayout.addComponent(mtControls, 1);
		 result = true;
		 } else {
		 getNewData = true;
		 }
		 preparedRefreshedTable = null;
		 }

		 if (getNewData && preparedRefreshedTable == null && (fetchData == null || !fetchData.isAlive())) {
		 LOG.debug("Data refresh requested.");
		 Authentication authentication = App.getApp().getAuthCtx().getAuthentication();
		 startFetchThread(pipelineExecution, selectedDpu, level, message, source, date, authentication);
		 }
		 return result && !getNewData;
		 }*/
	}

	public List<LogMessage> getData(PipelineExecution exec, DPUInstanceRecord dpu, Level level) {
		Set<Level> levels = logFacade.getLevels(level);

		if (dpu == null) {
			return logFacade.getLogs(exec, levels);
		} else {
			return logFacade.getLogs(exec, dpu, levels);
		}
	}

	/**
	 * Initializes the table.
	 *
	 * @param data List of {@link LogMessages} to show in table.
	 */
	private IntlibPagedTable loadMessageTable(Long executionId, ReadOnlyContainer container) {
		if (executionId != null) {
			container.addContainerFilter(new PropertiesFilter(LogMessage.MDPU_EXECUTION_KEY_NAME, executionId));
			//container.addDefaultFilter(new PropertiesFilter(LogMessage.MDPU_EXECUTION_KEY_NAME, executionId));
		}
		messageTable.setFilterGenerator(filterGenerator);
		messageTable.setContainerDataSource(container);
		messageTable.setSortEnabled(false);
		messageTable.setFilterBarVisible(true);

		return messageTable;
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

	private void refreshDpuSelector(ComboBox dpuSelector) {
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

	private FilterGenerator createFilterGenerator(final ComboBox dpuSelector, final ComboBox levelSelector) {
		return new FilterGenerator() {
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
		};
	}

	private void startFetchThread(final PipelineExecution exec, final DPUInstanceRecord dpu, final Level level, final String message, final String source, final Object date, final Authentication authentication) {
		fetchData = new Thread() {
			@Override
			public void run() {
				App.getApp().getAuthCtx().setAuthentication(authentication);
				final IntlibPagedTable fetchTable = messageTable;// loadMessageTable(exec.getId());
				setTableFilters(fetchTable, dpu, level, message, source, date);
				fetchTable.setCurrentPage(fetchTable.getTotalAmountOfPages());

				if (isInterrupted()) {
					return;
				}

				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						preparedRefreshedTable = fetchTable;
					}
				});
			}
		};
		fetchData.start();
	}

	private void setTableFilters(IntlibPagedTable fetchTable, DPUInstanceRecord dpu, Level level, String message, String source, Object date) {
		fetchTable.setFilterFieldValue("dpuInstanceId", dpu);
		fetchTable.setFilterFieldValue("level", level);
		fetchTable.setFilterFieldValue("message", message);
		fetchTable.setFilterFieldValue("source", source);
		fetchTable.setFilterFieldValue("date", date);
	}

	private boolean filterValuesStayedSame(DPUInstanceRecord selectedDpu, Level level, String message, String source, Object date) {
		boolean isSame = nullableEquals(selectedDpu, lastSelectedDpu);
		isSame &= nullableEquals(level, lastLevel);
		isSame &= nullableEquals(message, lastMessage);
		isSame &= nullableEquals(source, lastSource);
		isSame &= nullableEquals(date, lastDate);

		if (!isSame) {
			lastSelectedDpu = selectedDpu;
			lastDate = date;
			lastLevel = level;
			lastMessage = message;
			lastSource = source;
		}

		return isSame;
	}

	private boolean nullableEquals(Object obj1, Object obj2) {
		return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2));
	}

	public void setDpu(DPUInstanceRecord debugDpu) {
		this.dpu = debugDpu;
		refreshDpuSelector((ComboBox) messageTable.getFilterField("dpuInstanceId"));
	}
}
