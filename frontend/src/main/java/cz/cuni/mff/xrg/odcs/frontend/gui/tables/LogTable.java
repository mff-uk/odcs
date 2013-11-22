package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogRead;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandFileDownloader;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandStreamResource;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.NewLogAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.CachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.LogMessageDetail;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.datefilter.DateInterval;

/**
 * Table for displaying {@link Log}s.
 *
 * @author Petyr
 */
public class LogTable extends CustomComponent {

	private static final Logger LOG = LoggerFactory.getLogger(LogTable.class);

	@Autowired
	private DbLogRead access;

	@Autowired
	private LogFacade logFacade;

	@Autowired
	private PipelineFacade pipelineFacade;

	private VerticalLayout mainLayout;

	private IntlibPagedTable table;

	private DPUInstanceRecord dpu;

	private PipelineExecution execution;

	private ReadOnlyContainer<Log> container;
	
	private FilterGenerator filterGenerator;
	
	private LogMessageDetail detail = null;
	
	/**
	 * Filters on {@link #container}.
	 */
	private final List<Filter> coreFilters = new LinkedList<>();

	/**
	 * Contains names of {@link DPUInstanceRecord}s. Used as a cache for
	 * generated column.
	 */
	private final Map<Long, String> dpuNames = new HashMap<>();

	private CachedSource dataSouce;
			
	
	protected int getPageSize() {
		return 28;
	}

	/**
	 * Default constructor.
	 */
	public LogTable() {
		
	}

	@PostConstruct
	private void initialize() {
		// bind container to the access
		dataSouce = new CachedSource<>(access, new NewLogAccessor(), coreFilters);
		container = new ReadOnlyContainer<>(dataSouce);
	}

	/**
	 * Build user interface.
	 */
	public void enter() {
		mainLayout = new VerticalLayout();

		table = new IntlibPagedTable();
		table.setSelectable(true);
		table.setSizeFull();
		table.setPageLength(getPageSize());

		table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
				if (!table.isSelected(event.getItemId())) {
					ValueItem item = (ValueItem) event.getItem();
					long logId = (long) item.getItemProperty("id").getValue();
					Log log = access.getInstance(logId);
					showLogDetail(log);
				}
			}
		});
		// add interpreter for dpu column
		table.addGeneratedColumn("dpu", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Long dpuId = (Long) source.getItem(itemId).getItemProperty(columnId).getValue();
				if (dpuId == null) {
					return null;
				}
				return dpuNames.get(dpuId);
			}
		});
		table.addGeneratedColumn("logLevel", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Integer level = (Integer) source.getItem(itemId).getItemProperty(columnId).getValue();
				return Level.toLevel(level);
			}
		});		

		// add filter generation
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
		table.setFilterGenerator(filterGenerator);
		table.setSortEnabled(false);
		table.setFilterBarVisible(true);		
		

		// add to the main layout
		mainLayout.addComponent(table);
		mainLayout.addComponent(table.createControls());

		Button download = new Button("Download");
		FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamResource() {
			@Override
			public String getFilename() {
				return "log.txt";
			}

			@Override
			public InputStream getStream() {
				// get current dpu
				DPUInstanceRecord dpu = (DPUInstanceRecord) table.getFilterFieldValue("dpu");
				Level level = Level.toLevel((Integer)table.getFilterFieldValue("logLevel"));
				String message = (String) table.getFilterFieldValue("message");
				String source = (String) table.getFilterFieldValue("source");
				Object date = table.getFilterFieldValue("timestamp");
				Date start = null;
				Date end = null;
				if (date != null) {
					DateInterval interval = (DateInterval) date;
					start = interval.getFrom();
					end = interval.getTo();
				}
				return logFacade.getLogsAsStream(execution, dpu, level, message, source, start, end);
			}
		});
		fileDownloader.extend(download);
		mainLayout.addComponent(download);
		setCompositionRoot(mainLayout);

	}

	public void setDpu(DPUInstanceRecord debugDpu) {
		this.dpu = debugDpu;
		refreshDpuSelector((ComboBox) table.getFilterField("dpuInstanceId"));
	}	
	
	public void setDpu(PipelineExecution exec, DPUInstanceRecord dpu, ReadOnlyContainer container) {
		this.dpu = dpu;
		this.execution = exec;	
		
		// set data source .. chat a littele and use our data source
		table.setContainerDataSource(this.container);
		
		coreFilters.clear();
		coreFilters.add(new Compare.Equal("execution", exec.getId()));
			
		// refresh dpu names list
		dpuNames.clear();
				
		for (Node node : exec.getPipeline().getGraph().getNodes()) {
			DPUInstanceRecord nodeDpu = node.getDpuInstance();
			dpuNames.put(nodeDpu.getId(), nodeDpu.getName());			
		}
		LOG.info("Node count: {} ", exec.getPipeline().getGraph().getNodes().size());

		// remove all filters
		this.container.removeAllContainerFilters();
		// move to the last page
		table.setCurrentPage(table.getTotalAmountOfPages());
		
		
		refreshDpuSelector((ComboBox) table.getFilterField("dpu"));
	}

	private void refreshDpuSelector(ComboBox dpuSelector) {
		dpuSelector.removeAllItems();
		ExecutionContextInfo ctx = execution.getContextReadOnly();
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
	
	/**
	 * Refresh data.
	 *
	 * @param immediate
	 * @param getNewData
	 * @return
	 */
	public boolean refresh(boolean immediate, boolean getNewData) {
		// invalidata all the data
		// TODO : load new data in background
		dataSouce.invalidate();
		// use direct refresh
		container.refresh();
		return true;
	}

	/**
	 * Show windows with detail of single log.
	 *
	 * @param log
	 */
	protected void showLogDetail(Log log) {
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
			UI.getCurrent().addWindow(detailWindow);
		} else {
			detail.loadMessage(log);
			detail.bringToFront();
		}
	}
	
	private FilterGenerator createFilterGenerator(final ComboBox dpuSelector, final ComboBox levelSelector) {
		return new FilterGenerator() {
			@Override
			public Container.Filter generateFilter(Object propertyId, Object value) {
				if (propertyId.equals("logLevel")) {
					//return new InFilter(App.getLogs().getLevels((Level) value), "levelString");
					LOG.info("createFilterGenerator.generateFilter.level");
					Level level = (Level)value;
					return new Compare.GreaterOrEqual("logLevel", level.toInt());
				} else if (propertyId.equals("timestamp")) {
					DateInterval interval = (DateInterval) value;
					if (interval.getFrom() == null) {
						return new Compare.LessOrEqual("timestamp", interval.getTo().getTime());
					} else if (interval.getTo() == null) {
						return new Compare.GreaterOrEqual("timestamp", interval.getFrom().getTime());
					} else {
						return new Between("timestamp", interval.getFrom().getTime(), interval.getTo().getTime());
					}
				} else if (propertyId.equals("dpu")) {
					LOG.info("createFilterGenerator.generateFilter.dpu : {} ", value);
					if (value != null) {
						Long id = ( (DPUInstanceRecord) value ).getId();
						return new Compare.Equal("dpu", id);
					} else {
						return null;
					}
				}
				return null;
			}

			@Override
			public AbstractField<?> getCustomFilterComponent(Object propertyId) {
				if (propertyId == null) {
					return null;
				}
				if (propertyId.equals("logLevel")) {
					return levelSelector;
				} else if (propertyId.equals("dpu")) {
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
	
}
