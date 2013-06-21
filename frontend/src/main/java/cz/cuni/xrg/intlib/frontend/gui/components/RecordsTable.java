package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.app.execution.Record;

import static cz.cuni.xrg.intlib.commons.app.execution.RecordType.*;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;

import java.util.List;

/**
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private boolean isInitialized = false;
	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;

	public RecordsTable() {
		mainLayout = new VerticalLayout();
		messageTable = new IntlibPagedTable();
		messageTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					BeanItem beanItem = (BeanItem) event.getItem();
					long recordId = (long) beanItem.getItemProperty("id")
							.getValue();
					Record record = App.getDPUs().getDPURecord(recordId);
					showRecordDetail(record);
				}
			}
		});
		messageTable.setSizeFull();
		mainLayout.addComponent(messageTable);
		mainLayout.addComponent(messageTable.createControls());
		messageTable.setPageLength(5);
		setCompositionRoot(mainLayout);
	}

	public void setDataSource(List<Record> data) {
		loadMessageTable(data);
	}

	private void loadMessageTable(List<Record> data) {
		Container container = ContainerFactory.CreateExecutionMessages(data);
		messageTable.setContainerDataSource(container);
		if (!isInitialized) {
			messageTable.addGeneratedColumn("type", new Table.ColumnGenerator() {
				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {

					RecordType type = (RecordType) source.getItem(itemId).getItemProperty(columnId).getValue();
					ThemeResource img = null;
					switch (type) {
						case DPU_INFO:
							img = new ThemeResource("icons/ok.png");
							break;
						case DPU_LOG:
							img = new ThemeResource("icons/log.png");
							break;
						case DPU_DEBUG:
							img = new ThemeResource("icons/debug.png");
							break;
						case DPU_WARNING:
							img = new ThemeResource("icons/warning.png");
							break;
						case DPU_ERROR:
						case PIPELINE_ERROR:
							img = new ThemeResource("icons/error.png");
							break;
						default:
							//no img
							break;
					}
					Embedded emb = new Embedded(type.name(), img);
					emb.setDescription(type.name());
					return emb;
				}
			});
			// set columns
			isInitialized = true;
		}
		messageTable.setVisibleColumns(
					new String[]{"time", "type", "dpuInstance",
				"shortMessage"});
	}

	private void showRecordDetail(Record record) {
		RecordDetail detail = new RecordDetail(record);
		Window detailWindow = new Window("Record detail", detail);
		App.getApp().addWindow(detailWindow);
	}
}
