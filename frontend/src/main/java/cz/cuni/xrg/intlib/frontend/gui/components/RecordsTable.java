package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;

import java.util.List;

/**
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private Table messageTable;

	public RecordsTable() {
		messageTable = new Table();
		messageTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					BeanItem beanItem = (BeanItem) event.getItem();
					long recordId = (long)beanItem.getItemProperty("id").getValue();
					Record record = App.getDPUs().getDPURecord(recordId);
					showRecordDetail(record);
				}
			}
		});
		messageTable.setSizeFull();
		setCompositionRoot(messageTable);
	}

	public void setDataSource(List<Record> data) {
		loadMessageTable(data);
	}

	private void loadMessageTable(List<Record> data) {

		Container container = ContainerFactory.CreateExecutionMessages(data);
		messageTable.setContainerDataSource(container);

		// set columns
		messageTable.setVisibleColumns(new String[]{"time", "type", "dpuInstance",
			"shortMessage"});
	}

	private void showRecordDetail(Record record) {
		RecordDetail detail = new RecordDetail(record);
		Window detailWindow = new Window("Record detail", detail);
		App.getApp().addWindow(detailWindow);
	}
}
