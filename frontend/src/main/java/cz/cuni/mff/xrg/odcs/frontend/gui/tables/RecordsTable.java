package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import static cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType.*;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.RecordDetail;

import java.util.Collection;

/**
 * Table with event records related to given pipeline execution.
 *
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private static final int PAGE_LENGTH = 20;
	private boolean isInitialized = false;
	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;
	private RecordDetail detail = null;

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
				if (!messageTable.isSelected(event.getItemId())) {
					ValueItem item = (ValueItem) event.getItem();
					long recordId = (long) item.getItemProperty("id")
							.getValue();
					MessageRecord record = App.getDPUs().getDPURecord(recordId);
					showRecordDetail(record);
				}
			}
		});
		messageTable.setSizeFull();
		mainLayout.addComponent(messageTable);
		mainLayout.addComponent(messageTable.createControls());
		messageTable.setPageLength(PAGE_LENGTH);
		//loadMessageTable();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Sets data source.
	 *
	 * @param data List of {@link MessageRecord}s to show in table.
	 */
	public void setPipelineExecution(PipelineExecution execution, boolean isRefresh, ReadOnlyContainer container) {
		loadMessageTable(container);
		ReadOnlyContainer c = (ReadOnlyContainer) messageTable.getContainerDataSource().getContainer();
		if (!isRefresh) {
			c.removeAllContainerFilters();
			c.addContainerFilter(new Compare.Equal("execution.id", execution.getId()));
		}
		c.refresh();
		messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
	}

	public void refresh() {
		ReadOnlyContainer c = (ReadOnlyContainer) messageTable.getContainerDataSource().getContainer();
		c.refresh();
		messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
	}

	/**
	 * Loads data to the table.
	 *
	 */
	private void loadMessageTable(ReadOnlyContainer container) {
		messageTable.setSortEnabled(true);
		messageTable.setContainerDataSource(container);
		if (!isInitialized) {
			messageTable.addGeneratedColumn("type", new CustomTable.ColumnGenerator() {
				@Override
				public Object generateCell(CustomTable source, Object itemId,
						Object columnId) {

					MessageRecordType type = (MessageRecordType) source.getItem(itemId).getItemProperty(columnId).getValue();
					ThemeResource img = null;
					switch (type) {
						case DPU_INFO:
						case PIPELINE_INFO:
						case DPU_TERMINATION_REQUEST:
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
			messageTable.addGeneratedColumn("", new CustomTable.ColumnGenerator() {
				@Override
				public Object generateCell(CustomTable source, Object itemId, Object columnId) {
					final Long dpuId = (Long) source.getItem(itemId).getItemProperty("dpuInstance.id").getValue();
					if (dpuId == null) {
						return null;
					}
					Button logsLink = new Button("Logs");
					logsLink.setStyleName(BaseTheme.BUTTON_LINK);
					logsLink.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(Button.ClickEvent event) {
							fireEvent(new OpenLogsEvent(RecordsTable.this, dpuId));
						}
					});
					return logsLink;
				}
			});
			messageTable.setVisibleColumns();
			messageTable.setFilterDecorator(new filterDecorator());
			// set columns
			isInitialized = true;
		}
		messageTable.setFilterBarVisible(true);
	}

	/**
	 * Inform listeners, that the detail is closing.
	 */
	protected void fireEvent(Event event) {
		Collection<Listener> ls = (Collection<Listener>) this.getListeners(Component.Event.class);
		for (Listener l : ls) {
			l.componentEvent(event);
		}
	}

	/**
	 * Shows dialog with detail of selected record.
	 *
	 * @param record {@link MessageRecord} which detail to show.
	 */
	private void showRecordDetail(MessageRecord record) {
		if (detail == null) {
			final RecordDetail detailWindow = new RecordDetail(record);
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
			detail.loadMessage(record);
			detail.bringToFront();
		}
	}

	private class filterDecorator extends IntlibFilterDecorator {

		@Override
		public Resource getEnumFilterIcon(Object propertyId, Object value) {
			//if (propertyId.equals("type")) {
			ThemeResource img = null;
			MessageRecordType type = (MessageRecordType) value;
			switch (type) {
				case DPU_INFO:
				case PIPELINE_INFO:
				case DPU_TERMINATION_REQUEST:
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
			//}
			//return super.getEnumFilterIcon(propertyId, value);
		}
	};
}
