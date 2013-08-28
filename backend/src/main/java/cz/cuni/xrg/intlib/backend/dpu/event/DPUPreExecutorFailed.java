package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Used to announce problem in {@link PreExecutor} when preparing DPU and it's
 * context for execution.
 * 
 * @author Petyr
 *
 */
public class DPUPreExecutorFailed extends DPUEvent {

	public DPUPreExecutorFailed(Context context, Object source, 
			String shortMessage, String longMessage) {
		super(context, source, MessageRecordType.PIPELINE_ERROR, 
				shortMessage, longMessage);
	}
	
	public DPUPreExecutorFailed(Context context, Object source, 
			String shortMessage, Exception ex) {
		super(context, source, MessageRecordType.PIPELINE_ERROR, 
				shortMessage, ex);
	}	
	
}
