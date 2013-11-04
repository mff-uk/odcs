package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Container;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

/**
 * Takes care of authorization by filtering out container content, user is not
 * allowed to see.
 *
 * @author Jan Vojt
 */
public interface ContainerAuthorizator {
	
	/**
	 * Filters out unauthorized content from the container.
	 * 
	 * @param container 
	 * @param entityClass 
	 */
	public void authorize(Container.Filterable container, Class<? extends DataObject> entityClass);

}
