package cz.cuni.xrg.intlib.backend.logback;

import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.classic.Level;

import cz.cuni.xrg.intlib.commons.app.execution.log.LogMessage;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Use MDC value {@link LogMessage.MDPU_EXECUTION_KEY_NAME} to determine 
 * related execution and then do Level filtering based on stored informations.
 * 
 * @author Petyr
 *
 */
public class MdcExecutionLevelFilter extends ch.qos.logback.core.filter.Filter<ILoggingEvent> {

	/**
	 * Store required minimal levels for executions.
	 */
	private static ConcurrentHashMap<String, Level> levels = new ConcurrentHashMap<String, Level>();
	
	@Override
	public FilterReply decide(ILoggingEvent event) {
		String execution = event.getMDCPropertyMap().get(LogMessage.MDPU_EXECUTION_KEY_NAME);
		Level level = levels.get(execution);
		if (level == null) {
			// no restriction
			return FilterReply.NEUTRAL;
		} else {
			// use filter level
			if ( event.getLevel().isGreaterOrEqual(level) ) {
				return FilterReply.NEUTRAL;
			} else {
				// event.level < minLevel
				return FilterReply.DENY;
			}
		}
	}

	/**
	 * Add record for min. required level for given execution.
	 * @param execution
	 * @param level
	 */
	public static void add(String execution, Level level) {
		levels.put(execution, level);
	}
	
	/**
	 * Remove record for given execution.
	 * @param execution
	 */
	public static void remove(String execution) {
		levels.remove(execution);
	}
	
}
