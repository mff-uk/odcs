package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;

import static cz.cuni.xrg.intlib.commons.app.dpu.DPUType.*;

import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.views.SimpleTreeFilter;
import java.util.List;

/**
 * Tree showing available DPUs. Contains filters by accessibility and name. It
 * is possible to make nodes draggable and to add custom click listeners.
 *
 * @author Bogo
 */
public class DPUTree extends CustomComponent {

    VerticalLayout layoutTree;
    Tree dpuTree;
    
    Button btnMinimize;
    Button btnExpand;
    GridLayout filterBar;
    

    /**
     * Creates new DPUTree.
     */
    public DPUTree() {

        buildMainLayout();
        setCompositionRoot(layoutTree);
    }

    /**
     * Builds layout.
     */
    private void buildMainLayout() {

        layoutTree = new VerticalLayout();
        layoutTree.setSpacing(true);
        layoutTree.setImmediate(true);
        layoutTree.setHeight("100%");
        layoutTree.setMargin(true);
        layoutTree.setStyleName("dpuTreeLayout");

        HorizontalLayout topLine = new HorizontalLayout();
        Label lblTree = new Label("DPU Tree");
        topLine.addComponent(lblTree);
        btnMinimize = new Button("<<", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                setTreeState(false);
            }
        });
        btnMinimize.setDescription("Minimize DPU tree");
        topLine.addComponent(btnMinimize);
        btnExpand = new Button(">>", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                setTreeState(true);
            }
        });
        btnExpand.setDescription("Expand DPU tree");
        btnExpand.setVisible(false);
        topLine.addComponent(btnExpand);
        layoutTree.addComponent(topLine);


        // DPURecord tree filters
        filterBar = new GridLayout(2, 2);
        filterBar.setSpacing(false);

        CheckBox onlyMyDPU = new CheckBox();
        onlyMyDPU.setCaption("Only my DPU templates");
        filterBar.addComponent(onlyMyDPU, 0, 0, 1, 0);

        TextField treeFilter = new TextField();
        treeFilter.setImmediate(false);
        treeFilter.setInputPrompt("Type to filter tree");
        treeFilter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            SimpleTreeFilter filter = null;

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                Container.Filterable f = (Container.Filterable) dpuTree
                        .getContainerDataSource();

                // Remove old filter
                if (filter != null) {
                    f.removeContainerFilter(filter);
                }

                // Set new filter
                filter = new SimpleTreeFilter(event.getText(), true, false);
                f.addContainerFilter(filter);

            }
        });

        filterBar.addComponent(treeFilter, 1, 1);
        filterBar.setSizeUndefined();
        layoutTree.addComponent(filterBar);
        layoutTree.setExpandRatio(filterBar, 0.05f);

        // DPURecord tree 
        dpuTree = new Tree("DPU Templates");
        dpuTree.setImmediate(true);
        dpuTree.setHeight("100%");
        //	dpuTree.setHeight(600, Unit.PIXELS);
        dpuTree.setStyleName("dpuTree");
        fillTree(dpuTree);
        for (Object itemId : dpuTree.rootItemIds()) {
            dpuTree.expandItemsRecursively(itemId);
        }

        layoutTree.addComponent(dpuTree);
        layoutTree.setComponentAlignment(dpuTree, Alignment.TOP_LEFT);
        layoutTree.setExpandRatio(dpuTree, 0.95f);
    }

    private void setTreeState(boolean isStateExpanded) {
        btnMinimize.setVisible(isStateExpanded);
        btnExpand.setVisible(!isStateExpanded);
        filterBar.setVisible(isStateExpanded);
        dpuTree.setVisible(isStateExpanded);
        layoutTree.setSizeUndefined();
    }

    /**
     * Adds custom ItemClickListener to the DPUTRee.
     *
     * @param itemClickListener {@link ItemClickEvent.ItemClickListener} to add
     * to DPU tree.
     */
    public void addItemClickListener(
            ItemClickEvent.ItemClickListener itemClickListener) {
        dpuTree.addItemClickListener(itemClickListener);
    }

    /**
     * Reloads the contents of the DPUTree.
     */
    public void refresh() {
        fillTree(dpuTree);
        markAsDirty();
    }

    /**
     * Fills tree with available DPUs.
     *
     * @param tree {@link Tree} to fill.
     */
    private void fillTree(Tree tree) {

        tree.removeAllItems();

        DPURecord rootExtractor = new DPURecord("Extractors", null);
        tree.addItem(rootExtractor);
        DPURecord rootTransformer = new DPURecord("Transformers", null);
        tree.addItem(rootTransformer);
        DPURecord rootLoader = new DPURecord("Loaders", null);
        tree.addItem(rootLoader);

        List<DPUTemplateRecord> dpus = App.getApp().getDPUs().getAllTemplates();
        for (DPUTemplateRecord dpu : dpus) {
            if (dpu.getType() != null) {
                tree.addItem(dpu);
                DPUTemplateRecord parent = dpu.getParent();
                if (parent != null) {
//					DPUTemplateRecord parent = null;
//					for(DPUTemplateRecord candidate : dpus) {
//						if(candidate.getId() == parentId) {
//							parent = candidate;
//							break;
//						}
//					}
                    tree.setParent(dpu, parent);
                } else {
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

            for (Object itemId : tree.rootItemIds()) {
                tree.expandItemsRecursively(itemId);
            }
        }
    }

    /**
     * Sets nodes of the tree drag-able.
     *
     * @param dragable True if the nodes should be drag-able, false otherwise.
     */
    public void setDragable(boolean dragable) {
        if (dragable) {
            dpuTree.setDragMode(Tree.TreeDragMode.NODE);
        } else {
            dpuTree.setDragMode(Tree.TreeDragMode.NONE);
        }
    }
}
