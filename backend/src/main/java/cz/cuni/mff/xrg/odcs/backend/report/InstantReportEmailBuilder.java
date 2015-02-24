package cz.cuni.mff.xrg.odcs.backend.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;

/**
 * Create email with instant report.
 * 
 * @author Petyr
 */
@Component
class InstantReportEmailBuilder {

    @Autowired
    private DPUFacade dpuFacade;

    @Autowired
    private AppConfig config;

    public String build(PipelineExecution execution, Schedule schedule) {
        StringBuilder body = new StringBuilder();

        try {
            final String name = config.getString(ConfigProperty.BACKEND_NAME);
            body.append("<p>");
            body.append(Messages.getString("InstantReportEmailBuilder.instance"));
            body.append(name);
            body.append("</p><br/>");
        } catch (MissingConfigPropertyException e) {
            // no name is presented
        }

        body.append("<b>");
        body.append(Messages.getString("InstantReportEmailBuilder.report"));
        body.append("</b>");
        body.append(execution.getPipeline().getName());
        body.append("<br/>");
        body.append("<b>");
        body.append(Messages.getString("InstantReportEmailBuilder.execution"));
        body.append("</b>");
        body.append(execution.getId().toString());
        body.append("<br/>");
        body.append("<b>");
        body.append(Messages.getString("InstantReportEmailBuilder.execution.starts"));
        body.append("</b>");
        body.append(execution.getStart().toString());
        body.append("<br/>");
        body.append("<b>");
        body.append(Messages.getString("InstantReportEmailBuilder.execution.ends"));
        body.append("</b>");
        body.append(execution.getEnd().toString());
        body.append("<br/>");
        body.append("<b>");
        body.append(Messages.getString("InstantReportEmailBuilder.execution.result"));
        body.append("</b>");
        body.append(execution.getStatus());
        // add link to the execution detail if the url is specified
        try {
            String urlBase = config.getString(ConfigProperty.FRONTEND_URL);
            if (!urlBase.endsWith("/")) {
                urlBase = urlBase + "/";
            }
            urlBase = urlBase + "#!ExecutionList/exec=" + execution.getId().toString();
            // generate link
            body.append("<br/>");
            body.append("<a href=/");
            body.append(urlBase);
            body.append("\" >");
            body.append(Messages.getString("InstantReportEmailBuilder.execution.detail"));
            body.append("<a/> ");
        } catch (MissingConfigPropertyException e) {
            // no name is presented
        }
        body.append("<br/><br/>");
        // append messages
        final List<MessageRecord> messages = dpuFacade.getAllDPURecords(execution);
        body.append("<b>");
        body.append(Messages.getString("InstantReportEmailBuilder.published.messages"));
        body.append("</b> <br/>");
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
            if (message.getDpuInstance() == null) {
                // no dpu ..
            } else {
                // add the dpu name ..
                body.append(message.getDpuInstance().getName());
            }
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
