package cz.cuni.xrg.intlib.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineEvent;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;

/**
 * Store all DPU and Pipeline related events into database.
 * 
 * @author Petyr
 *
 */
public class EventListenerDatabase implements ApplicationListener {
	
	/**
	 * Access to database.
	 */
	private DatabaseAccess database;
	
	private Logger logger = LoggerFactory.getLogger(EventListenerDatabase.class);
	
	public EventListenerDatabase(DatabaseAccess database) {
		this.database = database;
	}
	
	/**
	 * Take care about DPUEvent.
	 * @param event
	 */
	private void onDPUEvent(DPUEvent event) {
		DPURecord record = event.getRecord();
		// publish event into database
		
		// TODO: DB
		logger.info("DPUEvent (source='" + record.getSource().getName() + "',type='" + record.getType() + "',shortMsg='" + record.getShortMessage() + "',lngMsg='" + record.getFullMessage());
	}
	
	/**
	 * Take care about PipelineEvent.
	 * @param event
	 */	
	private void onPipelineEvent(PipelineEvent event) {
		// TODO: DB
		logger.info("PiepelineEvent: " + event);
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
