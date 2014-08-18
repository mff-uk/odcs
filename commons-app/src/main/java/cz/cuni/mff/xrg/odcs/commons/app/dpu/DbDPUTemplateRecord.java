package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

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
    public List<DPUTemplateRecord> getAll();

    /**
     * Fetch DPU template using given DPU directory.
     * 
     * @param directory
     *            name where JAR file is located
     * @return DPU template
     */
    public DPUTemplateRecord getByDirectory(String directory);

    
    /**
     * Fetch DPU template using given DPU jar.
     * 
     * @param jarName
     *            name of dpu's jar
     * @return DPU template
     */
    public DPUTemplateRecord getByJarName(String jarName);

    /**
     * Fetch DPU template using given DPU name.
     *
     * @param name
     *            name of dpu's jar
     * @return DPU template
     */
    public DPUTemplateRecord getByName(String name);
    
    
    /**
     * Fetch all child DPU templates for a given DPU template.
     * 
     * @param parentDpu
     *            DPU template
     * @return list of child DPU templates or empty collection
     */
    public List<DPUTemplateRecord> getChilds(DPUTemplateRecord parentDpu);

}
