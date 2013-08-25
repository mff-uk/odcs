package cz.cuni.xrg.intlib.backend.dpu.event;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;

/**
 * Used to indicate that there is no output {@link DataUnit} for given DPU. Can
 * be used only for {@link extract} and {@link transform}.
 * 
 * @author Petyr
 * 
 */
public class DPUNoOutputEvent extends DPUEvent {

	public static final String MESSAGE = "Missing output DataUnit.";

	public DPUNoOutputEvent(Context context, Object source) {
		super(context, source, MessageRecordType.DPU_WARNING, MESSAGE);
	}

}
