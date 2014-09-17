package cz.cuni.mff.xrg.odcs.backend.execution;

import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

import java.util.ArrayList;
import java.util.List;

public class EngineMock extends Engine {

    public EngineMock() {
        this.startUpDone = true;
    }
    public final List<PipelineExecution> historyOfExecution = new ArrayList<>();

    @Override
    public synchronized void run(PipelineExecution execution) {
        //mockit
        System.out.println("everything is ok");
        historyOfExecution.add(execution);
        execution.setStatus(PipelineExecutionStatus.FINISHED_SUCCESS);
        this.pipelineFacade.save(execution);
    }

    public PipelineFacade getPipelineFacade() {
        return pipelineFacade;
    }

    public void setPipelineFacade(PipelineFacade pipelineFacade) {
        this.pipelineFacade = pipelineFacade;
    }


    public void doCheck(){
        checkJobs();
    }
}
