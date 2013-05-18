package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;

import java.util.List;

/**
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private Table messageTable;

	public RecordsTable(List<Record> data ) {

		loadMessageTable(data);
		messageTable.setSizeFull();
		setCompositionRoot(messageTable);
	}

	private void loadMessageTable(List<Record> data) {
		messageTable = new Table();
		Container container = ContainerFactory.CreateExecutionMessages(data);
		messageTable.setContainerDataSource(container);

		// set columns
		messageTable.setVisibleColumns(new String[]{"time", "type", "dpuInstance",
					"shortMessage"});
	}
}
