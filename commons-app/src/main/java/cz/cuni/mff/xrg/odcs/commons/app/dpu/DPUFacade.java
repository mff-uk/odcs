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

		@SuppressWarnings("unchecked")
		List<DPUTemplateRecord> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPUTemplateRecord e").getResultList(),
				DPUTemplateRecord.class
		);

		return resultList;
	}

	/**
	 * Return list of all DPUTemplateRecords currently persisted in database.
	 * 
	 * Is used by OSGIModuleFacade, so no permissions are applied here.
	 * 
	 * TODO Honza from Petyr: Please check this
	 * 
	 * @return
	 */
	public List<DPUTemplateRecord> getAllTemplatesNoPermission() {

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
	 * Fetch DPU template using given JAR file.
	 * 
	 * <p>
	 * TODO Currently files are compared only by filename. It would be better
	 *		if we compared file content hash instead.
	 * 
	 * <p>
	 * TODO This method cannot use any security filters, because it is used in
	 *		file upload listener. For details see GH-415.
	 * 
	 * @param jarFile
	 * @return DPU using given JAR file, or <code>null</code>
	 * 
	 * @deprecated Use {@link getTemplateByDirectory} instead.
	 */
	@Deprecated
	public DPUTemplateRecord getTemplateByJarFile(File jarFile) {
		
		DPUTemplateRecord result = null;
		try {
			result = em.createQuery(
				"SELECT e FROM DPUTemplateRecord e"
				+ " WHERE e.jarPath = :path", DPUTemplateRecord.class
			).setParameter("path", jarFile.getPath()).getSingleResult();
		} catch (NoResultException ex) {
			// just return null if nothing is found
		}
		
		return result;
	}

	/**
	 * Fetch DPU template using given DPU directory.
	 * 
	 * TODO This method do not use security filters. As it is used
	 * 		in OSGIChangeManager which use it to identify DPU to update. 
	 * 
	 * @param directory
	 * @return
	 */
	public DPUTemplateRecord getTemplateByDirectory(String directory) {
		
		DPUTemplateRecord result = null;
		try {
			result = em.createQuery(
				"SELECT e FROM DPUTemplateRecord e"
				+ " WHERE e.jarDirectory = :directory", DPUTemplateRecord.class
			).setParameter("directory", directory).getSingleResult();
		} catch (NoResultException ex) {
			// just return null if nothing is found
		}
		
		return result;
	}
	
	/**
	 * Saves any modifications made to the DPUTemplateRecord into the database.
	 * @param dpu
	 */
	@Transactional
	@PreAuthorize("hasPermission(#dpu,'save')")
	public void save(DPUTemplateRecord dpu) {
		if (dpu.getId() == null) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}
	}

	/**
	 * Save DPU template without using permissions.
	 * @param dpu
	 */
	public void saveNoPermission(DPUTemplateRecord dpu) {
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
	@PreAuthorize("hasPermission(#dpu,'delete')")
	public void delete(DPUTemplateRecord dpu) {
		// we might be trying to remove detached entity
		if (!em.contains(dpu) && dpu.getId() != null) {
			dpu = getTemplate(dpu.getId());
		}
		em.remove(dpu);
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
		if (!em.contains(dpu) && dpu.getId() != null) {
			dpu = getDPUInstance(dpu.getId());
		}
		em.remove(dpu);
	}

	/* **************** Methods for Record (messages) management ***************** */

	/**
	 * Returns list of all Records currently persisted in database.
	 *
	 * @return Record list
	 */
	public List<MessageRecord> getAllDPURecords() {

		@SuppressWarnings("unchecked")
		List<MessageRecord> resultList = Collections.checkedList(
			em.createQuery("SELECT e FROM MessageRecord e").getResultList(),
			MessageRecord.class
		);

		return resultList;
	}

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

	public List<DPUTemplateRecord> getChildDPUs(DPUTemplateRecord parent) {
		@SuppressWarnings("unchecked")
		List<DPUTemplateRecord> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPUTemplateRecord e WHERE e.parent = :tmpl").setParameter("tmpl", parent).getResultList(),
				DPUTemplateRecord.class
		);

		return resultList;
	}

}
