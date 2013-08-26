package cz.cuni.xrg.intlib.frontend.auxiliaries;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogMessage;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.xrg.intlib.rdf.impl.RDFTriple;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Level;
import org.vaadin.addons.lazyquerycontainer.LazyEntityContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryView;

/**
 * Class support creating vaadin container from List<?>.
 *
 * @author Petyr
 *
 */
public class ContainerFactory {

	/**
	 * Prevent from creating instance.
	 */
	private ContainerFactory() {
	}

	/**
	 * Create container for Pipelines and fill it with given data.
	 *
	 * @param data data for container
	 * @return
	 */
	public static Container createPipelines() {
		LazyEntityContainer container = new LazyEntityContainer<>(App.getApp().getLogs().getEntityManager(), Pipeline.class, 10, "id", true, true, true);
		container.addContainerProperty("id", Long.class, 0, true, true);
		container.addContainerProperty("name", String.class, "", true, true);
		container.addContainerProperty("description", String.class, "", true, true);

		return container;
	}

	/**
	 * Create container for DPUTemplateRecord and fill it with given data.
	 *
	 * @param data data for container
	 * @return
	 */
	public static Container createDPUTemplates(List<DPUTemplateRecord> data) {
		BeanContainer<Long, DPUTemplateRecord> container = new BeanContainer<>(DPUTemplateRecord.class);
		// set container id
		container.setBeanIdProperty("id");

		for (DPUTemplateRecord item : data) {
			container.addBean(item);
		}
		return container;
	}

	public static Container createExecutionMessages() {
		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(App.getApp().getLogs().getEntityManager(), MessageRecord.class, 16, "id", true, true, true);
		container.getQueryView().getQueryDefinition().setDefaultSortState(
				new Object[]{"time"}, new boolean[]{true});
		container.getQueryView().getQueryDefinition().setMaxNestedPropertyDepth(2);
		container.addContainerProperty("id", Long.class, 0, true, true);
		container.addContainerProperty("time", Date.class, null, true, true);
		container.addContainerProperty("type", MessageRecordType.class, MessageRecordType.DPU_INFO, true, true);
		container.addContainerProperty("dpuInstance.name", String.class, "", true, true);
		container.addContainerProperty("shortMessage", String.class, "", true, true);
		return container;
	}

	public static Container createRDFData(List<RDFTriple> data) {
		BeanContainer<Long, RDFTriple> container = new BeanContainer<>(RDFTriple.class);
		container.setBeanIdProperty("id");
		container.addAll(data);

		return container;
	}

	public static IntlibLazyQueryContainer createLogMessages() {
		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(App.getApp().getLogs().getEntityManager(), LogMessage.class, 16, "id", true, true, true);
		container.addContainerProperty("id", Long.class, 0, true, true);
		container.addContainerProperty("thread", String.class, "", true, true);
		container.addContainerProperty("level", Level.class, Level.ALL, true, true);
		container.addContainerProperty("source", String.class, "", true, true);
		container.addContainerProperty("message", String.class, "", true, true);
		container.addContainerProperty("date", Date.class, null, true, true);

		container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_QUERY_INDEX, Integer.class, 0, true, true);
		container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_INDEX, Integer.class, 0, true, true);
		container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_QUERY_TIME, Long.class, 0, true, false);
		return container;
	}
}