package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.ui.Notification;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RDFDataUnitHelper;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.impl.QueryFilterManager;
import cz.cuni.mff.xrg.odcs.rdf.impl.QueryRestriction;
import cz.cuni.mff.xrg.odcs.rdf.impl.RDFTriple;
import cz.cuni.mff.xrg.odcs.rdf.impl.RegexFilter;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.vaadin.addons.lazyquerycontainer.NestingBeanItem;
import org.vaadin.addons.lazyquerycontainer.Query;

/**
 * Implementation of {@link Query} interface for RDF queries. Just read-only
 * access to data is supported.
 *
 * @author Bogo
 */
public class RDFQuery implements Query {

	private String baseQuery;
	private int batchSize;
	private RDFQueryDefinition qd;

	public RDFQuery(RDFQueryDefinition qd) {
		this.baseQuery = qd.getBaseQuery();
		this.batchSize = qd.getBatchSize();
		this.qd = qd;
	}

	@Override
	public int size() {
		RDFDataUnit repository = RDFDataUnitHelper.getRepository(qd.getContext(), qd.getDpu(), qd.getDataUnit());
		if (repository == null) {
			throw new RuntimeException("Unable to load RDFDataUnit.");
		}
		try {
			String filteredQuery = setWhereCriteria(baseQuery);
			return (int) repository.getResultSizeForQuery(filteredQuery);
		} catch (InvalidQueryException ex) {
			Notification.show("Query Validator",
					"Query is not valid: "
					+ ex.getCause().getMessage(),
					Notification.Type.ERROR_MESSAGE);
		} finally {
			repository.shutDown();
		}
		return 0;
	}

	/**
	 * Load batch of items.
	 *
	 * @param startIndex Starting index of the item list.
	 * @param count Count of the items to be retrieved.
	 * @return List of items.
	 */
	@Override
	public List<Item> loadItems(int startIndex, int count) {
		RDFDataUnit repository = RDFDataUnitHelper.getRepository(qd.getContext(), qd.getDpu(), qd.getDataUnit());
		if (repository == null) {
			throw new RuntimeException("Unable to load RDFDataUnit.");
		}
		
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
		boolean isSelectQuery;
		Object data;
		try {
			isSelectQuery = isSelectQuery(query);

			if (isSelectQuery) {
				data = repository.executeSelectQueryAsTuples(query);
			} else {
				Graph queryResult = repository.executeConstructQuery(query);
				List<Statement> result = new ArrayList<>(queryResult.size());
				Iterator<Statement> it = queryResult.iterator();
				while (it.hasNext()) {
					result.add(it.next());
				}
				data = getRDFTriplesData(result);
			}

			List<Item> items = new ArrayList<>();
			if (isSelectQuery) {
				MyTupleQueryResult result = (MyTupleQueryResult) data;
				int id = 0;
				while (result.hasNext()) {
					items.add(toItem(result.next(), ++id));
				}

			} else {
				for (RDFTriple triple : (List<RDFTriple>) data) {
					items.add(toItem(triple));
				}
			}
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
		QueryFilterManager filterManager = new QueryFilterManager(query);
		for (Filter filter : filters) {
			if (filter.getClass() == RDFRegexFilter.class) {
				RDFRegexFilter rdfRegexFilter = (RDFRegexFilter) filter;
				RegexFilter rf = new RegexFilter(rdfRegexFilter.getColumnName(), rdfRegexFilter.getRegex());
				filterManager.addFilter(rf);
			}
		}
		return filterManager.getFilteredQuery();
	}

	private Item toItem(RDFTriple triple) {
		return new NestingBeanItem(triple, qd.getMaxNestedPropertyDepth(), qd.getPropertyIds());
	}

	private Item toItem(BindingSet binding, int id) {
		return new BindingSetItem(binding, id);
	}

	@Override
	public void saveItems(List<Item> list, List<Item> list1, List<Item> list2) {
		throw new UnsupportedOperationException("RDFLazyQueryContainer is read-only.");
	}

	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException("RDFLazyQueryContainer is read-only.");
	}

	@Override
	public Item constructItem() {
		throw new UnsupportedOperationException("RDFLazyQueryContainer is read-only.");
	}

	private boolean isSelectQuery(String query) throws InvalidQueryException {
		if (query.length() < 9) {
			//Due to expected exception format in catch block
			throw new InvalidQueryException(new InvalidQueryException(
					"Invalid query."));
		}
		String queryStart = query.trim().substring(0, 9).toLowerCase();
		return queryStart.startsWith("select");
	}

	private List<RDFTriple> getRDFTriplesData(List<Statement> statements) {

		List<RDFTriple> triples = new ArrayList<>();

		int count = 0;

		for (Statement next : statements) {
			String subject = next.getSubject().stringValue();
			String predicate = next.getPredicate().stringValue();
			String object = next.getObject().stringValue();

			count++;

			RDFTriple triple = new RDFTriple(count, subject, predicate, object);
			triples.add(triple);
		}

		return triples;
	}
}
