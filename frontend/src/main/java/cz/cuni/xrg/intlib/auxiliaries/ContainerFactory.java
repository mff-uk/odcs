package cz.cuni.xrg.intlib.auxiliaries;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;

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
	 * Create container.
	 * @param data data for container
	 * @return
	 */
	public static Container Create(List<Pipeline> data) {
		BeanContainer<Integer, Pipeline> container = new BeanContainer<Integer, Pipeline>( Pipeline.class );
		// set container id
		container.setBeanIdProperty("id");
		
		for (Pipeline item : data) {
			container.addBean(item);
		}
		return container;
	}
	
}
