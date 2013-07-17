package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
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
		if(levelSelector.getValue() != null) {
			level = (Level)levelSelector.getValue();
		}
		
		List<LogMessage> data = getData(pipelineExecution, dpu, level);		
		loadMessageTable(data);
	}

	public List<LogMessage> getData(PipelineExecution exec, DPUInstanceRecord dpu, Level level) {
		LogFacade facade = App.getLogs();
		
		Set<Level> levels = facade.getLevels(level);

		if(dpu == null) {
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
		messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
	}

}
