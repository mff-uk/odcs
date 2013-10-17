package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for managing DPUs, which tolerates database crashes. This facade
 * is specially altered for servicing backend, where we do not want to trash
 * all progress of unfinished pipeline runs just because of a short database
 * outage.
 *
 * <p>
 * TODO The concept of crash-proof facades could be solved nicer and with less
 *		code using AOP.
 * 
 * @author Jan Vojt
 */
public class DPUFacade extends cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade {
	
	/**
	 * Handler taking care of DB outages.
	 */
	@Autowired
	private ErrorHandler handler;

	@Override
	public List<DPUTemplateRecord> getAllTemplates() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllTemplates();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<DPUTemplateRecord> getAllTemplatesNoPermission() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllTemplatesNoPermission();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public DPUTemplateRecord getTemplate(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getTemplate(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public DPUTemplateRecord getTemplateByJarFile(File jarFile) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getTemplateByJarFile(jarFile);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public DPUTemplateRecord getTemplateByDirectory(String directory) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getTemplateByDirectory(directory);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void save(DPUTemplateRecord dpu) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(dpu);
			return;
		} catch (IllegalArgumentException ex) {
			// given DPU is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void saveNoPermission(DPUTemplateRecord dpu) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.saveNoPermission(dpu);
			return;
		} catch (IllegalArgumentException ex) {
			// given DPU is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void delete(DPUTemplateRecord dpu) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(dpu);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<DPUInstanceRecord> getAllDPUInstances() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllDPUInstances();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public DPUInstanceRecord getDPUInstance(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getDPUInstance(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void save(DPUInstanceRecord dpu) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(dpu);
			return;
		} catch (IllegalArgumentException ex) {
			// given DPU is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void delete(DPUInstanceRecord dpu) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(dpu);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<MessageRecord> getAllDPURecords() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllDPURecords();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<MessageRecord> getAllDPURecords(DPUInstanceRecord dpuInstance) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllDPURecords(dpuInstance);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<MessageRecord> getAllDPURecords(PipelineExecution pipelineExec) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllDPURecords(pipelineExec);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public MessageRecord getDPURecord(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getDPURecord(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void save(MessageRecord record) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(record);
			return;
		} catch (IllegalArgumentException ex) {
			// given DPU is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override @Transactional
	public void delete(MessageRecord record) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(record);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<DPUTemplateRecord> getChildDPUs(DPUTemplateRecord parent) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getChildDPUs(parent);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}
	
	
}
