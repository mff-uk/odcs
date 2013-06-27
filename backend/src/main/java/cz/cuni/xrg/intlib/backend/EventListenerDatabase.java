package cz.cuni.xrg.intlib.backend;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineEvent;
import cz.cuni.xrg.intlib.commons.app.execution.Record;

/**
 * Store all DPURecord and Pipeline related events into database.
 * 
 * @author Petyr
 *
 */
public class EventListenerDatabase implements ApplicationListener {
	
	/**
	 * Access to database.
	 */
	private DatabaseAccess database;
	
	public EventListenerDatabase(DatabaseAccess database) {
		this.database = database;
	}
	
	/**
	 * Take care about DPUEvent.
	 * @param event
	 */
	private void onDPUEvent(DPUEvent event) {
		Record record = event.getRecord();
		// store in database
		database.getDpu().save(record);
		// publish event into database
	}
	
	/**
	 * Take care about PipelineEvent.
	 * @param event
	 */	
	private void onPipelineEvent(PipelineEvent event) {
		Record record = event.getRecord();
		// store in database
		database.getDpu().save(record);		
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
