package cz.cuni.xrg.intlib.backend.execution.dpu.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link Configurator} class.
 * 
 * @author Petyr
 *
 */
@ContextConfiguration(locations = {"classpath:backend-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfiguratorTest {

	@Autowired
	private BeanFactory beanFactory;
	
	/**
	 * Try to pass non-configurable object. Nothing should happened.
	 */
	@Test
	public void nonConfigurableTest() {				
		DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
		Object dpuInstance = new Object();
		PipelineExecution execution = mock(PipelineExecution.class);
		Context context = mock(Context.class);
		
		Configurator config = beanFactory.getBean(Configurator.class);
		assertTrue(config.preAction(dpu, dpuInstance, execution, context));
	}
	
	/**
	 * Try configurable object, the configuration function should
	 * be called with configuration from dpuInstance.
	 * @throws ConfigException
	 */
	@Test
	public void configurableTest() throws ConfigException {	
		byte[] rawConfig = "<a/>".getBytes();
		
		DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
		when(dpu.getRawConf()).thenReturn(rawConfig);
		Configurable dpuInstance = mock(Configurable.class);
				
		PipelineExecution execution = mock(PipelineExecution.class);
		Context context = mock(Context.class);

		Configurator config = beanFactory.getBean(Configurator.class);
		assertTrue(config.preAction(dpu, dpuInstance, execution, context));
		// verify that the configure function has been called 
		verify(dpuInstance, times(1)).configure(rawConfig);
	}
	
	/**
	 * Configurable object throw exception then configured. The exception
	 * should not be propagate instead call should return 
	 * @throws ConfigException
	 */
	@Test
	public void throwTest() throws ConfigException {
		DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
		Configurable dpuInstance = mock(Configurable.class);
		doThrow(new ConfigException()).when(dpuInstance).configure(null);		
		PipelineExecution execution = mock(PipelineExecution.class);
		Context context = mock(Context.class);			
		
		Configurator config = beanFactory.getBean(Configurator.class);
		assertFalse(config.preAction(dpu, dpuInstance, execution, context));
		
		// TODO Petyr: check if the event has been published		
	}	
	
}
