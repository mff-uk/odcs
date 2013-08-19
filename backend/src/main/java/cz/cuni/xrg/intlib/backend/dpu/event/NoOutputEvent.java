package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Used to indicate that there is no output {@link DataUnit} for given DPU. Can
 * be used only for {@link extract} and {@link transform}.
 * 
 * @author Petyr
 * 
 */
public class NoOutputEvent extends DPUEvent {

	public static final String MESSAGE = "Missing output DataUnit.";

	public NoOutputEvent(DPUInstanceRecord dpuInstance,
			PipelineExecution execution,
			Object source) {
		super(source, dpuInstance, execution);
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.DPU_WARNING,
				dpuInstance, execution, MESSAGE, "");
	}

}
