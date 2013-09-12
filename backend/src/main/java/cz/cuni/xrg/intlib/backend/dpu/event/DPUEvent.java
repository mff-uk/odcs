package cz.cuni.xrg.intlib.backend.dpu.event;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.execution.dpu.PostExecutor;
import cz.cuni.xrg.intlib.backend.execution.dpu.PreExecutor;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;

/**
 * Base abstract class for the DPURecord event.
 *
 * @author Petyr
 *
 */
public class DPUEvent extends ApplicationEvent {

	/**
	 * Time of creation.
	 */
	protected Date time;

	/**
	 * Related context. Identify {@link PipelineExecution} as well as
	 * {@link DPUInstanceRecord}
	 */
	protected Context context;

	/**
	 * Message type.
	 */
	protected MessageRecordType type;

	/**
	 * Short event's message.
	 */
	protected String shortMessage;

	/**
	 * Long event's message.
	 */
	protected String longMessage;

	protected DPUEvent(Context context,
			Object source,
			MessageRecordType type,
			String shortMessage,
			Exception ex) {
		super(source);
		this.time = new Date();
		this.context = context;
		this.type = type;
		this.shortMessage = shortMessage + ex.getMessage();
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		this.longMessage = sw.toString();
	}

	protected DPUEvent(Context context,
			Object source,
			MessageRecordType type,
			String shortMessage,
			String longMessage) {
		super(source);
		this.time = new Date();
		this.context = context;
		this.type = type;
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	/**
	 * Create event which announce that DPU execution started.
	 *
	 * @param context
	 * @param source
	 * @return
	 */
	public static DPUEvent createStart(Context context, Object source) {
		return new DPUEvent(context, source, MessageRecordType.DPU_INFO,
				"DPU started.", "");
	}

	/**
	 * Create event for DPU successful execution.
	 *
	 * @param context
	 * @param source
	 * @return
	 */
	public static DPUEvent createComplete(Context context, Object source) {
		return new DPUEvent(context, source, MessageRecordType.DPU_INFO,
				"DPU completed.", "");
	}

	/**
	 * Create event which warn about missing DPU's output dataUnits.
	 *
	 * @param context
	 * @param source
	 * @return
	 */
	public static DPUEvent createNoOutputWarning(Context context, Object source) {
		return new DPUEvent(context, source, MessageRecordType.DPU_WARNING,
				"Missing output DataUnit.", "");
	}

	/**
	 * Create event that announce wrong DPU state before start of the execution.
	 *
	 * @param context
	 * @param source
	 * @return
	 */
	public static DPUEvent createWrongState(Context context, Object source) {
		return new DPUEvent(context, source, MessageRecordType.DPU_ERROR,
				"Unexpected state of DPU before execution", "");
	}

	/**
	 * Create event which indicate that there has been an error in executing
	 * single DPU's preprocessor.
	 *
	 * @param context
	 * @param source
	 * @param longMessage Description of the error.
	 * @return
	 */
	public static DPUEvent createPreExecutorFailed(Context context,
			PreExecutor source,
			String longMessage) {
		return new DPUEvent(context, source, MessageRecordType.DPU_ERROR,
				"DPU's pre-executor failed.", longMessage);
	}

	/**
	 * Create event which indicate that there has been an error in executing
	 * single DPU's post-processor.
	 *
	 * @param context
	 * @param source
	 * @param longMessage Description of the error.
	 * @return
	 */
	public static DPUEvent createPostExecutorFailed(Context context,
			PostExecutor source,
			String longMessage) {
		return new DPUEvent(context, source, MessageRecordType.DPU_ERROR,
				"DPU's post-executor failed.", longMessage);
	}

	/**
	 * Create event which indicate that the DPU execution failed because DPU
	 * throw exception.
	 *
	 * @param context
	 * @param source
	 * @param ex
	 * @return
	 */
	public static DPUEvent createFailed(Context context, Object source,
			Exception e) {
		return new DPUEvent(context, source, MessageRecordType.DPU_ERROR,
				"DPU execution failed. ", e);
	}

	/**
	 * Create event which indicate that the execution of DPU failed because of
	 * {@link DataUnitException}.
	 *
	 * @param context
	 * @param source
	 * @param e
	 * @return
	 */
	public static DPUEvent createDataUnitFailed(Context context, Object source,
			DataUnitException e) {
		return new DPUEvent(context, source, MessageRecordType.DPU_ERROR,
				"DataUnit error.", e);
	}
	
	/**
	 * Record that describes event.
	 *
	 * @return record
	 */
	public MessageRecord getRecord() {
		return new MessageRecord(time, type, context.getDpuInstance(),
				context.getExecution(), shortMessage, longMessage);

	}

	public PipelineExecution getExecution() {
		return context.getExecution();
	}

	public DPUInstanceRecord getDpuInstance() {
		return context.getDpuInstance();
	}
}
