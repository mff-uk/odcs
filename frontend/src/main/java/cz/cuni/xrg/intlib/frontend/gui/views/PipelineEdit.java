package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.components.DPUDetailDialog;
import cz.cuni.xrg.intlib.frontend.gui.components.pipelinecanvas.PipelineCanvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page for creating new/editing pipeline.
 * @author Bogo
 */
public class PipelineEdit extends CustomComponent implements View {

	private VerticalLayout mainLayout;

	private Label label;

    private TextField pipelineName;

    private TextArea pipelineDescription;

	/**
	 * Current pipeline entity.
	 */
	private com.vaadin.addon.jpacontainer.EntityItem<Pipeline> entity = null;

	private Pipeline pipeline = null;

    PipelineCanvas pc;


	public PipelineEdit() {
		// put init code into enter method
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);

		// top-level component properties
		setSizeUndefined();

        GridLayout pipelineSettingsLayout = new GridLayout(2,3);

		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label);

        Label nameLabel = new Label("Name");
        nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
        pipelineSettingsLayout.addComponent(nameLabel, 0, 0);

        pipelineName = new TextField();
        pipelineName.setImmediate(false);
		pipelineName.setWidth("200px");
		pipelineName.setHeight("-1px");
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

        mainLayout.addComponent(pipelineSettingsLayout);

        HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(true);
		pc = new PipelineCanvas();
        pc.setWidth(1060, Unit.PIXELS);
        pc.setHeight(960, Unit.PIXELS);
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
		dadWrapper.setDropHandler(new DropHandler() {

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

			@Override
			public void drop(DragAndDropEvent event) {
				Transferable t = (Transferable) event.getTransferable();
				DragAndDropWrapper.WrapperTargetDetails details = (DragAndDropWrapper.WrapperTargetDetails) event.getTargetDetails();

				Object obj = t.getData("itemId");

				if(obj.getClass() == DPU.class) {
					DPU dpu = (DPU) obj;
                    if(dpu.getId() >= 0) {
                        pc.addDpu(dpu);
                    }
				}

			}
		});

		layout.addComponent(dadWrapper);

		Tree dpuTree = new Tree("DPUs");
        dpuTree.setWidth(220, Unit.PIXELS);
		dpuTree.setDragMode(Tree.TreeDragMode.NODE);
		fillStubTree(dpuTree);
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

    private void fillStubTree(Tree tree) {

		DPU rootExtractor = new DPU(-1, "Extractors");
		tree.addItem(rootExtractor);
		DPU rootTransformer = new DPU(-2, "Transformers");
		tree.addItem(rootTransformer);
		DPU rootLoader = new DPU(-3, "Loaders");
		tree.addItem(rootLoader);

		DPU basicEx = new DPU(1, "RDF Extractor");
		tree.addItem(basicEx);
		tree.setParent(basicEx, rootExtractor);

		DPU sparqlEx = new DPU(2, "SPARQL endpoint");
		tree.addItem(sparqlEx);
		tree.setParent(sparqlEx, rootExtractor);
		DPU genericTr = new DPU(3, "Generic SPARQL");
		tree.addItem(genericTr);
		tree.setParent(genericTr, rootTransformer);
		DPU rdfLo = new DPU(4, "RDF Loader");
		tree.addItem(rdfLo);
		tree.setParent(rdfLo, rootLoader);
		DPU sparqlLo = new DPU(5, "SPARQL endpoint Loader");
		tree.addItem(sparqlLo);
		tree.setParent(sparqlLo, rootLoader);
	}

	/**
	 * Return true if given string is positive number.
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
			if ( Character.isDigit(str.charAt(i)) ) {

			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Load pipeline with given id from database.
	 * @param id
	 * @return
	 */
	protected Pipeline loadPipeline(String id) {
		// get data from DB ..
		this.entity = App.getDataAccess().pipelines().getPipeline(id);
		if (this.entity == null) {
			return null;
		} else {
			return this.entity.getEntity();
		}
	}

	/**
	 * Load pipeline to edit/create. Pipeline entity is loaded into
	 * this.entity. If /New parameter is passed in url, create just representation
	 * for pipeline.
	 * @param event
	 * @return Loaded pipeline class instance or null.
	 */
	protected Pipeline loadPipeline(ViewChangeEvent event) {
		// some information text ...
		String pipeIdstr = event.getParameters();
		if (pipeIdstr.compareTo( ViewNames.PipelineEdit_New.getParametr() ) == 0) {
			// create empty, for new record
			this.pipeline = new Pipeline("new pipeline", "description");
			this.entity = null;
		} else if (isInteger(pipeIdstr)) {
			// use pipeIdstr as id
			this.pipeline = loadPipeline(pipeIdstr);
		} else {
			// wring pipeIdstr
			this.pipeline = null;
			this.entity = null;
		}
		return this.pipeline;
	}

	/**
	 * Save loaded pipeline ie. this.entity.
	 */
	protected void savePipeline() {
        this.pipeline.setName(pipelineName.getValue());
        this.pipeline.setDescription(pipelineDescription.getValue());

		App.getApp().getPipelines().save(this.pipeline);

        App.getApp().getNavigator().navigateTo( ViewNames.PipelineList.getUrl() );
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
			label.setValue("<h3>Editing pipeline : " + this.pipeline.getName() + "</h3>");
            pipelineName.setValue(this.pipeline.getName());
            pipelineDescription.setValue(this.pipeline.getDescription());
		}

		// work with pipeline here ...

	}



}
