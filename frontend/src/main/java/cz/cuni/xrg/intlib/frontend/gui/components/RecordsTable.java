package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import cz.cuni.xrg.intlib.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.commons.ExecutionMessage;
import java.util.List;

/**
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private Table messageTable;

	public RecordsTable(List<ExecutionMessage> data ) {

		loadMessageTable(data);
		setCompositionRoot(messageTable);
	}

	private void loadMessageTable(List<ExecutionMessage> data) {
		messageTable = new Table();
		Container container = ContainerFactory.CreateExecutionMessages(data);
		messageTable.setContainerDataSource(container);

		// set columns
		messageTable.setVisibleColumns(new String[]{"time", "type", "source",
					"shortMessage"});
	}
}
