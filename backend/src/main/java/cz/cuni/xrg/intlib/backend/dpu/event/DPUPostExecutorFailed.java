package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Used to announce problem in {@link PostExecutor} when post-processing 
 * the DPU and it's context.
 * 
 * @author Petyr
 *
 */
public class DPUPostExecutorFailed extends DPUEvent {

	public DPUPostExecutorFailed(Context context, Object source, 
			String shortMessage, String longMessage) {
		super(context, source, MessageRecordType.PIPELINE_ERROR, 
				shortMessage, longMessage);
	}
	
	public DPUPostExecutorFailed(Context context, Object source, 
			String shortMessage, Exception ex) {
		super(context, source, MessageRecordType.PIPELINE_ERROR, 
				shortMessage, ex);
	}	
	
}
