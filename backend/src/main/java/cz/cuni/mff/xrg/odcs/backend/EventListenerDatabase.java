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
package cz.cuni.mff.xrg.odcs.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.mff.xrg.odcs.backend.dpu.event.DPUEvent;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineEvent;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;

/**
 * Store all DPURecord and Pipeline related events into database.
 * 
 * @author Petyr
 */
public class EventListenerDatabase implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private DPUFacade dpuFacade;

    /**
     * Take care about DPUEvent. Store event into database.
     * 
     * @param event
     */
    private void onDPUEvent(DPUEvent event) {
        MessageRecord record = event.getRecord();
        dpuFacade.save(record);
    }

    /**
     * Take care about PipelineEvent. Store event into database
     * 
     * @param event
     */
    private void onPipelineEvent(PipelineEvent event) {
        MessageRecord record = event.getRecord();
        dpuFacade.save(record);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // base on type .. 
        if (event instanceof DPUEvent) {
            onDPUEvent((DPUEvent) event);
        } else if (event instanceof PipelineEvent) {
            onPipelineEvent((PipelineEvent) event);
        } else {
            // unknown event ..
        }
    }

}
