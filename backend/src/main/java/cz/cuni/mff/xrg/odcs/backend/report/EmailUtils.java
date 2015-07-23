/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.backend.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.LocaleHolder;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

public class EmailUtils {

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd. MMM yyyy HH:mm:ss", LocaleHolder.getLocale());
        return format.format(date);
    }

    public static String getStatusAsString(PipelineExecutionStatus status) {
        switch (status) {
            case CANCELLED:
                return Messages.getString("DailyReportEmailBuilder.cancelled");
            case CANCELLING:
                return Messages.getString("DailyReportEmailBuilder.cancelling");
            case FAILED:
                return Messages.getString("DailyReportEmailBuilder.failed");
            case FINISHED_SUCCESS:
                return Messages.getString("DailyReportEmailBuilder.finished");
            case FINISHED_WARNING:
                return Messages.getString("DailyReportEmailBuilder.finished.with.warning");
            case QUEUED:
                return Messages.getString("DailyReportEmailBuilder.queued");
            case RUNNING:
                return Messages.getString("DailyReportEmailBuilder.running");
        }
        return "";
    }

    public static String getDisplayedExecutionOwner(PipelineExecution execution) {
        StringBuilder displayedName = new StringBuilder();
        displayedName.append(execution.getOwner().getFullName());
        if (execution.getActor() != null) {
            displayedName.append(" (");
            displayedName.append(execution.getActor().getName());
            displayedName.append(")");
        }

        return displayedName.toString();
    }

    public static String getMessageTypeAsString(MessageRecordType recordType) {
        switch (recordType) {
            case DPU_DEBUG:
                return Messages.getString("InstantReportEmailBuilder.dpu.debug");
            case DPU_ERROR:
                return Messages.getString("InstantReportEmailBuilder.dpu.error");
            case DPU_INFO:
                return Messages.getString("InstantReportEmailBuilder.dpu.info");
            case DPU_TERMINATION_REQUEST:
                return Messages.getString("InstantReportEmailBuilder.dpu.termination");
            case DPU_WARNING:
                return Messages.getString("InstantReportEmailBuilder.dpu.warning");
            case PIPELINE_ERROR:
                return Messages.getString("InstantReportEmailBuilder.pipeline.error");
            case PIPELINE_INFO:
                return Messages.getString("InstantReportEmailBuilder.pipeline.info");
            default:
                return "";

        }
    }

    public static String getTableRowHTMLStartTag(PipelineExecutionStatus status) {
        switch (status) {
            case FAILED:
                return "<tr bgcolor=\"#FFE0E0\">";
            case CANCELLED:
            case CANCELLING:
            case FINISHED_WARNING:
                return "<tr bgcolor=\"#FFFFA0\">";
            case FINISHED_SUCCESS:
            case QUEUED:
            case RUNNING:
            default:
                return "<tr bgcolor=\"#E0E0FF\">";

        }
    }

}
