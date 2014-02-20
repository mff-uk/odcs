package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessReadBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryBuilder;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.filter.Compare;
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
		Query query = em.createQuery("DELETE FROM Log l WHERE l.timestamp < :time");
		query.setParameter("time", date.getTime()).executeUpdate();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long getLastRelativeIndex(Long executionId) {
		DbQueryBuilder<Log> builder = createQueryBuilder();
		
		builder.addFilter(Compare.equal("execution", executionId));
		builder.sort("relativeId", false);
				
		Log lastLog = execute(builder.getQuery().limit(0, 1));
		return lastLog == null ? null : lastLog.getRelativeId();
	}
	
}
