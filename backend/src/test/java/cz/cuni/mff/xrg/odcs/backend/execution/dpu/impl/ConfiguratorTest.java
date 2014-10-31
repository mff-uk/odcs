package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.config.DPUConfigurable;
import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.spring.InMemoryEventListener;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Test suite for {@link Configurator} class.
 * 
 * @author Petyr
 */
@ContextConfiguration(locations = { "classpath:backend-test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfiguratorTest {

    @Autowired
    private BeanFactory beanFactory;

//	@Test
//	This is triple generating hidden here :)
    public void bababa() throws FileNotFoundException, RDFHandlerException {
        ValueFactory f = new MemValueFactory();

        FileOutputStream out = new FileOutputStream("/home/michal/file.ttl");
        RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
        writer.handleNamespace("", "http://example.org/ontology/");
        writer.startRDF();
        for (int i = 1; i < 160000000; i++) {
            writer.handleStatement(f.createStatement(
                    f.createURI("http://example.org/people/d" + String.valueOf(i++)),
                    f.createURI("http://example.org/ontology/e" + String.valueOf(i++)),
                    f.createLiteral("Alice" + String.valueOf(i++))
                    ));
        }
        writer.endRDF();
//			RepositoryConnection connection = null;
//			try {
//				connection = rdfDataUnit.getConnection();
//				URI contextName = rdfDataUnit.getDataGraph();
//				ValueFactory f = new MemValueFactory();
//				connection.begin();
//				for (int i = 1; i< 4000000;i++) {
//					  connection.add(f.createStatement(
//							  f.createURI("http://example.org/people/d" + String.valueOf(i++)),
//							  f.createURI("http://example.org/ontology/e" + String.valueOf(i++)),
//							  f.createLiteral("Alice"+ String.valueOf(i++))
//							  ), contextName);
//						if ((i % 100000) == 0) {
//							connection.commit();
//							LOG.debug("Number of triples {} ", i / 4);
//							if (context.canceled()) {
//								break;
//							}
//							connection.begin();
//						}
//				}
//				connection.commit();
//				LOG.debug("Number of triples {} ", connection.size(contextName));
//			} catch (RepositoryException ex) {
//				LOG.error("Error", ex);
//				context.sendMessage(MessageType.ERROR, ex.getMessage(), ex
//	                  .fillInStackTrace().toString());			
//			} finally {
//				if (connection !=null) try {connection.close();}catch (RepositoryException ex) {}
//			}		
    }

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
        assertTrue(config.preAction(node, contexts, dpuInstance, execution,
                null, true));
    }

    /**
     * Try configurable object, the configuration function should
     * be called with configuration from dpuInstance.
     * 
     * @throws DPUConfigException
     */
    @Test
    public void configurableTest() throws DPUConfigException {
        String rawConfig = "<a/>";

        DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
        when(dpu.getRawConf()).thenReturn(rawConfig);
        Node node = new Node(dpu);
        DPUConfigurable dpuInstance = mock(DPUConfigurable.class);
        PipelineExecution execution = mock(PipelineExecution.class);

        Context context = mock(Context.class);
        Map<Node, Context> contexts = new HashMap<>();
        contexts.put(node, context);

        Configurator config = beanFactory.getBean(Configurator.class);
        assertTrue(config.preAction(node, contexts, dpuInstance, execution,
                null, true));
        // verify that the configure function has been called 
        verify(dpuInstance, times(1)).configure(rawConfig);
    }

    /**
     * DPUConfigurable object throw exception then configured. The exception
     * should not be propagate instead call should return
     * 
     * @throws DPUConfigException
     */
    @Test
    public void throwTest() throws DPUConfigException {
        DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
        Node node = new Node(dpu);
        DPUConfigurable dpuInstance = mock(DPUConfigurable.class);
        doThrow(new DPUConfigException("dsfafds")).when(dpuInstance).configure(null);
        PipelineExecution execution = mock(PipelineExecution.class);

        // create Context with given DPUInstanceRecord
        Context context = mock(Context.class);
        DPUInstanceRecord dpuRecord = mock(DPUInstanceRecord.class);
        when(context.getDPU()).thenReturn(dpuRecord);
        when(dpuRecord.getName()).thenReturn("dpuName");

        Map<Node, Context> contexts = new HashMap<>();
        contexts.put(node, context);

        // we also check if the proper event has been published
        InMemoryEventListener listener = beanFactory.getBean(InMemoryEventListener.class);
        listener.getEventList().clear();

        Configurator config = beanFactory.getBean(Configurator.class);

        assertFalse(config.preAction(node, contexts, dpuInstance, execution,
                null, true));

        // something has been published
        assertTrue(listener.getEventList().size() >= 1);
    }

}
