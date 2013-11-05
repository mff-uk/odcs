package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.PipelineAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.exp.ContainerAuthorizator;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.ExecutionAccessor;
import cz.cuni.mff.xrg.odcs.rdf.help.RDFTriple;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class support creating Vaadin container from List<?>.
 *
 * @author Petyr
 *
 */
@Transactional(readOnly = true)
public class ContainerFactory {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DbPipeline dbPipeline;
    
    @Autowired
    private DbPipeline dbExecution;
    
    @Autowired
    private PipelineAccessor pipelineAccessor;
    
    @Autowired
    private ExecutionAccessor executionAccessor;
    
    @Autowired
    private ContainerAuthorizator containerAuth;

    /**
     * Create container for Pipelines and fill it with given data.
     *
     * @param data data for container
     * @return
     */
    public Container createPipelines(int pageLength) {

        ReadOnlyContainer c = new ReadOnlyContainer(dbPipeline, pipelineAccessor);
        containerAuth.authorize(c, pipelineAccessor.getEntityClass());
        return c;
//		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer(em, Pipeline.class, pageLength, "id", true, true, true);
//		container.getQueryView().getQueryDefinition().setDefaultSortState(
//				new Object[]{"id"}, new boolean[]{false});
//		container.addContainerProperty("id", Long.class, null, true, true);
//		container.addContainerProperty("name", String.class, "", true, true);
//		container.addContainerProperty("description", String.class, "", true, true);
//
//		return container;
    }

    /**
     * Should be used in scheduler list.
     * @param pageLength
     * @return 
     */
    public Container createPipelinesList(int pageLength) {
        
        IntlibLazyQueryContainer container = new IntlibLazyQueryContainer(em, Pipeline.class, pageLength, "id", true, true, true);
        container.getQueryView().getQueryDefinition().setDefaultSortState(
            new Object[]{"id"}, new boolean[]{false});
        container.addContainerProperty("id", Long.class, null, true, true);
        container.addContainerProperty("name", String.class, "", true, true);
        container.addContainerProperty("description", String.class, "", true, true);

        return container;
    }

    /**
     * Factory for table container with pipeline execution data.
     *
     * @return container with pipeline executions
     */
    public Container createExecutions(int pageLength) {

        IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(em, PipelineExecution.class, pageLength, "id", true, true, true);
        container.getQueryView().getQueryDefinition().setMaxNestedPropertyDepth(1);
        container.getQueryView().getQueryDefinition().setDefaultSortState(
            new Object[]{"id"}, new boolean[]{false}
        );

        container.addContainerProperty("id", Long.class, "");
		// Type used for date needs to be java.sql.Timestamp, because there
        // seems to be a bug in com.vaadin.data.util.filter.Compare#compareValue
        // that causes subclasses of java.util.Date to be uncomparable with
        // its superclass java.util.Date.
        // For details see github issue #135.
        container.addContainerProperty("start", Timestamp.class, null);
        container.addContainerProperty("pipeline.name", String.class, "");
        container.addContainerProperty("owner.username", String.class, "");
        container.addContainerProperty("duration", String.class, "");
        container.addContainerProperty("status", PipelineExecutionStatus.class, null);
        container.addContainerProperty("isDebugging", Boolean.class, false);
        container.addContainerProperty("lastChange", Timestamp.class, null);
        container.addContainerProperty("schedule", Schedule.class, null);

        return container;
    }

    /**
     * Create container for DPUTemplateRecord and fill it with given data.
     *
     * @param data data for container
     * @return
     */
    public Container createDPUTemplates(List<DPUTemplateRecord> data) {
        BeanContainer<Long, DPUTemplateRecord> container = new BeanContainer<>(DPUTemplateRecord.class);

        // set container id
        container.setBeanIdProperty("id");

        for (DPUTemplateRecord item : data) {
            container.addBean(item);
        }
        return container;
    }

    public Container createExecutionMessages(int pageLength) {
        IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(em, MessageRecord.class, pageLength, "id", true, true, true);
        container.getQueryView().getQueryDefinition().setDefaultSortState(
            new Object[]{"time"}, new boolean[]{true});
        container.getQueryView().getQueryDefinition().setMaxNestedPropertyDepth(1);
        container.addContainerProperty("id", Long.class, 0, true, true);
        container.addContainerProperty("time", Date.class, null, true, true);
        container.addContainerProperty("type", MessageRecordType.class, MessageRecordType.DPU_INFO, true, true);
        container.addContainerProperty("dpuInstance.name", String.class, "", true, true);
        container.addContainerProperty("dpuInstance.id", Long.class, null, true, true);
        container.addContainerProperty("shortMessage", String.class, "", true, true);

        return container;
    }

    @Transactional
    public Container createRDFData(List<RDFTriple> data) {
        BeanContainer<Long, RDFTriple> container = new BeanContainer<>(RDFTriple.class);

        container.setBeanIdProperty("id");
        container.addAll(data);

        return container;
    }

    public IntlibLazyQueryContainer createLogMessages(int pageLength) {
        IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(em, LogMessage.class, pageLength, "id", true, true, true);
        container.addContainerProperty("id", Long.class, 0, true, true);
        container.addContainerProperty("thread", String.class, "", true, true);
        container.addContainerProperty("level", Level.class, Level.ALL, true, true);
        container.addContainerProperty("source", String.class, "", true, true);
        container.addContainerProperty("message", String.class, "", true, true);
        container.addContainerProperty("date", Date.class, null, true, true);
        container.addContainerProperty("dpuInstanceId", Long.class, null, true, true);

		//container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_QUERY_INDEX, Integer.class, 0, true, true);
        //container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_INDEX, Integer.class, 0, true, true);
        //container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_QUERY_TIME, Long.class, 0, true, false);
        return container;
    }
}
