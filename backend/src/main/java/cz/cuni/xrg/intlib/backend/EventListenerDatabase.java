package cz.cuni.xrg.intlib.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineEvent;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;

/**
 * Store all DPURecord and Pipeline related events into database.
 * 
 * @author Petyr
 *
 */
public class EventListenerDatabase implements ApplicationListener<ApplicationEvent> {
	
	@Autowired
	private DPUFacade dpuFacade;
		
	/**
	 * Take care about DPUEvent. Store event into database.
	 * @param event
	 */
	private void onDPUEvent(DPUEvent event) {
		MessageRecord record = event.getRecord();
		dpuFacade.save(record);
	}
	
	/**
	 * Take care about PipelineEvent. Store event into database
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
			onDPUEvent( (DPUEvent) event );
		} else if (event instanceof PipelineEvent) {
			onPipelineEvent( (PipelineEvent) event);
		} else {
			// unknown event ..
		}		
	}	

}
