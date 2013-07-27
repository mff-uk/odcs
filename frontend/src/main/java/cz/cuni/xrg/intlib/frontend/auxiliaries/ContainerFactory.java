package cz.cuni.xrg.intlib.frontend.auxiliaries;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogMessage;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.rdf.impl.RDFTriple;
import java.sql.Timestamp;

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
	 * @param data data for container
	 * @return
	 */
	public static Container CreatePipelines(List<Pipeline> data) {
		BeanContainer<Long, Pipeline> container = new BeanContainer<>( Pipeline.class );
		// set container id
		container.setBeanIdProperty("id");

		for (Pipeline item : data) {
			container.addBean(item);
		}
		return container;
	}

	/**
	 * Create container for DPUTemplateRecord and fill it with given data.
	 * @param data data for container
	 * @return
	 */
	public static Container CreateDPUTemplates(List<DPUTemplateRecord> data) {
		BeanContainer<Long, DPUTemplateRecord> container = new BeanContainer<>( DPUTemplateRecord.class );
		// set container id
		container.setBeanIdProperty("id");

		for (DPUTemplateRecord item : data) {
			container.addBean(item);
		}
		return container;
	}

	public static Container CreateExecutionMessages(List<MessageRecord> data) {
		BeanContainer<Long, MessageRecord> container = new BeanContainer<>( MessageRecord.class);
		container.setBeanIdProperty("id");
		container.addNestedContainerProperty("timestamp");
		container.addAll(data);

		return container;
	}

	public static Container CreateRDFData(List<RDFTriple> data) {
		BeanContainer<Long, RDFTriple> container = new BeanContainer<> (RDFTriple.class);
		container.setBeanIdProperty("id");
		container.addAll(data);

		return container;
	}
	
	public static Container CreateLogMessages(List<LogMessage> data) {
		BeanContainer<Long, LogMessage> container = new BeanContainer<>( LogMessage.class);
		container.setBeanIdProperty("id");
		container.addAll(data);
		return container;
	}

}