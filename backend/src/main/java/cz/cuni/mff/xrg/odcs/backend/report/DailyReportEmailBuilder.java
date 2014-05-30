package cz.cuni.mff.xrg.odcs.backend.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Create email with daily report.
 * 
 * @author Petyr
 */
@Component
class DailyReportEmailBuilder {

    @Autowired
    private AppConfig config;

    public String build(List<PipelineExecution> executions) {
        StringBuilder body = new StringBuilder();

        try {
            final String name = config.getString(ConfigProperty.BACKEND_NAME);
            body.append("<p>Instance: ");
            body.append(name);
            body.append("</p><br/>");
        } catch (MissingConfigPropertyException e) {
            // no name is presented
        }

        String urlBase = null;
        try {
            urlBase = config.getString(ConfigProperty.FRONTEND_URL);
            if (!urlBase.endsWith("/")) {
                urlBase = urlBase + "/";
            }
            urlBase = urlBase + "#!ExecutionList/exec=";
        } catch (MissingConfigPropertyException e) {
            // no name is presented
        }

        body.append("<table border=2 cellpadding=2 >");
        body.append("<tr bgcolor=\"#C0C0C0\">");
        body.append("<th>pipeline</th><th>execution</th><th>start</th><th>end</th><th>result</th>");
        // add column for link
        if (urlBase != null) {
            body.append("<th>detail</th>");
        }
        body.append("</tr>");

        for (PipelineExecution exec : executions) {
            body.append("<tr>");
            // pipeline
            body.append("<td>");
            body.append(exec.getPipeline().getName());
            body.append("</td>");
            // execution id
            body.append("<td>");
            body.append(exec.getId().toString());
            body.append("</td>");
            // start
            body.append("<td>");
            body.append(exec.getStart().toString());
            body.append("</td>");
            // end
            body.append("<td>");
            body.append(exec.getEnd().toString());
            body.append("</td>");
            // result
            body.append("<td>");
            switch (exec.getStatus()) {
                case CANCELLED:
                    body.append("cancelled");
                    break;
                case CANCELLING:
                    body.append("canceling");
                    break;
                case FAILED:
                    body.append("failed");
                    break;
                case FINISHED_SUCCESS:
                    body.append("finished");
                    break;
                case FINISHED_WARNING:
                    body.append("finished with warning");
                    break;
                case QUEUED:
                    body.append("queued");
                    break;
                case RUNNING:
                    body.append("running");
                    break;
            }
            body.append("</td>");
            // link 
            if (urlBase != null) {
                body.append("<td> <a href=/");
                body.append(urlBase);
                body.append(exec.getId().toString());
                body.append("\" >Execution detail<a/> </td>");
            }

            // end line
            body.append("</tr>");
        }
        body.append("</table>");

        body.append("<br>");

        return body.toString();
    }

}
