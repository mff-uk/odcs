/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;

import static cz.cuni.xrg.intlib.commons.app.dpu.DPUType.*;

import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.views.SimpleTreeFilter;
import java.util.List;

/**
 * Tree showing available DPUs. Contains filters by accessibility and name. It
 * is possible to make nodes draggable and to add custom clicklisteners.
 *
 * @author Bogo
 */
public class DPUTree extends CustomComponent {

	VerticalLayout layoutTree;

	Tree dpuTree;

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


		// DPURecord tree filters
		GridLayout filterBar = new GridLayout(2, 2);
		filterBar.setSpacing(false);

		CheckBox onlyMyDPU = new CheckBox();
		onlyMyDPU.setCaption("Only My DPURecord");
		filterBar.addComponent(onlyMyDPU, 0, 0, 1, 0);

//		Label labelFilter = new Label();
//		labelFilter.setContentMode(ContentMode.TEXT);
//		labelFilter.setValue("Filter:");
//		filterBar.addComponent(labelFilter, 0, 1);

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
		dpuTree = new Tree("DPUs");
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

	/**
	 * Adds custom ItemClickListener to the DPUTRee.
	 *
	 * @param itemClickListener
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
	 * @param tree
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
			tree.addItem(dpu);

			switch (dpu.getType()) {
				case Extractor:
					tree.setParent(dpu, rootExtractor);
					break;
				case Transformer:
					tree.setParent(dpu, rootTransformer);
					break;
				case Loader:
					tree.setParent(dpu, rootLoader);
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		tree.expandItem(rootExtractor);
		tree.expandItem(rootTransformer);
		tree.expandItem(rootLoader);
	}

	/**
	 * Sets nodes of the tree draggable.
	 *
	 * @param draggable
	 */
	public void setDraggable(boolean draggable) {
		if (draggable) {
			dpuTree.setDragMode(Tree.TreeDragMode.NODE);
		} else {
			dpuTree.setDragMode(Tree.TreeDragMode.NONE);
		}
	}
}
