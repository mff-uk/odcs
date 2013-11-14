package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import java.util.List;

/**
 * Interface providing access to {@link DPUTemplateRecord} data objects.
 *
 * @author Jan Vojt
 */
public interface DbDPUTemplateRecord extends DbAccess<DPUTemplateRecord> {

	/**
	 * @return DPURecord list of all DPUTemplateRecords currently persisted in
	 *         database.
	 */
	public List<DPUTemplateRecord> getAllTemplates();

	/**
	 * Fetch DPU template using given DPU directory.
	 * 
	 * @param directory name where JAR file is located
	 * @return DPU template
	 */
	public DPUTemplateRecord getTemplateByDirectory(String directory);

}
