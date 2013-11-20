package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Partial implementation of {@link DbAccessRead} interface.
 * 
 * @author Petyr
 * @param <T> 
 */
public class DbAccessReadBase <T extends DataObject> implements DbAccessRead<T> {
	
	@PersistenceContext
	protected EntityManager em;

    @Autowired(required = false)
    protected Authorizator authorizator;
    
    @Autowired(required = false)
    protected List<FilterTranslator> translators;	
	
	protected final Class<T> entityClass;
	    
	public DbAccessReadBase(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
    @Transactional(readOnly = true)
    @Override
	public T getInstance(long id) {
		return em.find(entityClass, id);
	}

    @Transactional(readOnly = true)
	@Override
	public T getLightInstance(long id) {
		return em.find(entityClass, id);
	}

	@SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	@Override
	public T execute(DbQuery<T> query) {
		// set max count of results
		query.getQuery().setMaxResults(1);
		try {
			T result = (T) query.getQuery().getSingleResult();
			return result;
		} catch(javax.persistence.NoResultException e) {
			return null;
		}
	}

	@Override
    @Transactional(readOnly = true)
	public T execute(JPQLDbQuery<T> query) {
		TypedQuery<T> tq = em.createQuery(query.getQuery(), entityClass);
		for (Map.Entry<String, Object> p : query.getParameters()) {
			tq.setParameter(p.getKey(), p.getValue());
		}
		return execute(new DbQuery<>(tq));
	}
	
	

	@SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	@Override
	public List<T> executeList(DbQuery<T> query) {
		List<T> resultList = Collections.checkedList(
				query.getQuery().getResultList(), entityClass);
		return resultList;
	}

	@Override
    @Transactional(readOnly = true)
	public List<T> executeList(JPQLDbQuery<T> query) {
		TypedQuery<T> tq = em.createQuery(query.getQuery(), entityClass);
		for (Map.Entry<String, Object> p : query.getParameters()) {
			tq.setParameter(p.getKey(), p.getValue());
		}
		return executeList(new DbQuery<>(tq));
	}
	
    @Transactional(readOnly = true)
	@Override
	public long executeSize(DbQueryCount<T> query) {
		 Long result = (Long) query.getQuery().getSingleResult();
		 return result;
	}

	@Override
    @Transactional(readOnly = true)
	public long executeSize(JPQLDbQuery<T> query) {
		
		// We need to use abstract Number class here, because Virtuoso seems
		// to return arbitrary instances of Number for INTEGER data type
		// (Short, Long). See GH-745.
		TypedQuery<Number> tq = em.createQuery(query.getQuery(), Number.class);
		for (Map.Entry<String, Object> p : query.getParameters()) {
			tq.setParameter(p.getKey(), p.getValue());
		}
		
		Number result = tq.getSingleResult();
		return result.longValue();
	}
	
	@Override
	public DbQueryBuilder<T> createQueryBuilder() {
		return new DbQueryBuilderImpl<>(em, entityClass, 
            authorizator, translators);
	}
	
}
