package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import java.util.List;

/**
 *
 * @author Jan Vojt
 */
public class DbDPUTemplateRecordImpl extends DbAccessBase<DPUTemplateRecord>
		implements DbDPUTemplateRecord {

	public DbDPUTemplateRecordImpl() {
		super(DPUTemplateRecord.class);
	}

	@Override
	public List<DPUTemplateRecord> getAllTemplates() {
		JPQLDbQuery<DPUTemplateRecord> jpql = new JPQLDbQuery<>(
				"SELECT e FROM DPUTemplateRecord e");
		return executeList(jpql);
	}

	@Override
	public DPUTemplateRecord getTemplateByDirectory(String directory) {
		
		JPQLDbQuery<DPUTemplateRecord> jpql = new JPQLDbQuery<>(
				"SELECT e FROM DPUTemplateRecord e"
				+ " WHERE e.jarDirectory = :directory");
		jpql.setParameter("directory", directory);
		
		return execute(jpql);
	}

	@Override
	public List<DPUTemplateRecord> getChildDPUs(DPUTemplateRecord parentDpu) {
		JPQLDbQuery<DPUTemplateRecord> jpql = new JPQLDbQuery<DPUTemplateRecord>(
				"SELECT e FROM DPUTemplateRecord e WHERE e.parent = :tmpl");
		jpql.setParameter("tmpl", parentDpu);
		
		return executeList(jpql);
	}

}
