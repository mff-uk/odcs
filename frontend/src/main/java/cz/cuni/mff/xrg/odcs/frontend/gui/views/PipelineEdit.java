package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.QUEUED;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.RUNNING;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthAwarePermissionEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.OpenEvent;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Position;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportService;
import cz.cuni.mff.xrg.odcs.commons.app.user.Role;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.PipelineHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUTree;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.PipelineConflicts;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.DetailClosedEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.FormattingEnabledEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.GraphChangedEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.PipelineCanvas;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.ResizedEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.ShowDebugEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.dialog.PipelineExport;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist.ExecutionListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;

/**
 * Page for creating new pipeline or editing existing pipeline.
 * 
 * @author Bogo
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@Address(url = "PipelineEdit")
public class PipelineEdit extends ViewComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineEdit.class);

    private VerticalLayout mainLayout;

    private GridLayout formattingBar;

    private Label label;

    private Label readOnlyLabel;

    private Label idLabel;

    private Label idValue;

    private Label author;

    private TextField pipelineName;

    private TextArea pipelineDescription;

    private OptionGroup pipelineVisibility;

    private Pipeline pipeline = null;

    PipelineCanvas pipelineCanvas;

    @Autowired
    DPUTree dpuTree;

    TabSheet tabSheet;

    DragAndDropWrapper dadWrapper;

    Panel canvasPanel;

    Button undo;

    /**
     * Constant representing standard mode of pipeline edit.
     */
    public final static String STANDARD_MODE = "standard_mode";

    /**
     * Constant representing develop mode of pipeline edit.
     */
    public final static String DEVELOP_MODE = "develop_mode";

    private String canvasMode = DEVELOP_MODE;

    private Tab standardTab;

    private Tab developTab;

    Button buttonSave;

    Button buttonSaveAndClose;

    Button buttonSaveAndCloseAndDebug;

    Button buttonCancel;

    Button buttonConflicts;

    Button buttonCopy;

    Button buttonCopyAndClose;

    Button buttonExport;

    private Button btnMinimize;

    private Button btnExpand;

    //Paralel editing components
    private Label editConflicts;

    private HorizontalLayout paralelInfoLayout;

    private Button buttonRefresh;

    private boolean isExpanded = true;

    private GridLayout pipelineSettingsLayout;

    HorizontalLayout buttonBar;

    private ShowDebugEvent sde;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private DPUFacade dpuFacade;

    private RefreshManager refreshManager;

    @Autowired
    private PipelineHelper pipelineHelper;

    @Autowired
    private PipelineConflicts conflictDialog;

    @Autowired
    private AuthenticationContext authCtx;

    /**
     * Evaluates permissions of currently logged in user.
     */
    @Autowired
    private AuthAwarePermissionEvaluator permissions;

    /**
     * Access to the application context in order to provide possiblity to
     * create dialogs. TODO: This is give us more power then we need, we should
     * use some dialog factory instead.
     */
    @Autowired
    private ApplicationContext context;

    @Autowired
    private ExportService exportService;

    /**
     * Empty constructor.
     */
    public PipelineEdit() {
        // put init code into enter method
    }

    /**
     * Enter method for PIPELINE_EDIT view.
     * 
     * @param event
     *            {@link ViewChangeEvent}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
        buildMainLayout();
        UI.getCurrent().getPage().addBrowserWindowResizeListener(new Page.BrowserWindowResizeListener() {

            @Override
            public void browserWindowResized(Page.BrowserWindowResizeEvent event) {
                setupComponentSize();
            }
        });
        setCompositionRoot(mainLayout);
        // ..
        this.loadPipeline(event);
        // or use this.entity.getEntity();

        if (this.pipeline == null) {
            label.setValue("<h3>Pipeline '" + event.getParameters() + "' doesn't exist.</h3>");
        } else {
            setMode(hasPermission("save"));
            label.setValue("<h3>Pipeline detail<h3>");
        }

        refreshManager.addListener(RefreshManager.PIPELINE_EDIT, new Refresher.RefreshListener() {

            private long lastRefreshFinished = 0;

            @Override
            public void refresh(Refresher source) {
                if (pipeline != null && new Date().getTime() - lastRefreshFinished > RefreshManager.MIN_REFRESH_INTERVAL) {
                    pipelineFacade.createOpenEvent(pipeline);
                    List<OpenEvent> openEvents = pipelineFacade.getOpenPipelineEvents(pipeline);
                    if (!pipelineFacade.isUpToDate(pipeline)) {
                        editConflicts.setValue("Another user made changes to the version you are editing, please refresh the pipeline detail!");
                        paralelInfoLayout.setVisible(true);
                        buttonRefresh.setVisible(true);
                    } else if (openEvents.isEmpty()) {
                        paralelInfoLayout.setVisible(false);
                    } else {
                        String message;
                        if (openEvents.size() == 1) {
                            message = String.format("User %s is also browsing this pipeline.", openEvents.get(0).getUser().getUsername());
                        } else {
                            String userList = "";
                            for (OpenEvent openEvent : openEvents) {
                                userList += String.format("%s %s", openEvents.indexOf(openEvent) == 0 ? "" : ",", openEvent.getUser().getUsername());
                            }
                            message = String.format("Users %s are also browsing this pipeline.", userList);
                        }
                        editConflicts.setValue(message);
                        paralelInfoLayout.setVisible(true);
                        buttonRefresh.setVisible(false);
                    }
                    lastRefreshFinished = new Date().getTime();
                }
                LOG.debug("Open pipelines checked.");
            }
        });

        //Resizing canvas
        UI.getCurrent().setImmediate(true);
    }

    /**
     * Builds main layout of the page.
     * 
     * @return {@link VerticalLayout} is the main layout of the view.
     */
    private VerticalLayout buildMainLayout() {
        isExpanded = true;

        //verticalSplit = new VerticalSplitPanel();
        //verticalSplit.setSizeFull();
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setMargin(true);
        mainLayout.setStyleName("mainLayout");

        // top-level component properties
        //setSizeUndefined();
        // label
        label = new Label();
        label.setImmediate(false);
        label.setWidth("-1px");
        label.setHeight("-1px");
        label.setContentMode(ContentMode.HTML);

        readOnlyLabel = new Label("Pipeline is open in read-only mode");
        readOnlyLabel.setStyleName("readOnlyLabel");
        readOnlyLabel.setVisible(false);

        HorizontalLayout topLine = new HorizontalLayout(label, readOnlyLabel);
        topLine.setComponentAlignment(readOnlyLabel, Alignment.MIDDLE_CENTER);
        btnMinimize = new Button();
        btnMinimize.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                setDetailState(false);
            }
        });
        btnMinimize.setStyleName(BaseTheme.BUTTON_LINK);
        btnMinimize.setIcon(new ThemeResource("icons/collapse.png"));
        btnMinimize.setDescription("Minimize pipeline detail");
        btnMinimize.setVisible(isExpanded);
        topLine.addComponent(btnMinimize);
        topLine.setExpandRatio(btnMinimize, 1.0f);
        topLine.setComponentAlignment(btnMinimize, Alignment.MIDDLE_RIGHT);
        btnExpand = new Button();
        btnExpand.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                setDetailState(true);
            }
        });
        btnExpand.setStyleName(BaseTheme.BUTTON_LINK);
        btnExpand.setIcon(new ThemeResource("icons/expand.png"));
        btnExpand.setDescription("Expand pipeline detail");
        btnExpand.setVisible(false);
        topLine.addComponent(btnExpand);
        topLine.setExpandRatio(btnExpand, 1.0f);
        topLine.setComponentAlignment(btnExpand, Alignment.MIDDLE_RIGHT);

        btnExpand.setVisible(!isExpanded);
        //topLine.setWidth("100%");
        mainLayout.addComponent(topLine);

        pipelineSettingsLayout = buildPipelineSettingsLayout();
        mainLayout.addComponent(pipelineSettingsLayout);

        CssLayout layout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                if (c instanceof TabSheet) {
                    return "margin-left: 0px; margin-top: 20px;";
                } else if (c instanceof Panel) {
                    return "position: fixed; left: 20px; top: 300px; max-height:600px; overflow-y:auto; overflow-x: hidden; max-width: 375px";
                } else if (c instanceof HorizontalLayout) {
                    if (c.equals(buttonBar)) {
                        return "position: fixed; bottom: 0px; left: 20px; background: #eee;";
                    } else if (c.equals(paralelInfoLayout)) {
                        return "position: fixed; left:400px; top: 300px;";
                    }
                } else if (c instanceof VerticalLayout) {
                    return "position: fixed; right: 40px; top: 300px;";
                }
                return null;
            }
        };

        //layout.setMargin(true);
        pipelineCanvas = ((AppEntry) UI.getCurrent()).getBean(PipelineCanvas.class);
        pipelineCanvas.setImmediate(true);
        pipelineCanvas.setWidth(1060, Unit.PIXELS);
        pipelineCanvas.setHeight(630, Unit.PIXELS);
        pipelineCanvas.init();
        pipelineCanvas.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event.getClass() != DetailClosedEvent.class) {
                    return;
                }
                DetailClosedEvent dce = (DetailClosedEvent) event;

                Class klass = dce.getDetailClass();
                if (klass == Node.class) {
                    dpuTree.refresh();
                    dpuTree.markAsDirty();
                    setupButtons();
                } else if (klass == Edge.class) {
                    setupButtons();
                }
            }
        });

        pipelineCanvas.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event.getClass() != ShowDebugEvent.class) {
                    return;
                }
                sde = (ShowDebugEvent) event;
                savePipeline("debug");
            }
        });
        pipelineCanvas.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event.getClass() != GraphChangedEvent.class) {
                    return;
                }

                if (((GraphChangedEvent) event).isUndoable()) {
                    undo.setEnabled(true);
                }
                setupButtons();

            }
        });
        pipelineCanvas.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event.getClass() != FormattingEnabledEvent.class) {
                    return;
                }
                formattingBar.setEnabled(((FormattingEnabledEvent) event).isEnabled());
            }
        });
        pipelineCanvas.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                if (event.getClass() != ResizedEvent.class) {
                    return;
                }
                ResizedEvent resizedEvent = (ResizedEvent) event;
                calculateCanvasDimensions(resizedEvent.getWidth(), resizedEvent.getHeight());
            }
        });

        dadWrapper = new DragAndDropWrapper(pipelineCanvas);
        dadWrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.NONE);
        dadWrapper.setWidth(1060, Unit.PIXELS);
        dadWrapper.setHeight(630, Unit.PIXELS);
        dadWrapper.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                if (canvasMode.equals(STANDARD_MODE)) {
                    return;
                }
                Transferable t = (Transferable) event.getTransferable();
                DragAndDropWrapper.WrapperTargetDetails details = (DragAndDropWrapper.WrapperTargetDetails) event.getTargetDetails();
                MouseEventDetails mouse = details.getMouseEvent();

                Object obj = t.getData("itemId");

                if (obj.getClass() == DPUTemplateRecord.class) {
                    DPUTemplateRecord dpu = (DPUTemplateRecord) obj;
                    if (dpuFacade.getAllTemplates().contains(dpu)) {
                        pipelineCanvas.addDpu(dpu, mouse.getClientX() - 20, mouse.getClientY() - (isExpanded ? 350 : 150));
                    } else {
                        LOG.warn("Invalid drop operation.");
                    }
                }

            }
        });

        tabSheet = new TabSheet();

        standardTab = tabSheet.addTab(new Label("Under construction"), "Standard");
        standardTab.setEnabled(true);

        //canvasPanel = new Panel(dadWrapper);
        developTab = tabSheet.addTab(dadWrapper, "Develop");
        tabSheet.setSelectedTab(developTab);
        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                if (event.getTabSheet().getSelectedTab().getClass() != DragAndDropWrapper.class) {
                    if (canvasMode.equals(STANDARD_MODE)) {
                        canvasMode = DEVELOP_MODE;
                        developTab.setCaption("Develop");
                        standardTab.setCaption("Standard");
                        tabSheet.setTabPosition(developTab, 1);
                    } else {
                        canvasMode = STANDARD_MODE;
                        standardTab.setCaption("Develop");
                        developTab.setCaption("Standard");
                        tabSheet.setTabPosition(developTab, 0);
                    }
                    pipelineCanvas.changeMode(canvasMode);
                    tabSheet.setSelectedTab(developTab);
                }
            }
        });
        tabSheet.setImmediate(true);

        layout.addComponent(tabSheet);

        Panel leftPanel = new Panel();
        //VerticalLayout left = new VerticalLayout();
        leftPanel.setStyleName("changingposition");
        //left.setWidth(250, Unit.PIXELS);
        dpuTree.setExpandable(true);
        dpuTree.setStyleName("dpuTree");
        dpuTree.setSizeUndefined();
        dpuTree.setDragable(true);
        dpuTree.fillTree();
        //left.addComponentAsFirst(dpuTree);
        leftPanel.setContent(dpuTree);
        leftPanel.setSizeUndefined();
        layout.addComponent(leftPanel);

        editConflicts = new Label();
        editConflicts.setImmediate(true);

        buttonRefresh = new Button("Refresh");
        buttonRefresh.setHeight("25px");
        buttonRefresh.setWidth("100px");
        buttonRefresh.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                refreshPipeline();
                setFields();
                paralelInfoLayout.setVisible(false);
            }
        });

        paralelInfoLayout = new HorizontalLayout(editConflicts, buttonRefresh);
        paralelInfoLayout.setSpacing(true);
        paralelInfoLayout.setVisible(false);
        paralelInfoLayout.addStyleName("editConflicts");
        paralelInfoLayout.addStyleName("changingposition");
        paralelInfoLayout.setSizeUndefined();
        layout.addComponent(paralelInfoLayout);

        Button zoomIn = new Button();
        zoomIn.setDescription("Zoom In");
        zoomIn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Position bounds = pipelineCanvas.zoom(true);
                calculateCanvasDimensions(bounds.getX(), bounds.getY());
            }
        });
        zoomIn.setIcon(new ThemeResource("icons/zoom_in.png"), "Zoom in");
        //zoomIn.setWidth("110px");
        Button zoomOut = new Button();
        zoomOut.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Position bounds = pipelineCanvas.zoom(false);
                calculateCanvasDimensions(bounds.getX(), bounds.getY());
            }
        });
        zoomOut.setDescription("Zoom out");
        zoomOut.setIcon(new ThemeResource("icons/zoom_out.png"), "Zoom out");
        //zoomOut.setWidth("110px");
        undo = new Button();
        undo.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (!pipelineCanvas.undo()) {
                    event.getButton().setEnabled(false);
                }
            }
        });
        undo.setEnabled(false);
        undo.setImmediate(true);
        undo.setDescription("Undo");
        undo.setIcon(new ThemeResource("icons/undo.png"), "Undo");
        //undo.setWidth("110px");
        HorizontalLayout topActions = new HorizontalLayout(zoomIn, zoomOut, undo);

        formattingBar = createFormattingBar();
        formattingBar.setEnabled(false);
        VerticalLayout actionBar = new VerticalLayout(topActions, formattingBar);
        actionBar.setStyleName("changingposition");
        actionBar.setSizeUndefined();

        layout.addComponent(actionBar);

        buttonBar = new HorizontalLayout();

        Button buttonRevert = new Button("Revert to last commit");
        buttonRevert.setHeight("25px");
        buttonRevert.setWidth("100px");
        buttonRevert.setEnabled(false);
        buttonRevert.setVisible(false);
        buttonRevert.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            }
        });
        buttonBar.addComponent(buttonRevert);

        HorizontalLayout leftPartOfButtonBar = new HorizontalLayout();
        leftPartOfButtonBar.setSpacing(true);
        leftPartOfButtonBar.setMargin(new MarginInfo(false, true, false, false));

        Button buttonValidate = new Button("Validate");
        buttonValidate.setHeight("25px");
        buttonValidate.setWidth("100px");
        buttonValidate.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                pipelineCanvas.validateGraph();
            }
        });
        leftPartOfButtonBar.addComponent(buttonValidate);

        buttonConflicts = new Button("Conflicts");
        buttonConflicts.setHeight("25px");
        buttonConflicts.setWidth("100px");
        buttonConflicts.setImmediate(true);
        buttonConflicts.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                showConflictPipeline();

            }
        });
        leftPartOfButtonBar.addComponent(buttonConflicts);
        buttonBar.addComponent(leftPartOfButtonBar);

        Button buttonCommit = new Button("Save & Commit");
        buttonCommit.setHeight("25px");
        buttonCommit.setWidth("100px");
        buttonCommit.setEnabled(false);
        buttonCommit.setVisible(false);
        buttonCommit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // save current pipeline
                savePipeline("none");
            }
        });
        buttonBar.addComponent(buttonCommit);

        HorizontalLayout rightPartOfButtonBar = new HorizontalLayout();
        rightPartOfButtonBar.setSpacing(true);
        rightPartOfButtonBar.setMargin(new MarginInfo(false, false, false, true));

        HorizontalLayout copyLayout = new HorizontalLayout();

        buttonCopy = new Button("Copy");
        buttonCopy.setHeight("25px");
        buttonCopy.setWidth("100px");
        buttonCopy.setImmediate(true);
        buttonCopy.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // save current pipeline
                if (!pipelineFacade.isUpToDate(pipeline)) {
                    ConfirmDialog.show(UI.getCurrent(), "Copying not actual version", "You are copying version, which is not actual. If saved, you won't see changes made by other user.", "Copy anyway", "Cancel", new ConfirmDialog.Listener() {
                        @Override
                        public void onClose(ConfirmDialog cd) {
                            if (cd.isConfirmed()) {
                                savePipelineAsNew();
                                paralelInfoLayout.setVisible(false);
                            }
                        }
                    });
                } else {
                    savePipelineAsNew();
                }
            }
        });
        copyLayout.addComponent(buttonCopy);
        buttonCopyAndClose = new Button("Copy & Close");

        buttonCopyAndClose.setHeight("25px");
        buttonCopyAndClose.setWidth("100px");
        buttonCopyAndClose.setImmediate(true);
        buttonCopyAndClose.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (!pipelineFacade.isUpToDate(pipeline)) {
                    ConfirmDialog.show(UI.getCurrent(), "Copying not actual version", "You are copying version, which is not actual. If saved, you won't see changes made by other user.", "Copy anyway", "Cancel", new ConfirmDialog.Listener() {
                        @Override
                        public void onClose(ConfirmDialog cd) {
                            if (cd.isConfirmed()) {
                                savePipelineAsNew();
                                closeView();
                            }
                        }
                    });
                } else {
                    savePipelineAsNew();
                    closeView();
                }
            }
        });
        copyLayout.addComponent(buttonCopyAndClose);
        rightPartOfButtonBar.addComponent(copyLayout);

        HorizontalLayout saveLayout = new HorizontalLayout();

        buttonSave = new Button("Save");
        buttonSave.setHeight("25px");
        buttonSave.setWidth("100px");
        buttonSave.setImmediate(true);
        buttonSave.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // save current pipeline
                savePipeline("reload");
            }
        });
        saveLayout.addComponent(buttonSave);

        buttonSaveAndClose = new Button("Save & Close");
        buttonSaveAndClose.setHeight("25px");
        buttonSaveAndClose.setWidth("100px");
        buttonSaveAndClose.setImmediate(true);
        buttonSaveAndClose.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // save current pipeline
                savePipeline("close");
            }
        });
        saveLayout.addComponent(buttonSaveAndClose);
        buttonSaveAndCloseAndDebug = new Button("Save & Close & Debug");
        buttonSaveAndCloseAndDebug.setHeight("25px");
        //buttonSaveAndCloseAndDebug.setWidth("100px");
        buttonSaveAndCloseAndDebug.setImmediate(true);
        buttonSaveAndCloseAndDebug.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // save current pipeline
                savePipeline("close&debug");
            }
        });
        saveLayout.addComponent(buttonSaveAndCloseAndDebug);

        rightPartOfButtonBar.addComponent(saveLayout);

        buttonCancel = new Button("Close");

        buttonCancel.setHeight("25px");
        buttonCancel.setWidth("100px");
        buttonCancel.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                //pipelineName.discard();
                //pipelineDescription.discard();
                //pipelineCanvas.cancelChanges();
                closeView();
            }
        });
        rightPartOfButtonBar.addComponent(buttonCancel);

        buttonExport = new Button("Export");
        buttonExport.setHeight("25px");
        buttonExport.setWidth("100px");
        buttonExport.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final PipelineExport dialog = new PipelineExport(exportService, pipeline);
                UI.getCurrent().addWindow(dialog);
                dialog.bringToFront();
            }
        });

        rightPartOfButtonBar.addComponent(buttonExport);
        buttonBar.addComponent(rightPartOfButtonBar);

        buttonBar.setSpacing(true);
        layout.addComponent(buttonBar);

        mainLayout.addComponent(layout);
        Position bounds = pipelineCanvas.zoom(true);

        calculateCanvasDimensions(bounds.getX(), bounds.getY());
        return mainLayout;
    }

    private void setupComponentSize() {
        int browserWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 60;
        int browserHeight = UI.getCurrent().getPage().getBrowserWindowHeight();
        if (pipelineCanvas.getCanvasWidth() < browserWidth) {
            tabSheet.setWidth(pipelineCanvas.getCanvasWidth() + 40, Unit.PIXELS);
        } else {
            tabSheet.setWidth(100, Unit.PERCENTAGE);
        }
        int tabSheetHeight = browserHeight - (isExpanded ? 340 : 150);
        tabSheet.setHeight(Math.min(tabSheetHeight, pipelineCanvas.getCanvasHeight() + 60), Unit.PIXELS);
        tabSheet.markAsDirty();
    }

    private void showConflictPipeline() {

        // open scheduler dialog
        if (!conflictDialog.isInitialized()) {
            conflictDialog.init();
            conflictDialog.addCloseListener(new CloseListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void windowClose(CloseEvent e) {
                    setupButtons(conflictDialog.getResult());
                }
            });
        }

        // in every case set the data
        conflictDialog.setData(pipeline);

        if (!UI.getCurrent().getWindows().contains(conflictDialog)) {
            UI.getCurrent().addWindow(conflictDialog);
        }
    }

    /**
     * Check for permission.
     * 
     * @param type
     *            Required permission.
     * @return If the user has given permission
     */
    public boolean hasPermission(String type) {
        return permissions.hasPermission(pipeline, type);
    }

    private void setDetailState(boolean expand) {
        isExpanded = expand;
        btnMinimize.setVisible(expand);
        btnExpand.setVisible(!expand);
        pipelineSettingsLayout.setVisible(expand);
        setupComponentSize();
    }

    /**
     * Builds part of layout with pipeline settings.
     * 
     * @return {@link GridLayout} contains controls with information about
     *         pipeline settings.
     * @throws com.vaadin.ui.GridLayout.OverlapsException
     * @throws com.vaadin.ui.GridLayout.OutOfBoundsException
     */
    private GridLayout buildPipelineSettingsLayout() throws OverlapsException, OutOfBoundsException {

        pipelineSettingsLayout = new GridLayout(3, 5);
        pipelineSettingsLayout.setWidth(600, Unit.PIXELS);
        idLabel = new Label("ID");
        idLabel.setSizeUndefined();
        pipelineSettingsLayout.addComponent(idLabel, 0, 4);
        idValue = new Label("ID");
        idValue.setSizeUndefined();
        pipelineSettingsLayout.addComponent(idValue, 1, 4);
        Label nameLabel = new Label("Name");
        nameLabel.setImmediate(false);
        nameLabel.setSizeUndefined();
        pipelineSettingsLayout.addComponent(nameLabel, 0, 0);
        pipelineName = new TextField();
        pipelineName.setImmediate(true);
        pipelineName.setWidth("400px");
        pipelineName.setHeight("-1px");
        pipelineName.setBuffered(true);
        pipelineName.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws Validator.InvalidValueException {
                if (value.getClass() == String.class && !((String) value).isEmpty()) {
                    return;
                }
                throw new Validator.InvalidValueException("Name must be filled!");
            }
        });
        pipelineName.addValidator(new MaxLengthValidator(LenghtLimits.PIPELINE_NAME));
        pipelineName.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                setupButtons(true);
            }
        });
        pipelineSettingsLayout.addComponent(pipelineName, 1, 0);
        Label descriptionLabel = new Label("Description");
        descriptionLabel.setImmediate(false);
        descriptionLabel.setWidth("-1px");
        descriptionLabel.setHeight("-1px");
        pipelineSettingsLayout.addComponent(descriptionLabel, 0, 1);
        pipelineDescription = new TextArea();
        pipelineDescription.setImmediate(true);
        pipelineDescription.setWidth("400px");
        pipelineDescription.setHeight("60px");
        pipelineDescription.setBuffered(true);
        pipelineDescription.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                setupButtons(true);
            }
        });
        pipelineSettingsLayout.addComponent(pipelineDescription, 1, 1);

        Label visibilityLabel = new Label("Visibility");
        pipelineSettingsLayout.addComponent(visibilityLabel, 0, 2);

        pipelineVisibility = new OptionGroup();
        pipelineVisibility.addStyleName("horizontalgroup");
        pipelineVisibility.addItem(ShareType.PRIVATE);
        pipelineVisibility.setItemCaption(ShareType.PRIVATE, ShareType.PRIVATE.getName());
        pipelineVisibility.addItem(ShareType.PUBLIC_RO);
        pipelineVisibility.setItemCaption(ShareType.PUBLIC_RO, ShareType.PUBLIC_RO.getName());
        pipelineVisibility.addItem(ShareType.PUBLIC_RW);
        pipelineVisibility.setItemCaption(ShareType.PUBLIC_RW, ShareType.PUBLIC_RW.getName());
        pipelineVisibility.setImmediate(true);
        pipelineVisibility.setBuffered(true);
        pipelineVisibility.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                setupButtons(true);
            }
        });
        pipelineSettingsLayout.addComponent(pipelineVisibility, 1, 2);

        pipelineSettingsLayout.addComponent(new Label("Created by"), 0, 3);

        author = new Label();
        pipelineSettingsLayout.addComponent(author, 1, 3);

//		Label permissionLabel = new Label("Permissions");
//		permissionLabel.setImmediate(false);
//		permissionLabel.setWidth("-1px");
//		permissionLabel.setHeight("-1px");
//		pipelineSettingsLayout.addComponent(permissionLabel, 0, 2);
//
//		Table permissionsTable = new Table();
//
//		class actionColumnGenerator implements com.vaadin.ui.Table.ColumnGenerator {
//
//			@Override
//			public Object generateCell(final Table source, final Object itemId, Object columnId) {
//				HorizontalLayout layout = new HorizontalLayout();
//
//				// get item
//				final BeanItem<Pipeline> item = (BeanItem<Pipeline>) source.getItem(itemId);
//
//				Button daleteButton = new Button();
//				daleteButton.setCaption("delete");
//				daleteButton.addClickListener(new com.vaadin.ui.Button.ClickListener() {
//
//					@Override
//					public void buttonClick(ClickEvent event) {
//						//TODO: Delete permission record
//					}
//				});
//				layout.addComponent(daleteButton);
//
//				return layout;
//			}
//		}
//		TODO: Have some object for representing permissions for pipeline by user
//		permissionsTable = new Table();
//		permissionsTable.setWidth("400px");
//		permissionsTable.setHeight("100px");
//		//TODO: assign data source
//		Container container = ContainerFactory.CreatePipelines(App.getApp().getPipelines().getAllPipelines());
//		//permissionsTable.setContainerDataSource(container);
//
//		// set columns
//		permissionsTable.setVisibleColumns(new String[] {"User", "Read (Copy, Run)", "Developer"});
//
//		// add column
//		permissionsTable.addGeneratedColumn("Actions", new actionColumnGenerator() );
//		pipelineSettingsLayout.addComponent(permissionsTable, 1, 2);
        pipelineSettingsLayout.setStyleName("pipelineSettingsLayout");
        pipelineSettingsLayout.setMargin(true);
        pipelineSettingsLayout.setSpacing(true);

        //pipelineSettingsLayout.setWidth("100%");
        return pipelineSettingsLayout;
    }

    @Override
    public boolean isModified() {
        return (pipelineName.isModified() || pipelineDescription.isModified() || pipelineCanvas.isModified() || pipelineVisibility.isModified()) && hasPermission("save");
    }

    @Override
    public boolean saveChanges() {
        return savePipeline("none");
    }

    private void setupButtons() {
        setupButtons(isModified());
    }

    private void setupButtons(boolean isModified) {
        setupButtons(isModified, this.pipeline.getId() == null);
    }

    private void savePipelineAsNew() {
        if (!pipelineFacade.isUpToDate(pipeline)) {
        }
        if (!validate()) {
            return;
        }
        pipeline.setName(pipelineName.getValue());
        pipelineCanvas.saveGraph(pipeline);
        Pipeline copiedPipeline = pipelineFacade.copyPipeline(pipeline);
        pipelineName.setValue(copiedPipeline.getName());
        setIdLabel(copiedPipeline.getId());
        author.setValue(copiedPipeline.getOwner().getUsername());
        pipeline = copiedPipeline;
        finishSavePipeline(false, ShareType.PRIVATE, "reload");
        setMode(true);
    }

    /**
     * Return true if given string is positive number.
     * 
     * @param str
     *            {@link String} to check
     * @return True if given string is positive number, false otherwise.
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(str.charAt(i))) {
            } else {
                return false;
            }
        }
        return true;
    }

    private void setupButtons(boolean enabled, boolean isNew) {
        buttonSave.setEnabled(enabled && hasPermission("save"));
        buttonSaveAndClose.setEnabled(enabled && hasPermission("save"));
        buttonSaveAndCloseAndDebug.setEnabled(enabled && hasPermission("save"));
        buttonCopy.setEnabled(!isNew && hasPermission("copy"));
        buttonCopyAndClose.setEnabled(!isNew && hasPermission("copy"));
        // we reuse copy permision for exportPipeline
        buttonExport.setEnabled(hasPermission("copy"));
    }

    /**
     * Closes the view and returns to View which user came from, if any.
     */
    private void closeView() {
        ((AppEntry) UI.getCurrent()).navigateToLastView();
    }

    /**
     * Opens given {@link DebuggingView} in new window.
     * 
     * @param debug
     *            {@link DebuggingView} to show.
     */
    private void openDebug(PipelineExecution pExec, final Pipeline pip, final Node debugNode) {
        if (pExec == null) {
            //Solved by dialog if backend is offline in method runPipeline.
            //Notification.show("Pipeline execution failed!", Notification.Type.ERROR_MESSAGE);
            return;
        }
        final DPUInstanceRecord instance = debugNode.getDpuInstance();
        final DebuggingView debug = context.getBean(DebuggingView.class);

        debug.initialize(pExec, instance, true, true);
        debug.setExecution(pExec, instance);

        final Window debugWindow = new Window("Debug window");
        HorizontalLayout buttonLine = new HorizontalLayout();
        buttonLine.setSpacing(true);
        buttonLine.setWidth(100, Unit.PERCENTAGE);
        Button rerunButton = new Button("Rerun", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                PipelineExecution pExec = pipelineHelper.runPipeline(pip, true, debugNode);
                if (pExec == null) {
                    //Solved by dialog if backend is offline in method runPipeline.
                    return;
                }
                debug.setExecution(pExec, instance);
            }
        });
        rerunButton.setWidth(100, Unit.PIXELS);
        buttonLine.addComponent(rerunButton);
        Button closeButton = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                debugWindow.close();
            }
        });
        closeButton.setWidth(100, Unit.PIXELS);
        buttonLine.addComponent(closeButton);
        Label topLineFiller = new Label();
        buttonLine.addComponentAsFirst(topLineFiller);
        buttonLine.setExpandRatio(topLineFiller, 1.0f);

        VerticalLayout layout = new VerticalLayout(debug, buttonLine);
        debugWindow.setContent(layout);

        debugWindow.setImmediate(true);
        debugWindow.setWidth("700px");
        debugWindow.setHeight("850px");
        debugWindow.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent e) {
                refreshManager.removeListener(RefreshManager.DEBUGGINGVIEW);
            }
        });
        debugWindow.addResizeListener(new Window.ResizeListener() {
            @Override
            public void windowResized(Window.ResizeEvent e) {
                debug.resize(e.getWindow().getHeight());
            }
        });

        if (pExec.getStatus() == RUNNING || pExec.getStatus() == QUEUED) {
            refreshManager.addListener(RefreshManager.DEBUGGINGVIEW, RefreshManager.getDebugRefresher(debug, pExec, pipelineFacade));
        }
        UI.getCurrent().addWindow(debugWindow);
    }

    /**
     * Loads pipeline with given id from database.
     * 
     * @param id
     *            {@link String} with id of {@link Pipeline} to load
     * @return {@link Pipeline} with given id.
     */
    protected Pipeline loadPipeline(String id) {
        // get data from DB ..
        this.pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
        setIdLabel(pipeline.getId());
        author.setValue(pipeline.getOwner().getUsername());
        pipelineName.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getName()));
        pipelineDescription.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getDescription()));
        pipelineVisibility.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getShareType()));
        setupVisibilityOptions(this.pipeline.getShareType());
        setupButtons(false);
        return pipeline;
    }

    /**
     * Loads pipeline to edit or create. Pipeline entity is loaded into
     * this.entity. If /New parameter is passed in url, create just
     * representation for pipeline.
     * 
     * @param event
     *            {@link ViewChangeEvent} passed from enter method.
     * @return Loaded pipeline class instance or null.
     */
    protected Pipeline loadPipeline(ViewChangeEvent event) {
        // some information text ...
        String pipeIdstr = event.getParameters();
        if (isInteger(pipeIdstr)) {
            // use pipeIdstr as id
            this.pipeline = loadPipeline(pipeIdstr);
        } else {
            // create empty, for new record
            this.pipeline = pipelineFacade.createPipeline();
            pipeline.setName("");
            pipeline.setDescription("");
            pipeline.setShareType(ShareType.PRIVATE);
            pipelineName.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getName()));
            setIdLabel(null);
            author.setValue(authCtx.getUsername());
            pipelineDescription.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getDescription()));
            pipelineVisibility.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getShareType()));
            setupButtons(false);
            pipelineName.setInputPrompt("Insert pipeline name");
            pipelineDescription.setInputPrompt("Insert pipeline description");
        }

        if (pipeline != null) {
            pipelineCanvas.showPipeline(pipeline);
        }
        return this.pipeline;
    }

    /**
     * Saves current pipeline.
     * 
     * @param successAction
     * @return If current pipeline was saved
     */
    protected boolean savePipeline(final String successAction) {
        if (!validate()) {
            return false;
        }

        final boolean doCleanup = pipelineCanvas.saveGraph(pipeline);

        final ShareType visibility = (ShareType) pipelineVisibility.getValue();
        if (!pipelineFacade.isUpToDate(pipeline)) {
            ConfirmDialog.show(UI.getCurrent(), "Overwriting pipeline", "You are editing version, which is not actual. If saved, changes made by other user will be overwritten.", "Save anyway", "Cancel", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog cd) {
                    if (cd.isConfirmed()) {
                        finishSavePipeline(doCleanup, visibility, successAction);
                        paralelInfoLayout.setVisible(false);
                    }
                }
            });
            return false;
        } else if (pipeline.getShareType() == ShareType.PRIVATE && ShareType.PUBLIC.contains(visibility) && !pipelineFacade.getPrivateDPUs(pipeline).isEmpty()) {
            ConfirmDialog.show(UI.getCurrent(), "Saving public pipeline", "Saving pipeline as public will cause all DPU templates, the pipeline is using, to become public. When they become public, they cannot be reverted to private.", "Save", "Cancel", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog cd) {
                    if (cd.isConfirmed()) {
                        finishSavePipeline(doCleanup, visibility, successAction);
                    }
                }
            });
            return false;
        } else {
            return finishSavePipeline(doCleanup, visibility, successAction);
        }
    }

    private boolean finishSavePipeline(boolean doCleanup, ShareType visibility, String successAction) {
        setupVisibilityOptions(visibility);

        undo.setEnabled(false);
        this.pipeline.setName(pipelineName.getValue());
        pipelineName.commit();
        this.pipeline.setDescription(pipelineDescription.getValue());
        pipelineDescription.commit();

        this.pipeline.setShareType(visibility);
        pipelineVisibility.commit();

        pipelineFacade.save(this.pipeline);
        if (doCleanup) {
            pipelineCanvas.afterSaveCleanUp();
        }

        Notification.show("Pipeline saved successfully!", Notification.Type.HUMANIZED_MESSAGE);
        setupButtons();

        switch (successAction) {
            case "debug":
                refreshPipeline();
                PipelineExecution pExec = pipelineHelper.runPipeline(pipeline, true, sde.getDebugNode());
                openDebug(pExec, pipeline, sde.getDebugNode());
                break;
            case "close":
                closeView();
                break;
            case "reload":
                refreshPipeline();
                break;
            case "close&debug":
                PipelineExecution exec = pipelineHelper.runPipeline(pipeline, true);
                if (exec != null) {
                    ((AppEntry) UI.getCurrent()).getNavigation().navigateTo(ExecutionListPresenterImpl.class, String.format("exec=%s", exec.getId()));
                }
                break;
            default:
                return true;
        }
        return true;
    }

    /**
     * Calculates and sets canvas dimensions according to current size of
     * browser window and pipeline graph's bounds.
     * 
     * @param zoomBounds
     *            {@link Position} with bounds of pipeline graph.
     */
    private void calculateCanvasDimensions(int width, int height) {
//		int minWidth = UI.getCurrent().getPage().getBrowserWindowWidth() - 100;
//		int minHeight = (int)tabSheet.getHeight() - 60;
//		if (width < minWidth) {
//			width = minWidth;
//			//enable horizontal scrollbar
//		}
//		if (height < minHeight) {
//			height = minHeight;
//			//enable vertical scrollbar
//		}
        pipelineCanvas.setWidth(width, Unit.PIXELS);
        pipelineCanvas.setHeight(height, Unit.PIXELS);
        dadWrapper.setSizeUndefined();
        setupComponentSize();
    }

    /**
     * Validates fields with requirements on input. Shows errors as
     * notification.
     * 
     * @return validation result
     */
    private boolean validate() {
        try {
            pipelineName.validate();
            pipelineDescription.validate();
            pipelineVisibility.validate();
            if (pipelineFacade.hasPipelineWithName(pipelineName.getValue(), pipeline)) {
                throw new Validator.InvalidValueException("Pipeline with same name already exists in the system.");
            }
        } catch (Validator.InvalidValueException e) {
            Notification.show("Error saving pipeline", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void setMode(boolean isDevelop) {
        readOnlyLabel.setVisible(!isDevelop);
        if (isDevelop) {
            canvasMode = DEVELOP_MODE;
            standardTab.setCaption("Standard");
            standardTab.setEnabled(false);
            developTab.setCaption("Develop");
            tabSheet.setTabPosition(developTab, 0);
            pipelineCanvas.changeMode(canvasMode);
        } else {
            canvasMode = STANDARD_MODE;
            standardTab.setCaption("Develop");
            standardTab.setEnabled(false);
            developTab.setCaption("Standard");
            tabSheet.setTabPosition(developTab, 0);
            pipelineCanvas.changeMode(canvasMode);
        }
    }

    private void refreshPipeline() {
        pipeline = pipelineFacade.getPipeline(pipeline.getId());
        setIdLabel(pipeline.getId());
        author.setValue(pipeline.getOwner().getUsername());
        pipelineCanvas.showPipeline(pipeline);
    }

    private void setFields() {
        setIdLabel(pipeline.getId());
        pipelineName.setValue(this.pipeline.getName());
        pipelineDescription.setValue(this.pipeline.getDescription());
        pipelineVisibility.setValue(this.pipeline.getShareType());
        setupVisibilityOptions(this.pipeline.getShareType());
        setupButtons(false);
    }

    private GridLayout createFormattingBar() {

        ClickListener listener = new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                pipelineCanvas.formatAction((String) event.getButton().getData());
            }
        };

        GridLayout bar = new GridLayout(3, 3);
        Button topAlign = new Button();
        topAlign.setData("align_top");
        topAlign.setDescription("Align top");
        topAlign.setIcon(new ThemeResource("icons/arrow_top.png"), "Align top");
        topAlign.addClickListener(listener);
        bar.addComponent(topAlign, 1, 0);

        Button bottomAlign = new Button();
        bottomAlign.setData("align_bottom");
        bottomAlign.setDescription("Align bottom");
        bottomAlign.setIcon(new ThemeResource("icons/arrow_bottom.png"), "Align bottom");
        bottomAlign.addClickListener(listener);
        bar.addComponent(bottomAlign, 1, 2);

        Button leftAlign = new Button();
        leftAlign.setData("align_left");
        leftAlign.setDescription("Align left");
        leftAlign.setIcon(new ThemeResource("icons/arrow_left.png"), "Align left");
        leftAlign.addClickListener(listener);
        bar.addComponent(leftAlign, 0, 1);

        Button rightAlign = new Button();
        rightAlign.setData("align_right");
        rightAlign.setDescription("Align right");
        rightAlign.setIcon(new ThemeResource("icons/arrow_right.png"), "Align right");
        rightAlign.addClickListener(listener);
        bar.addComponent(rightAlign, 2, 1);

        Button distributeHorizontal = new Button();
        distributeHorizontal.setData("distribute_horizontal");
        distributeHorizontal.setDescription("Distribute horizontally");
        distributeHorizontal.setIcon(new ThemeResource("icons/distribute.png"), "Distribute horizontally");
        distributeHorizontal.addClickListener(listener);
        bar.addComponent(distributeHorizontal, 2, 0);

        Button distributeVertical = new Button();
        distributeVertical.setData("distribute_vertical");
        distributeVertical.setDescription("Distribute vertically");
        distributeVertical.setIcon(new ThemeResource("icons/distribute_v.png"), "Distribute vertically");
        distributeVertical.addClickListener(listener);
        bar.addComponent(distributeVertical, 2, 2);

        return bar;
    }

    private void setIdLabel(Long id) {
        boolean hasId = id != null;
        if (id != null) {
            idValue.setValue(id.toString());
        }
        idValue.setVisible(hasId);
        idLabel.setVisible(hasId);
    }

    private void setupVisibilityOptions(ShareType visibility) {
        pipelineVisibility.setItemEnabled(ShareType.PRIVATE, visibility == ShareType.PRIVATE);
        boolean publicRoAvalilable = visibility != ShareType.PUBLIC_RW || authCtx.getUser().equals(this.pipeline.getOwner()) || authCtx.getUser().getRoles().contains(Role.ROLE_ADMIN);
        pipelineVisibility.setItemEnabled(ShareType.PUBLIC_RO, publicRoAvalilable);
    }
}
