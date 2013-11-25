package cz.cuni.mff.xrg.odcs.backend.report;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Create email with instant report.
 * 
 * @author Petyr
 */
@Component
class InstantReportEmailBuilder {
	
	@Autowired
	private DPUFacade dpuFacade;
	
	public String build(PipelineExecution execution, Schedule schedule) {
		StringBuilder body = new StringBuilder();

		body.append("<b>Report for pipeline: </b>");
		body.append(execution.getPipeline().getName());
		body.append("<br/>");
		body.append("<b>Execution starts at: </b>");
		body.append(execution.getStart().toString());
		body.append("<br/>");
		body.append("<b>Execution ends at: </b>");
		body.append(execution.getEnd().toString());
		body.append("<br/>");
		body.append("<b>Execution result: </b>");
		body.append(execution.getStatus());
		body.append("<br/><br/>");
		// append messages
		final List<MessageRecord> messages = dpuFacade.getAllDPURecords(execution);
		body.append("<b>Published messages:</b> <br/>");
		body.append("<table border=2 cellpadding=2 >");
		body.append("<tr bgcolor=\"#C0C0C0\">");
		body.append("<th>dpu</th><th>time</th><th>type</th><th>short message</th>");
		body.append("</tr>");
		
		for (MessageRecord message : messages) {
			// set color based on type
			switch (message.getType()) {
			case DPU_DEBUG:
				body.append("<tr>");
				break;
			case DPU_ERROR:
			case PIPELINE_ERROR:
				body.append("<tr bgcolor=\"#FFE0E0\">");
				break;
			case DPU_INFO:
			case PIPELINE_INFO:
				body.append("<tr bgcolor=\"#E0E0FF\">");
				break;			
			case DPU_WARNING:
				body.append("<tr bgcolor=\"#FFFFA0\">");
				break;
			}
			// name
			body.append("<td>");
			body.append(message.getDpuInstance().getName());
			body.append("</td>");
			// time
			body.append("<td>");
			body.append(message.getTime());
			body.append("</td>");
			// type
			body.append("<td>");
			body.append(message.getType().toString());
			body.append("</td>");
			// short message			
			body.append("<td>");
			body.append(message.getShortMessage());
			body.append("</td>");
			// ...
			body.append("</tr>");
		}
		
		body.append("</table>");
		
		return body.toString();
	}
	
}
