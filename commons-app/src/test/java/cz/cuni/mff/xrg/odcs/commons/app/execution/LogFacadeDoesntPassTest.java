package cz.cuni.mff.xrg.odcs.commons.app.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Level;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.filter.Compare;
import cz.cuni.mff.xrg.odcs.commons.app.facade.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Test suite for fetching logs from database.
 * 
 * @author Jan Vojt
 */
@ContextConfiguration(locations = { "classpath:commons-app-test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class LogFacadeDoesntPassTest {

    @Autowired
    private LogFacade facade;

    @Test
    public void testExistLogs() {
        long execId = 1L;
        PipelineExecution exec = mock(PipelineExecution.class);
        when(exec.getId()).thenReturn(execId);

        boolean existInfo = facade.existLogsGreaterOrEqual(exec, Level.INFO);
        assertEquals(true, existInfo);

        boolean existWarn = facade.existLogsGreaterOrEqual(exec, Level.WARN);
        assertEquals(true, existWarn);

        boolean existErro = facade.existLogsGreaterOrEqual(exec, Level.ERROR);
        assertEquals(false, existErro);
    }

    @Test
    public void testStream() {
        assertNotNull(facade.getLogsAsStream(null));
        assertNotNull(facade.getLogsAsStream(Arrays.<Object> asList(Compare.greaterEqual("logLevel", Level.INFO.toInt()))));
        assertNull(facade.getLogsAsStream(Arrays.<Object> asList(
                Compare.greaterEqual("logLevel", Level.ERROR.toInt()),
                Compare.less("logLevel", Level.ERROR.toInt())
                )));
    }

}
