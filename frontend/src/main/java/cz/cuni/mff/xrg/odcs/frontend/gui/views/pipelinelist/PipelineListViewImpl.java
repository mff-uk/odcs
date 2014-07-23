package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.paged.PagedFilterTable;
import org.tepi.filtertable.paged.PagedTableChangeEvent;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.ActionColumnGenerator;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PipelineEdit;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;

/**
 * Vaadin implementation for PipelineListView.
 * 
 * @author Bogo
 */
@Component
@Scope("prototype")
public class PipelineListViewImpl extends CustomComponent implements PipelineListPresenter.PipelineListView {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineListViewImpl.class);

    /**
     * Column widths for pipeline table.
     */
    private static final int COLUMN_ACTIONS_WIDTH = 324;

    private static final int COLUMN_STATUS_WIDTH = 68;

    private static final int COLUMN_DURATION_WIDTH = 80;

    private static final int COLUMN_TIME_WIDTH = 115;

    private VerticalLayout mainLayout;

    private IntlibPagedTable tablePipelines;

    private Button btnCreatePipeline;

    private Button btnImportPipeline;
    
    @Autowired
    private Utils utils;

    private void buildPage(final PipelineListPresenter presenter) {
        // top-level component properties
        setWidth("100%");
        // we do not set heigth, so it enables scroll bars

        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        HorizontalLayout topLine = new HorizontalLayout();
        topLine.setSpacing(true);

        btnCreatePipeline = new Button();
        btnCreatePipeline.setCaption("Create pipeline");
        btnCreatePipeline.setHeight("25px");
        btnCreatePipeline.setWidth("120px");
        btnCreatePipeline.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.navigateToEventHandler(PipelineEdit.class, "New");
            }
        });
        topLine.addComponent(btnCreatePipeline);

        btnImportPipeline = new Button();
        btnImportPipeline.setCaption("Import pipeline");
        btnImportPipeline.setHeight("25px");
        btnImportPipeline.setWidth("120px");
        btnImportPipeline.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.importPipeline();
            }
        });
        topLine.addComponent(btnImportPipeline);

        Button buttonDeleteFilters = new Button();
        buttonDeleteFilters.setCaption("Clear Filters");
        buttonDeleteFilters.setHeight("25px");
        buttonDeleteFilters.setWidth("120px");
        buttonDeleteFilters.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tablePipelines.resetFilters();
            }
        });
        topLine.addComponent(buttonDeleteFilters);

        mainLayout.addComponent(topLine);

        tablePipelines = new IntlibPagedTable();
        tablePipelines.setWidth("100%");

        mainLayout.addComponent(tablePipelines);
        mainLayout.addComponent(tablePipelines.createControls());
        tablePipelines.setPageLength(utils.getPageLength());
        tablePipelines.setColumnCollapsingAllowed(true);

        // add column
        tablePipelines.setImmediate(true);
        tablePipelines.addGeneratedColumn("actions", 0, createColumnGenerator(presenter));
        tablePipelines.setColumnWidth("actions", COLUMN_ACTIONS_WIDTH);
        tablePipelines.setColumnWidth("duration", COLUMN_DURATION_WIDTH);
        tablePipelines.setColumnWidth("lastExecStatus", COLUMN_STATUS_WIDTH);
        tablePipelines.setColumnWidth("lastExecTime", COLUMN_TIME_WIDTH);

        tablePipelines.setColumnAlignment("lastExecStatus", CustomTable.Align.CENTER);
        tablePipelines.setColumnAlignment("duration", CustomTable.Align.RIGHT);
        tablePipelines.setVisibleColumns();

        tablePipelines.setFilterBarVisible(true);
        tablePipelines.setFilterLayout();
        tablePipelines.setSelectable(true);
        tablePipelines.addItemClickListener(
                new ItemClickEvent.ItemClickListener() {
                    @Override
                    public void itemClick(ItemClickEvent event) {
                        if (!tablePipelines.isSelected(event.getItemId())) {
                            ValueItem item = (ValueItem) event.getItem();
                            final long pipelineId = item.getId();

                            presenter.navigateToEventHandler(PipelineEdit.class, pipelineId);
                        }
                    }
                });
        tablePipelines.addListener(new PagedFilterTable.PageChangeListener() {

            @Override
            public void pageChanged(PagedTableChangeEvent event) {
                int newPageNumber = event.getCurrentPage();
                presenter.pageChangedHandler(newPageNumber);
            }
        });
        tablePipelines.addItemSetChangeListener(new Container.ItemSetChangeListener() {

            @Override
            public void containerItemSetChange(Container.ItemSetChangeEvent event) {
                for (Object id : event.getContainer().getContainerPropertyIds()) {
                    Object filterValue = tablePipelines.getFilterFieldValue(id);
                    presenter.filterParameterEventHander((String) id, filterValue);
                }
            }
        });

        setCompositionRoot(mainLayout);
    }

    private ActionColumnGenerator createColumnGenerator(final PipelineListPresenter presenter) {

        ActionColumnGenerator generator = new ActionColumnGenerator();
        // add action buttons

        generator.addButton("Run", null, new ActionColumnGenerator.Action() {
            @Override
            protected void action(long id) {
                presenter.runEventHandler(id, false);
            }
        }, new ThemeResource("icons/running.png"));

        generator.addButton("Debug", null, new ActionColumnGenerator.Action() {
            @Override
            protected void action(long id) {
                presenter.runEventHandler(id, true);
            }
        }, new ThemeResource("icons/debug.png"));

        generator.addButton("Schedule", null, new ActionColumnGenerator.Action() {
            @Override
            protected void action(long id) {
                presenter.scheduleEventHandler(id);
            }
        }, new ThemeResource("icons/scheduled.png"));

        generator.addButton("Copy", null, new ActionColumnGenerator.Action() {
            @Override
            protected void action(long id) {
                presenter.copyEventHandler(id);
            }
        }, new ThemeResource("img/copy.png"));

        generator.addButton("Edit", null, new ActionColumnGenerator.Action() {
            @Override
            protected void action(long id) {
                presenter.navigateToEventHandler(PipelineEdit.class, id);
            }
        }, new ThemeResource("icons/gear.png"));

        generator.addButton("Delete", null, new ActionColumnGenerator.Action() {
            @Override
            protected void action(final long id) {
                presenter.deleteEventHandler(id);
            }
        }, new ActionColumnGenerator.ButtonShowCondition() {
            @Override
            public boolean show(CustomTable source, long id) {
                return presenter.canDeletePipeline(id);
            }
        }, new ThemeResource("icons/trash.png"));

        return generator;
    }

    @Override
    public Object enter(PipelineListPresenter presenter) {
    	if (!presenter.isLayoutInitialized()) {
    		buildPage(presenter);
		}

        return this;
    }

    @Override
    public void setDisplay(PipelineListPresenter.PipelineListData dataObject) {
        tablePipelines.setContainerDataSource(dataObject.getContainer());
    }

    @Override
    public void setPage(int pageNumber) {
        tablePipelines.setCurrentPage(pageNumber);
    }

    @Override
    public void setFilter(String key, Object value) {
        tablePipelines.setFilterFieldValue(key, value);
    }

    @Override
    public void refreshTableControls() {
        tablePipelines.setCurrentPage(tablePipelines.getCurrentPage());
    }
}
