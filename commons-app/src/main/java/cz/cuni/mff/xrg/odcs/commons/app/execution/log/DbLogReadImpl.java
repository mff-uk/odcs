package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessReadBase;
import java.util.Date;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Petyr
 */
public class DbLogReadImpl extends DbAccessReadBase<Log> implements DbLogRead {

	public DbLogReadImpl() {
		super(Log.class);
	}
	
	@Transactional
	@Override	
	public void prune(Date date) {
		Query query = em.createQuery(
				"DELETE FROM Log l WHERE l.timestamp < :time");
		query.setParameter("time", date.getTime()).executeUpdate();
	}
	
}
