package cz.cuni.xrg.intlib.auxiliaries;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Class support creating vaadin container from List<?>.
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
		BeanContainer<Integer, Pipeline> container = new BeanContainer<Integer, Pipeline>( Pipeline.class );
		// set container id
		container.setBeanIdProperty("id");
		
		for (Pipeline item : data) {
			container.addBean(item);
		}
		return container;
	}
	
	/**
	 * Create container for DPUs and fill it with given data.
	 * @param data data for container
	 * @return
	 */
	public static Container CreateDPUs(List<DPU> data) {
		BeanContainer<Integer, DPU> container = new BeanContainer<Integer, DPU>( DPU.class );
		// set container id
		container.setBeanIdProperty("id");
		
		for (DPU item : data) {
			container.addBean(item);
		}		
		return container;
	}	
	
}
