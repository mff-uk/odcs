package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.util.Date;
import java.util.List;

/**
 * Interface for access to {@link OpenEvent}s.
 *
 * @author Jan Vojt
 */
public interface DbOpenEvent extends DbAccess<OpenEvent> {
	
	/**
	 * Finds open pipeline event by user and pipeline.
	 * 
	 * @param user
	 * @param pipeline
	 * @return open pipeline event
	 */
	public OpenEvent getOpenEvent(User user, Pipeline pipeline);
	
	/**
	 * Fetches all events when given pipeline was open in pipeline canvas.
	 * 
	 * @param pipeline
	 * @param from select only events with later timestamp
	 * @return list of events
	 */
	public List<OpenEvent> getOpenEvents(Pipeline pipeline, Date from);

}
