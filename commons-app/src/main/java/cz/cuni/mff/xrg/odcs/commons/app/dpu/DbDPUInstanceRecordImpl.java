package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import java.util.List;

/**
 * Implementation for accessing {@link DPUInstanceRecord} data objects.
 *
 * @author Jan Vojt
 */
public class DbDPUInstanceRecordImpl extends DbAccessBase<DPUInstanceRecord>
		implements DbDPUInstanceRecord {

	public DbDPUInstanceRecordImpl() {
		super(DPUInstanceRecord.class);
	}

	@Override
	public List<DPUInstanceRecord> getAllDPUInstances() {
		JPQLDbQuery<DPUInstanceRecord> jpql = new JPQLDbQuery<>("SELECT e FROM DPUInstanceRecord e");
		return executeList(jpql);
	}

}
