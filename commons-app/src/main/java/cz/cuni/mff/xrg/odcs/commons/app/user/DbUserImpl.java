package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface providing access to {@link User} data objects.
 * 
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbUserImpl extends DbAccessBase<User> implements DbUser {

	public DbUserImpl() {
		super(User.class);
	}

	@Override
	public List<User> getAll() {
		final String sringQuery = "SELECT e FROM User e";
		return executeList(sringQuery);		
	}

	@Override
	public User getByUsername(String username) {
		final String sringQuery = "SELECT e FROM User e WHERE e.username = :uname";
		TypedQuery<User> query = createTypedQuery(sringQuery);
		query.setParameter("uname", username);
		return execute(query);
	}
	
	
}
