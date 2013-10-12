package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for directory name generation by {@link ExecutionContextInfo}.
 *
 * @author Petyr
 */
public class ExecutionContextInfoTest {
	
	private Long execId = (long)1;
	
	private PipelineExecution execution; 
	
	private ExecutionContextInfo info;
	
	@Before
	public void init() {
		execution = mock(PipelineExecution.class);
		when(execution.getId()).thenReturn(execId);
		info = new ExecutionContextInfo(execution);
	}
	
	@Test
	public void getDPUTmpPathTest() {
		DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
		when(dpu.getId()).thenReturn((long)11);
		when(dpu.getName()).thenReturn("Q 	CK");
		
		String expected = File.separator + "1" + File.separator +
				"working" + File.separator + 
				"dpu_11_Q_CK" + File.separator +
				"tmp";
		String value = info.getDPUTmpPath(dpu);
		
		assertEquals(expected, value);
	}
	
}
