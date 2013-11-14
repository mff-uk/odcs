package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

import java.io.File;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
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
	
	@Autowired
	private DbDPUTemplateRecord templateDao;
	
	@Autowired
	private DbDPUInstanceRecord instanceDao;
	
	@Autowired(required = false)
	private AuthenticationContext authCtx;

	/* ******************* Methods for DPUTemplateRecord management *********************** */
	
	/**
	 * Creates DPU template with given name of given type and assigns currently
	 * logged in user as owner.
	 * 
	 * @param name
	 * @param type
	 * @return newly created DPU template
	 */
	public DPUTemplateRecord createTemplate(String name, DPUType type) {
		DPUTemplateRecord dpu = new DPUTemplateRecord(name, type);
		if (authCtx != null) {
			dpu.setOwner(authCtx.getUser());
		}
		return dpu;
	}

	/**
	 * Create copy of DPU template, as the owner the current user is set.
	 * 
	 * @return
	 */
	public DPUTemplateRecord createCopy(DPUTemplateRecord original) {
		DPUTemplateRecord copy = new DPUTemplateRecord(original);
		if (authCtx != null) {
			copy.setOwner(authCtx.getUser());
		}
		return copy;
	}
	
	/**
	 * Creates a new DPURecord with the same properties and configuration as in given
	 * {@link DPUInstance}. Note that newly created DPURecord is only returned, but
	 * not managed by database. To persist it, {@link #save(DPURecord)} must be called
	 * explicitly.
	 *
	 * @param instance
	 * @return new DPURecord
	 */
	public DPUTemplateRecord createTemplateFromInstance(DPUInstanceRecord instance) {
		DPUTemplateRecord template = new DPUTemplateRecord(instance);
                if(authCtx != null) {
                    template.setOwner(authCtx.getUser());
                }
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
	@PostFilter("hasPermission(filterObject,'view')")
	public List<DPUTemplateRecord> getAllTemplates() {
		return templateDao.getAllTemplates();
	}

	/**
	 * Return list of all DPUTemplateRecords currently persisted in database.
	 * 
	 * Is used by OSGIModuleFacade, so no permissions are applied here.
	 * 
	 * @return
	 * @deprecated refactor and call DAO directly
	 */
	@Deprecated
	public List<DPUTemplateRecord> getAllTemplatesNoPermission() {
		return templateDao.getAllTemplates();
	}	
	
	/**
	 * Find DPUTemplateRecord in database by ID and return it.
	 * @param id
	 * @return
	 */
	public DPUTemplateRecord getTemplate(long id) {
		return templateDao.getInstance(id);
	}

	/**
	 * Fetch DPU template using given DPU directory.
	 * 
	 * TODO This method do not use security filters. As it is used
	 * 		in OSGIChangeManager which use it to identify DPU to update. 
	 * 
	 * @param directory
	 * @return
	 * @deprecated refactor and call DAO directly
	 */
	@Deprecated
	public DPUTemplateRecord getTemplateByDirectory(String directory) {
		return templateDao.getTemplateByDirectory(directory);
	}
	
	/**
	 * Saves any modifications made to the DPUTemplateRecord into the database.
	 * 
	 * @param dpu
	 */
	@Transactional
	@PreAuthorize("hasPermission(#dpu,'save')")
	public void save(DPUTemplateRecord dpu) {
		templateDao.save(dpu);
	}

	/**
	 * Save DPU template without without checking permissions of currently
	 * logged user. This is useful when we are updating data in database with
	 * the contents in the JAR file.
	 * 
	 * @param dpu
	 * @deprecated refactor and call DAO directly
	 */
	@Transactional
	@Deprecated
	public void saveNoPermission(DPUTemplateRecord dpu) {
		save(dpu);
	}	
	
	/**
	 * Deletes DPUTemplateRecord from the database.
	 * @param dpu
	 */
	@Transactional
	@PreAuthorize("hasPermission(#dpu,'delete')")
	public void delete(DPUTemplateRecord dpu) {
		templateDao.delete(dpu);
	}

	/**
	 * Fetch all child DPU templates for a given DPU template.
	 * 
	 * @param parent DPU template
	 * @return list of child DPU templates or empty collection
	 */
	public List<DPUTemplateRecord> getChildDPUs(DPUTemplateRecord parent) {
		return templateDao.getChildDPUs(parent);
	}

	/* **************** Methods for DPUInstanceRecord Instance management ***************** */

	/**
	 * Creates DPUInstanceRecord with configuration copied from template without
	 * persisting it.
	 *
	 * @param dpuTemplate to create from
	 * @return newly created DPU instance
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
		return instanceDao.getAllDPUInstances();
	}

	/**
	 * Find DPUInstanceRecord in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	public DPUInstanceRecord getDPUInstance(long id) {
		return instanceDao.getInstance(id);
	}

	/**
	 * Saves any modifications made to the DPUInstanceRecord into the database.
	 * @param dpu
	 */
	@Transactional
	public void save(DPUInstanceRecord dpu) {
		instanceDao.save(dpu);
	}

	/**
	 * Deletes DPUInstance from the database.
	 * @param dpu
	 */
	@Transactional
	public void delete(DPUInstanceRecord dpu) {
		instanceDao.delete(dpu);
	}

	/* **************** Methods for Record (messages) management ***************** */

	/**
	 * Fetches all DPURecords emitted by given DPUInstance.
	 *
	 * @param dpuInstance
	 * @return
	 */
	public List<MessageRecord> getAllDPURecords(DPUInstanceRecord dpuInstance) {

		@SuppressWarnings("unchecked")
		List<MessageRecord> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM MessageRecord r WHERE r.dpuInstance = :ins")
				.setParameter("ins", dpuInstance)
				.getResultList(),
			MessageRecord.class
		);

		return resultList;
	}

	/**
	 * Fetches all DPURecords emitted by given PipelineExecution.
	 *
	 * @param pipelineExec
	 * @return
	 */
	public List<MessageRecord> getAllDPURecords(PipelineExecution pipelineExec) {

		@SuppressWarnings("unchecked")
		List<MessageRecord> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM MessageRecord r WHERE r.execution = :ins")
				.setParameter("ins", pipelineExec)
				.getResultList(),
			MessageRecord.class
		);

		return resultList;
	}

	/**
	 * Find Record in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	public MessageRecord getDPURecord(long id) {
		return em.find(MessageRecord.class, id);
	}

	/**
	 * Saves any modifications made to the Record into the database.
	 *
	 * @param record
	 */
	@Transactional
	public void save(MessageRecord record) {
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
	public void delete(MessageRecord record) {
		// we might be trying to remove detached entity
		if (!em.contains(record) && record.getId() != null) {
			record = getDPURecord(record.getId());
		}
		em.remove(record);
	}
}
