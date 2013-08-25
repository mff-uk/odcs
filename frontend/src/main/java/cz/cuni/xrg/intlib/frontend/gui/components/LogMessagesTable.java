package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogFacade;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogMessage;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.frontend.container.InFilter;
import cz.cuni.xrg.intlib.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.xrg.intlib.frontend.container.PropertiesFilter;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.tepi.filtertable.FilterGenerator;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;
import org.vaadin.addons.lazyquerycontainer.LazyEntityContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryView;

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
	private LogMessageDetail detail = null;

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
		messageTable.setPageLength(16);
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
		levelSelector.setValue(Level.INFO);
//		levelSelector.addValueChangeListener(new Property.ValueChangeListener() {
//			@Override
//			public void valueChange(Property.ValueChangeEvent event) {
//				filterLogMessages((Level) event.getProperty().getValue());
//			}
//		});
		//mainLayout.addComponentAsFirst(levelSelector);

		setCompositionRoot(mainLayout);
	}

	/**
	 * Filters messages to show only messages of given level and more severe.
	 *
	 * @param level {@link Level} to filter log messages.
	 */
	private void filterLogMessages(Level level) {

		List<LogMessage> data = null; //getData(pipelineExecution, dpu, level);

		// ... filter
//		List<LogMessage> filteredData = new ArrayList<>();
//		for (LogMessage message : data) {
//			if (message.getLevel().isGreaterOrEqual(level)) {
//				filteredData.add(message);
//			}
//		}
		loadMessageTable(data);
	}

	/**
	 * Show log messages related only to given DPU. If null is passed, data for
	 * whole pipeline are shown.
	 *
	 * @param exec {@link PipelineExecution} which log to show.
	 * @param dpu {@link DPUInstanceRecord} or null.
	 */
	public void setDpu(PipelineExecution exec, DPUInstanceRecord dpu) {
		this.dpu = dpu;
		this.pipelineExecution = exec;

		Level level = Level.ALL;
		if (levelSelector.getValue() != null) {
			level = (Level) levelSelector.getValue();
		}

		List<LogMessage> data = null; //getData(pipelineExecution, dpu, level);
		//if(data != null) {
			loadMessageTable(data);
		//} else {
		//	Notification.show("Error", "Failed to load log messages from database!", Notification.Type.ERROR_MESSAGE);
		//}
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
	private void loadMessageTable(List<LogMessage> data) {
                
                
		IntlibLazyQueryContainer container = ContainerFactory.CreateLogMessages();
                container.addDefaultFilter(new PropertiesFilter(LogMessage.MDPU_EXECUTION_KEY_NAME, pipelineExecution.getId()));
                messageTable.setFilterGenerator(new FilterGenerator() {

                    @Override
                    public Container.Filter generateFilter(Object propertyId, Object value) {
                        if(propertyId.equals("level")) {
                            Level level = (Level)value;
                            return new InFilter(App.getLogs().getLevels((Level)value), "levelString");
                        }
                        return null;
                    }

                    @Override
                    public Container.Filter generateFilter(Object propertyId, Field<?> originatingField) {
                        if(propertyId.equals("level")) {
                            Level value = (Level)((ComboBox)originatingField).getValue();
                            return new InFilter(App.getLogs().getLevels(value), "levelString");
                        }
                        return null;
                    }

                    @Override
                    public AbstractField<?> getCustomFilterComponent(Object propertyId) {
                        if(propertyId.equals("level")) {
                            return levelSelector;
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
                });
		messageTable.setContainerDataSource(container);
		messageTable.setVisibleColumns("date", "level", "message", "source", LazyQueryView.DEBUG_PROPERTY_ID_QUERY_INDEX, LazyQueryView.DEBUG_PROPERTY_ID_BATCH_INDEX, LazyQueryView.DEBUG_PROPERTY_ID_BATCH_QUERY_TIME);

                messageTable.setFilterBarVisible(true);
                
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
}
