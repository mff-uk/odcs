package cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import cz.cuni.mff.xrg.odcs.commons.app.auth.IntlibPermissionEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUReplaceException;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidator;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUTemplateWrap;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUWrapException;
import cz.cuni.mff.xrg.odcs.frontend.dpu.validator.DPUDialogValidator;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUCreate;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.PipelineStatus;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu.DPUView.DPUViewListener;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Bogo
 */
public class DPUPresenter implements DPUViewListener {

	@Autowired
	private PipelineFacade pipelineFacade;
	@Autowired
	private DPUFacade dpuFacade;
	DPUView view;
//	/**
//	 * View name.
//	 */
//	public static final String NAME = "DPURecord";
	/**
	 * Evaluates permissions of currently logged in user.
	 */
	private IntlibPermissionEvaluator permissions = App.getApp().getBean(IntlibPermissionEvaluator.class);
	private DPUTemplateRecord selectedDpu = null;
	private Logger LOG;

	@Override
	public void event(String name) {
		switch (name) {
			case "copyDPU":
				copyDPU();
				break;
			case "deleteDPU":
				deleteDPU();
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
		copyToTarget(file);
		DPUTemplateRecord dpu = App.getDPUs().getTemplate(selectedDpu.getId());
		view.selectNewDPU(dpu);
	}

	/**
	 * Reload DPU. The new DPU's jar file is accessible through the
	 * {@link FileUploadReceiver#path}. The current DPU, which is being replaced
	 * , is assumed to be stored in {@link selectedDpu}.
	 */
	private void copyToTarget(File newJar) {
		if (newJar == null) {
			// we have no file, end 
			return;
		}

		// prepare dpu validators
		List<DPUValidator> validators = new LinkedList<>();
		validators.add(new DPUDialogValidator());

		try {
			App.getApp().getDPUManipulator().replace(selectedDpu, newJar, validators);
		} catch (DPUReplaceException e) {
			Notification.show("Failed to replace DPU", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return;
		}

		// we are ending .. refresh data in dialog 
		view.refresh();
		//setGeneralTabValues();

		// and show message to the user that the replace has been successful
		Notification.show("Replace finished", Notification.Type.HUMANIZED_MESSAGE);
	}

	@Override
	public boolean hasPermission(String type) {
		return permissions.hasPermission(selectedDpu, type);
	}

	@Override
	public void saveDPU(DPUTemplateWrap dpuWrap) {
		// saving configuration
		try {
			dpuWrap.saveConfig();
		} catch (ConfigException e) {
			dpuWrap.getDPUTemplateRecord().setRawConf(null);
		} catch (DPUWrapException e) {
			Notification.show(
					"Unexpected error. The configuration may have not been saved.",
					e.getMessage(), Notification.Type.WARNING_MESSAGE);
			LOG.error("Unexpected error while saving configuration for {}", dpuWrap.getDPUTemplateRecord().getId(), e);
		}

		// store into DB
		dpuFacade.save(dpuWrap.getDPUTemplateRecord());
		Notification.show("DPURecord was saved",
				Notification.Type.HUMANIZED_MESSAGE);

	}

	@Override
	public void pipelineAction(Long pipeId, String action) {
		switch (action) {
			case "detail":
				// navigate to PIPELINE_EDIT
				App.getApp().getNavigator().navigateTo(ViewNames.PIPELINE_EDIT.getUrl() + "/" + pipeId.toString());
				break;
			case "delete":
				Pipeline pipe = pipelineFacade.getPipeline(pipeId);
				pipelineFacade.delete(pipe);
				break;
			case "status":
				PipelineStatus pipelineStatus = new PipelineStatus();
				Pipeline pipe2 = pipelineFacade.getPipeline(pipeId);
				pipelineStatus.setSelectedPipeline(pipe2);
				// open the window with status parameters.
				App.getApp().addWindow(pipelineStatus);
				break;
		}
	}

	private void copyDPU() {
		int i = 1;
		boolean found = true;
		String nameOfDpuCopy = "";
		List<DPUTemplateRecord> allDpus = dpuFacade.getAllTemplates();
		while (found) {
			found = false;
			nameOfDpuCopy = "Copy of " + selectedDpu.getName();
			if (i > 1) {
				nameOfDpuCopy = nameOfDpuCopy + " " + i;
			}

			for (DPUTemplateRecord dpu : allDpus) {
				if (dpu.getName().equals(nameOfDpuCopy)) {
					found = true;
					break;
				}
			}
			i++;
		}

		DPUTemplateRecord copyDpuTemplate = dpuFacade.createCopy(selectedDpu);
		copyDpuTemplate.setName(nameOfDpuCopy);
		copyDpuTemplate.setParent(selectedDpu.getParent());
		dpuFacade.save(copyDpuTemplate);
	}

	/**
	 * Return container with data that used in {@link #instancesTable}.
	 *
	 * @return result IndexedContainer for {@link #instancesTable}
	 */
	@Override
	public IndexedContainer getTableData() {

		IndexedContainer result = new IndexedContainer();
		// visible columns of instancesTable
		String[] visibleCols = new String[]{"id", "name", "description", "author", "actions"};

		for (String p : visibleCols) {
			// setting type of the columns
			if (p.equals("id")) {
				result.addContainerProperty(p, Long.class, null);
			} else {
				result.addContainerProperty(p, String.class, "");
			}
		}
		// getting all Pipelines with specified DPU in it
		Set<Pipeline> pipelines = new HashSet<>(pipelineFacade.getPipelinesUsingDPU(selectedDpu));

		for (Pipeline pitem : pipelines) {
			Object num = result.addItem();

			result.getContainerProperty(num, "id").setValue(pitem.getId());
			result.getContainerProperty(num, "name").setValue(pitem.getName());
			result.getContainerProperty(num, "description").setValue(pitem.getDescription());
			result.getContainerProperty(num, "author").setValue("");
		}

		return result;
	}

	/**
	 * Delete DPU Template if it's unused by any pipeline
	 */
	private void deleteDPU() {

		List<Pipeline> pipelines = pipelineFacade.getPipelinesUsingDPU(selectedDpu);

		//If DPU Template is unused by any pipeline
		if (pipelines.isEmpty()) {
			//find if DPU Template has child elements
			List<DPUTemplateRecord> childDpus = dpuFacade.getChildDPUs(selectedDpu);
			if (!childDpus.isEmpty()) {
				Notification.show("DPURecord can not be removed because it has child elements", Notification.Type.ERROR_MESSAGE);
				return;
			}

			//if DPU Template hasn't child elements then delete it.
			if (selectedDpu.getParent() == null) {
				// first level DPU .. delete it completely
				App.getApp().getDPUManipulator().delete(selectedDpu);
			} else {
				// 2+ level DPU .. just delete the database record
				dpuFacade.delete(selectedDpu);
			}
			// and refresh the layout
			view.refresh();
			view.selectNewDPU(null);
			

			Notification.show("DPURecord was removed",
					Notification.Type.HUMANIZED_MESSAGE);
		} //If DPU Template it used by any pipeline, than show the names of this pipelines.
		else if (pipelines.size() == 1) {
			Notification.show("DPURecord can not be removed because it has been used in Pipeline: ", pipelines.get(0).getName(), Notification.Type.WARNING_MESSAGE);
		} else {
			Iterator<Pipeline> iterator = pipelines.iterator();
			StringBuilder names = new StringBuilder(iterator.next().getName());
			while (iterator.hasNext()) {
				names.append(", ");
				names.append(iterator.next().getName());
			}
			names.append('.');
			Notification.show("DPURecord can not be removed because it has been used in Pipelines: ", names.toString(), Notification.Type.WARNING_MESSAGE);
		}
	}

	void setView(DPUView view) {
		this.view = view;
		view.setListener(this);
	}
}
