package cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface providing access to {@link NamespacePrefix} data objects.
 * 
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
class DbNamespacePrefixImpl extends DbAccessBase<NamespacePrefix>
									implements DbNamespacePrefix {

	public DbNamespacePrefixImpl() {
		super(NamespacePrefix.class);
	}
	
	@Override
	public List<NamespacePrefix> getAllPrefixes() {
		JPQLDbQuery<NamespacePrefix> jpql = new JPQLDbQuery<>(
				"SELECT e FROM NamespacePrefix e");
		return executeList(jpql);
	}

	@Override
	public NamespacePrefix getByName(String name) {
		JPQLDbQuery<NamespacePrefix> jpql = new JPQLDbQuery<>(
				"SELECT e FROM NamespacePrefix e WHERE e.name = :name");
		jpql.setParameter("name", name);
		
		return execute(jpql);
	}

}
