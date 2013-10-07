package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Item;
import cz.cuni.mff.xrg.odcs.frontend.browser.RDFDataUnitHelper;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.impl.MyTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.impl.RDFTriple;
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
 * Implementation of {@link Query} interface for RDF queries. 
 * Just read-only access to data is supported.
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
		try {
			if (isSelectQuery(baseQuery)) {
				return 1000;
			} else {
				Graph queryResult = repository.executeConstructQuery(baseQuery);
				return queryResult.size();
			}
		} catch (InvalidQueryException ex) {
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
		String query = baseQuery + String.format(" LIMIT %d", batchSize);
		int offset = startIndex / batchSize;
		if (offset > 0) {
			query += String.format(" OFFSET %d", offset * batchSize);
		}

		boolean isSelectQuery = false;
		Object data = null;
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
		} catch (QueryEvaluationException ex) {
		} finally {
			// close reporistory
			repository.shutDown();
		}
		return null;
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
