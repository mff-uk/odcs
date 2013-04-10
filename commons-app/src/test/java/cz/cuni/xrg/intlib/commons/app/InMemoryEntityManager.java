package cz.cuni.xrg.intlib.commons.app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

public class InMemoryEntityManager implements EntityManager {

	/**
	 * Entites are saved in here
	 */
	private Map<Object, Object> repo = new HashMap<Object, Object>();

	private boolean isOpen = true;

	private EntityTransaction tx = new EntityTransactionStub();

	private static int nextId = 1;

	@Override
	public void clear() {
		repo.clear();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object arg0) {
		return repo.containsValue(arg0);
	}

	@Override
	public Query createNamedQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0, Class arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object key) {
		return arg0.cast(repo.get(key));
	}

	@Override
	public void flush() {
		// do nothing
	}

	@Override
	public Object getDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlushModeType getFlushMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getReference(Class<T> arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityTransaction getTransaction() {
		return tx;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public void joinTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void lock(Object arg0, LockModeType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T merge(T arg0) {

		T e = (T) repo.get(getId(arg0));

		if (e == null) {
			return arg0;
		} else {
			persist(e);
			return e;
		}
	}

	@Override
	public void persist(Object o) {
		setId(o, getNextId());
		repo.put(getId(o), o);
	}

	/**
	 * Retrieves ID of entity
	 * @param o
	 * @return
	 */
	private Integer getId(Object o) {

		int id = 0;

		try {
			Method m = o.getClass().getMethod("getId");
			id = (Integer) m.invoke(o);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Integer(id);
	}

	/**
	 * Set ID to entity
	 * Uses reflections to modify private fields to mimic JPA behaviour
	 * @param o
	 * @param id
	 */
	private void setId(Object o, Integer id) {
		try {
			Field idField = o.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(o, id.intValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Unique ID generator
	 * @return
	 */
	private synchronized Integer getNextId() {
		int id = nextId++;
		return new Integer(id);
	}

	@Override
	public void refresh(Object arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(Object arg0) {
		repo.remove(getId(arg0));
	}

	@Override
	public void setFlushMode(FlushModeType arg0) {
		// TODO Auto-generated method stub

	}

	private class EntityTransactionStub implements EntityTransaction {

		@Override
		public void begin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void commit() {
			// TODO Auto-generated method stub

		}

		@Override
		public void rollback() {
			// TODO Auto-generated method stub

		}

		@Override
		public void setRollbackOnly() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean getRollbackOnly() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isActive() {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
