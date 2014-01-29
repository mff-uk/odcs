package cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthAwarePermissionEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUModuleManipulator;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUReplaceException;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidator;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUTemplateWrap;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUWrapException;
import cz.cuni.mff.xrg.odcs.frontend.dpu.validator.DPUDialogValidator;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUCreate;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.PipelineStatus;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PipelineEdit;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Bogo
 */
@Component
@Scope("prototype")
@Address(url = "DPURecord")
public class DPUPresenterImpl implements DPUPresenter {

	@Autowired
	private DPUView view;
	@Autowired
	PipelineStatus pipelineStatus;
	@Autowired
	private DPUCreate createDPU;
	ClassNavigator navigator;
	@Autowired
	private DPUModuleManipulator dpuManipulator;
	/**
	 * Evaluates permissions of currently logged in user.
	 */
	@Autowired
	private AuthAwarePermissionEvaluator permissions;
	private DPUTemplateRecord selectedDpu = null;
	@Autowired
	private PipelineFacade pipelineFacade;
	@Autowired
	private DPUFacade dpuFacade;
	private Window.CloseListener createDPUCloseListener;
	private static final Logger LOG = LoggerFactory.getLogger(DPUPresenterImpl.class);

	@Override
	public void saveDPUEventHandler(DPUTemplateWrap dpuWrap) {
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
		Notification.show("DPURecord was saved", Notification.Type.HUMANIZED_MESSAGE);
	}

	void copyDPU(DPUTemplateRecord selectedDpu) {
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
	 * Delete DPU Template if it's unused by any pipeline
	 */
	boolean deleteDPU(DPUTemplateRecord dpu) {

		List<Pipeline> pipelines = pipelineFacade.getPipelinesUsingDPU(dpu);

		//If DPU Template is unused by any pipeline
		if (pipelines.isEmpty()) {
			//find if DPU Template has child elements
			List<DPUTemplateRecord> childDpus = dpuFacade.getChildDPUs(dpu);
			if (!childDpus.isEmpty()) {
				Notification.show("DPURecord can not be removed because it has child elements", Notification.Type.ERROR_MESSAGE);
				return false;
			}

			//if DPU Template hasn't child elements then delete it.
			if (dpu.getParent() == null) {
				// first level DPU .. delete it completely
				dpuManipulator.delete(dpu);
			} else {
				// 2+ level DPU .. just delete the database record
				dpuFacade.delete(dpu);
			}

			Notification.show("DPURecord was removed",
					Notification.Type.HUMANIZED_MESSAGE);
			return true;
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
		return false;
	}

	void deletePipeline(Long pipeId) {
	}

	Pipeline getPipeline(Long pipeId) {
		return pipelineFacade.getPipeline(pipeId);
	}

	IndexedContainer getPipelinesForDpu(DPUTemplateRecord dpu) {
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
		Set<Pipeline> pipelines = new HashSet<>(pipelineFacade.getPipelinesUsingDPU(dpu));

		for (Pipeline pitem : pipelines) {
			Item item = result.addItem(pitem.getId());
			item.getItemProperty("id").setValue(pitem.getId());
			item.getItemProperty("name").setValue(pitem.getName());
			item.getItemProperty("description").setValue(pitem.getDescription());
			item.getItemProperty("author").setValue(pitem.getOwner().getUsername());
		}

		return result;
	}

	DPUTemplateRecord dpuUploaded(File file, DPUTemplateRecord dpu) {
		copyToTarget(file, dpu);
		return dpuFacade.getTemplate(dpu.getId());
	}

	/**
	 * Reload DPU. The new DPU's jar file is accessible through the
	 * {@link FileUploadReceiver#path}. The current DPU, which is being replaced
	 * , is assumed to be stored in {@link selectedDpu}.
	 */
	private void copyToTarget(File newJar, DPUTemplateRecord dpu) {
		if (newJar == null) {
			// we have no file, end 
			return;
		}

		// prepare dpu validators
		List<DPUValidator> validators = new LinkedList<>();
		validators.add(new DPUDialogValidator());

		try {
			dpuManipulator.replace(dpu, newJar, validators);
		} catch (DPUReplaceException e) {
			Notification.show("Failed to replace DPU", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return;
		}

		// and show message to the user that the replace has been successful
		Notification.show("Replace finished", Notification.Type.HUMANIZED_MESSAGE);
	}

	@Override
	public void selectDPUEventHandler(final DPUTemplateRecord dpu) {
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
					view.selectNewDPU(dpu);
					selectedDpu = dpu;
				}
			});

		} else {
			view.selectNewDPU(dpu);
			selectedDpu = dpu;
		}
	}

	@Override
	public void dpuUploadedEventHandler(File file) {
		DPUTemplateRecord dpu = dpuUploaded(file, selectedDpu);
		view.refresh();
		view.selectNewDPU(dpu);
	}

	@Override
	public boolean hasPermission(String type) {
		return permissions.hasPermission(selectedDpu, type);
	}

	/**
	 * Return container with data that used in {@link #instancesTable}.
	 *
	 * @return result IndexedContainer for {@link #instancesTable}
	 */
	@Override
	public IndexedContainer getTableData(DPUTemplateRecord dpu) {
		return getPipelinesForDpu(dpu);
	}

	@Override
	public Object enter() {
		navigator = ((AppEntry) UI.getCurrent()).getNavigation();
		selectedDpu = null;
		Object viewObject = view.enter(this);

		createDPUCloseListener = new Window.CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(Window.CloseEvent e) {
				//refresh DPU tree after closing DPU Template creation dialog 
				view.refresh();
			}
		};

		return viewObject;
	}

	@Override
	public void setParameters(Object configuration) {
		// we do not care about parameters, we always do the same job .. 
	}

	@Override
	public void openDPUCreateEventHandler() {
		//Open the dialog for DPU Template creation
		if (!UI.getCurrent().getWindows().contains(createDPU)) {
			createDPU.initClean();
			UI.getCurrent().addWindow(createDPU);
			createDPU.removeCloseListener(createDPUCloseListener);
			createDPU.addCloseListener(createDPUCloseListener);
		} else {
			createDPU.bringToFront();
		}
	}

	@Override
	public void importDPUTemplateEventHandler() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void exportAllEventHandler() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void copyDPUEventHandler() {
		copyDPU(selectedDpu);
	}

	@Override
	public void deleteDPUEventHandler() {
		boolean isDeleted = deleteDPU(selectedDpu);
		if (isDeleted) {
			// and refresh the layout
			view.refresh();
			view.selectNewDPU(null);
		}
	}

	@Override
	public void pipelineDetailEventHandler(Long id) {
		// navigate to PIPELINE_EDIT
		navigator.navigateTo(PipelineEdit.class, id.toString());
	}

	@Override
	public void pipelineDeleteEventHandler(Long id) {
		Pipeline pipe = pipelineFacade.getPipeline(id);
		List<PipelineExecution> executions = pipelineFacade.getExecutions(pipe, PipelineExecutionStatus.QUEUED);
		if (executions.isEmpty()) {
			executions = pipelineFacade.getExecutions(pipe, PipelineExecutionStatus.RUNNING);
		}
		if (!executions.isEmpty()) {
			Notification.show("Pipeline " + pipe.getName() + " has current(QUEUED or RUNNING) execution(s) and cannot be deleted now!", Notification.Type.WARNING_MESSAGE);
			return;
		}
		pipelineFacade.delete(pipe);
	}

	@Override
	public void pipelineStatusEventHandler(Long id) {
		Pipeline pipe = getPipeline(id);
		pipelineStatus.setSelectedPipeline(pipe);
		// open the window with status parameters.
		UI.getCurrent().addWindow(pipelineStatus);
	}
}
