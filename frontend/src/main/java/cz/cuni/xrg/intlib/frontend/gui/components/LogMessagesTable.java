package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.LogMessage;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 *	Component for viewing and filtering of log messages.
 * 
 * @author Bogo
 */
public class LogMessagesTable extends CustomComponent {

	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;
	private DPUInstanceRecord dpu;

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
		messageTable.setPageLength(25);

		ComboBox levelSelector = new ComboBox();
		levelSelector.setImmediate(true);
		levelSelector.setNullSelectionAllowed(false);
		levelSelector.addItem(Level.ALL);
		levelSelector.addItem(Level.INFO);
		levelSelector.addItem(Level.WARNING);
		levelSelector.addItem(Level.SEVERE);
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
	 * @param level 
	 */
	private void filterLogMessages(Level level) {
		//TODO: Replace with Facade call X store last data and use&filter them
		List<LogMessage> data = getData(dpu);
		List<LogMessage> filteredData = new ArrayList<>();
		for(LogMessage message : data) {
			if(message.getLevel().intValue() >= level.intValue()) {
				filteredData.add(message);
			}
		}
		loadMessageTable(filteredData);
	}

	/**
	 * Show log messages related only to given DPU. If null is passed, data for whole pipeline are shown.
	 * @param dpu DPU or null
	 */
	public void setDpu(DPUInstanceRecord dpu) {
		this.dpu = dpu;
		//TODO: Replace with Facade call - if null - get data for whole pipeline
		List<LogMessage> data = getData(dpu);
		loadMessageTable(data);
	}

	/**
	 * Initializes the table.
	 * @param data 
	 */
	private void loadMessageTable(List<LogMessage> data) {
		Container container = ContainerFactory.CreateLogMessages(data);
		messageTable.setContainerDataSource(container);


		messageTable.setVisibleColumns(
				new String[]{"date", "thread", "level",
			"source", "message"});
	}

	/**
	 * Stub method for providing stub data, until LogFacade is completed.
	 * @param dpu
	 * @return 
	 */
	private List<LogMessage> getData(DPUInstanceRecord dpu) {
		List<LogMessage> data = new ArrayList<>();
		data.add(new LogMessage(1L, Level.INFO, new Date(), "[pool-2-thread-1]", "c.c.x.i.b.execution.PipelineWorker", "Started"));
		data.add(new LogMessage(2L, Level.WARNING, new Date(), "[pool-2-thread-1]", "o.h.type.descriptor.sql.BasicBinder", "binding parameter [5] as [TIMESTAMP] - Mon Jun 24 08:32:29 CEST 2013"));
		data.add(new LogMessage(3L, Level.SEVERE, new Date(), "[pool-2-thread-1]", "o.o.rio.helpers.ParseErrorLogger", "'http://dbpedia.org/datatype/squareKilometre' is not recognized as a supported xsd datatype. (57, -1)"));

		return data;
	}
}
