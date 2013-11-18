package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import java.util.List;

/**
 * Implementation providing access to {@link LogExceptionLine} data objects.
 *
 * @author Jan Vojt
 */
public class DbLogExceptionLineImpl extends DbAccessBase<LogExceptionLine>
									implements DbLogExceptionLine {

	public DbLogExceptionLineImpl() {
		super(LogExceptionLine.class);
	}

	@Override
	public LogException getLogException(LogMessage message) {
		
		JPQLDbQuery<LogExceptionLine> jpql = new JPQLDbQuery<>(
				"SELECT l FROM LogExceptionLine l"
				+ " LEFT JOIN l.message m"
				+ " WHERE m = :msg"
				+ " ORDER BY l.lineIndex ASC");
		
		jpql.setParameter("msg", message);
		
		List<LogExceptionLine> exs = executeList(jpql);
		if (exs.isEmpty()) {
			return null;
		}
		return new LogException(exs);
	}
	
}
