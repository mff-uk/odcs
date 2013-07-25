package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.LogFacade;
import cz.cuni.xrg.intlib.commons.app.execution.LogMessage;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;

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
					BeanItem beanItem = (BeanItem) event.getItem();
					long logId = (long) beanItem.getItemProperty("id")
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
		levelSelector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				filterLogMessages((Level) event.getProperty().getValue());
			}
		});
		mainLayout.addComponentAsFirst(levelSelector);

		setCompositionRoot(mainLayout);
	}

	/**
	 * Filters messages to show only messages of given level and more severe.
	 *
	 * @param level {@link Level} to filter log messages.
	 */
	private void filterLogMessages(Level level) {

		List<LogMessage> data = getData(pipelineExecution, dpu, level);

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

		List<LogMessage> data = getData(pipelineExecution, dpu, level);
		if(data != null) {
			loadMessageTable(data);
		} else {
			Notification.show("Error", "Failed to load log messages from database!", Notification.Type.ERROR_MESSAGE);
		}
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
		Container container = ContainerFactory.CreateLogMessages(data);

		messageTable.setContainerDataSource(container);
		messageTable.setVisibleColumns("date", "thread", "level",
				"source", "message");
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
