package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.mvp.MVPModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author tomasknap
 * @author Bogo
 */
public class PipelineListModel implements MVPModel {

	@Autowired
	private PipelineFacade pipelineFacade;
	@Autowired
	private ContainerFactory containerFactory;

	public Container getDataSource(int pageLength) {
		return containerFactory.createPipelines(pageLength);
	}
	
	boolean copyPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		Pipeline nPipeline = pipelineFacade.copyPipeline(pipeline);
		String copiedPipelineName = "Copy of " + pipeline.getName();
		boolean isNameLengthOk = copiedPipelineName.length() <= MaxLengthValidator.NAME_LENGTH;
		if (isNameLengthOk) {
			nPipeline.setName(copiedPipelineName);
		}
		pipelineFacade.save(nPipeline);
		return isNameLengthOk;
	}

	void deletePipeline(long id) {
		final Pipeline pipeline = pipelineFacade.getPipeline(id);
		pipelineFacade.delete(pipeline);
	}

	PipelineExecution runPipeline(long id, boolean debug) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		return IntlibHelper.runPipeline(pipeline, debug);
	}

	Pipeline getPipeline(long id) {
		return pipelineFacade.getPipeline(id);
	}
}
