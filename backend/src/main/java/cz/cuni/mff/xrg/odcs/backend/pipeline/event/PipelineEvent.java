/*******************************************************************************
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
 *******************************************************************************/
/*******************************************************************************
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
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.backend.pipeline.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Base class for Pipeline events.
 * Every class that inherit from this class should LOG the creation.
 * 
 * @author Petyr
 */
public abstract class PipelineEvent extends ApplicationEvent {

    /**
     * Time of creation.
     */
    protected Date time;

    /**
     * The most related DPURecord to the event.
     */
    protected DPUInstanceRecord dpuInstance;

    /**
     * Associated pipeline execution.
     */
    protected PipelineExecution execution;

    protected PipelineEvent(DPUInstanceRecord dpuInstance,
            PipelineExecution execution,
            Object source) {
        super(source);
        time = new Date();
        this.dpuInstance = dpuInstance;
        this.execution = execution;
    }

    /**
     * Record that describe event.
     * 
     * @return MessageRecord
     */
    public abstract MessageRecord getRecord();

}
