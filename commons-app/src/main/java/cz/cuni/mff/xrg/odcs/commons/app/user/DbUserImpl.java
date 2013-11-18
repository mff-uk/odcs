package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import java.util.List;
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
	public List<User> getAllUsers() {
		JPQLDbQuery<User> jpql = new JPQLDbQuery<>("SELECT e FROM User e");
		return executeList(jpql);
	}

	@Override
	public User getByUsername(String username) {
		JPQLDbQuery<User> jpql = new JPQLDbQuery<>(
				"SELECT e FROM User e WHERE e.username = :uname");
		return execute(jpql.setParameter("uname", username));
	}
	
	
}
