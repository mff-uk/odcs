package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.util.Date;
import java.util.List;

/**
 * Implementation for accessing {@link OpenEvent} data objects.
 *
 * @author Jan Vojt
 */
public class DbOpenEventImpl extends DbAccessBase<OpenEvent>
								implements DbOpenEvent {

	public DbOpenEventImpl() {
		super(OpenEvent.class);
	}

	@Override
	public OpenEvent getOpenEvent(User user, Pipeline pipeline) {
		
		JPQLDbQuery<OpenEvent> jpql = new JPQLDbQuery<>(
				"SELECT e FROM OpenEvent e"
						+ " WHERE e.owner = :user"
						+ " AND e.pipeline = :ppl");
		
		jpql.setParameter("user", user)
			.setParameter("ppl", pipeline);
		
		return execute(jpql);
	}
	
	@Override
	public List<OpenEvent> getOpenEvents(Pipeline pipeline, Date from) {
		
		JPQLDbQuery<OpenEvent> jpql = new JPQLDbQuery<>();
		String query = "SELECT e FROM OpenEvent e"
				+ " LEFT JOIN FETCH e.owner u"; // eagerly load users
		
		if (from != null) {
			query += " WHERE e.timestamp >= :tsp";
			jpql.setParameter("tsp", from);
		}
		
		return executeList(jpql.setQuery(query));
	}

}
