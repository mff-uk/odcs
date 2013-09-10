package cz.cuni.xrg.intlib.commons.app.execution;

import cz.cuni.xrg.intlib.commons.app.execution.log.LogException;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogExceptionLine;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cz.cuni.xrg.intlib.commons.app.execution.log.LogFacade;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogMessage;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
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
		levels.add(Level.WARN);
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
		levels.add(Level.WARN);
		levels.add(Level.INFO);
		
		List<LogMessage> logs = facade.getLogs(exec, levels);
		for (LogMessage log : logs) {
			isOfLevel(log, levels);
			hasExecutionProperty(log, execId);
		}
	}
	
	@Test
	public void testExistLogs() {
		long execId = 1L;
		PipelineExecution exec = mock(PipelineExecution.class);
		when(exec.getId()).thenReturn(execId);

		Set<Level> levels = new HashSet<>(2);
		levels.add(Level.INFO);
		boolean exist = facade.existLogs(exec, levels); 
		
		List<LogMessage> logs = facade.getLogs(exec, levels);
		boolean existExpect = !logs.isEmpty();
				
		assertEquals(existExpect, exist);
	}
	
	@Test
	public void testGetLogException() {
		LogMessage message = facade.getLog(1L);
		assertNotNull(message);
		
		LogException logEx = facade.getLogException(message);
		assertNotNull(logEx);
		assertEquals(3, logEx.getLines().size());
		assertTrue(logEx.toString().contains("\n"));
		
		// check order
		int i = 1;
		for (LogExceptionLine line : logEx.getLines()) {
			assertEquals(i++, line.getLineIndex());
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