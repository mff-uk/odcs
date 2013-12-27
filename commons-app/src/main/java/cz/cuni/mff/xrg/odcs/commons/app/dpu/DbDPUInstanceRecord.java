package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import java.util.List;

/**
 * Interface providing access to {@link DPUInstanceRecord} data objects.
 *
 * @author Jan Vojt
 */
public interface DbDPUInstanceRecord extends DbAccess<DPUInstanceRecord> {

	/**
	 * Returns list of all DPUInstanceRecord currently persisted in database.
	 *
	 * @return DPUInstance list
	 */
	public List<DPUInstanceRecord> getAll();

}
