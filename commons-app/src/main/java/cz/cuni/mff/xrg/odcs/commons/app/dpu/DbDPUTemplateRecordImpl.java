package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation for accessing {@link DPUTemplateRecord} data objects.
 * 
 * @author Jan Vojt
 * @author petyr
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbDPUTemplateRecordImpl extends DbAccessBase<DPUTemplateRecord>
        implements DbDPUTemplateRecord {

    public DbDPUTemplateRecordImpl() {
        super(DPUTemplateRecord.class);
    }

    @Override
    public List<DPUTemplateRecord> getAll() {
        final String queryStr = "SELECT e FROM DPUTemplateRecord e";
        return executeList(queryStr);
    }

    @Override
    public DPUTemplateRecord getByDirectory(String directory) {
        final String stringQuery = "SELECT e FROM DPUTemplateRecord e"
                + " WHERE e.jarDirectory = :directory";

        TypedQuery<DPUTemplateRecord> query = createTypedQuery(stringQuery);
        query.setParameter("directory", directory);

        return execute(query);
    }

    @Override
    public List<DPUTemplateRecord> getChilds(DPUTemplateRecord parentDpu) {
        final String stringQuery = "SELECT e FROM DPUTemplateRecord e"
                + " WHERE e.parent = :tmpl";

        TypedQuery<DPUTemplateRecord> query = createTypedQuery(stringQuery);
        query.setParameter("tmpl", parentDpu);

        return executeList(query);
    }

}
