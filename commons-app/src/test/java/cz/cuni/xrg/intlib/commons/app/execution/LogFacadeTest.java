package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for fetching logs from database.
 *
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LogFacadeTest {
	
	@Autowired
	private LogFacade facade;
	
	@Test
	public void testGetAllLogs() {
		List<LogMessage> logs = facade.getAllLogs();
		assertEquals(10, logs.size());
		assertEquals(2, logs.get(0).getProperties().size());
		assertEquals(2, logs.get(1).getProperties().size());
		assertEquals(3, logs.get(2).getProperties().size());
		assertEquals(0, logs.get(3).getProperties().size());
	}
	
	@Test
	public void testLogsWarningOrInfo() {
		Set<Level> levels = new HashSet<>(2);
		levels.add(Level.WARNING);
		levels.add(Level.INFO);
		
		List<LogMessage> logs = facade.getLogs(levels);
		for (LogMessage log : logs) {
			isOfLevel(log, levels);
		}
	}
	
	@Test
	public void testLogsForExecution() {
		
		long execId = 1L;
		PipelineExecution exec = mock(PipelineExecution.class);
		when(exec.getId()).thenReturn(execId);
		
		List<LogMessage> logs = facade.getLogs(exec);
		for (LogMessage log : logs) {
			hasExecutionProperty(log, execId);
		}
	}
	
	@Test
	public void testExecutionLogsWarningOrInfo() {
		
		long execId = 1L;
		PipelineExecution exec = mock(PipelineExecution.class);
		when(exec.getId()).thenReturn(execId);
		
		Set<Level> levels = new HashSet<>(2);
		levels.add(Level.WARNING);
		levels.add(Level.INFO);
		
		List<LogMessage> logs = facade.getLogs(exec, levels);
		for (LogMessage log : logs) {
			isOfLevel(log, levels);
			hasExecutionProperty(log, execId);
		}
	}
	
	private void hasExecutionProperty(LogMessage log, long executionId) {
		
		assertNotNull(log.getProperties());
		assertTrue(log.getProperties().containsKey(LogMessage.MDPU_EXECUTION_KEY_NAME));
		
		assertEquals(
			log.getProperties().get(LogMessage.MDPU_EXECUTION_KEY_NAME),
			Long.toString(executionId)
		);
	}
	
	private void isOfLevel(LogMessage log, Set<Level> levels) {
		
		boolean foundLevel = false;
		for (Level level : levels) {
			foundLevel |= log.getLevel() == level;
		}
		assertTrue(foundLevel);
	}
}