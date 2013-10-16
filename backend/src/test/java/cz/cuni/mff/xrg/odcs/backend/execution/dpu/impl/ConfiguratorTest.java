package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.spring.InMemoryEventListener;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.Configurable;

import org.junit.Ignore;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link Configurator} class.
 * 
 * <p>
 * TODO Fix test and remove ignore annotation. Spring seems to be passing
 *		uninitialized {@link AppConfig} to {@link OSGiModuleFacadeConfig}.
 * 
 * @author Petyr
 * 
 */
@ContextConfiguration(locations = {"file:src/test/resource/backend-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class ConfiguratorTest {

	@Autowired
	private BeanFactory beanFactory;
	
	/**
	 * Try to pass non-configurable object. Nothing should happened.
	 */
	@Test
	public void nonConfigurableTest() {				
		DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
		Node node = new Node(dpu);				
		Object dpuInstance = new Object();
		PipelineExecution execution = mock(PipelineExecution.class);
		
		Context context = mock(Context.class);
		Map<Node, Context> contexts = new HashMap<>();
		contexts.put(node, context);
		
		Configurator config = beanFactory.getBean(Configurator.class);
		assertTrue(config.preAction(node, contexts, dpuInstance, execution, null));
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
		Node node = new Node(dpu);
		Configurable dpuInstance = mock(Configurable.class);
		PipelineExecution execution = mock(PipelineExecution.class);
		
		Context context = mock(Context.class);
		Map<Node, Context> contexts = new HashMap<>();
		contexts.put(node, context);
		
		Configurator config = beanFactory.getBean(Configurator.class);
		assertTrue(config.preAction(node, contexts, dpuInstance, execution, null));
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
		Node node = new Node(dpu);				
		Configurable dpuInstance = mock(Configurable.class);
		doThrow(new ConfigException()).when(dpuInstance).configure(null);
		PipelineExecution execution = mock(PipelineExecution.class);
		
		// create Context with given DPUInstanceRecord
		Context context = mock(Context.class);
		DPUInstanceRecord dpuRecord = mock(DPUInstanceRecord.class);
		when(context.getDpuInstance()).thenReturn(dpuRecord);
		when(dpuRecord.getName()).thenReturn("dpuName");
		
		Map<Node, Context> contexts = new HashMap<>();
		contexts.put(node, context);		
		
		// we also check if the proper event has been published
		InMemoryEventListener listener = beanFactory.getBean(InMemoryEventListener.class);
		listener.getEventList().clear();
		
		Configurator config = beanFactory.getBean(Configurator.class);
		
		assertFalse(config.preAction(node, contexts, dpuInstance, execution, null));
		
		// something has been published
		assertTrue(listener.getEventList().size() >= 1);		
	}
	
}
