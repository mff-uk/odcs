package cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu;

import com.vaadin.data.util.IndexedContainer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUTemplateWrap;
import java.io.File;

/**
 *
 * @author Bogo
 */
public interface DPUView {
	
	public void refresh();

	public boolean isChanged();

	public void saveDPUTemplate();

	public void selectNewDPU(DPUTemplateRecord dpu);
	
	
	interface DPUViewListener {
		void event(String name);

		public void selectDPU(DPUTemplateRecord dpuTemplateRecord);

		public boolean hasPermission(String save);

		public void dpuUploaded(File file);

		public void saveDPU(DPUTemplateWrap selectedDpuWrap);

		public void pipelineAction(Long pipeId, String detail);

		public IndexedContainer getTableData();
	}
	
	public void setListener(DPUViewListener listener);
	
}
