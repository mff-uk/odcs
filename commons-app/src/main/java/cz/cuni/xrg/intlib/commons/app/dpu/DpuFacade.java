package cz.cuni.xrg.intlib.commons.app.dpu;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import cz.cuni.xrg.intlib.commons.app.util.IntlibEntityManagerFactory;

/**
 * Facade for working with DPUs.
 * @author Jan Vojt <jan@vojt.net>
 *
 */
public class DpuFacade {
	
	/**
	 * Entity manager for accessing database with persisted objects.
	 * @todo autowire through Spring and remove setter and constructor
	 */
	private EntityManager em;
	
	/**
	 * Constructs facade and its dependencies.
	 */
	public DpuFacade() {
		this(IntlibEntityManagerFactory.getImem());
	}
	
	/**
	 * Construct with given Entity Manager
	 * @param em
	 */
	public DpuFacade(EntityManager em) {
		this.em = em;
	}
	
	/**
	 * Creates DPU without persisting it.
	 * @return
	 */
	public DPU createDpu() {
		DPU dpu = new DPU();
		return dpu;
	}
	
	/**
	 * Returns list of all DPUs currently persisted in database.
	 * @return DPU list
	 */
	public List<DPU> getAllDpus() {
		
		@SuppressWarnings("unchecked")
		List<DPU> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPU e").getResultList(),
				DPU.class
		);
		
		return resultList;
	}
	
	/**
	 * Find DPU in database by ID and return it.
	 * @param id
	 * @return
	 */
	public DPU getDpu(int id) {
		return em.find(DPU.class, id);
	}
	
	/**
	 * Saves any modifications made to the DPU into the database.
	 * @param dpu
	 */
	public void save(DPU dpu) {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		if (dpu.getId() == 0) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}
		
		tx.commit();
	}
	
	/**
	 * Deletes DPU from the database.
	 * @param dpu
	 */
	public void delete(DPU dpu) {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.remove(dpu);
		
		tx.commit();
	}

}
