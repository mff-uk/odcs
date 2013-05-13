package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import cz.cuni.xrg.intlib.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;

import java.util.List;

/**
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private Table messageTable;

	public RecordsTable(List<DPURecord> data ) {

		loadMessageTable(data);
		messageTable.setSizeFull();
		setCompositionRoot(messageTable);
	}

	private void loadMessageTable(List<DPURecord> data) {
		messageTable = new Table();
		Container container = ContainerFactory.CreateExecutionMessages(data);
		messageTable.setContainerDataSource(container);

		// set columns
		messageTable.setVisibleColumns(new String[]{"time", "type", "source",
					"shortMessage"});
	}
}
