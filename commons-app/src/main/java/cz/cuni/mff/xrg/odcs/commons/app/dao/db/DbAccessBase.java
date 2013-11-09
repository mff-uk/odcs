package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccess;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Partial implementation of {@link DataAccess} interface.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public abstract class DbAccessBase<T extends DataObject> implements DbAcess<T> {

	private static final Logger LOG = LoggerFactory.getLogger(DbAccessBase.class);
	
	/**
	 * Entity manager for accessing database with persisted objects
	 */
	@PersistenceContext
	protected EntityManager em;

    @Autowired(required = false)
    protected Authorizator authorizator;
    
    @Autowired(required = false)
    protected List<FilterTranslator> translators;
    
	/**
	 * Entity class.
	 */
	protected final Class<T> entityClass;
	    
	public DbAccessBase(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
    
	@Override
	public T create() {
		try {
			return entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			LOG.error("The class {} cannot be instatiated by no-arg constructor.", entityClass.getSimpleName(), ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public T copy(T object) {
		throw new UnsupportedOperationException();
	}
	
	@Transactional
	@Override
	public void save(T object) {
		if (object.getId() == null) {
			em.persist(object);
		} else {
			em.merge(object);
		}
	}

	@Transactional
	@Override
	public void delete(T object) {
		// we might be trying to remove detached entity
		if (!em.contains(object) && object.getId() != null) {
			object = getInstance(object.getId());
		}
		em.remove(object);
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

	@SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	@Override
	public List<T> executeList(DbQuery<T> query) {
		List<T> resultList = Collections.checkedList(
				query.getQuery().getResultList(), entityClass);
		return resultList;
	}
	
    @Transactional(readOnly = true)
	@Override
	public long executeSize(DbQueryCount<T> query) {
		 Long result = (Long) query.getQuery().getSingleResult();
		 return result;
	}
	
	@Override
	public DbQueryBuilder<T> createQueryBuilder() {
		return new DbQueryBuilderImpl<>(em, entityClass, 
            authorizator, translators);
	}
	
}
