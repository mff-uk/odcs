package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Validator;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Position;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.components.DPUTree;
import cz.cuni.xrg.intlib.frontend.gui.components.DebuggingView;
import cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas.DetailClosedListener;
import cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas.ShowDebugListener;
import cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas.PipelineCanvas;
import java.util.EventObject;

/**
 * Page for creating new pipeline or editing existing pipeline.
 *
 * @author Bogo
 */
class PipelineEdit extends ViewComponent {

	private VerticalLayout mainLayout;
	private Label label;
	private TextField pipelineName;
	private TextArea pipelineDescription;
	private Pipeline pipeline = null;
	PipelineCanvas pc;
	DPUTree dpuTree;
	TabSheet tabSheet;
	DragAndDropWrapper dadWrapper;

	/**
	 * Empty constructor.
	 */
	public PipelineEdit() {
		// put init code into enter method
	}

	/**
	 * Builds main layout of the page.
	 *
	 * @return
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

		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(true);
		pc = new PipelineCanvas();
		pc.setImmediate(true);
		pc.setWidth(1060, Unit.PIXELS);
		pc.setHeight(630, Unit.PIXELS);
		pc.init();
		pc.addListener(new DetailClosedListener() {
			@Override
			public void detailClosed(EventObject e) {
				dpuTree.refresh();
				dpuTree.markAsDirty();
				App.getApp().push();
			}

			@Override
			public void componentEvent(Event event) {
			}
		});
		pc.addListener(new ShowDebugListener() {
			@Override
			public void showDebug(PipelineExecution execution, DPUInstanceRecord instance) {
				DebuggingView dv = new DebuggingView(execution, instance, true);
				openDebug(dv);
			}

			@Override
			public void componentEvent(Event event) {
			}
		});
		
		pc.zoom(true);

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
				Transferable t = (Transferable) event.getTransferable();
				DragAndDropWrapper.WrapperTargetDetails details = (DragAndDropWrapper.WrapperTargetDetails) event.getTargetDetails();
				MouseEventDetails mouse = details.getMouseEvent();

				Object obj = t.getData("itemId");

				if (obj.getClass() == DPUTemplateRecord.class) {
					DPUTemplateRecord dpu = (DPUTemplateRecord) obj;
					if (App.getApp().getDPUs().getAllTemplates().contains(dpu)) {
						pc.addDpu(dpu, mouse.getClientX() - 261 + UI.getCurrent().getScrollLeft(), mouse.getClientY() - 256 + UI
								.getCurrent().getScrollTop());
					} else {
						// TODO log unknown DPURecord
					}
				}

			}
		});

		tabSheet = new TabSheet();

		Tab standardTab = tabSheet.addTab(new Label("Under construction"), "Standard");
		standardTab.setEnabled(false);

		Tab developTab = tabSheet.addTab(dadWrapper, "Develop");
		tabSheet.setSelectedTab(developTab);

		tabSheet.setWidth(1080, Unit.PIXELS);
		tabSheet.setHeight(670, Unit.PIXELS);
		tabSheet.setImmediate(true);

		layout.addComponent(tabSheet);

		VerticalLayout left = new VerticalLayout();
		dpuTree = new DPUTree();
		dpuTree.setStyleName("dpuTree");
		dpuTree.setWidth(220, Unit.PIXELS);
		dpuTree.setDraggable(true);
		left.addComponentAsFirst(dpuTree);

		Button zoomIn = new Button("Zoom In", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Position bounds = pc.zoom(true);
				calculateCanvasDimensions(bounds);
			}
		});
		Button zoomOut = new Button("Zoom Out", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Position bounds = pc.zoom(false);
				calculateCanvasDimensions(bounds);
			}
		});
		left.addComponent(zoomIn);
		left.addComponent(zoomOut);

		layout.addComponentAsFirst(left);
		mainLayout.addComponent(layout);

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidth("100%");
		Label labelFiller = new Label(" ");
		//labelFiller.setWidth("100%");
		buttonBar.addComponent(labelFiller);


		Button buttonRevert = new Button();
		buttonRevert.setCaption("Revert to last commit");
		buttonRevert.setHeight("25px");
		buttonRevert.setWidth("150px");
		buttonRevert.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
			}
		});
		buttonBar.addComponent(buttonRevert);

		Button buttonCommit = new Button();
		buttonCommit.setCaption("Save & Commit");
		buttonCommit.setHeight("25px");
		buttonCommit.setWidth("150px");
		buttonCommit.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// save current pipeline
				savePipeline();
			}
		});
		buttonBar.addComponent(buttonCommit);

		Button button = new Button();
		button.setCaption("Save");
		button.setHeight("25px");
		button.setWidth("150px");
		button.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// save current pipeline
				savePipeline();
			}
		});
		buttonBar.addComponent(button);
		buttonBar.setExpandRatio(labelFiller, 1.0f);
		mainLayout.addComponent(buttonBar);

		return mainLayout;
	}

	private void closeDebug() {
		//verticalSplit.setSplitPosition(100, Unit.PERCENTAGE);
		//verticalSplit.setSecondComponent(null);
		//verticalSplit.setLocked(true);
	}

	private void openDebug(DebuggingView debug) {
		Window debugWindow = new Window("Debug window", debug);
		debugWindow.setWidth("600px");
		debugWindow.setHeight("620px");
		debugWindow.addCloseListener(new Window.CloseListener() {
			@Override
			public void windowClose(Window.CloseEvent e) {
				//closeDebug();
			}
		});
		App.getApp().addWindow(debugWindow);
	}

	/**
	 * Builds part of layout with pipeline settings.
	 *
	 * @return
	 * @throws com.vaadin.ui.GridLayout.OverlapsException
	 * @throws com.vaadin.ui.GridLayout.OutOfBoundsException
	 */
	private GridLayout buildPipelineSettingsLayout() throws OverlapsException, OutOfBoundsException {

		GridLayout pipelineSettingsLayout = new GridLayout(2, 3);
		Label nameLabel = new Label("Name");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		pipelineSettingsLayout.addComponent(nameLabel, 0, 0);
		pipelineName = new TextField();
		pipelineName.setImmediate(false);
		pipelineName.setWidth("200px");
		pipelineName.setHeight("-1px");
		pipelineName.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class && !((String) value).isEmpty()) {
					return;
				}
				throw new InvalidValueException("Name must be filled!");
			}
		});
		pipelineSettingsLayout.addComponent(pipelineName, 1, 0);
		Label descriptionLabel = new Label("Description");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		pipelineSettingsLayout.addComponent(descriptionLabel, 0, 1);
		pipelineDescription = new TextArea();
		pipelineDescription.setImmediate(false);
		pipelineDescription.setWidth("400px");
		pipelineDescription.setHeight("60px");
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
		pipelineSettingsLayout.setWidth("100%");
		return pipelineSettingsLayout;
	}

	/**
	 * Return true if given string is positive number.
	 *
	 * @param str
	 * @return
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

	/**
	 * Loads pipeline with given id from database.
	 *
	 * @param id
	 * @return
	 */
	protected Pipeline loadPipeline(String id) {
		// get data from DB ..
		this.pipeline = App.getApp().getPipelines().getPipeline(Long.parseLong(id));
		pipelineName.setValue(this.pipeline.getName());
		pipelineDescription.setValue(this.pipeline.getDescription());
		return pipeline;
	}

	/**
	 * Loads pipeline to edit or create. Pipeline entity is loaded into
	 * this.entity. If /New parameter is passed in url, create just
	 * representation for pipeline.
	 *
	 * @param event
	 * @return Loaded pipeline class instance or null.
	 */
	protected Pipeline loadPipeline(ViewChangeEvent event) {
		// some information text ...
		String pipeIdstr = event.getParameters();
		if (pipeIdstr.compareTo(ViewNames.PipelineEdit_New.getParametr()) == 0) {
			// create empty, for new record
			this.pipeline = App.getApp().getPipelines().createPipeline();
			pipeline.setName("empty pipeline");
			pipeline.setDescription("empty pipeline description");
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
	 * Saves loaded pipeline.
	 */
	protected void savePipeline() {
		if (!pipelineName.isValid()) {
			Notification.show("Error saving pipeline", "Pipeline name is required!", Notification.Type.ERROR_MESSAGE);
			return;
		}
		this.pipeline.setName(pipelineName.getValue());
		this.pipeline.setDescription(pipelineDescription.getValue());
		pc.saveGraph(pipeline);

		App.getApp().getPipelines().save(this.pipeline);

		App.getApp().getNavigator().navigateTo(ViewNames.PipelineList.getUrl());
	}

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
		resizeCanvas(UI.getCurrent().getPage().getBrowserWindowWidth());
		UI.getCurrent().getPage().addBrowserWindowResizeListener(new Page.BrowserWindowResizeListener() {
			@Override
			public void browserWindowResized(Page.BrowserWindowResizeEvent event) {
				int width = event.getWidth();
				resizeCanvas(width);
			}
		});



		// work with pipeline here ...

	}
	
	private void calculateCanvasDimensions(Position zoomBounds) {
		int browserWidth = 1050 + (UI.getCurrent().getPage().getBrowserWindowWidth() - 1350);
		int browserHeight = 630;
		if(zoomBounds.getX() > browserWidth) {
			browserWidth = zoomBounds.getX();
			//enable horizontal scrollbar
		}
		if(zoomBounds.getY() > browserHeight) {
			browserHeight = zoomBounds.getY();
			//enable vertical scrollbar
		}
		pc.setWidth(browserWidth, Unit.PIXELS);
		pc.setHeight(browserHeight, Unit.PIXELS);
		dadWrapper.setSizeUndefined();
		tabSheet.markAsDirty();
		
		//pc.resizeCanvas(browserHeight, browserWidth);
		//tabSheet.setWidth(browserWidth + 20, Unit.PIXELS);
		//tabSheet.setHeight(browserHeight + 40, Unit.PIXELS);
	}

	private void resizeCanvas(int width) {
		if (width > 1350) {
			int newWidth = 1050 + (width - 1350);
			pc.setWidth(newWidth, Unit.PIXELS);
			pc.resizeCanvas(630, newWidth);
			tabSheet.setWidth(1070 + (width - 1350), Unit.PIXELS);
		}
	}
}
