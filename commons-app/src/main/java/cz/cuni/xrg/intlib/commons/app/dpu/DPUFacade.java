package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for working with DPUs.
 *
 * @author Jan Vojt
 */
public class DPUFacade {

	private static final Logger LOG = LoggerFactory.getLogger(DPUFacade.class);

	/**
	 * Entity manager for accessing database with persisted objects.
	 */
	@PersistenceContext
	private EntityManager em;

	/* ******************* Methods for DPUTemplateRecord management *********************** */

	/**
	 * Creates a new DPURecord with the same properties and configuration as in given
	 * {@link DPUInstance}. Note that newly created DPURecord is only returned, but
	 * not managed by database. To persist it, {@link #save(DPURecord)} must be called
	 * explicitly.
	 *
	 * @param instance
	 * @return new DPURecord
	 */
	public DPUTemplateRecord creatTemplateFromInstance(DPUInstanceRecord instance) {
		DPUTemplateRecord template = new DPUTemplateRecord(instance);
		if(instance.getTemplate().getParent() == null) {
			template.setParent(instance.getTemplate());
		} else {
			template.setParent(instance.getTemplate().getParent());
		}
		return template;
	}

	/**
	 * Returns list of all DPUTemplateRecords currently persisted in database.
	 * @return DPURecord list
	 */
	public List<DPUTemplateRecord> getAllTemplates() {

		@SuppressWarnings("unchecked")
		List<DPUTemplateRecord> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPUTemplateRecord e").getResultList(),
				DPUTemplateRecord.class
		);

		return resultList;
	}

	/**
	 * Find DPUTemplateRecord in database by ID and return it.
	 * @param id
	 * @return
	 */
	public DPUTemplateRecord getTemplate(long id) {
		return em.find(DPUTemplateRecord.class, id);
	}

	/**
	 * Saves any modifications made to the DPUTemplateRecord into the database.
	 * @param dpu
	 */
	@Transactional
	public void save(DPUTemplateRecord dpu) {
		if (dpu.getId() == null) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}
	}

	/**
	 * Deletes DPUTemplateRecord from the database.
	 * @param dpu
	 */
	@Transactional
	public void delete(DPUTemplateRecord dpu) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		DPUTemplateRecord d = dpu.getId() == null ? dpu : getTemplate(dpu.getId());
		if (d != null) {
			em.remove(d);
		} else {
			LOG.warn("DPURecord with ID " + dpu.getId() + " was not found and so cannot be deleted!");
		}
	}

	/* **************** Methods for DPUInstanceRecord Instance management ***************** */

	/**
	 * Creates DPUInstanceRecord with configuration copied from template without
	 * persisting it.
	 *
	 * @return
	 */
	public DPUInstanceRecord createInstanceFromTemplate(DPUTemplateRecord dpuTemplate) {
		DPUInstanceRecord dpuInstance = new DPUInstanceRecord(dpuTemplate);		
		return dpuInstance;
	}

	/**
	 * Returns list of all DPUInstanceRecord currently persisted in database.
	 *
	 * @return DPUInstance list
	 */
	public List<DPUInstanceRecord> getAllDPUInstances() {

		@SuppressWarnings("unchecked")
		List<DPUInstanceRecord> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPUInstanceRecord e").getResultList(),
				DPUInstanceRecord.class
		);

		return resultList;
	}

	/**
	 * Find DPUInstanceRecord in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	public DPUInstanceRecord getDPUInstance(long id) {
		return em.find(DPUInstanceRecord.class, id);
	}

	/**
	 * Saves any modifications made to the DPUInstanceRecord into the database.
	 * @param dpu
	 */
	@Transactional
	public void save(DPUInstanceRecord dpu) {
		if (dpu.getId() == null) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}
	}

	/**
	 * Deletes DPUInstance from the database.
	 * @param dpu
	 */
	@Transactional
	public void delete(DPUInstanceRecord dpu) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		DPUInstanceRecord d = dpu.getId() == null
				? dpu : getDPUInstance(dpu.getId());
		if (d != null) {
			em.remove(d);
		} else {
			LOG.warn("DPURecord instance with ID " + dpu.getId() + " was not found and so cannot be deleted!");
		}
	}

	/* **************** Methods for Record (messages) management ***************** */

	/**
	 * Returns list of all Records currently persisted in database.
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
	public List<Record> getAllDPURecords(DPUInstanceRecord dpuInstance) {

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
	 * Fetches all DPURecords emitted by given PipelineExecution.
	 *
	 * @param pipelineExec
	 * @return
	 */
	public List<Record> getAllDPURecords(PipelineExecution pipelineExec) {

		@SuppressWarnings("unchecked")
		List<Record> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM Record r WHERE r.execution = :ins")
				.setParameter("ins", pipelineExec)
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
	@Transactional
	public void save(Record record) {
		if (record.getId() == null) {
			em.persist(record);
		} else {
			em.merge(record);
		}
	}

	/**
	 * Deletes Record from the database.
	 *
	 * @param record
	 */
	@Transactional
	public void delete(Record record) {
		em.remove(record);
	}

}
