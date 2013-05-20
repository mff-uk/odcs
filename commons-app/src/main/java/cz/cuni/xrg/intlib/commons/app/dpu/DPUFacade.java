package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.execution.Record;
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
	 * Creates DPU and its {@link TemplateConfiguration} without persisting it.
	 * 
	 * @return
	 */
	public DPU createDpu() {
		DPU dpu = new DPU();
		dpu.setTemplateConfiguration(new TemplateConfiguration());
		return dpu;
	}
	
	/**
	 * Creates a new DPU with the same properties and configuration as in given
	 * {@link DPUInstance}. Note that newly created DPU is only returned, but
	 * not managed by database. To persist it, {@link #save(DPU)} must be called
	 * explicitly. 
	 * 
	 * @param instance
	 * @return new DPU
	 */
	public DPU createDpuFromInstance(DPUInstance instance) {
		
		DPU oDpu = instance.getDpu();
		DPU nDpu = new DPU();
		
		// copy properties
		nDpu.setName(instance.getName());
		nDpu.setDescription(instance.getDescription());
		nDpu.setJarPath(oDpu.getJarPath());
		nDpu.setVisibility(VisibilityType.PRIVATE);
		
		// copy configuration
		TemplateConfiguration conf = new TemplateConfiguration();
		conf.setValues(instance.getInstanceConfig().getValues());
		nDpu.setTemplateConfiguration(conf);
		
		return nDpu;
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
	public DPU getDpu(long id) {
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
	 * Creates DPUInstance with configuration copied from template without
	 * persisting it.
	 * 
	 * @return
	 */
	public DPUInstance createDPUInstance(DPU dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);
		
		// convert template configuration to instance configuration
		InstanceConfiguration conf = new InstanceConfiguration();
		conf.setValues(dpu.getTemplateConfiguration().getValues());
		dpuInstance.setInstanceConfig(conf);
		
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
	public DPUInstance getDPUInstance(long id) {
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
	 * @return Record list
	 */
	public List<Record> getAllDPURecords() {

		@SuppressWarnings("unchecked")
		List<Record> resultList = Collections.checkedList(
			em.createQuery("SELECT e FROM Record e").getResultList(),
			Record.class
		);

		return resultList;
	}
	
	/**
	 * Fetches all DPURecords emitted by given DPUInstance.
	 * 
	 * @param dpuInstance
	 * @return 
	 */
	public List<Record> getAllDPURecords(DPUInstance dpuInstance) {

		@SuppressWarnings("unchecked")
		List<Record> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM Record r WHERE r.dpuInstance = :ins")
				.setParameter("ins", dpuInstance)
				.getResultList(),
			Record.class
		);

		return resultList;
	}

	/**
	 * Find Record in database by ID and return it.
	 * 
	 * @param id
	 * @return
	 */
	public Record getDPURecord(long id) {
		return em.find(Record.class, id);
	}

	/**
	 * Saves any modifications made to the Record into the database.
	 * 
	 * @param record
	 */
	public void save(Record record) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(record);

		tx.commit();
	}

	/**
	 * Deletes Record from the database.
	 * 
	 * @param record
	 */
	public void delete(Record record) {

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.remove(record);

		tx.commit();
	}
}
