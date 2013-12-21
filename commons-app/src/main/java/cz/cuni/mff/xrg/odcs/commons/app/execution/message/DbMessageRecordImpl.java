package cz.cuni.mff.xrg.odcs.commons.app.execution.message;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation providing access to {@link MessageRecord} data objects.
 *
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbMessageRecordImpl extends DbAccessBase<MessageRecord>
		implements DbMessageRecord {

	public DbMessageRecordImpl() {
		super(MessageRecord.class);
	}

	@Override
	public List<MessageRecord> getAllDPURecords(PipelineExecution pipelineExec) {
		JPQLDbQuery<MessageRecord> jpql = new JPQLDbQuery<>(
				"SELECT r FROM MessageRecord r WHERE r.execution = :ins");
		jpql.setParameter("ins", pipelineExec);

		return executeList(jpql);
	}

}
