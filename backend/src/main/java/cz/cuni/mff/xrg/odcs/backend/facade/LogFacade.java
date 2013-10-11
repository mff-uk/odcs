package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogException;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Facade for managing logs, which tolerates database crashes. This facade
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
public class LogFacade extends cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade {
	
	/**
	 * Handler taking care of DB outages.
	 */
	@Autowired
	private ErrorHandler handler;

	@Override
	public List<LogMessage> getAllLogs() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllLogs();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<LogMessage> getLogs(Set<Level> levels) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogs(levels);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<LogMessage> getLogs(PipelineExecution exec) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogs(exec);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<LogMessage> getLogs(PipelineExecution exec, Set<Level> levels) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogs(exec, levels);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<LogMessage> getLogs(PipelineExecution exec, DPUInstanceRecord dpu, Set<Level> levels) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogs(exec, dpu, levels);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public LogException getLogException(LogMessage message) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogException(message);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public boolean existLogs(PipelineExecution exec, Set<Level> levels) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.existLogs(exec, levels);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<LogMessage> getLogs(PipelineExecution exec, DPUInstanceRecord dpu) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogs(exec, dpu);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public LogMessage getLog(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLog(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public InputStream getLogsAsStream(PipelineExecution pipelineExecution, DPUInstanceRecord dpu, Level level, String message, String source, Date start, Date end) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLogsAsStream(pipelineExecution, dpu, level, message, source, start, end);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}
	
	
}
