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

public final class PipelineStarted extends PipelineInfo {

    private static final Logger LOG = LoggerFactory
            .getLogger(PipelineStarted.class);

    public PipelineStarted(PipelineExecution execution, Object source) {
        super(execution, source,
                Messages.getString("PipelineInfo.starting", execution.getId()),
                Messages.getString("PipelineInfo.starting.detail", execution.getId(), execution.getPipeline().getName()));

        LOG.info("Execution #{} for pipeline {} started", execution.getId(), execution.getPipeline().getName());
    }

    public PipelineExecution getExecution() {
        return this.execution;
    }

}
