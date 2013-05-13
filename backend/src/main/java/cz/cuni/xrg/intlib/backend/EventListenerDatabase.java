package cz.cuni.xrg.intlib.backend;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUMessage;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractEvent;
import cz.cuni.xrg.intlib.backend.loader.events.LoadEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineEvent;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformEvent;
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
	}
	
	/**
	 * Take care about PipelineEvent.
	 * @param event
	 */	
	private void onPipelineEvent(PipelineEvent event) {
		// TODO: DB
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
