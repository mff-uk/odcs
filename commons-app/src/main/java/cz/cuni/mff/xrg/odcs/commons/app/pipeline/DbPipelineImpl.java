package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import java.util.List;

/**
 * Implementation for accessing {@link Pipeline} data objects.
 *
 * @author Petyr
 * @author Jan Vojt
 */
class DbPipelineImpl extends DbAccessBase<Pipeline> implements DbPipeline {

    protected DbPipelineImpl() {
        super(Pipeline.class);
    }
	
	@Override
	public List<Pipeline> getAll() {
		JPQLDbQuery<Pipeline> jpql = new JPQLDbQuery<>("SELECT e FROM Pipeline e");
		return executeList(jpql);
	}

	@Override
	public List<Pipeline> getPipelinesUsingDPU(DPUTemplateRecord dpu) {
		
		JPQLDbQuery<Pipeline> jpql = new JPQLDbQuery<>(
				"SELECT e FROM Pipeline e"
				+ " LEFT JOIN e.graph g"
				+ " LEFT JOIN g.nodes n"
				+ " LEFT JOIN n.dpuInstance i"
				+ " LEFT JOIN i.template t"
				+ " WHERE t = :dpu");
		jpql.setParameter("dpu", dpu);
		
		return executeList(jpql);
	}

	@Override
	public Pipeline getPipelineByName(String name) {
		
		JPQLDbQuery<Pipeline> jpql = new JPQLDbQuery<>(
				"SELECT e FROM Pipeline e"
                + " WHERE e.name = :name");
		jpql.setParameter("name", name);
		
		return execute(jpql);
	}
	
	
	

}
