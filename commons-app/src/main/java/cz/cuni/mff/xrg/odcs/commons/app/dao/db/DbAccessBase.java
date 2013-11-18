package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Partial implementation of {@link DbAccess} interface.
 * 
 * @author Petyr
 *
 * @param <T>
 */
public abstract class DbAccessBase<T extends DataObject> 
	extends DbAccessReadBase<T> implements DbAccess<T> {

	private static final Logger LOG = LoggerFactory.getLogger(DbAccessBase.class);
		    
	public DbAccessBase(Class<T> entityClass) {
		super(entityClass);
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
	
}
