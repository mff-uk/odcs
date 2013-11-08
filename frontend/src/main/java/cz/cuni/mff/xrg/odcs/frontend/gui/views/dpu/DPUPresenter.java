package cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import cz.cuni.mff.xrg.odcs.commons.app.auth.IntlibPermissionEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.ViewNavigator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUTemplateWrap;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUCreate;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.PipelineStatus;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PipelineEdit;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu.DPUView.DPUViewListener;
import cz.cuni.mff.xrg.odcs.frontend.mvp.BasePresenter;
import cz.cuni.mff.xrg.odcs.frontend.mvp.MVPModel;
import cz.cuni.mff.xrg.odcs.frontend.mvp.MVPView;
import cz.cuni.mff.xrg.odcs.frontend.mvp.Model;
import cz.cuni.mff.xrg.odcs.frontend.mvp.View;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.dialogs.ConfirmDialog;
import ru.xpoft.vaadin.VaadinView;

/**
 *
 * @author Bogo
 */
@Component
@Scope("prototype")
@VaadinView(DPUPresenter.NAME)
@Model(DPUModel.class)
@View(DPUViewImpl.class)
@Address(url = "DPURecord")
public class DPUPresenter extends BasePresenter implements DPUViewListener {

	/**
	 * View name.
	 */
	public static final String NAME = "DPURecord";
	private DPUView view;
	private DPUModel model;
	@Autowired
	ViewNavigator navigator;
	/**
	 * Evaluates permissions of currently logged in user.
	 */
	private IntlibPermissionEvaluator permissions = App.getApp().getBean(IntlibPermissionEvaluator.class);
	private DPUTemplateRecord selectedDpu = null;

	@Override
	public void event(String name) {
		switch (name) {
			case "copyDPU":
				model.copyDPU(selectedDpu);
				break;
			case "deleteDPU":
				boolean isDeleted = model.deleteDPU(selectedDpu);
				if (isDeleted) {
					// and refresh the layout
					view.refresh();
					view.selectNewDPU(null);
				}
				break;
			case "openDPUCreate":
				openDPUCreate();
				break;
			case "importDPUTemplate":
			case "exportAll":
			default:
				break;

		}
	}

	private void openDPUCreate() {
		//Open the dialog for DPU Template creation
		DPUCreate createDPU = new DPUCreate();
		App.getApp().addWindow(createDPU);
		createDPU.addCloseListener(new Window.CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(Window.CloseEvent e) {
				//refresh DPU tree after closing DPU Template creation dialog 
				view.refresh();
			}
		});
	}

	@Override
	public void selectDPU(final DPUTemplateRecord dpu) {
		//if the previous selected
		if (selectedDpu != null && selectedDpu.getId() != null && view.isChanged() && hasPermission("save")) {

			//open confirmation dialog
			ConfirmDialog.show(UI.getCurrent(), "Unsaved changes",
					"There are unsaved changes.\nDo you wish to save them or discard?",
					"Save", "Discard changes",
					new ConfirmDialog.Listener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClose(ConfirmDialog cd) {
					if (cd.isConfirmed()) {
						view.saveDPUTemplate();
						view.refresh();
					}
					selectedDpu = dpu;
					view.selectNewDPU(dpu);
				}
			});

		} else {
			selectedDpu = dpu;
			view.selectNewDPU(dpu);
		}
	}

	@Override
	public void dpuUploaded(File file) {
		DPUTemplateRecord dpu = model.dpuUploaded(file, selectedDpu);
		view.refresh();
		view.selectNewDPU(dpu);
	}

	@Override
	public boolean hasPermission(String type) {
		return permissions.hasPermission(selectedDpu, type);
	}

	@Override
	public void saveDPU(DPUTemplateWrap dpuWrap) {
		model.saveDPU(dpuWrap);
	}

	@Override
	public void pipelineAction(Long pipeId, String action) {
		switch (action) {
			case "detail":
				// navigate to PIPELINE_EDIT
				navigator.navigateTo(PipelineEdit.NAME, pipeId);
				break;
			case "delete":
				model.deletePipeline(pipeId);
				break;
			case "status":
				PipelineStatus pipelineStatus = new PipelineStatus();
				Pipeline pipe = model.getPipeline(pipeId);
				pipelineStatus.setSelectedPipeline(pipe);
				// open the window with status parameters.
				App.getApp().addWindow(pipelineStatus);
				break;
		}
	}

	/**
	 * Return container with data that used in {@link #instancesTable}.
	 *
	 * @return result IndexedContainer for {@link #instancesTable}
	 */
	@Override
	public IndexedContainer getTableData() {
		return model.getPipelinesForDpu(selectedDpu);
	}

	@Override
	public void setModel(MVPModel model) {
		if (!DPUModel.class.isInstance(model)) {
			return;
		}
		this.model = (DPUModel) model;
	}

	@Override
	public void setView(MVPView view) {
		if (!DPUView.class.isInstance(view)) {
			return;
		}
		this.view = (DPUView) view;
		this.view.setListener(this);
	}
}
