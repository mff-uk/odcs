package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas.PipelineCanvas;
import java.util.List;

/**
 * Page for creating new pipeline or editing existing pipeline.
 *
 * @author Bogo
 */
public class PipelineEdit extends CustomComponent implements View {

	private VerticalLayout mainLayout;
	private Label label;
	private TextField pipelineName;
	private TextArea pipelineDescription;
	private Pipeline pipeline = null;
	PipelineCanvas pc;

	/**
	 * Empty constructor.
	 */
	public PipelineEdit() {
		// put init code into enter method
	}

	/**
	 * Builds main layout of the page.
	 * @return
	 */
	private VerticalLayout buildMainLayout() {
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
		pc.setWidth(1060, Unit.PIXELS);
		pc.setHeight(630, Unit.PIXELS);
		pc.init();

//        try {
//            pc.addListener(ActionEvent.class, this, PipelineEdit.class.getMethod("showDPUDetail", new Class[]{DPUInstance.class}));
//    //        pc.addListener(new ActionListener() {
//    //
//    //            @Override
//    //            public void actionPerformed(ActionEvent ae) {
//    //                if(ae.getActionCommand().equals("detail")) {
//    //                    DPUInstance dpu = (DPUInstance) ae.getSource();
//    //                }
//    //            }
//    //
//    //        });
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(PipelineEdit.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(PipelineEdit.class.getName()).log(Level.SEVERE, null, ex);
//        }

		DragAndDropWrapper dadWrapper = new DragAndDropWrapper(pc);
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
				MouseEventDetails mouse =  details.getMouseEvent();

				Object obj = t.getData("itemId");

				if (obj.getClass() == DPU.class) {
					DPU dpu = (DPU) obj;
					if (App.getApp().getDPUs().getAllDpus().contains(dpu)) {
						pc.addDpu(dpu, mouse.getClientX() - 261, mouse.getClientY() - 256);
					}
				}

			}
		});

		TabSheet tabSheet = new TabSheet();

		Tab standardTab = tabSheet.addTab(new Label("Under construction"), "Standard");
		standardTab.setEnabled(false);

		Tab developTab = tabSheet.addTab(dadWrapper, "Develop");
		tabSheet.setSelectedTab(developTab);

		tabSheet.setWidth(1080, Unit.PIXELS);
		tabSheet.setHeight(670, Unit.PIXELS);

		layout.addComponent(tabSheet);

		Tree dpuTree = new Tree("DPUs");
		dpuTree.setStyleName("dpuTree");
		dpuTree.setWidth(220, Unit.PIXELS);
		dpuTree.setDragMode(Tree.TreeDragMode.NODE);
		fillTree(dpuTree);
		layout.addComponentAsFirst(dpuTree);

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
		buttonRevert.addClickListener(new com.vaadin.ui.Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
			}
		});
		buttonBar.addComponent(buttonRevert);

		Button buttonCommit = new Button();
		buttonCommit.setCaption("Save & Commit");
		buttonCommit.setHeight("25px");
		buttonCommit.setWidth("150px");
		buttonCommit.addClickListener(new com.vaadin.ui.Button.ClickListener() {

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
//		Button button = new Button("Click Me");
//		button.addClickListener(new Button.ClickListener() {
//			public void buttonClick(ClickEvent event) {
//				pc.getPipeline();
//			}
//		});
//		layout.addComponent(button);

		return mainLayout;
	}

	/**
	 * Builds part of layout with pipeline settings.
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
				if(value.getClass() == String.class && !((String)value).isEmpty()) {
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
	 * Fills tree with available DPUs.
	 * @param tree
	 */
	private void fillTree(Tree tree) {

		DPU rootExtractor = new DPU("Extractors", null);
		tree.addItem(rootExtractor);
		DPU rootTransformer = new DPU("Transformers", null);
		tree.addItem(rootTransformer);
		DPU rootLoader = new DPU("Loaders", null);
		tree.addItem(rootLoader);

		List<DPU> dpus = App.getApp().getDPUs().getAllDpus();
		for (DPU dpu : dpus) {
			tree.addItem(dpu);

			switch (dpu.getType()) {
				case EXTRACTOR:
					tree.setParent(dpu, rootExtractor);
					break;
				case TRANSFORMER:
					tree.setParent(dpu, rootTransformer);
					break;
				case LOADER:
					tree.setParent(dpu, rootLoader);
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
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
	 * Loads pipeline to edit or create. Pipeline entity is loaded into this.entity.
	 * If /New parameter is passed in url, create just representation for
	 * pipeline.
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
		if(!pipelineName.isValid()) {
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

		// work with pipeline here ...

	}
}
