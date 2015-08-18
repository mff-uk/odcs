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
package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.context.ContextException;
import cz.cuni.mff.xrg.odcs.backend.context.ContextFacade;
import cz.cuni.mff.xrg.odcs.backend.dpu.event.DPUEvent;
import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Examine the {@link DependencyGraph} for given {@link PipelineExecution}. Add
 * data from precedents' context to the context of the current DPU, that is
 * specified by {@link Node}.
 * We execute this only for {@link DPUExecutionState#PREPROCESSING} state as for any other state the context has been already prepared.
 * 
 * @author Petyr
 */
@Component
class DPUPreExecutorContextPreparator extends DPUPreExecutorBase {

    /**
     * Pre-executor order.
     */
    public static final int ORDER = 0;

    /**
     * Event publisher used to publish error event.
     */
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ContextFacade contextFacade;

    public DPUPreExecutorContextPreparator() {
        super(Arrays.asList(DPUExecutionState.PREPROCESSING));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * In case of error log the error, publish message and the return false.
     */
    @Override
    protected boolean execute(Node node,
            Map<Node, Context> contexts,
            Object dpuInstance,
            PipelineExecution execution,
            ProcessingUnitInfo unitInfo) {
        // get current context
        Context context = contexts.get(node);

        // ! ! ! !
        // the context can contains data from previous 
        // PREPROCESSING phase that has been interrupted
        // so some DataUnit can already been created and may contains some
        // data .. we solve this in contextFacade.merge
        // which take care about this

        // looks for edges that lead to our node
        Set<Edge> edges = execution.getPipeline().getGraph().getEdges();
        for (Edge edge : edges) {
            if (edge.getTo() == node) {
                // we are the target .. add data
                Node sourceNode = edge.getFrom();
                Context sourceContext = contexts.get(sourceNode);
                if (sourceContext == null) {
                    // publish message
                    eventPublisher.publishEvent(
                            DPUEvent.createPreExecutorFailed(context, this, Messages.getString("DPUPreExecutorContextPreparator.missing.context", sourceNode.getDpuInstance().getName())));
                    return false;
                }
                // else add data
                try {
                    contextFacade.merge(context, sourceContext, edge.getScript());
                } catch (ContextException e) {
                    eventPublisher.publishEvent(
                            DPUEvent.createPreExecutorFailed(context, this,
                                    Messages.getString("DPUPreExecutorContextPreparator.merge.failed"), e));
                    return false;
                }
            }
        }
        return true;
    }

}
