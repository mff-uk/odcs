package cz.cuni.mff.xrg.odcs.frontend.container.rdf;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.ui.Notification;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RDFDataUnitHelper;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryRestriction;
import cz.cuni.mff.xrg.odcs.rdf.help.RDFTriple;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryPart;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.NestingBeanItem;
import org.vaadin.addons.lazyquerycontainer.Query;

/**
 * Implementation of {@link Query} interface for RDF queries. Just read-only
 * access to data is supported.
 *
 * @author Bogo
 */
public class RDFQuery implements Query {

	private static final Logger LOG = LoggerFactory.getLogger(RDFQuery.class);

	private static final String ALL_STATEMENT_QUERY = "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}";

	private String baseQuery;

	private int batchSize;

	private RDFQueryDefinition qd;

	private ArrayList<Item> cachedItems;

	private ManagableRdfDataUnit repository;

	/**
	 * Constructor.
	 *
	 * @param qd Query definition.
	 */
	public RDFQuery(RDFQueryDefinition qd) {
		this.baseQuery = qd.getBaseQuery();
		this.batchSize = qd.getBatchSize();
		this.qd = qd;
		this.repository = RDFDataUnitHelper
				.getRepository(qd.getInfo(), qd.getDpu(), qd.getDataUnit());


	}

	/**
	 * Size of the query result.
	 *
	 * @return Size of the result.
	 */
	@Override
	public int size() {

		if (repository == null) {
			throw new RuntimeException("Unable to load RDFDataUnit.");
		}
		try {
			repository.initialize();
			int count;
			String filteredQuery = setWhereCriteria(baseQuery);
			LOG.debug("Size query started...");
			if (isAllStatementQuery(filteredQuery)) {
				count = (int) repository.getResultSizeForDataCollection();
			} else {
				count = (int) repository.getResultSizeForQuery(filteredQuery);
			}
			LOG.debug("Size query finished!");
			return count;
		} catch (InvalidQueryException ex) {
			Notification.show("Query Validator",
					"Query is not valid: "
					+ ex.getCause().getMessage(),
					Notification.Type.ERROR_MESSAGE);
			LOG.debug("Size query exception", ex);
		} finally {
			repository.shutDown();
		}
		return 0;
	}

	private boolean isAllStatementQuery(String query) {
		return query.equalsIgnoreCase(ALL_STATEMENT_QUERY);
	}

	/**
	 * Load batch of items.
	 *
	 * @param startIndex Starting index of the item list.
	 * @param count      Count of the items to be retrieved.
	 * @return List of items.
	 */
	@Override
	public List<Item> loadItems(int startIndex, int count) {
		LOG.debug(String.format("Loading %d items from %d started...", count,
				startIndex));
		if (cachedItems != null) {
			LOG.debug("Using cached items.");
			return cachedItems.subList(startIndex, startIndex + count);
		}

		if (repository == null) {
			LOG.debug("Unable to load RDFDataUnit.");
			throw new RuntimeException("Unable to load RDFDataUnit.");
		}

		repository.initialize();
		String filteredQuery = setWhereCriteria(baseQuery);

		QueryRestriction restriction = new QueryRestriction(filteredQuery);
		restriction.setLimit(batchSize);
		//String query = baseQuery + String.format(" LIMIT %d", batchSize);
		int offset = startIndex / batchSize;
		if (offset > 0) {
			//query += String.format(" OFFSET %d", offset * batchSize);
			restriction.setOffset(offset * batchSize);
		}
		String query = restriction.getRestrictedQuery();
		SPARQLQueryType type;
		Object data;
		try {
			type = getQueryType(query);

			switch (type) {
				case SELECT:
					data = repository.executeSelectQueryAsTuples(query);
					break;
				case CONSTRUCT:
					data = getRDFTriplesData(repository.executeConstructQuery(
							query));
					break;
				case DESCRIBE:
					String resource = query.substring(query.indexOf('<') + 1,
							query.indexOf('>'));
					URIImpl uri = new URIImpl(resource);
					data = getRDFTriplesData(repository.describeURI(uri));
					break;
				default:
					return null;
			}

			List<Item> items = new ArrayList<>();
			switch (type) {
				case SELECT:
					MyTupleQueryResult result = (MyTupleQueryResult) data;
					int id = 0;
					while (result.hasNext()) {
						items.add(
								toItem(result.getBindingNames(), result.next(),
								++id));
					}
					result.close();
					break;
				case CONSTRUCT:
					for (RDFTriple triple : (List<RDFTriple>) data) {
						items.add(toItem(triple));
					}
					break;
				case DESCRIBE:
					cachedItems = new ArrayList<>();
					for (RDFTriple triple : (List<RDFTriple>) data) {
						cachedItems.add(toItem(triple));
					}
					LOG.debug(
							"Loading of items finished, whole result preloaded and cached.");
					return cachedItems.subList(startIndex, startIndex + count);
			}
			LOG.debug("Loading of items finished.");
			return items;
		} catch (InvalidQueryException ex) {
			Notification.show("Query Validator",
					"Query is not valid: "
					+ ex.getCause().getMessage(),
					Notification.Type.ERROR_MESSAGE);
		} catch (QueryEvaluationException ex) {
			Notification.show("Query Evaluation",
					"Error in query evaluation: "
					+ ex.getCause().getMessage(),
					Notification.Type.ERROR_MESSAGE);
		} finally {
			// close reporistory
			repository.shutDown();
		}
		return null;
	}

	private String setWhereCriteria(String query) {
		List<Filter> filters = qd.getFilters();
		return RDFDataUnitHelper.filterRDFQuery(query, filters);
	}

	private Item toItem(RDFTriple triple) {
		return new NestingBeanItem(triple, qd.getMaxNestedPropertyDepth(), qd
				.getPropertyIds());
	}

	private Item toItem(List<String> headers, BindingSet binding, int id) {
		return new BindingSetItem(headers, binding, id);
	}

	/**
	 * Override from {@link Query}. Not applicable for our use.
	 *
	 * @param list
	 * @param list1
	 * @param list2
	 */
	@Override
	public void saveItems(List<Item> list, List<Item> list1, List<Item> list2) {
		throw new UnsupportedOperationException(
				"RDFLazyQueryContainer is read-only.");
	}

	/**
	 * Override from {@link Query}. Not applicable for our use.
	 * @return not supported
	 */
	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException(
				"RDFLazyQueryContainer is read-only.");
	}

	/**
	 * Override from {@link Query}. Not applicable for our use.
	 * @return not supported
	 */
	@Override
	public Item constructItem() {
		throw new UnsupportedOperationException(
				"RDFLazyQueryContainer is read-only.");
	}

	private List<RDFTriple> getRDFTriplesData(Graph graph) {

		List<RDFTriple> triples = new ArrayList<>();

		int count = 0;

		for (Statement next : graph) {
			String subject = next.getSubject().stringValue();
			String predicate = next.getPredicate().stringValue();
			String object = next.getObject().stringValue();

			count++;

			RDFTriple triple = new RDFTriple(count, subject, predicate, object);
			triples.add(triple);
		}

		return triples;
	}

	private SPARQLQueryType getQueryType(String query) throws InvalidQueryException {
		if (query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException(
					"Invalid query: " + query));
		}
		QueryPart queryPart = new QueryPart(query);
		SPARQLQueryType type = queryPart.getSPARQLQueryType();

		return type;
	}
}
