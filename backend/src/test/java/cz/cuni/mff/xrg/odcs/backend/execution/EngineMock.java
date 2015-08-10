/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.backend.execution;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.facade.ExecutionFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

public class EngineMock extends Engine {

    public EngineMock() {
        this.startUpDone = true;
        this.backendID = "TestBackend";
    }

    public final List<PipelineExecution> historyOfExecution = new ArrayList<>();

    @Override
    public synchronized void run(PipelineExecution execution) {
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

    public void setExecutionFacade(ExecutionFacade executionFacade) {
        this.executionFacade = executionFacade;
    }

    public void doCheck() {
        checkJobs();
    }
}
