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
package cz.cuni.mff.xrg.odcs.backend.pipeline.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

/**
 * Report that pipelineExecution is finished.
 * Is used also to inform {@link cz.cuni.mff.xrg.odcs.backend.scheduling.Scheduler}
 * 
 * @author Petyr
 */
public final class PipelineFinished extends PipelineInfo {

    private static final Logger LOG = LoggerFactory
            .getLogger(PipelineFinished.class);

    public PipelineFinished(PipelineExecution execution, Object source) {
        super(execution, source, Messages.getString("PipelineFinished.execution.finished",
                execution.getId()), "");

        LOG.info("Execution {} finished with status: {}", execution.getId(),
                execution.getStatus().toString());
    }

    /**
     * @return True if respective execution finished with {@link PipelineExecutionStatus#FINISHED_SUCCESS} or {@link PipelineExecutionStatus#FINISHED_WARNING}.
     */
    public boolean sucess() {
        return execution.getStatus() == PipelineExecutionStatus.FINISHED_SUCCESS
                || execution.getStatus() == PipelineExecutionStatus.FINISHED_WARNING;
    }

    public PipelineExecution getExecution() {
        return execution;
    }

}
