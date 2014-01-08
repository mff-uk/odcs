package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import static cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType.*;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.CachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.RecordDetail;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Table with event records related to given pipeline execution.
 *
 * @author Petyr
 * @author Bogo
 */
public class RecordsTable extends CustomComponent {

	private static final Logger LOG = LoggerFactory.getLogger(RecordsTable.class);
	private boolean isInitialized = false;
	private VerticalLayout mainLayout;
	private IntlibPagedTable messageTable;
	private RecordDetail detail = null;
	/**
	 * Access to data for retrieving detail log information. TODO replace with
	 * ContainerSource
	 */
	private final CachedSource<MessageRecord> dataSouce;
	private final ReadOnlyContainer<MessageRecord> container;

	/**
	 * Default constructor. Initializes the layout.
	 *
	 * @param dataSouce
	 */
	public RecordsTable(CachedSource<MessageRecord> dataSouce, int pageLenght) {
		this.dataSouce = dataSouce;
		this.container = new ReadOnlyContainer<>(dataSouce);
		//build layout
		buildLayout(pageLenght);
	}

	private void buildLayout(int pageLenght) {
		mainLayout = new VerticalLayout();
		messageTable = new IntlibPagedTable();
		messageTable.setSelectable(true);
		messageTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (!messageTable.isSelected(event.getItemId())) {
					ValueItem item = (ValueItem) event.getItem();

					final long recordId = item.getId();
					MessageRecord record = dataSouce.getObject(recordId);
					showRecordDetail(record);
				}
			}
		});
		messageTable.setSizeFull();
		messageTable.setPageLength(pageLenght);

		mainLayout.addComponent(messageTable);
		mainLayout.addComponent(messageTable.createControls());

		setCompositionRoot(mainLayout);
	}

	/**
	 * Sets the execution and refresh data.
	 *
	 * @param execution
	 */
	public void setExecution(PipelineExecution execution) {

		messageTable.setSortEnabled(true);

		container.removeAllContainerFilters();
		// set container to the table -- we may possibly re-set this
		// but that does not do anything bad
		messageTable.setContainerDataSource(container);

		if (isInitialized) {
			// all has been done .. just set the page to the
			// end and return
			messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
		} else {
			// add generated columns
			buildGeneratedColumns();
			isInitialized = true;
			messageTable.setCurrentPage(messageTable.getTotalAmountOfPages());
		}
	}

	/**
	 * Add generated columns and filters to the table.
	 */
	public void buildGeneratedColumns() {
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
	}

	/**
	 * Reload data from source, do not refresh the source it self!!
	 */
	public void refresh() {
		int lastPage = messageTable.getTotalAmountOfPages();
		if (messageTable.getCurrentPage() == lastPage) {
			container.refresh();
		} else {
			messageTable.setCurrentPage(lastPage);
		}
	}

	/**
	 * Inform listeners, that the detail is closing.
	 *
	 * @param event
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
			UI.getCurrent().addWindow(detailWindow);
		} else {
			detail.loadMessage(record);
			detail.bringToFront();
		}
	}

	public void setPageLength(int pageLength) {
		messageTable.setPageLength(pageLength);
	}

	/**
	 * Class for filter enchanting.
	 */
	private class filterDecorator extends IntlibFilterDecorator {

		@Override
		public Resource getEnumFilterIcon(Object propertyId, Object value) {
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
		}
	};
}
