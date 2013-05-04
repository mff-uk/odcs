/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.util.IntlibEntityManagerFactory;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author Bogo
 */
public class DPUInstanceFacade {

	/**
	 * Entity manager for accessing database with persisted objects.
	 * @todo autowire through Spring and remove setter and constructor
	 */
	private EntityManager em;

	/**
	 * Constructs facade and its dependencies.
	 */
	public DPUInstanceFacade() {
		this(IntlibEntityManagerFactory.getImem());
	}

	/**
	 * Construct with given Entity Manager
	 * @param em
	 */
	public DPUInstanceFacade(EntityManager em) {
		this.em = em;
	}

	/**
	 * Creates DPUInstance without persisting it.
	 * @return
	 */
	public DPUInstance createDPUInstance(DPU dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);
		return dpuInstance;
	}

	/**
	 * Returns list of all DPUInstances currently persisted in database.
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

		if (dpu.getId() == 0) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}

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

}
