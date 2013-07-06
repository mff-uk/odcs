package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.LogFacade;
import cz.cuni.xrg.intlib.commons.app.execution.LogMessage;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

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

	/**
	 * Default constructor.
	 */
	public LogMessagesTable() {
		mainLayout = new VerticalLayout();
		messageTable = new IntlibPagedTable();
		//messageTable.setSelectable(true);

		messageTable.setSizeFull();
		mainLayout.addComponent(messageTable);
		mainLayout.addComponent(messageTable.createControls());
		messageTable.setPageLength(19);

		ComboBox levelSelector = new ComboBox();
		levelSelector.setImmediate(true);
		levelSelector.setNullSelectionAllowed(false);
		levelSelector.addItem(Level.ALL);
		for (Level level : new Level[]{Level.INFO, Level.WARNING, Level.SEVERE}) {
			levelSelector.addItem(level);
			levelSelector.setItemCaption(level, level.getName() + "+");
		}
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
		
		Set<Level> levels = new HashSet<>();
		levels.add(level);
		
		List<LogMessage> data = getData(pipelineExecution, dpu, levels);
		
		// ... filter
		List<LogMessage> filteredData = new ArrayList<>();
		for (LogMessage message : data) {
			if (message.getLevel().intValue() >= level.intValue()) {
				filteredData.add(message);
			}
		}
		loadMessageTable(filteredData);
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
		
		Set<Level> levels = new HashSet<>();
		levels.add(Level.ALL);
		
		List<LogMessage> data = getData(pipelineExecution, dpu, levels);		
		loadMessageTable(data);
	}

	public List<LogMessage> getData(PipelineExecution exec, DPUInstanceRecord dpu, Set<Level> level) {
		LogFacade facade = App.getLogs();
		// TODO Add information about DPUInstanceRecord
		return facade.getLogs(exec, level);
	}
	
	/**
	 * Initializes the table.
	 *
	 * @param data List of {@link LogMessages} to show in table.
	 */
	private void loadMessageTable(List<LogMessage> data) {
		Container container = ContainerFactory.CreateLogMessages(data);
		
		messageTable.setContainerDataSource(container);
		messageTable.setVisibleColumns(
				new String[]{"date", "thread", "level",
			"source", "message"});
	}

}
