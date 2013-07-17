package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
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
import org.tepi.filtertable.FilterDecorator;

/**
 * Table with event records related to given pipeline execution.
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private boolean isInitialized = false;
	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;

	/**
	 * Default constructor. Initializes the layout.
	 */
	public RecordsTable() {
		mainLayout = new VerticalLayout();
		messageTable = new IntlibPagedTable();
		messageTable.setSelectable(true);
		messageTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//if (event.isDoubleClick()) {
				if (!messageTable.isSelected(event.getItemId())) {
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

	/**
	 * Sets data source.
	 *
	 * @param data List of {@link Record}s to show in table.
	 */
	public void setDataSource(List<Record> data) {
		loadMessageTable(data);
	}

	/**
	 * Loads data to the table.
	 *
	 * @param data List of {@link Record}s to show in table.
	 */
	private void loadMessageTable(List<Record> data) {
		Container container = ContainerFactory.CreateExecutionMessages(data);
		messageTable.setContainerDataSource(container);
		if (!isInitialized) {
			messageTable.addGeneratedColumn("type", new CustomTable.ColumnGenerator() {
				@Override
				public Object generateCell(CustomTable source, Object itemId,
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
			messageTable.setFilterDecorator(new filterDecorator());
			// set columns
			isInitialized = true;
		}
		messageTable.setVisibleColumns("time", "type", "dpuInstance", "shortMessage");
		messageTable.refreshRowCache();
	}

	/**
	 * Shows dialog with detail of selected record.
	 *
	 * @param record {@link Record} which detail to show.
	 */
	private void showRecordDetail(Record record) {
		final RecordDetail detail = new RecordDetail(record);
		Window detailWindow = new Window("Record detail", detail);
		detailWindow.setHeight(600, Unit.PIXELS);
		detailWindow.setWidth(400, Unit.PIXELS);
		detailWindow.setImmediate(true);
		detail.setContentHeight(600, Unit.PIXELS);
		detailWindow.addResizeListener(new Window.ResizeListener() {
			@Override
			public void windowResized(Window.ResizeEvent e) {
				detail.setContentHeight(e.getWindow().getHeight(), Unit.PIXELS);
			}
		});
		App.getApp().addWindow(detailWindow);
	}

	private class filterDecorator extends IntlibFilterDecorator {

		@Override
		public Resource getEnumFilterIcon(Object propertyId, Object value) {
			if (propertyId == "type") {
				ThemeResource img = null;
				RecordType type = (RecordType) value;
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
				return img;
			}
			return super.getEnumFilterIcon(propertyId, value);
		}
	};
}
