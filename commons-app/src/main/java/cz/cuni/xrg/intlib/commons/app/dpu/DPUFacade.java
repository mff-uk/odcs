package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.util.IntlibEntityManagerFactory;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Facade for working with DPUs.
 * @author Jan Vojt <jan@vojt.net>
 *
 */
public class DPUFacade {

	/**
	 * Entity manager for accessing database with persisted objects.
	 * @todo autowire through Spring and remove setter and constructor
	 */
	private EntityManager em;

	/**
	 * Constructs facade and its dependencies.
	 */
	public DPUFacade() {
		this(IntlibEntityManagerFactory.getEm());
	}

	/**
	 * Construct with given Entity Manager
	 * @param em
	 */
	public DPUFacade(EntityManager em) {
		this.em = em;
	}
	
	/* ******************* Methods for DPU management *********************** */

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

		em.persist(dpu);

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
	
	/* **************** Methods for DPU Instance management ***************** */

	/**
	 * Creates DPUInstance without persisting it.
	 * 
	 * @return
	 */
	public DPUInstance createDPUInstance(DPU dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);
		return dpuInstance;
	}

	/**
	 * Returns list of all DPUInstances currently persisted in database.
	 * 
	 * @return DPUInstance list
	 */
	public List<DPUInstance> getAllDPUInstances() {

		@SuppressWarnings("unchecked")
		List<DPUInstance> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPUInstance e").getResultList(),
				DPUInstance.class
		);

		return resultList;
	}

	/**
	 * Find DPUInstance in database by ID and return it.
	 * 
	 * @param id
	 * @return
	 */
	public DPUInstance getDPUInstance(int id) {
		return em.find(DPUInstance.class, id);
	}

	/**
	 * Saves any modifications made to the DPUInstance into the database.
	 * @param dpu
	 */
	public void save(DPUInstance dpu) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(dpu);

		tx.commit();
	}

	/**
	 * Deletes DPUInstance from the database.
	 * @param dpu
	 */
	public void delete(DPUInstance dpu) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.remove(dpu);

		tx.commit();
	}
	
	/* **************** Methods for DPU Record management ***************** */

	/**
	 * Returns list of all DPURecords currently persisted in database.
	 * 
	 * @return DPURecord list
	 */
	public List<DPURecord> getAllDPURecords() {

		@SuppressWarnings("unchecked")
		List<DPURecord> resultList = Collections.checkedList(
			em.createQuery("SELECT e FROM DPURecord e").getResultList(),
			DPURecord.class
		);

		return resultList;
	}
	
	/**
	 * Fetches all DPURecords emitted by given DPUInstance.
	 * 
	 * @param dpuInstance
	 * @return 
	 */
	public List<DPURecord> getAllDPURecords(DPUInstance dpuInstance) {

		@SuppressWarnings("unchecked")
		List<DPURecord> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM DPURecord r WHERE r.dpuInstance = :ins")
				.setParameter("ins", dpuInstance)
				.getResultList(),
			DPURecord.class
		);

		return resultList;
	}

	/**
	 * Find DPURecord in database by ID and return it.
	 * 
	 * @param id
	 * @return
	 */
	public DPURecord getDPURecord(int id) {
		return em.find(DPURecord.class, id);
	}

	/**
	 * Saves any modifications made to the DPURecord into the database.
	 * 
	 * @param record
	 */
	public void save(DPURecord record) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(record);

		tx.commit();
	}

	/**
	 * Deletes DPURecord from the database.
	 * 
	 * @param record
	 */
	public void delete(DPURecord record) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.remove(record);

		tx.commit();
	}
}
