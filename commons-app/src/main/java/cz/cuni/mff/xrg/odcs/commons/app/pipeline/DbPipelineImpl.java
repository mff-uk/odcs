package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation for accessing {@link Pipeline} data objects.
 *
 * @author Petyr
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
class DbPipelineImpl extends DbAccessBase<Pipeline> implements DbPipeline {

    protected DbPipelineImpl() {
        super(Pipeline.class);
    }
	
	@Override
	public List<Pipeline> getAll() {
		final String queryStr = "SELECT e FROM Pipeline e";
		return executeList(queryStr);		
	}

	@Override
	public List<Pipeline> getPipelinesUsingDPU(DPUTemplateRecord dpu) {
		final String stringQuery = "SELECT e FROM Pipeline e"
				+ " LEFT JOIN e.graph g"
				+ " LEFT JOIN g.nodes n"
				+ " LEFT JOIN n.dpuInstance i"
				+ " LEFT JOIN i.template t"
				+ " WHERE t.id = :dpuid";
		TypedQuery<Pipeline> query = createTypedQuery(stringQuery);	
		query.setParameter("dpuid", dpu.getId());
		return executeList(query);
	}

	@Override
	public Pipeline getPipelineByName(String name) {
		final String stringQuery = "SELECT e FROM Pipeline e"
                + " WHERE e.name = :name";
		TypedQuery<Pipeline> query = createTypedQuery(stringQuery);	
		query.setParameter("name", name);
		return execute(query);		
	}	

}
