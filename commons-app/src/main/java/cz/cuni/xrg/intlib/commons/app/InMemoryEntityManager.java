package cz.cuni.xrg.intlib.commons.app;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public class InMemoryEntityManager implements EntityManager {

	/**
	 * Entities are saved in here
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
		isOpen = false;
	}

	@Override
	public boolean contains(Object arg0) {
		return repo.containsValue(arg0);
	}

	@Override
	public Query createNamedQuery(String arg0) {
		return new QueryStub();
	}

	@Override
	public Query createNativeQuery(String arg0) {
		return new QueryStub();
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		return new QueryStub();
	}

	@Override
	public Query createNativeQuery(String sqlString,
			@SuppressWarnings("rawtypes") Class resultClass) {
		return new QueryStub();
	}

	@Override
	public Query createQuery(String arg0) {
		return new QueryStub(arg0);
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

		@SuppressWarnings("unchecked")
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

	/**
	 * Stub to immitate transaction in tests.
	 * @author Jan Vojt <jan@vojt.net>
	 */
	private class EntityTransactionStub implements EntityTransaction {

		private boolean inTransaction = false;

		@Override
		public void begin() {
			inTransaction = true;
		}

		@Override
		public void commit() {
			inTransaction = false;
		}

		@Override
		public void rollback() {
			inTransaction = false;
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
			return inTransaction;
		}

	}

	/**
	 * Stub to immitate qeuries in tests..
	 * @author Jan Vojt <jan@vojt.net>
	 */
	private class QueryStub implements Query {

        private String classString;

        private QueryStub() {

        }

        private QueryStub(String arg0) {
            if(arg0.contains("DPU")) {
                classString = "DPU";
            } else if(arg0.contains("Pipeline")) {
                classString = "Pipeline";
            }
        }

		@Override
		public List<Object> getResultList() {
            List<Object> result = new ArrayList<Object>();
            for(Object o : repo.values()) {
                if(classString.equals("DPU") && o.getClass() == DPU.class) {
                    result.add(o);
                } else if(classString.equals("Pipeline") && o.getClass() == Pipeline.class) {
                    result.add(o);
                }
            }
			return result;
		}

		@Override
		public Object getSingleResult() {
			// not supported, use EntityManager#find instead
			return null;
		}

		@Override
		public int executeUpdate() {
			return 0;
		}

		@Override
		public Query setMaxResults(int maxResult) {
			return this;
		}

		@Override
		public Query setFirstResult(int startPosition) {
			return this;
		}

		@Override
		public Query setHint(String hintName, Object value) {
			return this;
		}

		@Override
		public Query setParameter(String name, Object value) {
			return this;
		}

		@Override
		public Query setParameter(String name, Date value,
				TemporalType temporalType) {
			return this;
		}

		@Override
		public Query setParameter(String name, Calendar value,
				TemporalType temporalType) {
			return this;
		}

		@Override
		public Query setParameter(int position, Object value) {
			return this;
		}

		@Override
		public Query setParameter(int position, Date value,
				TemporalType temporalType) {
			return this;
		}

		@Override
		public Query setParameter(int position, Calendar value,
				TemporalType temporalType) {
			return this;
		}

		@Override
		public Query setFlushMode(FlushModeType flushMode) {
			return this;
		}

	}
}
