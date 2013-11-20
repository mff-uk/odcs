package cz.cuni.mff.xrg.odcs.frontend.container;

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;
import org.vaadin.addons.lazyquerycontainer.QueryView;

/**
 * Container for lazy querying of RDF data. Works with SELECT or CONSTRUCT queries.
 * 
 *
 * @author Bogo
 * 
 * TODO (petyr) we should use DbAccess and ReadOnly container instead to unify the data access
 */
public class RDFLazyQueryContainer extends LazyQueryContainer {

	public RDFLazyQueryContainer(QueryView queryView) {
		super(queryView);
	}

	public RDFLazyQueryContainer(QueryDefinition queryDefinition, QueryFactory queryFactory) {
		super(queryDefinition, queryFactory);
	}

	public RDFLazyQueryContainer(QueryFactory queryFactory, Object idPropertyId, int batchSize, boolean compositeItems) {
		super(queryFactory, idPropertyId, batchSize, compositeItems);
	}
	
}
