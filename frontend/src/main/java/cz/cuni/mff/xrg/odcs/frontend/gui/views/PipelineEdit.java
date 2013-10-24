package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import com.vaadin.data.Validator;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Position;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUTree;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.DetailClosedEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.PipelineCanvas;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.ShowDebugEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.pipelinecanvas.GraphChangedEvent;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.RUNNING;
import static cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus.SCHEDULED;
import java.net.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.VaadinView;

/**
 * Page for creating new pipeline or editing existing pipeline.
 *
 * @author Bogo
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(PipelineEdit.NAME)
class PipelineEdit extends ViewComponent {

	/**
	 * View name.
	 */
	public static final String NAME = "PipelineEdit";
	private static final Logger LOG = LoggerFactory.getLogger(PipelineEdit.class);
	private VerticalLayout mainLayout;
	private Label label;
	private TextField pipelineName;
	private TextArea pipelineDescription;
	private Pipeline pipeline = null;
	PipelineCanvas pc;
	DPUTree dpuTree;
	TabSheet tabSheet;
	DragAndDropWrapper dadWrapper;
	Button undo;
	private final String STANDARD_MODE = "standard_mode";
	private final String DEVELOP_MODE = "develop_mode";
	private String canvasMode = DEVELOP_MODE;
	private Tab standardTab;
	private Tab developTab;
	Button buttonSave;
	Button buttonSaveAndClose;
	Button buttonCancel;
	HorizontalLayout buttonBar;

	/**
	 * Empty constructor.
	 */
	public PipelineEdit() {
		// put init code into enter method
	}

	/**
	 * Enter method for PIPELINE_EDIT view.
	 *
	 * @param event {@link ViewChangeEvent}
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// ..
		this.loadPipeline(event);
		// or use this.entity.getEntity();

		if (this.pipeline == null) {
			label.setValue("<h3>Pipeline '" + event.getParameters() + "' doesn't exist.</h3>");
		} else {
			label.setValue("<h3>Editing pipeline<h3>");// + this.pipeline.getName() + "</h3>");
		}

		//Resizing canvas
		UI.getCurrent().setImmediate(true);
//		resizeCanvas(UI.getCurrent().getPage().getBrowserWindowWidth());
//		UI.getCurrent().getPage().addBrowserWindowResizeListener(new Page.BrowserWindowResizeListener() {
//			@Override
//			public void browserWindowResized(Page.BrowserWindowResizeEvent event) {
//				int width = event.getWidth();
//				resizeCanvas(width);
//			}
//		});



		// work with pipeline here ...

	}

	/**
	 * Builds main layout of the page.
	 *
	 * @return {@link VerticalLayout} is the main layout of the view.
	 */
	private VerticalLayout buildMainLayout() {

		//verticalSplit = new VerticalSplitPanel();
		//verticalSplit.setSizeFull();

		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setStyleName("mainLayout");

		// top-level component properties
		setSizeUndefined();

		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label);
		
		GridLayout pipelineSettingsLayout = buildPipelineSettingsLayout();
		mainLayout.addComponent(pipelineSettingsLayout);
		
		CssLayout layout = new CssLayout() {
			@Override
			protected String getCss(Component c) {
				if (c instanceof TabSheet) {
					return "margin-left: 0px; margin-top: 20px;";
				} else if (c instanceof Panel) {
					return "position: fixed; left: 20px; top: 280px; max-height:600px; overflow-y:auto; overflow-x: hidden; max-width: 375px";
				} else if (c instanceof HorizontalLayout) {
					if(c.equals(buttonBar)) {
					return "position: fixed; bottom: 20px; left: 20px; height: 30px; background: #eee; padding: 10px;";
					} else {
						return "position: fixed; right: 20px; top: 280px;";
					}
				}
				return null;
			}
		};
		//layout.setMargin(true);
		pc = new PipelineCanvas();
		pc.setImmediate(true);
		pc.setWidth(1060, Unit.PIXELS);
		pc.setHeight(630, Unit.PIXELS);
		pc.init();
		pc.addListener(new Listener() {
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
					App.getApp().push();
				} else if (klass == Edge.class) {
					setupButtons();
				}
			}
		});
		
		pc.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				if (event.getClass() != ShowDebugEvent.class) {
					return;
				}
				ShowDebugEvent sde = (ShowDebugEvent) event;
				if (savePipeline()) {
					openDebug(pipeline, sde.getDebugNode());
				}
			}
		});
		pc.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				if (event.getClass() != GraphChangedEvent.class) {
					return;
				}
				
				if (((GraphChangedEvent) event).getIsUndoable()) {
					undo.setEnabled(true);
				}
				setupButtons();
				
			}
		});
		
		dadWrapper = new DragAndDropWrapper(pc);
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
					if (App.getApp().getDPUs().getAllTemplates().contains(dpu)) {
						pc.addDpu(dpu, mouse.getClientX() - 20 + UI.getCurrent().getScrollLeft(), mouse.getClientY() - 280 + UI
								.getCurrent().getScrollTop());
					} else {
						LOG.warn("Invalid drop operation.");
					}
				}
				
			}
		});
		
		tabSheet = new TabSheet();
		
		standardTab = tabSheet.addTab(new Label("Under construction"), "Standard");
		standardTab.setEnabled(true);
		
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
					pc.changeMode(canvasMode);
					tabSheet.setSelectedTab(developTab);
				}
			}
		});
		
		tabSheet.setWidth(1080, Unit.PIXELS);
		tabSheet.setHeight(670, Unit.PIXELS);
		tabSheet.setImmediate(true);
		
		layout.addComponent(tabSheet);
		
		Panel leftPanel = new Panel();
		//VerticalLayout left = new VerticalLayout();
		leftPanel.setStyleName("changingposition");
		//left.setWidth(250, Unit.PIXELS);
		dpuTree = new DPUTree(true);
		dpuTree.setStyleName("dpuTree");
		dpuTree.setSizeUndefined();
		dpuTree.setDragable(true);
		//left.addComponentAsFirst(dpuTree);
		leftPanel.setContent(dpuTree);
		leftPanel.setSizeUndefined();
		layout.addComponent(leftPanel);
		
		
		
		Button zoomIn = new Button();
		zoomIn.setDescription("Zoom In");
		zoomIn.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Position bounds = pc.zoom(true);
				calculateCanvasDimensions(bounds);
			}
		});
		zoomIn.setIcon(new ThemeResource("icons/zoom_in.png"), "Zoom in");
		//zoomIn.setWidth("110px");
		Button zoomOut = new Button();
		zoomOut.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Position bounds = pc.zoom(false);
				calculateCanvasDimensions(bounds);
			}
		});
		zoomOut.setDescription("Zoom out");
		zoomOut.setIcon(new ThemeResource("icons/zoom_out.png"), "Zoom out");
		//zoomOut.setWidth("110px");
		undo = new Button();
		undo.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (!pc.undo()) {
					event.getButton().setEnabled(false);
				}
			}
		});
		undo.setEnabled(false);
		undo.setImmediate(true);
		undo.setDescription("Undo");
		undo.setIcon(new ThemeResource("icons/undo.png"), "Undo");
		//undo.setWidth("110px");
		HorizontalLayout actionBar = new HorizontalLayout(zoomIn, zoomOut, undo);
		actionBar.setStyleName("changingposition");
		actionBar.setSizeUndefined();
		layout.addComponent(actionBar);
	
		
		buttonBar = new HorizontalLayout();
	
		Button buttonRevert = new Button("Revert to last commit");
		buttonRevert.setHeight("25px");
		buttonRevert.setWidth("150px");
		buttonRevert.setEnabled(false);
		buttonRevert.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
			}
		});
		buttonBar.addComponent(buttonRevert);
		
		Button buttonCommit = new Button("Save & Commit");
		buttonCommit.setHeight("25px");
		buttonCommit.setWidth("150px");
		buttonCommit.setEnabled(false);
		buttonCommit.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// save current pipeline
				savePipeline();
			}
		});
		buttonBar.addComponent(buttonCommit);
		
		buttonSave = new Button("Save");
		buttonSave.setHeight("25px");
		buttonSave.setWidth("150px");
		buttonSave.setImmediate(true);
		buttonSave.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// save current pipeline
				if (savePipeline()) {
					pipeline = App.getApp().getPipelines().getPipeline(pipeline.getId());
					pc.showPipeline(pipeline);
				}
			}
		});
		buttonBar.addComponent(buttonSave);
		buttonSaveAndClose = new Button("Save & Close");
		buttonSaveAndClose.setHeight("25px");
		buttonSaveAndClose.setWidth("150px");
		buttonSaveAndClose.setImmediate(true);
		buttonSaveAndClose.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// save current pipeline
				if (savePipeline()) {
					closeView();
				}
			}
		});
		buttonBar.addComponent(buttonSaveAndClose);
		buttonCancel = new Button("Close");
		buttonCancel.setHeight("25px");
		buttonCancel.setWidth("150px");
		buttonCancel.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				pipelineName.discard();
				pipelineDescription.discard();
				pc.cancelChanges();
				closeView();
			}
		});
		buttonBar.addComponent(buttonCancel);
		
		buttonBar.setSpacing(true);
		layout.addComponent(buttonBar);
		mainLayout.addComponent(layout);
		
		Position bounds = pc.zoom(true);
		calculateCanvasDimensions(bounds);
		
		return mainLayout;
	}

	/**
	 * Builds part of layout with pipeline settings.
	 *
	 * @return {@link GridLayout} contains controls with information about
	 * pipeline settings.
	 * @throws com.vaadin.ui.GridLayout.OverlapsException
	 * @throws com.vaadin.ui.GridLayout.OutOfBoundsException
	 */
	private GridLayout buildPipelineSettingsLayout() throws OverlapsException, OutOfBoundsException {
		
		GridLayout pipelineSettingsLayout = new GridLayout(2, 3);
		pipelineSettingsLayout.setWidth(600, Unit.PIXELS);
		Label nameLabel = new Label("Name");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
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
		pipelineName.addValidator(new MaxLengthValidator(MaxLengthValidator.NAME_LENGTH));
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
//		permissionsTable.setHeight("150px");
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
		return pipelineName.isModified() || pipelineDescription.isModified() || pc.isModified();
	}
	
	@Override
	public boolean saveChanges() {
		return savePipeline();
	}
	
	private void setupButtons() {
		setupButtons(isModified());
	}

	/**
	 * Return true if given string is positive number.
	 *
	 * @param str {@link String} to check
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
	
	private void setupButtons(boolean enabled) {
		buttonSave.setEnabled(enabled);
		buttonSaveAndClose.setEnabled(enabled);
	}

	/**
	 * Closes the view and returns to View which user came from, if any.
	 *
	 */
	private void closeView() {
		App.getApp().navigateToLastView();
	}

	/**
	 * Opens given {@link DebuggingView} in new window.
	 *
	 * @param debug {@link DebuggingView} to show.
	 */
	private void openDebug(final Pipeline pip, final Node debugNode) {
		PipelineExecution pExec = IntlibHelper.runPipeline(pip, true, debugNode);
		if (pExec == null) {
			//Solved by dialog if backend is offline in method runPipeline.
			//Notification.show("Pipeline execution failed!", Notification.Type.ERROR_MESSAGE);
			return;
		}
		final DPUInstanceRecord instance = debugNode.getDpuInstance();
		final DebuggingView debug = new DebuggingView(pExec, instance, true, true);
		
		final Window debugWindow = new Window("Debug window");
		HorizontalLayout buttonLine = new HorizontalLayout();
		buttonLine.setSpacing(true);
		buttonLine.setWidth(100, Unit.PERCENTAGE);
		Button rerunButton = new Button("Rerun", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				PipelineExecution pExec = IntlibHelper.runPipeline(pip, true, debugNode);
				if (pExec == null) {
					//Solved by dialog if backend is offline in method runPipeline.
					//Notification.show("Pipeline execution failed!", Notification.Type.ERROR_MESSAGE);
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
		debugWindow.setWidth("600px");
		debugWindow.setHeight("785px");
		debugWindow.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(Window.CloseEvent e) {
				//App.getApp().getRefreshThread().refreshExecution(null, null);
				App.getApp().getRefreshManager().removeListener(RefreshManager.DEBUGGINGVIEW);
			}
		});
		debugWindow.addResizeListener(new Window.ResizeListener() {
			@Override
			public void windowResized(Window.ResizeEvent e) {
				debug.resize(e.getWindow().getHeight());
			}
		});
		
		if (pExec.getStatus() == RUNNING || pExec.getStatus() == SCHEDULED) {
			//App.getApp().getRefreshThread().refreshExecution(pExec, debug);
			App.getApp().getRefreshManager().addListener(RefreshManager.DEBUGGINGVIEW, RefreshManager.getDebugRefresher(debug, pExec));
		}
		App.getApp().addWindow(debugWindow);
	}

	/**
	 * Loads pipeline with given id from database.
	 *
	 * @param id {@link String} with id of {@link Pipeline} to load
	 * @return {@link Pipeline} with given id.
	 */
	protected Pipeline loadPipeline(String id) {
		// get data from DB ..
		this.pipeline = App.getApp().getPipelines().getPipeline(Long.parseLong(id));
		pipelineName.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getName()));
		pipelineDescription.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getDescription()));
		setupButtons(false);
		return pipeline;
	}

	/**
	 * Loads pipeline to edit or create. Pipeline entity is loaded into
	 * this.entity. If /New parameter is passed in url, create just
	 * representation for pipeline.
	 *
	 * @param event {@link ViewChangeEvent} passed from enter method.
	 * @return Loaded pipeline class instance or null.
	 */
	protected Pipeline loadPipeline(ViewChangeEvent event) {
		// some information text ...
		String pipeIdstr = event.getParameters();
		if (pipeIdstr.compareTo(ViewNames.PIPELINE_EDIT_NEW.getParametr()) == 0) {
			// create empty, for new record
			this.pipeline = App.getApp().getPipelines().createPipeline();
			pipeline.setName("");
			pipeline.setDescription("");
			pipelineName.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getName()));
			pipelineDescription.setPropertyDataSource(new ObjectProperty<>(this.pipeline.getDescription()));
			setupButtons(false);
			pipelineName.setInputPrompt("Insert pipeline name");
			pipelineDescription.setInputPrompt("Insert pipeline description");
		} else if (isInteger(pipeIdstr)) {
			// use pipeIdstr as id
			this.pipeline = loadPipeline(pipeIdstr);
		} else {
			// wring pipeIdstr
			this.pipeline = null;
		}
		if (pipeline != null) {
			pc.showPipeline(pipeline);
		}
		return this.pipeline;
	}

	/**
	 * Saves current pipeline.
	 */
	protected boolean savePipeline() {
		if (!validate()) {
			//Notification.show("Error saving pipeline", "Pipeline name is required!", Notification.Type.ERROR_MESSAGE);
			return false;
		}
		undo.setEnabled(false);
		this.pipeline.setName(pipelineName.getValue());
		pipelineName.commit();
		this.pipeline.setDescription(pipelineDescription.getValue());
		pipelineDescription.commit();
		boolean doCleanup = pc.saveGraph(pipeline);
		
		App.getApp().getPipelines().save(this.pipeline);
		if (doCleanup) {
			pc.afterSaveCleanUp();
		}
		
		Notification.show("Pipeline saved successfully!", Notification.Type.HUMANIZED_MESSAGE);
		setupButtons();
		return true;
	}

	/**
	 * Calculates and sets canvas dimensions according to current size of
	 * browser window and pipeline graph's bounds.
	 *
	 * @param zoomBounds {@link Position} with bounds of pipeline graph.
	 */
	private void calculateCanvasDimensions(Position zoomBounds) {
		int browserWidth = 1050 + (UI.getCurrent().getPage().getBrowserWindowWidth() - 1350);
		int browserHeight = 630;
		if (zoomBounds.getX() > browserWidth) {
			browserWidth = zoomBounds.getX();
			//enable horizontal scrollbar
		}
		if (zoomBounds.getY() > browserHeight) {
			browserHeight = zoomBounds.getY();
			//enable vertical scrollbar
		}
		pc.setWidth(browserWidth, Unit.PIXELS);
		pc.setHeight(browserHeight, Unit.PIXELS);
		dadWrapper.setSizeUndefined();
		tabSheet.setWidth(browserWidth + 40, Unit.PIXELS);
		tabSheet.setHeight(browserHeight + 60, Unit.PIXELS);
		mainLayout.setSizeUndefined();
		mainLayout.markAsDirty();

		//pc.resizeCanvas(browserHeight, browserWidth);
		//tabSheet.setWidth(browserWidth + 20, Unit.PIXELS);
		//tabSheet.setHeight(browserHeight + 40, Unit.PIXELS);
	}

	/**
	 * Resizes canvas according to changed width of browser window.
	 *
	 * @param width New width of browser window.
	 *
	 */
	private void resizeCanvas(int width) {
//		if (width > 1350) {
//			int newWidth = 1050 + (width - 1350);
//			pc.setWidth(newWidth, Unit.PIXELS);
//			pc.resizeCanvas(630, newWidth);
//			tabSheet.setWidth(1070 + (width - 1350), Unit.PIXELS);
//		}
	}

	/**
	 * Validates fields with requirements on input. Shows errors as
	 * notification.
	 *
	 * @return validation result
	 *
	 */
	private boolean validate() {
		try {
			pipelineName.validate();
			pipelineDescription.validate();
		} catch (Validator.InvalidValueException e) {
			Notification.show("Error saving pipeline", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
}
