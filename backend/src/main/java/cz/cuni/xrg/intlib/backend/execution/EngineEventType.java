package cz.cuni.xrg.intlib.backend.execution;

/**
 * Contains type of Engine's event. 
 * 
 * @see EngineEvent
 * @author Petyr
 *
 */
public enum EngineEventType {
	/**
	 * Ask Engine to check database and run scheduled pipelines.
	 */
	CheckDatabase,
	/**
	 * Let engine do start up check. For example check for 
	 * running pipelines. 
	 */
	StartUp
}
